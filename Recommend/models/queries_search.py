"""
Search-specific database queries.
"""
from config.db import execute_query
from config.config import MIN_SIMILARITY_SCORE

def find_similar_content(embedding, exclude_ids=None, limit=10, min_similarity=MIN_SIMILARITY_SCORE):
    """Find recipes with similar embeddings."""
    # Convert embedding to a proper format for PostgreSQL vector
    if isinstance(embedding, list):
        embedding_str = str(embedding).replace('[', '{').replace(']', '}')
    else:
        embedding_str = embedding
    
    # Prepare query parameters
    params = {
        "embedding": embedding_str,
        "limit": limit,
        "min_similarity": min_similarity
    }
    
    # Build exclusion clause if needed
    exclude_clause = ""
    if exclude_ids and len(exclude_ids) > 0:
        exclude_ids_str = ",".join(str(id) for id in exclude_ids)
        exclude_clause = f"AND r.recipe_id NOT IN ({exclude_ids_str})"
    
    # Execute similarity search
    query = f"""
    SELECT 
        r.recipe_id,
        r.recipe_title, 
        1 - (re.embedding <=> %(embedding)s::vector) AS similarity
    FROM recipe_embeddings re
    JOIN recipes r ON re.recipe_id = r.recipe_id
    WHERE 1 = 1 {exclude_clause}
    AND 1 - (re.embedding <=> %(embedding)s::vector) > %(min_similarity)s
    ORDER BY similarity DESC
    LIMIT %(limit)s
    """
    
    return execute_query(query, params)

def get_similar_by_ingredients(recipe_id, limit=10):
    """Get recipes similar by ingredients."""
    query = """
    WITH recipe_ingredients AS (
        SELECT ingredient_id
        FROM recipe_ingredients
        WHERE recipe_id = %(recipe_id)s
    ),
    similarity_scores AS (
        SELECT 
            r.recipe_id, 
            r.recipe_title, 
            COUNT(ri.ingredient_id) as ingredient_match_count,
            COUNT(ri.ingredient_id)::float / 
            (SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id = %(recipe_id)s)::float as similarity
        FROM recipes r
        JOIN recipe_ingredients ri ON r.recipe_id = ri.recipe_id
        JOIN recipe_ingredients src ON ri.ingredient_id = src.ingredient_id
        WHERE r.recipe_id != %(recipe_id)s
        GROUP BY r.recipe_id, r.recipe_title
    )
    SELECT recipe_id, recipe_title as title, ingredient_match_count, similarity
    FROM similarity_scores
    WHERE similarity > 0.2
    ORDER BY similarity DESC, ingredient_match_count DESC
    LIMIT %(limit)s
    """
    
    return execute_query(query, {"recipe_id": recipe_id, "limit": limit})

def get_cuisine_recommendations(cuisine_name, limit=10):
    """Get recipe recommendations for a specific cuisine or region."""
    query = """
    SELECT r.recipe_id, r.recipe_title as title
    FROM recipes r
    WHERE r.region = %(cuisine_name)s OR r.sub_region = %(cuisine_name)s
    ORDER BY r.recipe_id
    LIMIT %(limit)s
    """
    
    return execute_query(query, {"cuisine_name": cuisine_name, "limit": limit})

def get_dietary_recommendations(dietary_restriction, limit=10):
    """Get recipe recommendations based on dietary restrictions."""
    query = """
    SELECT r.recipe_id, r.recipe_title as title
    FROM recipes r
    JOIN recipe_diet_attributes rda ON r.recipe_id = rda.recipe_id
    WHERE 
        CASE 
            WHEN %(dietary)s = 'vegan' THEN rda.vegan = TRUE
            WHEN %(dietary)s = 'pescetarian' THEN rda.pescetarian = TRUE
            WHEN %(dietary)s = 'lacto_vegetarian' THEN rda.lacto_vegetarian = TRUE
            ELSE FALSE
        END
    ORDER BY r.recipe_id
    LIMIT %(limit)s
    """
    
    return execute_query(query, {"dietary": dietary_restriction, "limit": limit})