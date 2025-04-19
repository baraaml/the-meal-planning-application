"""
Factory function to get different types of recommenders.
"""
import logging
from typing import Optional

from recommenders.base_recommender import BaseRecommender
from recommenders.content_recommender import ContentRecommender
from recommenders.collaborative_recommender import CollaborativeRecommender
from recommenders.popularity_recommender import PopularityRecommender
from recommenders.hybrid_recommender import HybridRecommender

logger = logging.getLogger(__name__)

def get_recommender(recommender_type: str) -> BaseRecommender:
    """
    Get recommender instance based on type.
    
    Args:
        recommender_type: Type of recommender to create
            "hybrid" - HybridRecommender
            "content" - ContentRecommender
            "collaborative" - CollaborativeRecommender
            "popularity" - PopularityRecommender
    
    Returns:
        Recommender instance
    """
    recommender_map = {
        "hybrid": HybridRecommender,
        "content": ContentRecommender,
        "collaborative": CollaborativeRecommender,
        "popularity": PopularityRecommender
    }
    
    # Default to hybrid recommender if type not recognized
    if recommender_type not in recommender_map:
        logger.warning(f"Unknown recommender type: {recommender_type}, using hybrid recommender")
        recommender_type = "hybrid"
    
    return recommender_map[recommender_type]()