"""
Popularity-based recommender implementation.
Recommends trending or popular content based on interaction counts.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from data.repositories.interaction_repository import InteractionRepository
from config.settings import DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS

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
        content_id: Optional[str] = None,
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
            content_id: Not used for popularity recommendations
            content_type: Optional content type filter ('post', 'community', 'all')
            limit: Maximum number of recommendations
            time_window: Time window for trending content ('day', 'week', 'month')
            exclude_ids: Optional list of content IDs to exclude
            
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
            limit=limit
        )
        
        # Filter out excluded IDs if specified
        if exclude_ids:
            popular_items = [item for item in popular_items if item["id"] not in exclude_ids]
        
        return popular_items
    
    def get_category_recommendations(
        self,
        category_id: str,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT
    ) -> List[Dict[str, Any]]:
        """
        Get recommendations for a specific category.
        
        Args:
            category_id: The ID of the category
            limit: Maximum number of recommendations
            
        Returns:
            List of communities in the category
        """
        return self.repository.get_category_communities(
            category_id=category_id,
            limit=limit
        )