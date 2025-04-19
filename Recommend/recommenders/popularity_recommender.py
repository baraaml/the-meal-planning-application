"""
Popularity-based recommender implementation.
"""
import logging
from typing import List, Dict, Any, Optional

from recommenders.base_recommender import BaseRecommender
from models.queries_recommend import get_trending_recipes

logger = logging.getLogger(__name__)

class PopularityRecommender(BaseRecommender):
    """Popularity-based recommendation strategy."""
    
    def get_recommendations(self, user_id: Optional[str] = None, content_type: Optional[str] = None, 
                           limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get popularity-based recommendations.
        
        Args:
            user_id: Optional user ID (not used in popularity-based recommendations)
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            kwargs: Additional parameters
            
        Returns:
            List of trending recipes
        """
        # Get trending items with weekly time window
        time_window = kwargs.get('time_window', 'week')
        cuisine = kwargs.get('cuisine')
        dietary_restriction = kwargs.get('dietary_restriction')
        
        return self.get_trending_recommendations(
            time_window=time_window,
            limit=limit,
            cuisine=cuisine,
            dietary_restriction=dietary_restriction
        )
    
    def get_trending_recommendations(self, time_window: str = "day", limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get trending recipes based on recent interactions.
        
        Args:
            time_window: Time window for trending items (day, week, month)
            limit: Maximum number of recommendations
            kwargs: Additional filters like cuisine and dietary_restriction
            
        Returns:
            List of trending recipes
        """
        cuisine = kwargs.get('cuisine')
        dietary_restriction = kwargs.get('dietary_restriction')
        
        trending_items = get_trending_recipes(
            time_window=time_window,
            limit=limit,
            cuisine=cuisine,
            dietary_restriction=dietary_restriction
        )
        
        return trending_items