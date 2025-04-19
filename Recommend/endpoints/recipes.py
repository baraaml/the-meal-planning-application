"""
API endpoints for recipe management.
"""
from fastapi import APIRouter, HTTPException, Query, status
from typing import Dict, Optional, List, Any
import time
import math

from models.models import RecipeCreate, RecipeUpdate, RecipeBase, RecipeDetail
from models.queries_recipe import (
    get_recipe, get_recipes, create_recipe, update_recipe, delete_recipe,
    filter_recipes_by_calories, search_recipes
)
from embedding.embeddings import EmbeddingGenerator

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
    offset: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    region: Optional[str] = None,
    sub_region: Optional[str] = None,
    min_calories: Optional[int] = None,
    max_calories: Optional[int] = None
    ):
    """Get a list of recipes with optional filtering."""
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
    recipe_id = create_recipe(recipe.model_dump())
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
    success = update_recipe(recipe_id, recipe.model_dump(exclude_unset=True))
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
def filter_recipes_by_calories_endpoint(
    min: float = Query(0, ge=0),
    max: float = Query(1000, ge=0),
    offset: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100)
    ):
    """Filter recipes by calorie range."""
    return filter_recipes_by_calories(min, max, limit, offset)

@router.get("/recipes/search", response_model=Dict[str, Any])
def search_recipes_endpoint(
    query: Optional[str] = None,
    cuisines: Optional[str] = None,
    dietary: Optional[str] = None,
    include_ingredients: Optional[str] = None,
    exclude_ingredients: Optional[str] = None,
    min_calories: Optional[float] = None,
    max_calories: Optional[float] = None,
    max_prep_time: Optional[int] = None,
    max_cook_time: Optional[int] = None,
    max_total_time: Optional[int] = None,
    sort_by: str = "relevance",
    sort_order: str = "desc",
    offset: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100)
):
    """
    Advanced search for recipes with multiple criteria.
    """
    start_time = time.time()
    
    # Search recipes
    results = search_recipes(
        query=query,
        cuisines=cuisines,
        dietary=dietary,
        include_ingredients=include_ingredients,
        exclude_ingredients=exclude_ingredients,
        min_calories=min_calories,
        max_calories=max_calories,
        max_prep_time=max_prep_time,
        max_cook_time=max_cook_time,
        max_total_time=max_total_time,
        sort_by=sort_by,
        sort_order=sort_order,
        limit=limit,
        offset=offset
    )
    
    # Add execution time
    execution_time = (time.time() - start_time) * 1000
    results["execution_time_ms"] = round(execution_time, 2)
    
    return results