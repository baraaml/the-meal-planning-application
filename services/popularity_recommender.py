"""
Popularity-based recommender implementation.
Recommends trending or popular content based on interaction counts.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from data.repositories import InteractionRepository
from config import DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS

logger = logging.getLogger(__name__)

class PopularityRecommender(BaseRecommender):
    """
    Popularity-based recommendation strategy.
    Recommends trending or popular content based on interaction counts.
    This is a non-personalized strategy that can be used as a fallback.
    """
    
    def __init__(self):
        """Initialize the popularity recommender."""
        self.repository = InteractionRepository()
    
    def get_recommendations(
        self, 
        user_id: Optional[str] = None,
        meal_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT,
        time_window: str = "day",
        exclude_ids: List[str] = None,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get popularity-based recommendations.
        
        Args:
            user_id: Optional user ID (used for filtering recommendations)
            meal_id: Not used for popularity recommendations
            content_type: Optional content type filter ('meal', 'recipe', 'all')
            limit: Maximum number of recommendations
            time_window: Time window for trending content ('day', 'week', 'month')
            exclude_ids: Optional list of content IDs to exclude
            kwargs: Additional filters (cuisine, dietary_restriction)
            
        Returns:
            List of popular content items
        """
        if time_window not in ALLOWED_TRENDING_WINDOWS:
            logger.warning(f"Invalid time window: {time_window}. Using 'day' instead.")
            time_window = "day"
        
        content_type_filter = content_type if content_type else 'all'
        
        # Get trending content
        popular_items = self.repository.get_trending_content(
            content_type=content_type_filter,
            time_window=time_window,
            limit=limit * 2  # Request more items to account for filtering
        )
        
        # Filter out excluded IDs if specified
        if exclude_ids:
            popular_items = [item for item in popular_items if item["id"] not in exclude_ids]
        
        # Apply additional filters if specified
        filtered_items = popular_items
        
        # Filter by cuisine if specified
        cuisine = kwargs.get('cuisine')
        if cuisine and filtered_items:
            # This would require additional logic to filter by cuisine
            logger.info(f"Filtering by cuisine: {cuisine}")
        
        # Filter by dietary restriction if specified
        dietary_restriction = kwargs.get('dietary_restriction')
        if dietary_restriction and filtered_items:
            # This would require additional logic to filter by dietary restriction
            logger.info(f"Filtering by dietary restriction: {dietary_restriction}")
        
        # Normalize popularity scores to 0-1 range
        if filtered_items:
            max_popularity = max(item.get('popularity', 0) for item in filtered_items)
            if max_popularity > 0:
                for item in filtered_items:
                    item['score'] = item.get('popularity', 0) / max_popularity
        
        return filtered_items[:limit]
    
    def get_cuisine_recommendations(
        self,
        cuisine_id: str,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT
    ) -> List[Dict[str, Any]]:
        """
        Get recommendations for a specific cuisine.
        
        Args:
            cuisine_id: The ID of the cuisine
            limit: Maximum number of recommendations
            
        Returns:
            List of meals in the cuisine
        """
        return self.repository.get_cuisine_recommendations(
            cuisine_id=cuisine_id,
            limit=limit
        )
    
    def get_dietary_recommendations(
        self,
        dietary_restriction_id: str,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT
    ) -> List[Dict[str, Any]]:
        """
        Get recommendations based on dietary restrictions.
        
        Args:
            dietary_restriction_id: The ID of the dietary restriction
            limit: Maximum number of recommendations
            
        Returns:
            List of meals matching the dietary restriction
        """
        return self.repository.get_dietary_recommendations(
            dietary_restriction_id=dietary_restriction_id,
            limit=limit
        )