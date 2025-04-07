"""
Base recommender interface.
Defines the common interface for all recommendation strategies.
"""
from abc import ABC, abstractmethod
from typing import List, Dict, Any, Optional

class BaseRecommender(ABC):
    """Base interface for all recommendation strategies."""
    
    @abstractmethod
    def get_recommendations(
        self, 
        user_id: Optional[str] = None,
        meal_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = 10,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get recommendations based on the strategy implementation.
        
        Args:
            user_id: Optional user ID for personalized recommendations
            meal_id: Optional meal ID for similar meal recommendations
            content_type: Optional content type filter ('meal', 'recipe')
            limit: Maximum number of recommendations to return
            kwargs: Additional strategy-specific parameters
            
        Returns:
            List of recommended items
        """
        pass