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
        content_id: Optional[str] = None,
        content_type: Optional[str] = None,
        limit: int = 10,
        **kwargs
    ) -> List[Dict[str, Any]]:
        """
        Get recommendations based on the strategy implementation.
        
        Args:
            user_id: Optional user ID for personalized recommendations
            content_id: Optional content ID for similar content recommendations
            content_type: Optional content type filter
            limit: Maximum number of recommendations to return
            kwargs: Additional strategy-specific parameters
            
        Returns:
            List of recommended items
        """
        pass
    
    def format_recommendations(
        self, 
        items: List[Dict[str, Any]]
    ) -> Dict[str, List[Dict[str, Any]]]:
        """
        Format recommendations for API response.
        
        Args:
            items: List of recommendation items
            
        Returns:
            Formatted recommendations
        """
        return {"items": items}