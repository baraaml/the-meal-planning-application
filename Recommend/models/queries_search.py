"""
Vector-based search queries using recipe embeddings.
"""
import logging
from typing import List, Dict, Any, Optional, Union
import numpy as np
from config.db import execute_query, execute_query_single
from config.config import MIN_SIMILARITY_SCORE

logger = logging.getLogger(__name__)

def find_similar_content(embedding: List[float], exclude_ids: List[int] = None, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Find recipes similar to the provided embedding vector.
    
    Args:
        embedding: Embedding vector to compare against
        exclude_ids: Optional list of recipe IDs to exclude from results
        limit: Maximum number of results to return
        
    Returns:
        List of similar recipes with similarity scores
    """
    try:
        # Format embedding for PostgreSQL vector comparison
        if not isinstance(embedding, list):
            embedding = list(embedding)
        
        pg_vector = f"[{', '.join(map(str, embedding))}]"
        
        # Build the query with exclusion if needed
        exclude_clause = ""
        params = {"embedding": pg_vector, "limit": limit, "min_score": MIN_SIMILARITY_SCORE}
        
        if exclude_ids and len(exclude_ids) > 0:
            exclude_placeholders = [f"%(exclude{i})s" for i in range(len(exclude_ids))]
            exclude_clause = f"AND r.recipe_id NOT IN ({','.join(exclude_placeholders)})"
            
            for i, recipe_id in enumerate(exclude_ids):
                params[f"exclude{i}"] = recipe_id
        
        # Query using cosine similarity with the pgvector extension
        query = f"""
        SELECT 
            r.recipe_id as id,
            r.recipe_title as title,
            1 - (re.embedding <=> %(embedding)s::vector) as score
        FROM recipe_embeddings re
        JOIN recipes r ON re.recipe_id = r.recipe_id
        WHERE 1 - (re.embedding <=> %(embedding)s::vector) >= %(min_score)s
        {exclude_clause}
        ORDER BY score DESC
        LIMIT %(limit)s
        """
        
        results = execute_query(query, params)
        
        # Return the results with scores
        return results
        
    except Exception as e:
        logger.error(f"Error finding similar content: {e}")
        return []

def search_by_text_embedding(query_text: str, limit: int = 10, filters: Dict[str, Any] = None) -> List[Dict[str, Any]]:
    """
    Search recipes by generating an embedding from query text.
    
    Args:
        query_text: Text to search for
        limit: Maximum number of results
        filters: Optional filters to apply to the search
        
    Returns:
        List of similar recipes
    """
    try:
        # Import here to avoid circular import
        from embedding.embeddings import EmbeddingGenerator
        
        # Generate embedding for the query text
        generator = EmbeddingGenerator()
        mock_recipe = {
            "recipe_title": query_text,
            "instructions": query_text,
            "ingredients": []
        }
        embedding = generator.generate_recipe_embedding(mock_recipe)
        
        if not embedding:
            logger.error(f"Failed to generate embedding for query: {query_text}")
            return []
        
        # Build filter clauses
        filter_clauses = []
        params = {"limit": limit}
        
        if filters:
            if "cuisine" in filters and filters["cuisine"]:
                filter_clauses.append("(r.region = %(cuisine)s OR r.sub_region = %(cuisine)s)")
                params["cuisine"] = filters["cuisine"]
                
            if "dietary" in filters and filters["dietary"]:
                dietary = filters["dietary"].lower()
                if dietary == "vegan":
                    filter_clauses.append("EXISTS (SELECT 1 FROM recipe_diet_attributes rda WHERE rda.recipe_id = r.recipe_id AND rda.vegan = TRUE)")
                elif dietary == "vegetarian":
                    filter_clauses.append("EXISTS (SELECT 1 FROM recipe_diet_attributes rda WHERE rda.recipe_id = r.recipe_id AND rda.lacto_vegetarian = TRUE)")
                elif dietary == "pescetarian":
                    filter_clauses.append("EXISTS (SELECT 1 FROM recipe_diet_attributes rda WHERE rda.recipe_id = r.recipe_id AND rda.pescetarian = TRUE)")
            
            if "max_time" in filters and filters["max_time"]:
                filter_clauses.append("(r.total_time <= %(max_time)s OR (r.total_time IS NULL AND (r.prep_time + COALESCE(r.cook_time, 0)) <= %(max_time)s))")
                params["max_time"] = filters["max_time"]
                
            if "calories" in filters:
                if "min" in filters["calories"] and filters["calories"]["min"] is not None:
                    filter_clauses.append("r.calories >= %(min_calories)s")
                    params["min_calories"] = filters["calories"]["min"]
                if "max" in filters["calories"] and filters["calories"]["max"] is not None:
                    filter_clauses.append("r.calories <= %(max_calories)s")
                    params["max_calories"] = filters["calories"]["max"]
        
        # Convert embedding for PostgreSQL
        pg_vector = f"[{', '.join(map(str, embedding))}]"
        params["embedding"] = pg_vector
        params["min_score"] = MIN_SIMILARITY_SCORE
        
        # Build the WHERE clause
        where_clause = " AND ".join(filter_clauses) if filter_clauses else "1=1"
        
        # Query using cosine similarity
        query = f"""
        SELECT 
            r.recipe_id as id,
            r.recipe_title as title,
            r.region,
            r.sub_region,
            r.image_url,
            r.total_time,
            r.prep_time,
            r.cook_time,
            r.calories,
            1 - (re.embedding <=> %(embedding)s::vector) as score
        FROM recipe_embeddings re
        JOIN recipes r ON re.recipe_id = r.recipe_id
        WHERE {where_clause}
        AND 1 - (re.embedding <=> %(embedding)s::vector) >= %(min_score)s
        ORDER BY score DESC
        LIMIT %(limit)s
        """
        
        results = execute_query(query, params)
        
        # Return the results
        return results
    
    except Exception as e:
        logger.error(f"Error searching by text embedding: {e}")
        return []

def get_similar_by_ingredients(recipe_id: int, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Find recipes with similar ingredients.
    
    Args:
        recipe_id: ID of the reference recipe
        limit: Maximum number of results
        
    Returns:
        List of recipes with similar ingredients
    """
    try:
        query = """
        WITH recipe_ingredients AS (
            SELECT ingredient_id 
            FROM recipe_ingredients 
            WHERE recipe_id = %(recipe_id)s
        ),
        recipe_matches AS (
            SELECT 
                r.recipe_id as id,
                r.recipe_title as title,
                COUNT(ri.ingredient_id) as common_ingredients,
                (
                    SELECT COUNT(ingredient_id) 
                    FROM recipe_ingredients 
                    WHERE recipe_id = r.recipe_id
                ) as total_ingredients
            FROM recipes r
            JOIN recipe_ingredients ri ON r.recipe_id = ri.recipe_id
            WHERE ri.ingredient_id IN (SELECT ingredient_id FROM recipe_ingredients)
            AND r.recipe_id != %(recipe_id)s
            GROUP BY r.recipe_id, r.recipe_title
            HAVING COUNT(ri.ingredient_id) >= %(min_common)s
        )
        SELECT 
            id, 
            title, 
            common_ingredients, 
            total_ingredients,
            (common_ingredients::float / total_ingredients) as score
        FROM recipe_matches
        ORDER BY score DESC, common_ingredients DESC
        LIMIT %(limit)s
        """
        
        params = {
            "recipe_id": recipe_id,
            "min_common": MIN_COMMON_ITEMS,
            "limit": limit
        }
        
        results = execute_query(query, params)
        return results
        
    except Exception as e:
        logger.error(f"Error finding similar by ingredients: {e}")
        return []

def get_cuisine_recommendations(cuisine_name: str, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Get recipes for a specific cuisine or region.
    
    Args:
        cuisine_name: Name of cuisine or region
        limit: Maximum number of results
        
    Returns:
        List of cuisine-specific recipes
    """
    try:
        query = """
        SELECT 
            r.recipe_id as id,
            r.recipe_title as title
        FROM recipes r
        WHERE r.region = %(cuisine)s OR r.sub_region = %(cuisine)s
        ORDER BY RANDOM()
        LIMIT %(limit)s
        """
        
        params = {
            "cuisine": cuisine_name,
            "limit": limit
        }
        
        results = execute_query(query, params)
        return results
        
    except Exception as e:
        logger.error(f"Error getting cuisine recommendations: {e}")
        return []

def get_dietary_recommendations(dietary_restriction: str, limit: int = 10) -> List[Dict[str, Any]]:
    """
    Get recipes based on dietary restrictions.
    
    Args:
        dietary_restriction: Type of dietary restriction
        limit: Maximum number of results
        
    Returns:
        List of dietary-specific recipes
    """
    try:
        # Determine the dietary column
        column_name = "vegan"  # Default
        if dietary_restriction.lower() == "vegetarian":
            column_name = "lacto_vegetarian"
        elif dietary_restriction.lower() == "pescetarian":
            column_name = "pescetarian"
        
        query = f"""
        SELECT 
            r.recipe_id as id,
            r.recipe_title as title
        FROM recipes r
        JOIN recipe_diet_attributes rda ON r.recipe_id = rda.recipe_id
        WHERE rda.{column_name} = TRUE
        ORDER BY RANDOM()
        LIMIT %(limit)s
        """
        
        params = {
            "limit": limit
        }
        
        results = execute_query(query, params)
        return results
        
    except Exception as e:
        logger.error(f"Error getting dietary recommendations: {e}")
        return []

def get_quick_recipes(max_time: int = 30, limit: int = 10, cuisine: Optional[str] = None, 
                     dietary_restriction: Optional[str] = None) -> List[Dict[str, Any]]:
    """
    Get quick recipe recommendations based on preparation time.
    
    Args:
        max_time: Maximum preparation time in minutes
        limit: Maximum number of results
        cuisine: Optional cuisine filter
        dietary_restriction: Optional dietary restriction
        
    Returns:
        List of quick recipes
    """
    try:
        where_clauses = [
            "(r.total_time <= %(max_time)s OR (r.total_time IS NULL AND (r.prep_time + COALESCE(r.cook_time, 0)) <= %(max_time)s))"
        ]
        
        params = {
            "max_time": max_time,
            "limit": limit
        }
        
        # Add cuisine filter if provided
        if cuisine:
            where_clauses.append("(r.region = %(cuisine)s OR r.sub_region = %(cuisine)s)")
            params["cuisine"] = cuisine
        
        # Add dietary filter if provided
        join_clause = ""
        if dietary_restriction:
            join_clause = "JOIN recipe_diet_attributes rda ON r.recipe_id = rda.recipe_id"
            
            if dietary_restriction.lower() == "vegan":
                where_clauses.append("rda.vegan = TRUE")
            elif dietary_restriction.lower() == "vegetarian":
                where_clauses.append("rda.lacto_vegetarian = TRUE")
            elif dietary_restriction.lower() == "pescetarian":
                where_clauses.append("rda.pescetarian = TRUE")
        
        # Build the WHERE clause
        where_clause = " AND ".join(where_clauses)
        
        query = f"""
        SELECT 
            r.recipe_id as id,
            r.recipe_title as title,
            r.total_time,
            r.prep_time,
            r.cook_time
        FROM recipes r
        {join_clause}
        WHERE {where_clause}
        ORDER BY COALESCE(r.total_time, r.prep_time + COALESCE(r.cook_time, 0)) ASC
        LIMIT %(limit)s
        """
        
        results = execute_query(query, params)
        return results
        
    except Exception as e:
        logger.error(f"Error getting quick recipes: {e}")
        return []