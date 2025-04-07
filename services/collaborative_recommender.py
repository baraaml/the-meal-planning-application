"""
Collaborative filtering recommender implementation.
Recommends items based on similar users' interactions.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from data.repositories import InteractionRepository
from config import DEFAULT_RECOMMENDATION_LIMIT, MIN_COMMON_ITEMS

logger = logging.getLogger(__name__)

class CollaborativeRecommender(BaseRecommender):
    """
    Collaborative filtering recommendation strategy.
    Finds users with similar interaction patterns and recommends items they've interacted with.
    This is a user-based collaborative filtering approach.
    """
    
    def __init__(self):
        """Initialize the collaborative recommender."""
        self.repository = InteractionRepository()
    
    def get_recommendations(
        self, 
        user_id: Optional[str] = None,
        meal_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT,
        min_common_items: int = MIN_COMMON_ITEMS,
        max_similar_users: int = 10,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get collaborative filtering recommendations.
        
        Args:
            user_id: The ID of the user
            meal_id: Not used for collaborative recommendations
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            min_common_items: Minimum number of items in common to consider users similar
            max_similar_users: Maximum number of similar users to consider
            kwargs: Additional filters (cuisine, dietary_restriction)
            
        Returns:
            List of recommended items based on similar users
        """
        if not user_id:
            logger.warning("User ID required for collaborative recommendations")
            return []
        
        # Find users with similar interaction patterns
        similar_users = self.repository.find_similar_users(
            user_id, 
            min_common_items=min_common_items,
            limit=max_similar_users
        )
        
        if not similar_users:
            logger.info(f"No similar users found for user {user_id}")
            return []
        
        # Get content that similar users have interacted with
        recommended_items = self.repository.get_content_from_similar_users(
            similar_users,
            user_id,
            content_type=content_type,
            limit=limit
        )
        
        # Apply additional filters if specified
        filtered_items = recommended_items
        
        # Filter by cuisine if specified
        cuisine = kwargs.get('cuisine')
        if cuisine and filtered_items:
            # This would require additional logic to filter by cuisine
            # For simplicity, we're just logging it for now
            logger.info(f"Filtering by cuisine: {cuisine}")
        
        # Filter by dietary restriction if specified
        dietary_restriction = kwargs.get('dietary_restriction')
        if dietary_restriction and filtered_items:
            # This would require additional logic to filter by dietary restriction
            # For simplicity, we're just logging it for now
            logger.info(f"Filtering by dietary restriction: {dietary_restriction}")
        
        # Transform the interaction count to a score between 0 and 1
        if filtered_items:
            max_count = max(item.get('interaction_count', 0) for item in filtered_items)
            if max_count > 0:
                for item in filtered_items:
                    item['score'] = item.get('interaction_count', 0) / max_count
        
        return filtered_items