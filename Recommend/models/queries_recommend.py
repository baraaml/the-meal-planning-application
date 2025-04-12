"""
Recommendation-specific database queries.
"""
from config.db import execute_query, execute_query_single

def get_trending_recipes(time_window="day", limit=10, content_type=None, cuisine=None, dietary_restriction=None):
    """Get trending recipes based on recent interactions."""
    # Map time window to SQL interval
    time_intervals = {
        'day': "interval '1 day'",
        'week': "interval '7 days'",
        'month': "interval '30 days'"
    }
    
    interval = time_intervals.get(time_window, "interval '1 day'")
    
    query = f"""
    WITH popular_recipes AS (
        SELECT 
            r.recipe_id,
            r.recipe_title,
            COUNT(ui.interaction_id) as popularity,
            ROW_NUMBER() OVER (ORDER BY COUNT(ui.interaction_id) DESC) as rank
        FROM recipes r
        LEFT JOIN user_interactions ui ON r.recipe_id = ui.recipe_id
        WHERE ui.created_at > CURRENT_TIMESTAMP - {interval}
    """
    
    params = {"limit": limit}
    
    # Add filters
    if cuisine:
        query += " AND (r.region = %(cuisine)s OR r.sub_region = %(cuisine)s)"
        params["cuisine"] = cuisine
    
    if dietary_restriction:
        query += """
        AND EXISTS (
            SELECT 1 FROM recipe_diet_attributes rda 
            WHERE rda.recipe_id = r.recipe_id AND
            CASE 
                WHEN %(dietary)s = 'vegan' THEN rda.vegan = TRUE
                WHEN %(dietary)s = 'pescetarian' THEN rda.pescetarian = TRUE
                WHEN %(dietary)s = 'lacto_vegetarian' THEN rda.lacto_vegetarian = TRUE
                ELSE FALSE
            END
        )
        """
        params["dietary"] = dietary_restriction
    
    # Complete the query
    query += """
        GROUP BY r.recipe_id, r.recipe_title
    )
    SELECT 
        recipe_id,
        recipe_title as title,
        popularity
    FROM popular_recipes
    WHERE rank <= %(limit)s
    ORDER BY popularity DESC
    """
    
    results = execute_query(query, params)
    
    # If no results (no interactions yet), get fallback recommendations
    if not results:
        return get_fallback_recommendations(limit, cuisine, dietary_restriction)
    
    return results

def get_fallback_recommendations(limit=10, cuisine=None, dietary_restriction=None):
    """Get fallback recommendations when no interaction data is available."""
    query = """
    SELECT recipe_id, recipe_title as title
    FROM recipes
    WHERE 1=1
    """
    
    params = {"limit": limit}
    
    # Add filters
    if cuisine:
        query += " AND (region = %(cuisine)s OR sub_region = %(cuisine)s)"
        params["cuisine"] = cuisine
    
    if dietary_restriction:
        query += """
        AND EXISTS (
            SELECT 1 FROM recipe_diet_attributes rda 
            WHERE rda.recipe_id = recipes.recipe_id AND
            CASE 
                WHEN %(dietary)s = 'vegan' THEN rda.vegan = TRUE
                WHEN %(dietary)s = 'pescetarian' THEN rda.pescetarian = TRUE
                WHEN %(dietary)s = 'lacto_vegetarian' THEN rda.lacto_vegetarian = TRUE
                ELSE FALSE
            END
        )
        """
        params["dietary"] = dietary_restriction
    
    query += """
    ORDER BY recipe_id
    LIMIT %(limit)s
    """
    
    return execute_query(query, params)

def get_user_recent_interactions(user_id, limit=5, content_type=None):
    """Get a user's recent interactions."""
    query = """
    SELECT recipe_id, interaction_type, rating, created_at
    FROM user_interactions
    WHERE user_id = %(user_id)s
    """
    
    params = {
        "user_id": user_id,
        "limit": limit
    }
    
    query += """
    ORDER BY created_at DESC
    LIMIT %(limit)s
    """
    
    return execute_query(query, params)

def record_interaction(user_id, recipe_id, interaction_type, rating=None):
    """Record a user interaction with a recipe."""
    query = """
    INSERT INTO user_interactions
    (user_id, recipe_id, interaction_type, rating)
    VALUES (%(user_id)s, %(recipe_id)s, %(interaction_type)s, %(rating)s)
    ON CONFLICT (user_id, recipe_id, interaction_type) 
    DO UPDATE SET 
        rating = EXCLUDED.rating,
        created_at = CURRENT_TIMESTAMP
    """
    
    params = {
        "user_id": user_id,
        "recipe_id": recipe_id,
        "interaction_type": interaction_type,
        "rating": rating if interaction_type == 'rating' else None
    }
    
    execute_query(query, params)
    return True

def find_similar_users(user_id, min_common_items=2, limit=10):
    """Find users with similar interaction patterns."""
    query = """
    WITH user_items AS (
        SELECT recipe_id
        FROM user_interactions
        WHERE user_id = %(user_id)s
    ),
    similar_users AS (
        SELECT 
            ui.user_id,
            COUNT(DISTINCT ui.recipe_id) as common_items,
            ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT ui.recipe_id) DESC) as rank
        FROM user_interactions ui
        JOIN user_items uir ON ui.recipe_id = uir.recipe_id
        WHERE ui.user_id != %(user_id)s
        GROUP BY ui.user_id
        HAVING COUNT(DISTINCT ui.recipe_id) >= %(min_common)s
    )
    SELECT user_id
    FROM similar_users
    WHERE rank <= %(limit)s
    """
    
    params = {
        "user_id": user_id,
        "min_common": min_common_items,
        "limit": limit
    }
    
    results = execute_query(query, params)
    return [row["user_id"] for row in results] if results else []

def get_content_from_similar_users(similar_users, user_id, limit=10):
    """Get content that similar users have interacted with."""
    if not similar_users:
        return []
    
    # Create placeholders for IN clause
    placeholders = [f"%({i})s" for i in range(len(similar_users))]
    user_placeholders = ", ".join(placeholders)
    
    # Create parameters dictionary
    params = {
        "user_id": user_id,
        "limit": limit
    }
    
    # Add user IDs to parameters
    for i, user in enumerate(similar_users):
        params[str(i)] = user
    
    query = f"""
    WITH user_recommendations AS (
        SELECT 
            ui.recipe_id,
            r.recipe_title as title,
            COUNT(*) as interaction_count,
            ROW_NUMBER() OVER (ORDER BY COUNT(*) DESC) as rank
        FROM user_interactions ui
        JOIN recipes r ON ui.recipe_id = r.recipe_id
        WHERE ui.user_id IN ({user_placeholders})
        AND NOT EXISTS (
            SELECT 1 
            FROM user_interactions ui2
            WHERE ui2.user_id = %(user_id)s
            AND ui2.recipe_id = ui.recipe_id
        )
        GROUP BY ui.recipe_id, r.recipe_title
    )
    SELECT recipe_id, title, interaction_count
    FROM user_recommendations
    WHERE rank <= %(limit)s
    ORDER BY interaction_count DESC
    """
    
    return execute_query(query, params)

def get_user_dietary_preferences(user_id):
    """Get a user's dietary preferences."""
    query = """
    SELECT dietary_restriction
    FROM user_dietary_preference
    WHERE user_id = %(user_id)s
    """
    
    results = execute_query(query, {"user_id": user_id})
    return [row["dietary_restriction"] for row in results] if results else []

def set_user_dietary_preference(user_id, dietary_restriction):
    """Add a dietary preference for a user."""
    query = """
    INSERT INTO user_dietary_preference (user_id, dietary_restriction)
    VALUES (%(user_id)s, %(dietary_restriction)s)
    ON CONFLICT (user_id, dietary_restriction) DO NOTHING
    """
    
    execute_query(query, {
        "user_id": user_id,
        "dietary_restriction": dietary_restriction
    })
    return True

def remove_user_dietary_preference(user_id, dietary_restriction):
    """Remove a dietary preference for a user."""
    query = """
    DELETE FROM user_dietary_preference
    WHERE user_id = %(user_id)s AND dietary_restriction = %(dietary_restriction)s
    """
    
    execute_query(query, {
        "user_id": user_id,
        "dietary_restriction": dietary_restriction
    })
    return True