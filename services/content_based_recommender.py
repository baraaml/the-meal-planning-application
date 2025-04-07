"""
Content-based recommender implementation.
Uses vector embeddings to find similar content.
"""
from typing import List, Dict, Any, Optional
import logging

from services.base_recommender import BaseRecommender
from data.repositories.content_embedding_repository import ContentEmbeddingRepository
from config.settings import DEFAULT_RECOMMENDATION_LIMIT, CONTENT_TYPES

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
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get content-based recommendations.
        
        Args:
            user_id: Not used for content-based recommendations
            meal_id: The ID of the source content
            content_type: The type of content ('post', 'community')
            limit: Maximum number of recommendations
            
        Returns:
            List of similar content items
        """
        if not meal_id or not content_type:
            logger.warning("Content ID and type required for content-based recommendations")
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
        
        return similar_items