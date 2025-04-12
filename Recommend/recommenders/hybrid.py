"""
Hybrid recommender implementation combining multiple recommendation strategies.
"""
import logging
from typing import List, Dict, Any, Optional

from models.queries_recommend import (
    get_trending_recipes, get_user_recent_interactions, 
    find_similar_users, get_content_from_similar_users
)
from models.queries_recipe import get_recipe
from models.queries_search import find_similar_content

logger = logging.getLogger(__name__)

class HybridRecommender:
    """
    Hybrid recommendation strategy.
    Combines multiple recommendation strategies with fallbacks:
    1. User-based collaborative filtering
    2. Content-based recommendations
    3. Popularity-based recommendations
    """
    
    def get_recommendations(
        self, 
        user_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = 10,
        recommendation_type: str = "hybrid",
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get hybrid recommendations using multiple strategies with fallbacks.
        
        Args:
            user_id: The ID of the user for personalized recommendations
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            recommendation_type: Type of recommendation algorithm to use
            kwargs: Additional parameters like cuisine and dietary_restriction
            
        Returns:
            List of recommended items
        """
        all_recommendations = {}
        strategy_weights = {
            'collaborative': 1.0,
            'content': 0.8,
            'popularity': 0.5
        }
        
        # If a specific recommendation type is requested
        if recommendation_type != "hybrid":
            if recommendation_type == "collaborative":
                return self._get_collaborative_recommendations(user_id, content_type, limit, **kwargs)
            elif recommendation_type == "content":
                return self._get_content_based_recommendations(user_id, content_type, limit, **kwargs)
            elif recommendation_type == "popularity":
                return self._get_popularity_recommendations(content_type, limit, **kwargs)
        
        # Step 1: Try collaborative filtering if we have a user_id
        if user_id:
            collaborative_items = self._get_collaborative_recommendations(
                user_id=user_id,
                content_type=content_type,
                limit=limit * 2,  # Request more items for better blending
                **kwargs
            )
            
            # Add to all recommendations with collaborative weight
            for item in collaborative_items:
                item_id = item.get("id")
                if item_id:
                    score = item.get("score", 0.5) * strategy_weights['collaborative']
                    if item_id in all_recommendations:
                        all_recommendations[item_id]['score'] = max(
                            all_recommendations[item_id]['score'],
                            score
                        )
                    else:
                        item['score'] = score
                        all_recommendations[item_id] = item
            
            logger.info(f"Collaborative filtering added {len(collaborative_items)} items")
        
        # Step 2: Try content-based if we have user interactions
        if user_id:
            content_based_items = self._get_content_based_recommendations(
                user_id=user_id,
                content_type=content_type,
                limit=limit * 2,  # Request more items for better blending
                **kwargs
            )
            
            # Add to all recommendations with content-based weight
            for item in content_based_items:
                item_id = item.get("id")
                if item_id:
                    score = item.get("score", 0.5) * strategy_weights['content']
                    if item_id in all_recommendations:
                        all_recommendations[item_id]['score'] = max(
                            all_recommendations[item_id]['score'],
                            score
                        )
                    else:
                        item['score'] = score
                        all_recommendations[item_id] = item
            
            logger.info(f"Content-based filtering added {len(content_based_items)} items")
        
        # Step 3: Add popularity-based recommendations to fill any gaps
        popularity_items = self._get_popularity_recommendations(
            content_type=content_type,
            limit=limit * 2,  # Request more items for better blending
            **kwargs
        )
        
        # Add to all recommendations with popularity weight
        for item in popularity_items:
            item_id = item.get("id")
            if item_id:
                score = item.get("score", 0.5) * strategy_weights['popularity']
                if item_id in all_recommendations:
                    # Only use popularity as a boost, not a replacement
                    all_recommendations[item_id]['score'] += score * 0.2
                else:
                    item['score'] = score
                    all_recommendations[item_id] = item
        
        logger.info(f"Popularity-based filtering added {len(popularity_items)} items")
        
        # Convert dict to list, sort by score, and limit results
        recommended_items = list(all_recommendations.values())
        recommended_items.sort(key=lambda x: x.get('score', 0), reverse=True)
        
        logger.info(f"Hybrid recommender generated {len(recommended_items)} items total")
        return recommended_items[:limit]
    
    def _get_collaborative_recommendations(
        self, 
        user_id: str,
        content_type: Optional[str] = None,
        limit: int = 10,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """Get collaborative filtering recommendations for a user."""
        # Find similar users
        similar_users = find_similar_users(
            user_id=user_id,
            min_common_items=2,
            limit=10
        )
        
        if not similar_users:
            logger.info(f"No similar users found for user {user_id}")
            return []
        
        # Get content from similar users
        items = get_content_from_similar_users(
            similar_users=similar_users,
            user_id=user_id,
            limit=limit
        )
        
        # Transform the data format
        recommendations = []
        for item in items:
            recommendations.append({
                "id": str(item["recipe_id"]),
                "content_type": "recipe",
                "title": item["title"],
                "score": item.get("interaction_count", 1) / 10.0  # Normalize
            })
        
        # Apply additional filters
        cuisine = kwargs.get('cuisine')
        if cuisine and recommendations:
            # This would be more efficient with a separate query, but for now just filter
            recommendations = [r for r in recommendations if cuisine.lower() in r["title"].lower()]
        
        return recommendations
    
    def _get_content_based_recommendations(
        self, 
        user_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = 10,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """Get content-based recommendations for a user."""
        # If we have a user, get their recent interactions
        if user_id:
            recent_interactions = get_user_recent_interactions(
                user_id=user_id,
                limit=1
            )
            
            if not recent_interactions:
                logger.info(f"No recent interactions found for user {user_id}")
                return []
            
            # Get the most recent interaction
            recent = recent_interactions[0]
            recipe_id = recent["recipe_id"]
            
            # Get the recipe to generate an embedding
            recipe = get_recipe(recipe_id)
            if not recipe:
                logger.info(f"Recipe {recipe_id} not found")
                return []
            
            # Generate embedding
            from embedding.embeddings import EmbeddingGenerator
            generator = EmbeddingGenerator()
            embedding = generator.generate_recipe_embedding(recipe)
            
            if not embedding:
                logger.info(f"Failed to generate embedding for recipe {recipe_id}")
                return []
            
            # Find similar content
            similar_items = find_similar_content(
                embedding=embedding,
                exclude_ids=[recipe_id],
                limit=limit
            )
            
            # Transform the data format
            recommendations = []
            for item in similar_items:
                recommendations.append({
                    "id": str(item["recipe_id"]),
                    "content_type": "recipe",
                    "title": item["recipe_title"],
                    "score": item.get("similarity", 0.5)
                })
            
            # Apply additional filters
            cuisine = kwargs.get('cuisine')
            if cuisine and recommendations:
                recommendations = [r for r in recommendations if cuisine.lower() in r["title"].lower()]
            
            return recommendations
        
        return []
    
    def _get_popularity_recommendations(
        self, 
        content_type: Optional[str] = None,
        limit: int = 10,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """Get popularity-based recommendations."""
        # Get trending recipes
        cuisine = kwargs.get('cuisine')
        dietary_restriction = kwargs.get('dietary_restriction')
        
        trending_items = get_trending_recipes(
            time_window="week",
            limit=limit,
            cuisine=cuisine,
            dietary_restriction=dietary_restriction
        )
        
        # Transform the data format
        recommendations = []
        for item in trending_items:
            recommendations.append({
                "id": str(item["recipe_id"]),
                "content_type": "recipe",
                "title": item["title"],
                "score": item.get("popularity", 1) / 10.0 if item.get("popularity") else 0.5
            })
        
        return recommendations