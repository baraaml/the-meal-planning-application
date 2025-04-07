"""
Hybrid recommender implementation.
Combines multiple recommendation strategies to provide robust recommendations.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from services.collaborative import CollaborativeRecommender
from services.content_based import ContentBasedRecommender
from services.popularity import PopularityRecommender
from services.item_based import ItemBasedRecommender
from data.repositories import InteractionRepository
from config import DEFAULT_RECOMMENDATION_LIMIT

logger = logging.getLogger(__name__)

class HybridRecommender(BaseRecommender):
    """
    Hybrid recommendation strategy.
    Combines multiple recommendation strategies with fallbacks:
    1. Collaborative filtering (user-based)
    2. Content-based recommendations
    3. Item-based collaborative filtering
    4. Popularity-based recommendations
    
    Each strategy contributes to the final recommendation list, with
    weights assigned to prioritize and blend the recommendations.
    """
    
    def __init__(self):
        """Initialize the hybrid recommender with various strategies."""
        self.collaborative_recommender = CollaborativeRecommender()
        self.content_based_recommender = ContentBasedRecommender()
        self.item_based_recommender = ItemBasedRecommender()
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
            kwargs: Additional parameters like cuisine and dietary_restriction
            
        Returns:
            List of recommended items
        """
        all_recommendations = {}
        strategy_weights = {
            'collaborative': 1.0,
            'content': 0.8,
            'item_based': 0.7,
            'popularity': 0.5
        }
        
        # Step 1: Try collaborative filtering if we have a user_id
        if user_id:
            collaborative_items = self.collaborative_recommender.get_recommendations(
                user_id=user_id,
                content_type=content_type,
                limit=limit * 2,  # Request more items for better blending
                **kwargs
            )
            
            # Add to all recommendations with collaborative weight
            for item in collaborative_items:
                item_id = item.get('id')
                if item_id:
                    score = item.get('score', 0.5) * strategy_weights['collaborative']
                    if item_id in all_recommendations:
                        all_recommendations[item_id]['score'] = max(
                            all_recommendations[item_id]['score'],
                            score
                        )
                    else:
                        item['score'] = score
                        all_recommendations[item_id] = item
            
            logger.info(f"Collaborative filtering added {len(collaborative_items)} items")
        
        # Step 2: Try content-based if we have user interactions or meal_id
        recent_meal_id = meal_id
        recent_content_type = content_type
        
        # If no meal_id but we have user_id, get their most recent interaction
        if not meal_id and user_id:
            recent_interactions = self.interaction_repository.get_user_recent_interactions(
                user_id=user_id,
                content_type=content_type,
                limit=1
            )
            
            if recent_interactions:
                recent = recent_interactions[0]
                recent_meal_id = recent['meal_id']
                recent_content_type = recent['content_type']
        
        # If we have a meal to base content similarity on, get recommendations
        if recent_meal_id and recent_content_type:
            content_based_items = self.content_based_recommender.get_recommendations(
                meal_id=recent_meal_id,
                content_type=recent_content_type,
                limit=limit * 2,  # Request more items for better blending
                **kwargs
            )
            
            # Add to all recommendations with content-based weight
            for item in content_based_items:
                item_id = item.get('id')
                if item_id:
                    score = item.get('score', 0.5) * strategy_weights['content']
                    if item_id in all_recommendations:
                        all_recommendations[item_id]['score'] = max(
                            all_recommendations[item_id]['score'],
                            score
                        )
                    else:
                        item['score'] = score
                        all_recommendations[item_id] = item
            
            logger.info(f"Content-based filtering added {len(content_based_items)} items")
        
        # Step 3: Try item-based collaborative filtering if we have user_id
        if user_id:
            item_based_items = self.item_based_recommender.get_recommendations(
                user_id=user_id,
                content_type=content_type,
                limit=limit * 2,  # Request more items for better blending
                **kwargs
            )
            
            # Add to all recommendations with item-based weight
            for item in item_based_items:
                item_id = item.get('id')
                if item_id:
                    score = item.get('score', 0.5) * strategy_weights['item_based']
                    if item_id in all_recommendations:
                        all_recommendations[item_id]['score'] = max(
                            all_recommendations[item_id]['score'],
                            score
                        )
                    else:
                        item['score'] = score
                        all_recommendations[item_id] = item
            
            logger.info(f"Item-based filtering added {len(item_based_items)} items")
        
        # Step 4: Add popularity-based recommendations to fill any gaps
        popularity_items = self.popularity_recommender.get_recommendations(
            content_type=content_type,
            limit=limit * 2,  # Request more items for better blending
            **kwargs
        )
        
        # Add to all recommendations with popularity weight
        for item in popularity_items:
            item_id = item.get('id')
            if item_id:
                score = item.get('score', 0.5) * strategy_weights['popularity']
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