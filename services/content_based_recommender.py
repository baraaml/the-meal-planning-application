"""
Content-based recommender implementation.
Uses vector embeddings to find similar content.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from data.repositories import ContentEmbeddingRepository
from config import DEFAULT_RECOMMENDATION_LIMIT, CONTENT_TYPES, MIN_SIMILARITY_SCORE

logger = logging.getLogger(__name__)

class ContentBasedRecommender(BaseRecommender):
    """
    Content-based recommendation strategy.
    Uses vector embeddings to find similar content based on content features.
    """
    
    def __init__(self):
        """Initialize the content-based recommender."""
        self.repository = ContentEmbeddingRepository()
    
    def get_recommendations(
        self, 
        user_id: Optional[str] = None,
        meal_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT,
        min_similarity: float = MIN_SIMILARITY_SCORE,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get content-based recommendations.
        
        Args:
            user_id: Not used for content-based recommendations
            meal_id: The ID of the source meal
            content_type: The type of content ('meal', 'recipe')
            limit: Maximum number of recommendations
            min_similarity: Minimum similarity score threshold
            kwargs: Additional filters (not used for content-based)
            
        Returns:
            List of similar content items
        """
        if not meal_id or not content_type:
            logger.warning("Meal ID and content type required for content-based recommendations")
            return []
        
        if content_type not in CONTENT_TYPES:
            logger.warning(f"Invalid content type: {content_type}")
            return []
        
        # Get the embedding for the source content
        embedding = self.repository.get_embedding(meal_id, content_type)
        if not embedding:
            logger.warning(f"No embedding found for {content_type} with ID {meal_id}")
            return []
        
        # Find similar content
        exclude_ids = [meal_id]  # Exclude the source content
        similar_items = self.repository.find_similar_content(
            embedding, 
            content_type=content_type,
            exclude_ids=exclude_ids,
            limit=limit
        )
        
        # Filter by minimum similarity threshold
        filtered_items = [item for item in similar_items if item.get('similarity', 0) >= min_similarity]
        
        # Rename similarity to score for consistent interface
        for item in filtered_items:
            item['score'] = item.pop('similarity', 0)
        
        return filtered_items
    
    def get_similar_by_ingredients(
        self,
        meal_id: str,
        content_type: str,
        limit: int = DEFAULT_RECOMMENDATION_LIMIT
    ) -> List[Dict[str, Any]]:
        """
        Get meals similar by ingredients.
        
        Args:
            meal_id: The ID of the meal
            content_type: The type of content
            limit: Maximum number of results
            
        Returns:
            List of similar meals
        """
        similar_items = self.repository.get_similar_by_ingredients(
            meal_id=meal_id,
            content_type=content_type,
            limit=limit
        )
        
        # Rename similarity to score for consistent interface
        for item in similar_items:
            item['score'] = item.pop('similarity', 0)
        
        return similar_items