"""
Repository for user interactions.
Handles database operations for user interactions with content.
"""
from typing import List, Dict, Any, Optional
from datetime import datetime

from data.database import execute_query
from config.settings import CONTENT_TYPES
from data.queries.interaction_queries import (
    RECORD_INTERACTION,
    GET_USER_RECENT_INTERACTIONS_BASE,
    GET_TRENDING_CONTENT_BASE,
    FIND_SIMILAR_USERS,
    GET_CONTENT_FROM_SIMILAR_USERS_BASE,
    GET_CATEGORY_COMMUNITIES
)

class InteractionRepository:
    """Repository for user interactions."""
    
    def record_interaction(
        self, 
        user_id: str, 
        content_id: str, 
        content_type: str, 
        interaction_type: str
    ) -> bool:
        """
        Record a user interaction with content.
        
        Args:
            user_id: The ID of the user
            content_id: The ID of the content
            content_type: The type of content ('post', 'community', 'comment')
            interaction_type: The type of interaction ('view', 'click', 'vote', etc.)
            
        Returns:
            bool: Success status
        """
        try:
            if content_type not in CONTENT_TYPES:
                raise ValueError(f"Invalid content type: {content_type}")
                
            execute_query(
                RECORD_INTERACTION,
                {
                    "user_id": user_id,
                    "content_id": content_id,
                    "content_type": content_type,
                    "interaction_type": interaction_type
                },
                is_transaction=True
            )
            return True
        except Exception as e:
            print(f"Error recording interaction: {e}")
            return False
    
    def get_user_recent_interactions(
        self, 
        user_id: str, 
        content_type: Optional[str] = None,
        limit: int = 10
    ) -> List[Dict[str, Any]]:
        """
        Get recent interactions for a user.
        
        Args:
            user_id: The ID of the user
            content_type: Optional filter for content type
            limit: Maximum number of interactions to retrieve
            
        Returns:
            List of recent interactions
        """
        params = {"user_id": user_id, "limit": limit}
        
        type_filter = ""
        if content_type:
            if content_type not in CONTENT_TYPES:
                raise ValueError(f"Invalid content type: {content_type}")
            type_filter = "AND content_type = :content_type"
            params["content_type"] = content_type
            
        # Format the query with the type filter
        formatted_query = GET_USER_RECENT_INTERACTIONS_BASE.format(
            type_filter=type_filter
        )
        
        result = execute_query(
            formatted_query,
            params
        )
        
        interactions = []
        for row in result:
            interactions.append({
                "content_id": row[0],
                "content_type": row[1],
                "interaction_type": row[2],
                "created_at": row[3]
            })
        
        return interactions
    
    def get_trending_content(
        self, 
        content_type: str = 'all', 
        time_window: str = 'day',
        limit: int = 10
    ) -> List[Dict[str, Any]]:
        """
        Get trending content based on recent interactions.
        
        Args:
            content_type: The type of content ('post', 'community', 'all')
            time_window: Time window ('day', 'week', 'month')
            limit: Maximum number of items
            
        Returns:
            List of trending content items with popularity scores
        """
        # Determine time interval
        time_clause = "NOW() - INTERVAL '1 day'"
        if time_window == "week":
            time_clause = "NOW() - INTERVAL '7 days'"
        elif time_window == "month":
            time_clause = "NOW() - INTERVAL '30 days'"
        
        type_filter = ""
        params = {"limit": limit}
        
        if content_type != 'all':
            if content_type not in CONTENT_TYPES:
                raise ValueError(f"Invalid content type: {content_type}")
            type_filter = "AND ri.content_type = :content_type"
            params["content_type"] = content_type
        
        # Format the query with the time clause and type filter
        formatted_query = GET_TRENDING_CONTENT_BASE.format(
            time_clause=time_clause,
            type_filter=type_filter
        )
        
        result = execute_query(
            formatted_query,
            params
        )
        
        trending_items = []
        for row in result:
            trending_items.append({
                "id": row[0],
                "content_type": row[1],
                "title": row[2],
                "popularity": row[3]
            })
        
        return trending_items
    
    def find_similar_users(self, user_id: str, min_common_items: int = 2, limit: int = 10) -> List[str]:
        """
        Find users with similar interaction patterns.
        
        Args:
            user_id: The ID of the user
            min_common_items: Minimum number of common items to consider users similar
            limit: Maximum number of similar users to retrieve
            
        Returns:
            List of similar user IDs
        """
        result = execute_query(
            FIND_SIMILAR_USERS,
            {
                "user_id": user_id, 
                "min_common_items": min_common_items,
                "limit": limit
            }
        )
        
        return [row[0] for row in result]
    
    def get_content_from_similar_users(
        self, 
        similar_users: List[str],
        user_id: str,
        content_type: Optional[str] = None,
        limit: int = 10
    ) -> List[Dict[str, Any]]:
        """
        Get content items that similar users interacted with.
        
        Args:
            similar_users: List of similar user IDs
            user_id: The ID of the current user (to exclude content they've already interacted with)
            content_type: Optional filter for content type
            limit: Maximum number of content items to retrieve
            
        Returns:
            List of content items with interaction counts
        """
        if not similar_users:
            return []
        
        # Create parameter placeholders for similar users
        user_placeholders = ','.join([f':user_{i}' for i in range(len(similar_users))])
        params = {"user_id": user_id, "limit": limit}
        
        # Add similar user IDs to parameters
        for i, similar_user in enumerate(similar_users):
            params[f"user_{i}"] = similar_user
        
        # Add content type filter if specified
        type_filter = ""
        if content_type:
            if content_type not in CONTENT_TYPES:
                raise ValueError(f"Invalid content type: {content_type}")
            type_filter = "AND ri.content_type = :content_type"
            params["content_type"] = content_type
        
        # Format the query with the user placeholders and type filter
        formatted_query = GET_CONTENT_FROM_SIMILAR_USERS_BASE.format(
            user_placeholders=user_placeholders,
            type_filter=type_filter
        )
        
        result = execute_query(
            formatted_query,
            params
        )
        
        content_items = []
        for row in result:
            content_items.append({
                "id": row[0],
                "content_type": row[1],
                "interaction_count": row[2],
                "title": row[3]
            })
        
        return content_items
        
    def get_category_communities(self, category_id: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        Get communities in a specific category.
        
        Args:
            category_id: The ID of the category
            limit: Maximum number of communities to retrieve
            
        Returns:
            List of communities in the category
        """
        result = execute_query(
            GET_CATEGORY_COMMUNITIES,
            {"category_id": category_id, "limit": limit}
        )
        
        communities = []
        for row in result:
            communities.append({
                "id": row[0],
                "content_type": "community",
                "title": row[1]
            })
        
        return communities