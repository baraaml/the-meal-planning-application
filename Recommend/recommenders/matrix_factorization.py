"""
Matrix Factorization implementation for collaborative filtering.
"""
import logging
import numpy as np
import json
import pickle
from typing import Dict, List, Any, Optional, Tuple

from config.db import execute_query, execute_query_single, execute_transaction

logger = logging.getLogger(__name__)

class MatrixFactorization:
    """
    Matrix Factorization model for collaborative filtering.
    Implements Alternating Least Squares (ALS) algorithm.
    """
    
    def __init__(self, n_factors=50, reg_param=0.1, n_iterations=20, model_name="default"):
        """
        Initialize the matrix factorization model.
        
        Args:
            n_factors: Number of latent factors
            reg_param: Regularization parameter
            n_iterations: Number of iterations for ALS
            model_name: Name of the model (for database storage)
        """
        self.n_factors = n_factors
        self.reg_param = reg_param
        self.n_iterations = n_iterations
        self.model_name = model_name
        
        # Model components
        self.user_factors = None
        self.item_factors = None
        self.user_map = {}
        self.item_map = {}
        self.global_mean = 0.0
        
        # Load model if it exists
        self.model_loaded = self._load_model()
    
    def _load_model(self) -> bool:
        """
        Load model from database if it exists.
        
        Returns:
            bool: True if model was loaded successfully, False otherwise
        """
        try:
            query = """
            SELECT 
                model_id, model_data, user_map, item_map, n_factors, 
                regularization, global_mean, created_at
            FROM matrix_factorization_models
            WHERE model_name = %(model_name)s
            ORDER BY created_at DESC
            LIMIT 1
            """
            
            result = execute_query_single(query, {"model_name": self.model_name})
            
            if not result:
                logger.info(f"No existing model found for {self.model_name}")
                return False
            
            # Load model parameters
            model_data = pickle.loads(result["model_data"])
            self.user_map = json.loads(result["user_map"])
            self.item_map = json.loads(result["item_map"])
            self.n_factors = result["n_factors"]
            self.reg_param = result["regularization"]
            self.global_mean = result["global_mean"]
            self.user_factors = model_data["user_factors"]
            self.item_factors = model_data["item_factors"]
            
            logger.info(f"Loaded matrix factorization model {self.model_name}")
            return True
            
        except Exception as e:
            logger.error(f"Error loading matrix factorization model: {e}")
            return False
    
    def _save_model(self) -> bool:
        """
        Save model to database.
        
        Returns:
            bool: True if model was saved successfully, False otherwise
        """
        try:
            # Prepare model data
            model_data = {
                "user_factors": self.user_factors,
                "item_factors": self.item_factors
            }
            
            # Serialize data
            model_data_serialized = pickle.dumps(model_data)
            user_map_json = json.dumps(self.user_map)
            item_map_json = json.dumps(self.item_map)
            
            # Insert or update model
            query = """
            INSERT INTO matrix_factorization_models
            (model_name, model_data, user_map, item_map, n_factors, 
             regularization, global_mean, created_at, updated_at)
            VALUES
            (%(model_name)s, %(model_data)s, %(user_map)s, %(item_map)s,
             %(n_factors)s, %(regularization)s, %(global_mean)s, NOW(), NOW())
            ON CONFLICT (model_name) 
            DO UPDATE SET
                model_data = %(model_data)s,
                user_map = %(user_map)s,
                item_map = %(item_map)s,
                n_factors = %(n_factors)s,
                regularization = %(regularization)s,
                global_mean = %(global_mean)s,
                updated_at = NOW()
            RETURNING model_id
            """
            
            params = {
                "model_name": self.model_name,
                "model_data": model_data_serialized,
                "user_map": user_map_json,
                "item_map": item_map_json,
                "n_factors": self.n_factors,
                "regularization": float(self.reg_param),
                "global_mean": float(self.global_mean)
            }
            
            result = execute_query_single(query, params)
            
            if result:
                logger.info(f"Saved matrix factorization model {self.model_name}")
                return True
            
            return False
            
        except Exception as e:
            logger.error(f"Error saving matrix factorization model: {e}")
            return False
    
    def train(self, interactions: Optional[List[Dict[str, Any]]] = None) -> bool:
        """
        Train the matrix factorization model using ALS algorithm.
        
        Args:
            interactions: Optional list of user-item interactions
                If None, interactions will be loaded from the database
                
        Returns:
            bool: True if training was successful, False otherwise
        """
        try:
            # Load interactions from database if not provided
            if interactions is None:
                interactions = self._load_interactions()
            
            if not interactions or len(interactions) < 10:
                logger.warning("Not enough interactions to train model")
                return False
            
            # Create user and item mappings
            self._create_mappings(interactions)
            
            # Create rating matrix
            ratings_matrix, mask_matrix = self._create_rating_matrix(interactions)
            
            # Calculate global mean of ratings
            masked_ratings = ratings_matrix[mask_matrix]
            self.global_mean = np.mean(masked_ratings) if masked_ratings.size > 0 else 0.0
            
            # Initialize factors randomly
            n_users = len(self.user_map)
            n_items = len(self.item_map)
            
            self.user_factors = np.random.normal(0, 0.1, (n_users, self.n_factors))
            self.item_factors = np.random.normal(0, 0.1, (n_items, self.n_factors))
            
            # Alternating Least Squares optimization
            for iteration in range(self.n_iterations):
                # Update user factors
                for u in range(n_users):
                    # Get indices of items user u has rated
                    item_indices = np.where(mask_matrix[u, :])[0]
                    if len(item_indices) == 0:
                        continue
                    
                    # Get ratings and item factors for those items
                    u_ratings = ratings_matrix[u, item_indices]
                    u_items = self.item_factors[item_indices, :]
                    
                    # Solve for user factors
                    A = u_items.T @ u_items + self.reg_param * np.eye(self.n_factors)
                    b = u_items.T @ u_ratings
                    self.user_factors[u, :] = np.linalg.solve(A, b)
                
                # Update item factors
                for i in range(n_items):
                    # Get indices of users who have rated item i
                    user_indices = np.where(mask_matrix[:, i])[0]
                    if len(user_indices) == 0:
                        continue
                    
                    # Get ratings and user factors for those users
                    i_ratings = ratings_matrix[user_indices, i]
                    i_users = self.user_factors[user_indices, :]
                    
                    # Solve for item factors
                    A = i_users.T @ i_users + self.reg_param * np.eye(self.n_factors)
                    b = i_users.T @ i_ratings
                    self.item_factors[i, :] = np.linalg.solve(A, b)
                
                # Calculate error
                error = self._calculate_rmse(ratings_matrix, mask_matrix)
                logger.info(f"Iteration {iteration+1}/{self.n_iterations}, RMSE: {error:.4f}")
            
            # Save model to database
            self._save_model()
            
            return True
            
        except Exception as e:
            logger.error(f"Error training matrix factorization model: {e}")
            return False
    
    def _load_interactions(self) -> List[Dict[str, Any]]:
        """
        Load user-item interactions from database.
        
        Returns:
            List of interactions
        """
        try:
            query = """
            SELECT 
                user_id, recipe_id, interaction_type,
                CASE 
                    WHEN interaction_type = 'rating' THEN rating
                    WHEN interaction_type = 'like' THEN 1.0
                    WHEN interaction_type = 'save' THEN 1.0
                    WHEN interaction_type = 'cook' THEN 1.0
                    ELSE 0.0
                END as rating
            FROM user_interactions
            WHERE interaction_type IN ('rating', 'like', 'save', 'cook')
            """
            
            return execute_query(query)
            
        except Exception as e:
            logger.error(f"Error loading interactions: {e}")
            return []
    
    def _create_mappings(self, interactions: List[Dict[str, Any]]) -> None:
        """
        Create user and item mappings from interactions.
        
        Args:
            interactions: List of user-item interactions
        """
        # Get unique users and items
        unique_users = set()
        unique_items = set()
        
        for interaction in interactions:
            unique_users.add(interaction["user_id"])
            unique_items.add(interaction["recipe_id"])
        
        # Create mappings
        self.user_map = {user_id: idx for idx, user_id in enumerate(unique_users)}
        self.item_map = {item_id: idx for idx, item_id in enumerate(unique_items)}
    
    def _create_rating_matrix(self, interactions: List[Dict[str, Any]]) -> Tuple[np.ndarray, np.ndarray]:
        """
        Create rating matrix and mask matrix from interactions.
        
        Args:
            interactions: List of user-item interactions
            
        Returns:
            Tuple of (rating_matrix, mask_matrix)
        """
        n_users = len(self.user_map)
        n_items = len(self.item_map)
        
        # Initialize rating matrix
        ratings_matrix = np.zeros((n_users, n_items))
        mask_matrix = np.zeros((n_users, n_items), dtype=bool)
        
        # Fill rating matrix
        for interaction in interactions:
            user_id = interaction["user_id"]
            item_id = interaction["recipe_id"]
            rating = interaction["rating"]
            
            # Skip if user or item not in mappings
            if user_id not in self.user_map or item_id not in self.item_map:
                continue
            
            # Get matrix indices
            user_idx = self.user_map[user_id]
            item_idx = self.item_map[item_id]
            
            # Update rating matrix
            ratings_matrix[user_idx, item_idx] = rating
            mask_matrix[user_idx, item_idx] = True
        
        return ratings_matrix, mask_matrix
    
    def _calculate_rmse(self, ratings_matrix: np.ndarray, mask_matrix: np.ndarray) -> float:
        """
        Calculate RMSE between predicted and actual ratings.
        
        Args:
            ratings_matrix: Matrix of actual ratings
            mask_matrix: Mask for observed ratings
            
        Returns:
            RMSE value
        """
        # Calculate predicted ratings
        pred_matrix = self.user_factors @ self.item_factors.T
        
        # Calculate error only on observed ratings
        error = ratings_matrix - pred_matrix
        masked_error = error[mask_matrix]
        
        # Calculate RMSE
        rmse = np.sqrt(np.mean(masked_error ** 2))
        return rmse
    
    def predict_rating(self, user_id: str, item_id: str) -> Optional[float]:
        """
        Predict rating for a user-item pair.
        
        Args:
            user_id: User ID
            item_id: Item ID
            
        Returns:
            Predicted rating or None if user or item not in mappings
        """
        try:
            # Check if model is loaded
            if not self.model_loaded:
                logger.warning("Model not loaded, cannot predict")
                return None
            
            # Check if user and item are in mappings
            if user_id not in self.user_map or item_id not in self.item_map:
                logger.warning(f"User {user_id} or item {item_id} not in mappings")
                return None
            
            # Get user and item indices
            user_idx = self.user_map[user_id]
            item_idx = self.item_map[item_id]
            
            # Predict rating
            pred = np.dot(self.user_factors[user_idx], self.item_factors[item_idx])
            
            return pred
            
        except Exception as e:
            logger.error(f"Error predicting rating: {e}")
            return None
    
    def get_top_items_for_user(self, user_id: str, n: int = 10, exclude_items: Optional[List[str]] = None) -> List[Dict[str, Any]]:
        """
        Get top N recommended items for a user.
        
        Args:
            user_id: User ID
            n: Number of recommendations
            exclude_items: Optional list of item IDs to exclude
            
        Returns:
            List of recommended items with scores
        """
        try:
            # Check if model is loaded
            if not self.model_loaded:
                logger.warning("Model not loaded, cannot recommend")
                return []
            
            # Check if user is in mapping
            if user_id not in self.user_map:
                logger.warning(f"User {user_id} not in mappings")
                return []
            
            # Create set of items to exclude
            exclude_set = set(exclude_items) if exclude_items else set()
            
            # Get user index
            user_idx = self.user_map[user_id]
            
            # Calculate scores for all items
            scores = np.dot(self.user_factors[user_idx], self.item_factors.T)
            
            # Create list of (item_id, score) pairs
            item_scores = []
            for item_id, item_idx in self.item_map.items():
                # Skip excluded items
                if item_id in exclude_set:
                    continue
                
                item_scores.append((item_id, scores[item_idx]))
            
            # Sort by score (descending)
            item_scores.sort(key=lambda x: x[1], reverse=True)
            
            # Get top N items
            top_items = []
            for item_id, score in item_scores[:n]:
                top_items.append({
                    "id": item_id,
                    "score": float(score),
                    "content_type": "recipe"
                })
            
            return top_items
            
        except Exception as e:
            logger.error(f"Error getting top items: {e}")
            return []