"""
Repository for content embeddings.
Handles database operations for content embeddings.
"""
from typing import List, Dict, Any, Optional, Tuple
import numpy as np

from data.database import execute_query
from config.settings import EMBEDDING_DIMENSION
from data.queries.content_embedding_queries import (
    SAVE_EMBEDDING,
    GET_EMBEDDING,
    FIND_SIMILAR_CONTENT_BASE,
    GET_POSTS_WITHOUT_EMBEDDINGS,
    GET_COMMUNITIES_WITHOUT_EMBEDDINGS
)

class ContentEmbeddingRepository:
    """Repository for content embeddings."""
    
    def save_embedding(self, meal_id: str, content_type: str, embedding: List[float]) -> bool:
        """
        Save or update a content embedding.
        
        Args:
            meal_id: The ID of the content
            content_type: The type of content ('post', 'community', etc.)
            embedding: The vector embedding
            
        Returns:
            bool: Success status
        """
        try:
            execute_query(
                SAVE_EMBEDDING,
                {
                    "meal_id": meal_id,
                    "content_type": content_type,
                    "embedding": embedding
                },
                is_transaction=True
            )
            return True
        except Exception as e:
            print(f"Error saving embedding: {e}")
            return False
    
    def get_embedding(self, meal_id: str, content_type: str) -> Optional[List[float]]:
        """
        Get the embedding for specific content.
        
        Args:
            meal_id: The ID of the content
            content_type: The type of content
            
        Returns:
            The embedding vector or None if not found
        """
        result = execute_query(
            GET_EMBEDDING,
            {"meal_id": meal_id, "content_type": content_type}
        )
        
        row = result.fetchone()
        return row[0] if row else None
    
    def find_similar_content(
        self, 
        embedding: List[float], 
        content_type: Optional[str] = None, 
        exclude_ids: List[str] = None,
        limit: int = 10
    ) -> List[Dict[str, Any]]:
        """
        Find content with similar embeddings.
        
        Args:
            embedding: The source embedding to compare against
            content_type: Optional filter for content type
            exclude_ids: List of content IDs to exclude
            limit: Maximum number of results
            
        Returns:
            List of similar content items with similarity scores
        """
        params = {"embedding": embedding, "limit": limit}
        
        # Build query parts
        type_filter = ""
        if content_type:
            type_filter = "AND ce.content_type = :content_type"
            params["content_type"] = content_type
        
        exclude_clause = ""
        if exclude_ids and len(exclude_ids) > 0:
            placeholder_list = ','.join([f':exclude_{i}' for i in range(len(exclude_ids))])
            exclude_clause = f"AND ce.meal_id NOT IN ({placeholder_list})"
            for i, id_val in enumerate(exclude_ids):
                params[f"exclude_{i}"] = id_val
        
        # Execute query
        formatted_query = FIND_SIMILAR_CONTENT_BASE.format(
            type_filter=type_filter,
            exclude_clause=exclude_clause
        )
        
        result = execute_query(
            formatted_query,
            params
        )
        
        items = []
        for row in result:
            items.append({
                "id": row[0],
                "content_type": row[1],
                "title": row[2],
                "similarity": row[3]
            })
        
        return items
    
    def get_content_without_embeddings(self, content_type: str, limit: int = 500) -> List[Tuple[str, str, str]]:
        """
        Get content items that don't have embeddings yet.
        
        Args:
            content_type: The type of content
            limit: Maximum number of items to retrieve
            
        Returns:
            List of tuples (id, title, content) for items without embeddings
        """
        if content_type == 'post':
            result = execute_query(
                GET_POSTS_WITHOUT_EMBEDDINGS,
                {"limit": limit}
            )
        elif content_type == 'community':
            result = execute_query(
                GET_COMMUNITIES_WITHOUT_EMBEDDINGS,
                {"limit": limit}
            )
        else:
            return []
        
        return [(row[0], row[1] or '', row[2] or '') for row in result.fetchall()]