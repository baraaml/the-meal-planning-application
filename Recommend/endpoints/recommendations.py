"""
API endpoints for recommendation features.
"""
import time
from fastapi import APIRouter, HTTPException, Query, Depends
from typing import Dict, List, Optional

from config.config import DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS
from models.models import RecommendationResponse, RecommendationItem, InteractionCreate
from models.queries_recommend import (
    get_trending_recipes, record_interaction, get_user_recent_interactions,
    find_similar_users, get_content_from_similar_users
)
from models.queries_search import (
    find_similar_content, get_similar_by_ingredients, 
    get_cuisine_recommendations, get_dietary_recommendations
)
from recommenders.hybrid import HybridRecommender

router = APIRouter(prefix="/api/v1", tags=["recommendations"])

@router.get("/recommend/user/{user_id}", response_model=RecommendationResponse)
def get_user_recommendations(
    user_id: str,
    content_type: Optional[str] = None,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    recommendation_type: str = Query("hybrid", description="Type of recommendation algorithm to use"),
    cuisine: Optional[str] = None,
    dietary_restriction: Optional[str] = None
):
    """Get personalized recommendations for a user."""
    start_time = time.time()
    
    # Use HybridRecommender from recommenders module
    recommender = HybridRecommender()
    recommended_items = recommender.get_recommendations(
        user_id=user_id,
        content_type=content_type,
        limit=limit,
        recommendation_type=recommendation_type,
        cuisine=cuisine,
        dietary_restriction=dietary_restriction
    )
    
    # Format the items
    items = []
    for item in recommended_items:
        items.append(RecommendationItem(
            id=str(item["id"]),
            content_type=item.get("content_type", "recipe"),
            title=item.get("title", ""),
            score=item.get("score")
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return RecommendationResponse(
        items=items,
        count=len(items),
        execution_time_ms=round(execution_time, 2)
    )

@router.get("/recommend/similar/{recipe_id}", response_model=RecommendationResponse)
def get_similar_recommendations(
    recipe_id: int,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    similarity_method: str = Query("content", description="Method to determine similarity")
):
    """Get recommendations similar to a specific item."""
    start_time = time.time()
    
    # Choose similarity method
    if similarity_method == "ingredient":
        similar_items = get_similar_by_ingredients(recipe_id, limit)
    else:  # Default to content-based
        # First get the embedding of the recipe
        from models.queries_recipe import get_recipe
        recipe = get_recipe(recipe_id)
        if not recipe:
            raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
        
        from embedding.embeddings import EmbeddingGenerator
        generator = EmbeddingGenerator()
        embedding = generator.generate_recipe_embedding(recipe)
        
        if not embedding:
            raise HTTPException(status_code=500, detail="Failed to generate embedding")
        
        similar_items = find_similar_content(embedding, exclude_ids=[recipe_id], limit=limit)
    
    # Format the items
    items = []
    for item in similar_items:
        items.append(RecommendationItem(
            id=str(item["recipe_id"]),
            content_type="recipe",
            title=item["title"] if "title" in item else item.get("recipe_title", ""),
            score=item.get("similarity", 0)
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return RecommendationResponse(
        items=items,
        count=len(items),
        execution_time_ms=round(execution_time, 2)
    )

@router.get("/trending", response_model=RecommendationResponse)
def get_trending_content(
    time_window: str = Query("day", description="Time window for trending items"),
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    cuisine: Optional[str] = None,
    dietary_restriction: Optional[str] = None
):
    """Get trending recipes based on recent interactions."""
    start_time = time.time()
    
    if time_window not in ALLOWED_TRENDING_WINDOWS:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid time window. Allowed values are: {', '.join(ALLOWED_TRENDING_WINDOWS)}"
        )
    
    trending_items = get_trending_recipes(
        time_window=time_window,
        limit=limit,
        cuisine=cuisine,
        dietary_restriction=dietary_restriction
    )
    
    # Format the items
    items = []
    for item in trending_items:
        items.append(RecommendationItem(
            id=str(item["recipe_id"]),
            content_type="recipe",
            title=item["title"],
            score=item.get("popularity", 0) / 100 if item.get("popularity") else 0.5
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return RecommendationResponse(
        items=items,
        count=len(items),
        execution_time_ms=round(execution_time, 2)
    )

@router.get("/recommend/cuisine/{cuisine_id}", response_model=RecommendationResponse)
def get_cuisine_recommendations_endpoint(
    cuisine_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items")
):
    """Get recipe recommendations for a specific cuisine or region."""
    start_time = time.time()
    
    cuisine_items = get_cuisine_recommendations(cuisine_name=cuisine_id, limit=limit)
    
    # Format the items
    items = []
    for item in cuisine_items:
        items.append(RecommendationItem(
            id=str(item["recipe_id"]),
            content_type="recipe",
            title=item["title"],
            score=None
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return RecommendationResponse(
        items=items,
        count=len(items),
        execution_time_ms=round(execution_time, 2)
    )

@router.get("/recommend/dietary/{dietary_restriction}", response_model=RecommendationResponse)
def get_dietary_recommendations_endpoint(
    dietary_restriction: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items")
):
    """Get recipe recommendations based on dietary restrictions."""
    start_time = time.time()
    
    dietary_items = get_dietary_recommendations(
        dietary_restriction=dietary_restriction,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in dietary_items:
        items.append(RecommendationItem(
            id=str(item["recipe_id"]),
            content_type="recipe",
            title=item["title"],
            score=None
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return RecommendationResponse(
        items=items,
        count=len(items),
        execution_time_ms=round(execution_time, 2)
    )

@router.post("/interactions")
def create_interaction_endpoint(interaction: InteractionCreate):
    """Record a user interaction with a recipe."""
    start_time = time.time()
    
    success = record_interaction(
        user_id=interaction.user_id,
        recipe_id=interaction.meal_id,
        interaction_type=interaction.interaction_type,
        rating=interaction.rating if interaction.interaction_type == 'rating' else None
    )
    
    execution_time = (time.time() - start_time) * 1000
    
    if success:
        return {
            "status": "recorded",
            "execution_time_ms": round(execution_time, 2)
        }
    else:
        raise HTTPException(
            status_code=500,
            detail="Failed to record interaction"
        )