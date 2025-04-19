"""
Base class for recommenders with common functionality.
"""
import logging
from abc import ABC, abstractmethod
from typing import List, Dict, Any, Optional

logger = logging.getLogger(__name__)

class BaseRecommender(ABC):
    """Base class for recommendation strategies."""
    
    @abstractmethod
    def get_recommendations(self, **kwargs) -> List[Dict[str, Any]]:
        """Get recommendations based on implementation strategy."""
        pass
    
    def get_similar_recommendations(self, recipe_id: int, similarity_method: str = "content", limit: int = 10) -> List[Dict[str, Any]]:
        """Get recommendations similar to a specific recipe."""
        raise NotImplementedError("This recommender does not support similar recommendations")
    
    def get_cuisine_recommendations(self, cuisine_name: str, limit: int = 10) -> List[Dict[str, Any]]:
        """Get recommendations for a specific cuisine."""
        raise NotImplementedError("This recommender does not support cuisine recommendations")
    
    def get_dietary_recommendations(self, dietary_restriction: str, limit: int = 10) -> List[Dict[str, Any]]:
        """Get recommendations for a specific dietary restriction."""
        raise NotImplementedError("This recommender does not support dietary recommendations")
    
    def get_trending_recommendations(self, time_window: str = "day", limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """Get trending recommendations."""
        raise NotImplementedError("This recommender does not support trending recommendations")
    
    def get_quick_recommendations(self, max_time: int = 30, limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """Get quick recipe recommendations."""
        raise NotImplementedError("This recommender does not support quick recommendations")