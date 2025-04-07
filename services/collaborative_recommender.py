"""
Collaborative filtering recommender implementation.
Recommends items based on similar users' interactions.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from data.repositories.interaction_repository import InteractionRepository
from config.settings import DEFAULT_RECOMMENDATION_LIMIT

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
        content_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT,
        min_common_items: int = 2,
        max_similar_users: int = 10,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get collaborative filtering recommendations.
        
        Args:
            user_id: The ID of the user
            content_id: Not used for collaborative recommendations
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            min_common_items: Minimum number of items in common to consider users similar
            max_similar_users: Maximum number of similar users to consider
            
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
        
        return recommended_items