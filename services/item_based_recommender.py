"""
Item-based collaborative filtering recommender implementation.
Recommends items based on co-occurrence patterns with items the user has interacted with.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from data.repositories.interaction_repository import InteractionRepository
from config.settings import DEFAULT_RECOMMENDATION_LIMIT
from data.queries.service_queries import FIND_SIMILAR_ITEMS
from data.database import execute_query

logger = logging.getLogger(__name__)

class ItemBasedRecommender(BaseRecommender):
    """
    Item-based collaborative filtering recommendation strategy.
    Finds items similar to those the user has already interacted with,
    based on how frequently items co-occur in user interactions.
    """
    
    def __init__(self):
        """Initialize the item-based recommender."""
        self.repository = InteractionRepository()
    
    def get_recommendations(
        self, 
        user_id: Optional[str] = None,
        meal_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get item-based collaborative filtering recommendations.
        
        Args:
            user_id: The ID of the user
            meal_id: Optional specific content ID to find similar items for
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            
        Returns:
            List of recommended items based on item similarity
        """
        if not user_id and not meal_id:
            logger.warning("Either user_id or meal_id required for item-based recommendations")
            return []
        
        # If meal_id is provided, use it directly to find similar items
        if meal_id:
            return self._get_similar_items(meal_id, content_type, limit)
        
        # Otherwise, use the user's recent interactions to find similar items
        return self._get_user_item_based_recommendations(user_id, content_type, limit)
    
    def _get_similar_items(
        self,
        meal_id: str,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT
    ) -> List[Dict[str, Any]]:
        """
        Find items similar to a specific content item based on co-occurrence patterns.
        
        Args:
            meal_id: The ID of the content to find similar items for
            content_type: Optional content type filter
            limit: Maximum number of similar items to return
            
        Returns:
            List of similar items
        """
        # Execute SQL to find items that frequently co-occur with the given meal_id
        # This is done by finding users who interacted with meal_id and then
        # counting other items they interacted with
        
        params = {
            "meal_id": meal_id,
            "limit": limit
        }
        
        type_filter = ""
        if content_type:
            type_filter = "AND ri2.content_type = :content_type"
            params["content_type"] = content_type
        
        # Format the query with the type filter
        formatted_query = FIND_SIMILAR_ITEMS.format(
            type_filter=type_filter
        )
        
        result = execute_query(
            formatted_query,
            params
        )
        
        similar_items = []
        for row in result:
            similar_items.append({
                "id": row[0],
                "content_type": row[1],
                "title": row[2],
                "co_occurrence_count": row[3]
            })
        
        return similar_items
    
    def _get_user_item_based_recommendations(
        self,
        user_id: str,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT
    ) -> List[Dict[str, Any]]:
        """
        Get item-based recommendations for a user by finding items similar
        to those they've already interacted with.
        
        Args:
            user_id: The ID of the user
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            
        Returns:
            List of recommended items
        """
        # Get user's recent interactions
        recent_interactions = self.repository.get_user_recent_interactions(
            user_id=user_id,
            content_type=content_type,
            limit=5  # Use top 5 recent interactions
        )
        
        if not recent_interactions:
            logger.info(f"No recent interactions found for user {user_id}")
            return []
        
        all_recommendations = []
        user_items = [interaction["meal_id"] for interaction in recent_interactions]
        
        # For each item the user has interacted with, find similar items
        for interaction in recent_interactions:
            meal_id = interaction["meal_id"]
            similar_items = self._get_similar_items(
                meal_id=meal_id,
                content_type=content_type,
                limit=limit
            )
            
            # Filter out items the user has already interacted with
            filtered_items = [item for item in similar_items if item["id"] not in user_items]
            all_recommendations.extend(filtered_items)
        
        # Remove duplicates by creating a dictionary keyed by item ID
        unique_items = {}
        for item in all_recommendations:
            item_id = item["id"]
            if item_id not in unique_items or item.get("co_occurrence_count", 0) > unique_items[item_id].get("co_occurrence_count", 0):
                unique_items[item_id] = item
        
        # Sort by co-occurrence count and limit results
        sorted_items = sorted(
            unique_items.values(), 
            key=lambda x: x.get("co_occurrence_count", 0), 
            reverse=True
        )
        
        return sorted_items[:limit]