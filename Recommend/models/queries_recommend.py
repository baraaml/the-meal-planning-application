"""
Database query functions for recommendations and user interactions.
"""
import logging
from typing import Dict, List, Any, Optional
from datetime import datetime, timedelta

from config.db import execute_query, execute_query_single
from config.config import INTERACTION_TYPES

logger = logging.getLogger(__name__)

def record_interaction(user_id: str, recipe_id: str, interaction_type: str, rating: Optional[float] = None) -> bool:
    """
    Record a user interaction with a recipe.
    
    Args:
        user_id: User ID
        recipe_id: Recipe ID
        interaction_type: Type of interaction (view, like, save, etc.)
        rating: Optional rating value (for 'rating' interaction type)
        
    Returns:
        bool: True if interaction was recorded successfully
    """
    try:
        # Validate interaction type
        if interaction_type not in INTERACTION_TYPES:
            logger.warning(f"Invalid interaction type: {interaction_type}")
            return False
        
        # Validate rating
        if interaction_type == 'rating' and (rating is None or rating < 0 or rating > 5):
            logger.warning(f"Invalid rating: {rating}")
            return False
        
        # Check if the interaction already exists
        check_query = """
        SELECT interaction_id
        FROM user_interactions
        WHERE user_id = %(user_id)s
          AND recipe_id = %(recipe_id)s
          AND interaction_type = %(interaction_type)s
        """
        
        existing = execute_query_single(check_query, {
            "user_id": user_id,
            "recipe_id": recipe_id,
            "interaction_type": interaction_type
        })
        
        if existing:
            # Update existing interaction
            update_query = """
            UPDATE user_interactions
            SET timestamp = NOW(),
                rating = %(rating)s
            WHERE interaction_id = %(interaction_id)s
            """
            
            execute_query(update_query, {
                "interaction_id": existing["interaction_id"],
                "rating": rating
            })
        else:
            # Create new interaction
            insert_query = """
            INSERT INTO user_interactions
            (user_id, recipe_id, interaction_type, rating, timestamp)
            VALUES
            (%(user_id)s, %(recipe_id)s, %(interaction_type)s, %(rating)s, NOW())
            """
            
            execute_query(insert_query, {
                "user_id": user_id,
                "recipe_id": recipe_id,
                "interaction_type": interaction_type,
                "rating": rating
            })
        
        return True
        
    except Exception as e:
        logger.error(f"Error recording interaction: {e}")
        return False

def get_user_recent_interactions(user_id: str, limit: int = 10, exclude_types: Optional[List[str]] = None) -> List[Dict[str, Any]]:
    """
    Get recent interactions for a user.
    
    Args:
        user_id: User ID
        limit: Maximum number of interactions
        exclude_types: Optional list of interaction types to exclude
        
    Returns:
        List of recent interactions
    """
    try:
        query = """
        SELECT 
            ui.interaction_id, ui.user_id, ui.recipe_id, 
            ui.interaction_type, ui.rating, ui.timestamp,
            r.recipe_title as title
        FROM user_interactions ui
        JOIN recipes r ON ui.recipe_id::integer = r.recipe_id
        WHERE ui.user_id = %(user_id)s
        """
        
        params = {
            "user_id": user_id,
            "limit": limit
        }
        
        # Add exclusion filter if provided
        if exclude_types and len(exclude_types) > 0:
            exclude_list = ", ".join([f"'{exclude}'" for exclude in exclude_types])
            query += f" AND ui.interaction_type NOT IN ({exclude_list})"
        
        # Order and limit
        query += """
        ORDER BY ui.timestamp DESC
        LIMIT %(limit)s
        """
        
        return execute_query(query, params)
        
    except Exception as e:
        logger.error(f"Error getting recent interactions: {e}")
        return []

def find_similar_users(user_id: str, min_common_items: int = 2, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Find users with similar interaction patterns.
    
    Args:
        user_id: User ID to find similar users for
        min_common_items: Minimum number of common interactions
        limit: Maximum number of similar users
        
    Returns:
        List of similar users with similarity scores
    """
    try:
        # Find users who have interacted with the same items
        query = """
        WITH user_items AS (
            -- Get items the target user has interacted with positively
            SELECT recipe_id
            FROM user_interactions
            WHERE user_id = %(user_id)s
            AND interaction_type IN ('like', 'save', 'cook', 'rating')
            AND (interaction_type != 'rating' OR rating >= 3)
        ),
        similar_users AS (
            -- Find users who have interacted with the same items
            SELECT 
                ui.user_id,
                COUNT(*) as common_items,
                -- Calculate similarity score weighted by interaction type
                SUM(
                    CASE 
                        WHEN ui.interaction_type = 'rating' THEN ui.rating / 5.0
                        WHEN ui.interaction_type = 'cook' THEN 0.9
                        WHEN ui.interaction_type = 'save' THEN 0.8
                        WHEN ui.interaction_type = 'like' THEN 0.7
                        WHEN ui.interaction_type = 'view' THEN 0.3
                        ELSE 0.1
                    END
                ) as similarity_score
            FROM user_interactions ui
            JOIN user_items ON ui.recipe_id = user_items.recipe_id
            WHERE ui.user_id != %(user_id)s
            GROUP BY ui.user_id
            HAVING COUNT(*) >= %(min_common_items)s
            ORDER BY similarity_score DESC, common_items DESC
            LIMIT %(limit)s
        )
        SELECT 
            user_id, 
            common_items, 
            ROUND(similarity_score::numeric, 2) as similarity_score
        FROM similar_users
        """
        
        params = {
            "user_id": user_id,
            "min_common_items": min_common_items,
            "limit": limit
        }
        
        return execute_query(query, params)
        
    except Exception as e:
        logger.error(f"Error finding similar users: {e}")
        return []

def get_content_from_similar_users(similar_users: List[Dict[str, Any]], user_id: str, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Get content recommendations based on similar users' interactions.
    
    Args:
        similar_users: List of similar users with similarity scores
        user_id: User ID to get recommendations for
        limit: Maximum number of recommendations
        
    Returns:
        List of recommended items
    """
    try:
        # If no similar users, return empty list
        if not similar_users:
            return []
        
        # Create a list of user IDs and weights for the SQL query
        user_weights = []
        for user in similar_users:
            user_weights.append({
                "user_id": user["user_id"],
                "weight": user["similarity_score"]
            })
        
        # Convert to JSON array
        import json
        user_weights_json = json.dumps(user_weights)
        
        # Query for recommendations from similar users
        query = """
        WITH user_weights AS (
            SELECT 
                user_id, 
                (weight::numeric / SUM(weight::numeric) OVER ()) as normalized_weight
            FROM json_array_elements(%(user_weights)s::json) as uw(user_weights)
            CROSS JOIN LATERAL json_to_record(user_weights) as x(user_id text, weight numeric)
        ),
        user_items AS (
            -- Items the target user has already interacted with
            SELECT DISTINCT recipe_id
            FROM user_interactions
            WHERE user_id = %(user_id)s
        ),
        weighted_recommendations AS (
            -- Get recommendations from similar users
            SELECT 
                ui.recipe_id,
                r.recipe_title as title,
                -- Calculate weighted score
                SUM(
                    uw.normalized_weight * 
                    CASE 
                        WHEN ui.interaction_type = 'rating' THEN ui.rating / 5.0
                        WHEN ui.interaction_type = 'cook' THEN 0.9
                        WHEN ui.interaction_type = 'save' THEN 0.8
                        WHEN ui.interaction_type = 'like' THEN 0.7
                        WHEN ui.interaction_type = 'view' THEN 0.3
                        ELSE 0.1
                    END
                ) as score
            FROM user_interactions ui
            JOIN user_weights uw ON ui.user_id = uw.user_id
            JOIN recipes r ON ui.recipe_id::integer = r.recipe_id
            WHERE NOT EXISTS (
                -- Exclude items the user has already interacted with
                SELECT 1 FROM user_items
                WHERE user_items.recipe_id = ui.recipe_id
            )
            GROUP BY ui.recipe_id, r.recipe_title
            ORDER BY score DESC
            LIMIT %(limit)s
        )
        SELECT 
            recipe_id as id,
            title,
            ROUND(score * 10, 2) as score
        FROM weighted_recommendations
        """
        
        params = {
            "user_weights": user_weights_json,
            "user_id": user_id,
            "limit": limit
        }
        
        return execute_query(query, params)
        
    except Exception as e:
        logger.error(f"Error getting content from similar users: {e}")
        return []

def get_trending_recipes(time_window: str = "day", limit: int = 10, cuisine: Optional[str] = None, 
                        dietary_restriction: Optional[str] = None) -> List[Dict[str, Any]]:
    """
    Get trending recipes based on recent interactions.
    
    Args:
        time_window: Time window for trending items (day, week, month)
        limit: Maximum number of recommendations
        cuisine: Optional cuisine filter
        dietary_restriction: Optional dietary restriction filter
        
    Returns:
        List of trending recipes
    """
    try:
        # Calculate timestamp for the time window
        now = datetime.now()
        if time_window == "day":
            since = now - timedelta(days=1)
        elif time_window == "week":
            since = now - timedelta(weeks=1)
        elif time_window == "month":
            since = now - timedelta(days=30)
        else:
            since = now - timedelta(days=7)  # Default to week
        
        # Build the query
        query = """
        WITH interaction_counts AS (
            -- Count interactions by recipe
            SELECT 
                ui.recipe_id,
                COUNT(*) as interaction_count,
                -- Calculate weighted score based on interaction types
                SUM(
                    CASE 
                        WHEN ui.interaction_type = 'rating' THEN ui.rating / 5.0
                        WHEN ui.interaction_type = 'cook' THEN 1.0
                        WHEN ui.interaction_type = 'save' THEN 0.8
                        WHEN ui.interaction_type = 'like' THEN 0.6
                        WHEN ui.interaction_type = 'view' THEN 0.2
                        ELSE 0.1
                    END
                ) as weighted_score
            FROM user_interactions ui
            WHERE ui.timestamp >= %(since)s
            GROUP BY ui.recipe_id
        )
        SELECT 
            r.recipe_id as id,
            r.recipe_title as title,
            ROUND(ic.weighted_score::numeric / ic.interaction_count, 2) as score
        FROM interaction_counts ic
        JOIN recipes r ON ic.recipe_id::integer = r.recipe_id
        """
        
        params = {
            "since": since,
            "limit": limit
        }
        
        # Add cuisine filter if provided
        if cuisine:
            query += """
            WHERE (r.region ILIKE %(cuisine)s OR r.sub_region ILIKE %(cuisine)s)
            """
            params["cuisine"] = f"%{cuisine}%"
        
        # Add dietary filter if provided
        if dietary_restriction:
            # We need to join with recipe_diet_attributes
            if "WHERE" in query:
                query += " AND "
            else:
                query += " WHERE "
            
            query += """
            EXISTS (
                SELECT 1 FROM recipe_diet_attributes rda
                WHERE rda.recipe_id = r.recipe_id
                AND rda."""
            
            # Add the specific dietary restriction
            if dietary_restriction.lower() == "vegan":
                query += "vegan = TRUE"
            elif dietary_restriction.lower() == "vegetarian":
                query += "vegetarian = TRUE"
            elif dietary_restriction.lower() == "pescetarian":
                query += "pescetarian = TRUE"
            else:
                query += f"{dietary_restriction.lower()} = TRUE"
            
            query += ")"
        
        # Add ordering and limit
        query += """
        ORDER BY 
            ic.weighted_score DESC,
            ic.interaction_count DESC
        LIMIT %(limit)s
        """
        
        return execute_query(query, params)
        
    except Exception as e:
        logger.error(f"Error getting trending recipes: {e}")
        return []