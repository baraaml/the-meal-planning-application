"""
Hybrid recommender implementation.
Combines multiple recommendation strategies to provide robust recommendations.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from services.collaborative_recommender import CollaborativeRecommender
from services.content_based_recommender import ContentBasedRecommender
from services.popularity_recommender import PopularityRecommender
from data.repositories.interaction_repository import InteractionRepository
from config.settings import DEFAULT_RECOMMENDATION_LIMIT

logger = logging.getLogger(__name__)

class HybridRecommender(BaseRecommender):
    """
    Hybrid recommendation strategy.
    Combines multiple recommendation strategies with fallbacks:
    1. Collaborative filtering (user-based)
    2. Content-based recommendations
    3. Popularity-based recommendations
    """
    
    def __init__(self):
        """Initialize the hybrid recommender with various strategies."""
        self.collaborative_recommender = CollaborativeRecommender()
        self.content_based_recommender = ContentBasedRecommender()
        self.popularity_recommender = PopularityRecommender()
        self.interaction_repository = InteractionRepository()
    
    def get_recommendations(
        self, 
        user_id: Optional[str] = None,
        meal_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get hybrid recommendations using multiple strategies with fallbacks.
        
        Args:
            user_id: The ID of the user for personalized recommendations
            meal_id: Optional content ID for similar content recommendations
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            
        Returns:
            List of recommended items
        """
        recommended_items = []
        
        # Step 1: Try collaborative filtering first (if we have a user_id)
        if user_id:
            collaborative_items = self.collaborative_recommender.get_recommendations(
                user_id=user_id,
                content_type=content_type,
                limit=limit,
                **kwargs
            )
            
            recommended_items.extend(collaborative_items)
            logger.info(f"Collaborative filtering returned {len(collaborative_items)} items")
        
        # Step 2: If we don't have enough recommendations yet, try content-based
        if len(recommended_items) < limit and user_id:
            # Get user's most recent interaction for content-based recommendations
            recent_interactions = self.interaction_repository.get_user_recent_interactions(
                user_id=user_id,
                content_type=content_type,
                limit=1
            )
            
            if recent_interactions:
                recent = recent_interactions[0]
                meal_id = recent['meal_id']
                content_type_for_content = recent['content_type']
                
                # Get additional recommendations based on this content
                # Exclude already recommended items
                existing_ids = [item.get("id") for item in recommended_items]
                
                remaining = limit - len(recommended_items)
                content_based_items = self.content_based_recommender.get_recommendations(
                    meal_id=meal_id,
                    content_type=content_type_for_content,
                    limit=remaining,
                    **kwargs
                )
                
                # Filter out duplicates
                content_based_items = [
                    item for item in content_based_items 
                    if item.get("id") not in existing_ids
                ]
                
                recommended_items.extend(content_based_items)
                logger.info(f"Content-based filtering added {len(content_based_items)} items")
        
        # Step 3: If still not enough, use popularity-based recommendations
        if len(recommended_items) < limit:
            # Exclude already recommended items
            existing_ids = [item.get("id") for item in recommended_items]
            
            remaining = limit - len(recommended_items)
            popularity_items = self.popularity_recommender.get_recommendations(
                content_type=content_type,
                limit=remaining,
                exclude_ids=existing_ids,
                **kwargs
            )
            
            recommended_items.extend(popularity_items)
            logger.info(f"Popularity-based filtering added {len(popularity_items)} items")
        
        # Return the combined recommendations (limited to the requested number)
        return recommended_items[:limit]