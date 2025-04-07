"""
Repositories for data access.
All repositories are consolidated in this module for easier maintenance.
"""
from typing import List, Dict, Any, Optional, Tuple
import logging
from datetime import datetime

from data.database import execute_query
import data.queries as queries
from config import CONTENT_TYPES, MIN_COMMON_ITEMS

logger = logging.getLogger(__name__)

class ContentEmbeddingRepository:
    """Repository for content embeddings."""
    
    def save_embedding(self, meal_id: str, content_type: str, embedding: List[float]) -> bool:
        """
        Save or update a content embedding.
        
        Args:
            meal_id: The ID of the content
            content_type: The type of content ('meal', 'recipe')
            embedding: The vector embedding
            
        Returns:
            bool: Success status
        """
        try:
            execute_query(
                queries.SAVE_EMBEDDING,
                {
                    "meal_id": meal_id,
                    "content_type": content_type,
                    "embedding": embedding
                },
                is_transaction=True
            )
            return True
        except Exception as e:
            logger.error(f"Error saving embedding: {e}")
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
            queries.GET_EMBEDDING,
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
        formatted_query = queries.FIND_SIMILAR_CONTENT_BASE.format(
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
        if content_type == 'meal':
            result = execute_query(
                queries.GET_MEALS_WITHOUT_EMBEDDINGS,
                {"limit": limit}
            )
        elif content_type == 'recipe':
            result = execute_query(
                queries.GET_RECIPES_WITHOUT_EMBEDDINGS,
                {"limit": limit}
            )
        else:
            return []
        
        return [(row[0], row[1] or '', row[2] or '') for row in result.fetchall()]
    
    def get_meal_ingredients(self, meal_id: str) -> List[Dict[str, Any]]:
        """
        Get ingredients for a meal.
        
        Args:
            meal_id: The ID of the meal
            
        Returns:
            List of ingredients
        """
        result = execute_query(
            queries.GET_MEAL_INGREDIENTS,
            {"meal_id": meal_id}
        )
        
        ingredients = []
        for row in result:
            ingredients.append({
                "id": row[0],
                "name": row[1],
                "amount": row[2]
            })
        
        return ingredients
    
    def get_recipe_ingredients(self, recipe_id: str) -> List[Dict[str, Any]]:
        """
        Get ingredients for a recipe.
        
        Args:
            recipe_id: The ID of the recipe
            
        Returns:
            List of ingredients
        """
        result = execute_query(
            queries.GET_RECIPE_INGREDIENTS,
            {"recipe_id": recipe_id}
        )
        
        ingredients = []
        for row in result:
            ingredients.append({
                "id": row[0],
                "name": row[1],
                "amount": row[2]
            })
        
        return ingredients
    
    def get_similar_by_ingredients(
        self,
        meal_id: str,
        content_type: str,
        limit: int = 10
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
        if content_type != 'meal':
            return []
        
        result = execute_query(
            queries.GET_SIMILAR_MEALS_BY_INGREDIENTS,
            {"meal_id": meal_id, "limit": limit}
        )
        
        items = []
        for row in result:
            items.append({
                "id": row[0],
                "content_type": "meal",
                "title": row[1],
                "similarity": row[2] / 10.0  # Normalize to 0-1 range
            })
        
        return items

class InteractionRepository:
    """Repository for user interactions."""
    
    def record_interaction(
        self, 
        user_id: str, 
        meal_id: str, 
        content_type: str, 
        interaction_type: str
    ) -> bool:
        """
        Record a user interaction with content.
        
        Args:
            user_id: The ID of the user
            meal_id: The ID of the content
            content_type: The type of content ('meal', 'recipe')
            interaction_type: The type of interaction ('view', 'like', 'save', 'cook')
            
        Returns:
            bool: Success status
        """
        try:
            if content_type not in CONTENT_TYPES:
                raise ValueError(f"Invalid content type: {content_type}")
                
            execute_query(
                queries.RECORD_INTERACTION,
                {
                    "user_id": user_id,
                    "meal_id": meal_id,
                    "content_type": content_type,
                    "interaction_type": interaction_type
                },
                is_transaction=True
            )
            return True
        except Exception as e:
            logger.error(f"Error recording interaction: {e}")
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
        formatted_query = queries.GET_USER_RECENT_INTERACTIONS_BASE.format(
            type_filter=type_filter
        )
        
        result = execute_query(
            formatted_query,
            params
        )
        
        interactions = []
        for row in result:
            interactions.append({
                "meal_id": row[0],
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
            content_type: The type of content ('meal', 'recipe', 'all')
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
        formatted_query = queries.GET_TRENDING_MEALS_BASE.format(
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
    
    def find_similar_users(
        self, 
        user_id: str, 
        min_common_items: int = MIN_COMMON_ITEMS, 
        limit: int = 10
    ) -> List[str]:
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
            queries.FIND_SIMILAR_USERS,
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
        formatted_query = queries.GET_MEALS_FROM_SIMILAR_USERS_BASE.format(
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
    
    def get_cuisine_recommendations(
        self, 
        cuisine_id: str, 
        limit: int = 10
    ) -> List[Dict[str, Any]]:
        """
        Get meals in a specific cuisine.
        
        Args:
            cuisine_id: The ID of the cuisine
            limit: Maximum number of meals to retrieve
            
        Returns:
            List of meals in the cuisine
        """
        result = execute_query(
            queries.GET_CUISINE_MEALS,
            {"cuisine_id": cuisine_id, "limit": limit}
        )
        
        meals = []
        for row in result:
            meals.append({
                "id": row[0],
                "content_type": "meal",
                "title": row[1]
            })
        
        return meals
    
    def get_dietary_recommendations(
        self, 
        dietary_restriction_id: str, 
        limit: int = 10
    ) -> List[Dict[str, Any]]:
        """
        Get meals that match a dietary restriction.
        
        Args:
            dietary_restriction_id: The ID of the dietary restriction
            limit: Maximum number of meals to retrieve
            
        Returns:
            List of meals matching the dietary restriction
        """
        result = execute_query(
            queries.GET_DIETARY_PREFERENCE_MEALS,
            {"dietary_restriction_id": dietary_restriction_id, "limit": limit}
        )
        
        meals = []
        for row in result:
            meals.append({
                "id": row[0],
                "content_type": "meal",
                "title": row[1]
            })
        
        return meals
    
    def get_user_dietary_preferences(
        self, 
        user_id: str
    ) -> List[Dict[str, Any]]:
        """
        Get a user's dietary preferences.
        
        Args:
            user_id: The ID of the user
            
        Returns:
            List of dietary preferences
        """
        result = execute_query(
            queries.GET_USER_DIETARY_PREFERENCES,
            {"user_id": user_id}
        )
        
        preferences = []
        for row in result:
            preferences.append({
                "id": row[0],
                "name": row[1]
            })
        
        return preferences
    
    def add_user_dietary_preference(
        self, 
        user_id: str, 
        dietary_restriction_id: int
    ) -> bool:
        """
        Add a dietary preference for a user.
        
        Args:
            user_id: The ID of the user
            dietary_restriction_id: The ID of the dietary restriction
            
        Returns:
            Success status
        """
        try:
            execute_query(
                """
                INSERT INTO "UserDietaryPreference" (user_id, dietary_restriction_id)
                VALUES (:user_id, :dietary_restriction_id)
                ON CONFLICT (user_id, dietary_restriction_id) DO NOTHING
                """,
                {
                    "user_id": user_id,
                    "dietary_restriction_id": dietary_restriction_id
                },
                is_transaction=True
            )
            return True
        except Exception as e:
            logger.error(f"Error adding dietary preference: {e}")
            return False