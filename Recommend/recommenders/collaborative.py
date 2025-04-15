# recommenders/collaborative.py

import logging
import json
import pickle
from datetime import datetime
from typing import List, Dict, Any, Optional

import torch
import numpy as np
from torch.utils.data import TensorDataset, DataLoader
from cf_step import Step, SimpleCF, SGD

from config.db import execute_query, execute_query_single, execute_transaction
from config.config import DEFAULT_RECOMMENDATION_LIMIT

logger = logging.getLogger(__name__)

class CollaborativeRecommender:
    """
    Collaborative filtering recommender using CF-STEP library.
    Implements incremental matrix factorization for positive-only feedback.
    """
    
    def __init__(self, n_factors=50, model_name="default"):
        """Initialize the collaborative filtering recommender."""
        self.n_factors = n_factors
        self.model_name = model_name
        self.device = 'cuda' if torch.cuda.is_available() else 'cpu'
        self.model = None
        
        # Load model if exists
        self.model_loaded = self._load_model()
    
    def _load_model(self):
        """Load model from database if it exists."""
        try:
            query = """
            SELECT model_id, user_map, item_map, model_data, n_factors, created_at
            FROM matrix_factorization_models
            WHERE model_name = %(model_name)s
            ORDER BY created_at DESC
            LIMIT 1
            """
            
            result = execute_query_single(query, {"model_name": self.model_name})
            
            if not result:
                logger.info(f"No existing model found for {self.model_name}")
                return False
                
            self.user_map = json.loads(result["user_map"])
            self.item_map = json.loads(result["item_map"])
            self.n_factors = result["n_factors"]
            self.model_id = result["model_id"]
            self.created_at = result["created_at"]
            
            # Initialize CF-STEP model
            n_users = max(int(uid) for uid in self.user_map.values()) + 1
            n_items = max(int(iid) for iid in self.item_map.values()) + 1
            
            net = SimpleCF(n_users, n_items, factors=self.n_factors, mean=0., std=0.1)
            objective = lambda pred, targ: targ - pred
            optimizer = SGD(net.parameters(), lr=0.06)
            
            self.model = Step(net, objective, optimizer, device=self.device)
            
            # Load model weights
            model_data = pickle.loads(result["model_data"])
            self.model.net.load_state_dict(model_data)
            
            logger.info(f"Loaded CF model {self.model_name}, created at {self.created_at}")
            return True
            
        except Exception as e:
            logger.error(f"Error loading collaborative filtering model: {e}")
            return False
    
    def train(self):
        """
        Train the collaborative filtering model using all available interaction data.
        Uses CF-STEP library for incremental matrix factorization.
        """
        try:
            # Get all user interactions with positive feedback
            query = """
            SELECT user_id, recipe_id, 
                   CASE 
                       WHEN interaction_type = 'rating' AND rating >= 4 THEN 1
                       WHEN interaction_type = 'like' THEN 1
                       WHEN interaction_type = 'save' THEN 1
                       WHEN interaction_type = 'cook' THEN 1
                       ELSE 0
                   END as preference
            FROM user_interactions
            WHERE interaction_type IN ('rating', 'like', 'save', 'cook')
            ORDER BY created_at ASC
            """
            
            interactions = execute_query(query)
            
            if not interactions or len(interactions) < 10:
                logger.warning("Not enough interactions to train collaborative filtering model")
                return False
            
            # Filter positive interactions
            positive_interactions = [i for i in interactions if i["preference"] == 1]
            
            if len(positive_interactions) < 10:
                logger.warning("Not enough positive interactions to train collaborative filtering model")
                return False
            
            # Create user and item mappings
            unique_users = list(set(item["user_id"] for item in positive_interactions))
            unique_items = list(set(str(item["recipe_id"]) for item in positive_interactions))
            
            user_map = {user: idx for idx, user in enumerate(unique_users)}
            item_map = {item: idx for idx, item in enumerate(unique_items)}
            
            # Set up CF-STEP model
            n_users = len(user_map)
            n_items = len(item_map)
            
            net = SimpleCF(n_users, n_items, factors=self.n_factors, mean=0., std=0.1)
            objective = lambda pred, targ: targ - pred
            optimizer = SGD(net.parameters(), lr=0.06)
            
            self.model = Step(net, objective, optimizer, device=self.device)
            
            # Prepare bootstrapping data (20%)
            bootstrap_count = int(len(positive_interactions) * 0.2)
            bootstrap_data = positive_interactions[:bootstrap_count]
            
            # Prepare dataset for batch training
            features = []
            targets = []
            
            for interaction in bootstrap_data:
                user_idx = user_map[interaction["user_id"]]
                item_idx = item_map[str(interaction["recipe_id"])]
                preference = interaction["preference"]
                
                features.append([user_idx, item_idx, 1.0])  # Rating doesn't matter for CF-STEP
                targets.append([preference])
            
            train_dataset = TensorDataset(
                torch.tensor(features, dtype=torch.long),
                torch.tensor(targets, dtype=torch.float)
            )
            train_loader = DataLoader(train_dataset, batch_size=64, shuffle=True)
            
            # Batch fit for bootstrapping
            logger.info(f"Bootstrapping with {bootstrap_count} interactions")
            self.model.batch_fit(train_loader)
            
            # Incremental updates with remaining data
            streaming_data = positive_interactions[bootstrap_count:]
            logger.info(f"Incrementally updating with {len(streaming_data)} interactions")
            
            for interaction in streaming_data:
                user_idx = torch.tensor([user_map[interaction["user_id"]]], dtype=torch.long)
                item_idx = torch.tensor([item_map[str(interaction["recipe_id"])]], dtype=torch.long)
                rating = torch.tensor([1.0], dtype=torch.float)  # Always 1 for positive feedback
                pref = torch.tensor([1.0], dtype=torch.float)    # Always 1 for positive feedback
                
                self.model.step(user_idx, item_idx, rating, pref)
            
            # Store mappings
            self.user_map = user_map
            self.item_map = item_map
            
            # Save model to database
            self._save_model()
            
            return True
            
        except Exception as e:
            logger.error(f"Error training collaborative filtering model: {e}")
            return False
    
    def _save_model(self):
        """Save the trained model to the database."""
        try:
            # Serialize model data
            model_data = pickle.dumps(self.model.net.state_dict())
            user_map_json = json.dumps(self.user_map)
            item_map_json = json.dumps(self.item_map)
            
            query = """
            INSERT INTO matrix_factorization_models
            (model_name, user_map, item_map, model_data, n_factors, created_at, updated_at)
            VALUES
            (%(model_name)s, %(user_map)s, %(item_map)s, %(model_data)s, %(n_factors)s, NOW(), NOW())
            RETURNING model_id
            """
            
            params = {
                "model_name": self.model_name,
                "user_map": user_map_json,
                "item_map": item_map_json,
                "model_data": model_data,
                "n_factors": self.n_factors
            }
            
            result = execute_query_single(query, params)
            
            if result:
                self.model_id = result["model_id"]
                self.model_loaded = True
                logger.info(f"Saved collaborative filtering model {self.model_name} with ID {self.model_id}")
                return True
            
            return False
            
        except Exception as e:
            logger.error(f"Error saving collaborative filtering model: {e}")
            return False
    
    def get_recommendations(self, user_id, limit=DEFAULT_RECOMMENDATION_LIMIT, exclude_ids=None, filters=None):
        """Get recommendations for a user using collaborative filtering."""
        try:
            if not self.model_loaded or not self.model:
                logger.warning("Collaborative filtering model not loaded")
                return []
            
            # Check if user exists in our model
            if user_id not in self.user_map:
                logger.info(f"User {user_id} not found in collaborative filtering model")
                
                # Try to find the most similar user instead
                similar_user = self._find_most_similar_user(user_id)
                if not similar_user:
                    return []
                
                user_id = similar_user
            
            user_idx = self.user_map[user_id]
            
            # Get top-k recommendations using CF-STEP predict method
            user_tensor = torch.tensor([user_idx], dtype=torch.long).to(self.device)
            predictions = self.model.predict(user_tensor, k=limit*2)  # Get more to allow for filtering
            
            # Convert tensor to list and map back to recipe IDs
            idx_to_item = {idx: item_id for item_id, idx in self.item_map.items()}
            recommended_items = []
            
            for idx in predictions[0].cpu().numpy():
                if idx in idx_to_item:
                    item_id = idx_to_item[idx]
                    recommended_items.append({
                        "id": item_id,
                        "content_type": "recipe",
                        "score": 0.9  # CF-STEP doesn't provide scores, use high confidence
                    })
            
            # Exclude already interacted items
            if exclude_ids:
                exclude_set = set(str(id) for id in exclude_ids)
                recommended_items = [item for item in recommended_items if item["id"] not in exclude_set]
            
            # Apply additional filters if specified
            if filters:
                filtered_items = self._apply_filters(recommended_items, filters)
                recommended_items = filtered_items
            
            # Get recipe titles for recommendations
            from models.queries_recipe import get_recipe
            for item in recommended_items:
                recipe_id = item["id"]
                recipe = get_recipe(recipe_id)
                if recipe:
                    item["title"] = recipe["recipe_title"]
            
            return recommended_items[:limit]
            
        except Exception as e:
            logger.error(f"Error generating collaborative filtering recommendations: {e}")
            return []
    
    def _apply_filters(self, items, filters):
        """Apply filters to recommended items."""
        # Query to check if items match filters
        item_ids = [item["id"] for item in items]
        if not item_ids:
            return []
            
        filter_query = "SELECT recipe_id::TEXT FROM recipes WHERE recipe_id::TEXT = ANY(%(item_ids)s)"
        filter_params = {"item_ids": item_ids}
        
        # Add filter conditions
        if filters.get("region"):
            filter_query += " AND region = %(region)s"
            filter_params["region"] = filters["region"]
        
        if filters.get("sub_region"):
            filter_query += " AND sub_region = %(sub_region)s"
            filter_params["sub_region"] = filters["sub_region"]
        
        if filters.get("dietary_restriction"):
            filter_query += """
            AND EXISTS (
                SELECT 1 FROM recipe_diet_attributes
                WHERE recipe_id = recipes.recipe_id
                AND CASE 
                    WHEN %(dietary)s = 'vegan' THEN vegan = TRUE
                    WHEN %(dietary)s = 'pescetarian' THEN pescetarian = TRUE
                    WHEN %(dietary)s = 'lacto_vegetarian' THEN lacto_vegetarian = TRUE
                    ELSE FALSE
                END
            )
            """
            filter_params["dietary"] = filters["dietary_restriction"]
        
        # Execute filter query
        matching_items = execute_query(filter_query, filter_params)
        matching_ids = set(item["recipe_id"] for item in matching_items)
        
        # Filter items
        return [item for item in items if item["id"] in matching_ids]
    
    def _find_most_similar_user(self, user_id):
        """Find the most similar user in the model to a user not in the model."""
        try:
            # Get recent interactions for the user
            query = """
            SELECT recipe_id FROM user_interactions
            WHERE user_id = %(user_id)s
            ORDER BY created_at DESC
            LIMIT 10
            """
            
            user_interactions = execute_query(query, {"user_id": user_id})
            
            if not user_interactions:
                return None
            
            # Get users who interacted with the same items
            user_items = [str(interaction["recipe_id"]) for interaction in user_interactions]
            
            query = """
            SELECT user_id, COUNT(*) as common_count
            FROM user_interactions
            WHERE recipe_id::TEXT = ANY(%(item_ids)s)
              AND user_id != %(user_id)s
            GROUP BY user_id
            ORDER BY common_count DESC
            LIMIT 5
            """
            
            similar_users = execute_query(query, {"item_ids": user_items, "user_id": user_id})
            
            if not similar_users:
                return None
            
            # Find a user who exists in our model
            for user in similar_users:
                if user["user_id"] in self.user_map:
                    return user["user_id"]
            
            return None
            
        except Exception as e:
            logger.error(f"Error finding similar user: {e}")
            return None