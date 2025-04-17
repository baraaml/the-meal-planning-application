"""
API endpoints for vector-based recipe search.
"""
import time
import logging
from fastapi import APIRouter, HTTPException, Query, Depends
from typing import Dict, List, Optional, Any

from models.models import RecipeDetail, RecipeBase
from models.queries_search import (
    search_by_text_embedding, 
    find_similar_content,
    get_similar_by_ingredients
)
from embedding.embeddings import EmbeddingGenerator

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1", tags=["search"])

@router.get("/search/vector")
def vector_search_endpoint(
    query: str,
    limit: int = Query(20, ge=1, le=100),
    cuisine: Optional[str] = None,
    dietary: Optional[str] = None,
    max_time: Optional[int] = None,
    min_calories: Optional[float] = None,
    max_calories: Optional[float] = None
):
    """
    Search recipes using embedding vectors generated from the query text.
    
    Args:
        query: Search query text
        limit: Maximum number of results
        cuisine: Optional cuisine filter
        dietary: Optional dietary restriction
        max_time: Maximum recipe time in minutes
        min_calories: Minimum calories
        max_calories: Maximum calories
        
    Returns:
        List of matching recipes with similarity scores
    """
    start_time = time.time()
    
    # Prepare filters
    filters = {}
    if cuisine:
        filters["cuisine"] = cuisine
    if dietary:
        filters["dietary"] = dietary
    if max_time:
        filters["max_time"] = max_time
    
    # Add calorie filters
    if min_calories is not None or max_calories is not None:
        filters["calories"] = {}
        if min_calories is not None:
            filters["calories"]["min"] = min_calories
        if max_calories is not None:
            filters["calories"]["max"] = max_calories
    
    # Perform vector search
    results = search_by_text_embedding(
        query_text=query,
        limit=limit,
        filters=filters
    )
    
    # Calculate execution time
    execution_time = time.time() - start_time
    
    return {
        "query": query,
        "results": results,
        "count": len(results),
        "filters": filters,
        "execution_time_ms": round(execution_time * 1000, 2)
    }

@router.get("/search/similar/{recipe_id}")
def similar_recipes_endpoint(
    recipe_id: int,
    method: str = Query("embedding", description="Similarity method: 'embedding' or 'ingredients'"),
    limit: int = Query(10, ge=1, le=50)
):
    """
    Find recipes similar to a specific recipe.
    
    Args:
        recipe_id: ID of the reference recipe
        method: Method to determine similarity ('embedding' or 'ingredients')
        limit: Maximum number of results
        
    Returns:
        List of similar recipes
    """
    start_time = time.time()
    
    if method == "ingredients":
        # Use ingredient-based similarity
        similar_recipes = get_similar_by_ingredients(recipe_id, limit)
    else:
        # Use embedding-based similarity
        # Get recipe embedding
        from models.queries_recipe import get_recipe
        recipe = get_recipe(recipe_id)
        if not recipe:
            raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
        
        # Get embedding from database or generate it
        query = """
        SELECT embedding FROM recipe_embeddings WHERE recipe_id = %(recipe_id)s
        """
        from config.db import execute_query_single
        embedding_result = execute_query_single(query, {"recipe_id": recipe_id})
        
        if embedding_result and "embedding" in embedding_result:
            # Use existing embedding
            embedding = embedding_result["embedding"]
        else:
            # Generate new embedding
            generator = EmbeddingGenerator()
            embedding = generator.generate_recipe_embedding(recipe)
            
            if not embedding:
                raise HTTPException(status_code=500, detail="Failed to generate embedding for the recipe")
        
        # Find similar recipes
        similar_recipes = find_similar_content(embedding, exclude_ids=[recipe_id], limit=limit)
    
    # Calculate execution time
    execution_time = time.time() - start_time
    
    return {
        "recipe_id": recipe_id,
        "method": method,
        "results": similar_recipes,
        "count": len(similar_recipes),
        "execution_time_ms": round(execution_time * 1000, 2)
    }