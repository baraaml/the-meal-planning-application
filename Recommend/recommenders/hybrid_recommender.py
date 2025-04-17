"""
Hybrid recommender implementation combining multiple recommendation strategies.
"""
import logging
from typing import List, Dict, Any, Optional
import time

from recommenders.base_recommender import BaseRecommender
from recommenders.content_recommender import ContentRecommender
from recommenders.collaborative_recommender import CollaborativeRecommender
from recommenders.popularity_recommender import PopularityRecommender

logger = logging.getLogger(__name__)

class HybridRecommender(BaseRecommender):
    """
    Hybrid recommendation strategy.
    Combines multiple recommendation strategies with weights:
    1. Collaborative filtering (highest weight)
    2. Content-based recommendations (medium weight)
    3. Popularity-based recommendations (lowest weight)
    """
    
    def __init__(self):
        """Initialize recommender components."""
        self.collaborative = CollaborativeRecommender()
        self.content = ContentRecommender()
        self.popularity = PopularityRecommender()
        
        # Define strategy weights
        self.strategy_weights = {
            'collaborative': 1.0,
            'content': 0.8, 
            'popularity': 0.6
        }
    
    def get_recommendations(self, user_id: Optional[str] = None, content_type: Optional[str] = None, 
                           limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get hybrid recommendations using multiple strategies.
        
        Args:
            user_id: Optional user ID
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            kwargs: Additional parameters
            
        Returns:
            List of recommended items
        """
        start_time = time.time()
        all_recommendations = {}
        
        # Step 1: Try collaborative filtering if we have a user_id
        if user_id:
            try:
                collaborative_items = self.collaborative.get_recommendations(
                    user_id=user_id,
                    content_type=content_type,
                    limit=limit * 2,  # Request more items for better blending
                    **kwargs
                )
                
                # Add to all recommendations with collaborative weight
                for item in collaborative_items:
                    item_id = item.get("id")
                    if item_id:
                        score = item.get("score", 0.5) * self.strategy_weights['collaborative']
                        if item_id in all_recommendations:
                            all_recommendations[item_id]['score'] = max(
                                all_recommendations[item_id]['score'],
                                score
                            )
                        else:
                            item['score'] = score
                            all_recommendations[item_id] = item
                
                logger.info(f"Collaborative filtering added {len(collaborative_items)} items")
            except Exception as e:
                logger.error(f"Error getting collaborative recommendations: {e}")
        
        # Step 2: Try content-based if we have user interactions
        if user_id:
            try:
                content_based_items = self.content.get_recommendations(
                    user_id=user_id,
                    content_type=content_type,
                    limit=limit * 2,  # Request more items for better blending
                    **kwargs
                )
                
                # Add to all recommendations with content-based weight
                for item in content_based_items:
                    item_id = item.get("id")
                    if item_id:
                        score = item.get("score", 0.5) * self.strategy_weights['content']
                        if item_id in all_recommendations:
                            all_recommendations[item_id]['score'] = max(
                                all_recommendations[item_id]['score'],
                                score
                            )
                        else:
                            item['score'] = score
                            all_recommendations[item_id] = item
                
                logger.info(f"Content-based filtering added {len(content_based_items)} items")
            except Exception as e:
                logger.error(f"Error getting content-based recommendations: {e}")
        
        # Step 3: Add popularity-based recommendations to fill any gaps
        try:
            popularity_items = self.popularity.get_recommendations(
                content_type=content_type,
                limit=limit * 2,  # Request more items for better blending
                **kwargs
            )
            
            # Add to all recommendations with popularity weight
            for item in popularity_items:
                item_id = item.get("id")
                if item_id:
                    score = item.get("score", 0.5) * self.strategy_weights['popularity']
                    if item_id in all_recommendations:
                        # Only use popularity as a boost, not a replacement
                        all_recommendations[item_id]['score'] += score * 0.2
                    else:
                        item['score'] = score
                        all_recommendations[item_id] = item
            
            logger.info(f"Popularity-based filtering added {len(popularity_items)} items")
        except Exception as e:
            logger.error(f"Error getting popularity-based recommendations: {e}")
        
        # Convert dict to list, sort by score, and limit results
        recommended_items = list(all_recommendations.values())
        recommended_items.sort(key=lambda x: x.get('score', 0), reverse=True)
        
        execution_time = time.time() - start_time
        logger.info(f"Hybrid recommender generated {len(recommended_items)} items in {execution_time:.3f}s")
        
        return recommended_items[:limit]
    
    def get_similar_recommendations(self, recipe_id: int, similarity_method: str = "content", limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get recommendations similar to a specific recipe using content-based approach.
        
        Args:
            recipe_id: ID of the reference recipe
            similarity_method: Method to determine similarity
            limit: Maximum number of recommendations
            
        Returns:
            List of similar recipes
        """
        return self.content.get_similar_recommendations(recipe_id, similarity_method, limit, **kwargs)
    
    def get_cuisine_recommendations(self, cuisine_name: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        Get recommendations for a specific cuisine.
        
        Args:
            cuisine_name: Name of the cuisine or region
            limit: Maximum number of recommendations
            
        Returns:
            List of cuisine-specific recipes
        """
        return self.content.get_cuisine_recommendations(cuisine_name, limit)
    
    def get_dietary_recommendations(self, dietary_restriction: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        Get recommendations for a specific dietary restriction.
        
        Args:
            dietary_restriction: Type of dietary restriction
            limit: Maximum number of recommendations
            
        Returns:
            List of dietary-specific recipes
        """
        return self.content.get_dietary_recommendations(dietary_restriction, limit)
    
    def get_trending_recommendations(self, time_window: str = "day", limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get trending recipes based on recent interactions.
        
        Args:
            time_window: Time window for trending items
            limit: Maximum number of recommendations
            kwargs: Additional filters
            
        Returns:
            List of trending recipes
        """
        return self.popularity.get_trending_recommendations(time_window, limit, **kwargs)
    
    def get_quick_recommendations(self, max_time: int = 30, limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get quick recipe recommendations.
        
        Args:
            max_time: Maximum preparation time
            limit: Maximum number of recommendations
            kwargs: Additional filters
            
        Returns:
            List of quick recipes
        """
        return self.content.get_quick_recommendations(max_time, limit, **kwargs)