"""
API endpoints for recipe management.
"""
from fastapi import APIRouter, HTTPException, Query, status
from typing import Dict, Optional, List

from models.models import RecipeCreate, RecipeUpdate, RecipeBase, RecipeDetail
from models.queries_recipe import (
    get_recipe, get_recipes, create_recipe, update_recipe, delete_recipe
)
from embedding.embeddings import EmbeddingGenerator
from config.db import execute_query  # Add this import

router = APIRouter(prefix="/api/v1", tags=["recipes"])

@router.get("/recipes/{recipe_id}", response_model=RecipeDetail)
def get_recipe_endpoint(recipe_id: int):
    """Get a recipe by ID."""
    recipe = get_recipe(recipe_id)
    if not recipe:
        raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
    return recipe


@router.get("/recipes", response_model=List[RecipeBase])
def get_recipes_endpoint(
    page: int = Query(1, ge=1),
    limit: int = Query(20, ge=1, le=100),
    region: Optional[str] = None,
    sub_region: Optional[str] = None,
    min_calories: Optional[int] = None,
    max_calories: Optional[int] = None
):
    """Get a list of recipes with optional filtering."""
    # Calculate offset based on page and limit
    offset = (page - 1) * limit
    
    # Prepare filters
    filters = {}
    if region:
        filters["region"] = region
    if sub_region:
        filters["sub_region"] = sub_region
        
    # Add calorie filters to the query parameters
    calories_filter = {}
    if min_calories is not None:
        calories_filter["min_calories"] = min_calories
    if max_calories is not None:
        calories_filter["max_calories"] = max_calories
    
    recipes = get_recipes(limit=limit, offset=offset, filters=filters, calories_filter=calories_filter)
    return recipes

@router.post("/recipes", response_model=RecipeDetail, status_code=status.HTTP_201_CREATED)
def create_recipe_endpoint(recipe: RecipeCreate):
    """Create a new recipe."""
    recipe_id = create_recipe(recipe.dict())
    if not recipe_id:
        raise HTTPException(status_code=500, detail="Failed to create recipe")
    
    # Generate embedding in background (could be moved to a background task)
    generator = EmbeddingGenerator()
    generator.update_recipe_embedding(recipe_id)
    
    return get_recipe(recipe_id)

@router.put("/recipes/{recipe_id}", response_model=RecipeDetail)
def update_recipe_endpoint(recipe_id: int, recipe: RecipeUpdate):
    """Update a recipe."""
    # Check if recipe exists
    existing_recipe = get_recipe(recipe_id)
    if not existing_recipe:
        raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
    
    # Update recipe
    success = update_recipe(recipe_id, recipe.dict(exclude_unset=True))
    if not success:
        raise HTTPException(status_code=500, detail="Failed to update recipe")
    
    # Update embedding if recipe content changed
    if recipe.recipe_title:
        generator = EmbeddingGenerator()
        generator.update_recipe_embedding(recipe_id)
    
    return get_recipe(recipe_id)

@router.delete("/recipes/{recipe_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_recipe_endpoint(recipe_id: int):
    """Delete a recipe."""
    # Check if recipe exists
    existing_recipe = get_recipe(recipe_id)
    if not existing_recipe:
        raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
    
    # Delete recipe
    success = delete_recipe(recipe_id)
    if not success:
        raise HTTPException(status_code=500, detail="Failed to delete recipe")
    
    return None


@router.get("/recipes/filter/calories", response_model=List[RecipeBase])
def filter_recipes_by_calories(
    min: float = Query(0, ge=0),
    max: float = Query(1000, ge=0),
    page: int = Query(1, ge=1),
    limit: int = Query(20, ge=1, le=100)
):
    """Filter recipes by calorie range."""
    # Calculate offset based on page and limit
    offset = (page - 1) * limit
    
    query = """
    SELECT 
        recipe_id, recipe_title, region, sub_region
    FROM recipes
    WHERE calories >= %(min)s AND calories <= %(max)s
    ORDER BY recipe_id
    LIMIT %(limit)s OFFSET %(offset)s
    """
    
    params = {
        "min": min,
        "max": max,
        "limit": limit,
        "offset": offset
    }
    
    return execute_query(query, params)

