"""
Collaborative filtering recommender implementation.
"""
import logging
import json
import numpy as np
from typing import List, Dict, Any, Optional

from recommenders.base_recommender import BaseRecommender
from models.queries_recommend import (
    find_similar_users, get_content_from_similar_users,
    get_user_recent_interactions
)
from config.config import MIN_COMMON_ITEMS

logger = logging.getLogger(__name__)

class CollaborativeRecommender(BaseRecommender):
    """User-based collaborative filtering recommendation strategy."""
    
    def get_recommendations(self, user_id: Optional[str] = None, content_type: Optional[str] = None, 
                           limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get collaborative filtering recommendations based on similar users.
        
        Args:
            user_id: User ID to get recommendations for
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            kwargs: Additional parameters
            
        Returns:
            List of recommended items
        """
        if not user_id:
            # Collaborative filtering requires a user ID
            logger.info("User ID is required for collaborative filtering")
            return self._get_fallback_recommendations(limit, **kwargs)
        
        # Find similar users
        similar_users = find_similar_users(
            user_id=user_id,
            min_common_items=MIN_COMMON_ITEMS,
            limit=20  # Get more similar users to increase recommendation pool
        )
        
        if not similar_users:
            logger.info(f"No similar users found for {user_id}")
            return self._get_fallback_recommendations(limit, **kwargs)
        
        logger.info(f"Found {len(similar_users)} similar users for {user_id}")
        
        # Get content from similar users that the current user hasn't interacted with
        recommendations = get_content_from_similar_users(
            similar_users=similar_users,
            user_id=user_id,
            limit=limit*2  # Get more to allow for filtering
        )
        
        if not recommendations:
            logger.info(f"No collaborative recommendations found for {user_id}")
            return self._get_fallback_recommendations(limit, **kwargs)
        
        # Apply additional filters if specified
        filtered_recommendations = self._apply_filters(recommendations, **kwargs)
        
        # Format the recommendations
        formatted_recommendations = [
            {
                "id": str(item["id"]),
                "title": item["title"],
                "content_type": "recipe",
                "score": float(item["score"]) / 10.0 if "score" in item else 0.5  # Normalize score
            }
            for item in filtered_recommendations
        ]
        
        # If we don't have enough recommendations after filtering, add fallbacks
        if len(formatted_recommendations) < limit:
            fallbacks = self._get_fallback_recommendations(
                limit - len(formatted_recommendations),
                **kwargs
            )
            formatted_recommendations.extend(fallbacks)
        
        return formatted_recommendations[:limit]
    
    def _apply_filters(self, recommendations: List[Dict[str, Any]], **kwargs) -> List[Dict[str, Any]]:
        """
        Apply filters to recommendations.
        
        Args:
            recommendations: List of recommendations to filter
            kwargs: Filter parameters like cuisine and dietary_restriction
            
        Returns:
            Filtered list of recommendations
        """
        filtered = recommendations
        
        # Filter by cuisine if specified
        cuisine = kwargs.get('cuisine')
        if cuisine and filtered:
            cuisine_lower = cuisine.lower()
            filtered = [r for r in filtered if cuisine_lower in r.get("title", "").lower()]
        
        return filtered
    
    def _get_fallback_recommendations(self, limit: int, **kwargs) -> List[Dict[str, Any]]:
        """
        Get fallback recommendations when collaborative filtering fails.
        
        Args:
            limit: Maximum number of recommendations
            kwargs: Additional parameters
            
        Returns:
            List of fallback recommendations
        """
        # Use content-based recommendations as fallback
        from recommenders.content_recommender import ContentRecommender
        content = ContentRecommender()
        return content.get_recommendations(limit=limit, **kwargs)