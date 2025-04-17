"""
API endpoints for recommendation features.
"""
import time
import logging
from fastapi import APIRouter, HTTPException, Query, Depends
from typing import Dict, List, Optional, Any

from config.config import DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS
from models.models import RecommendationResponse, RecommendationItem, InteractionCreate
from recommenders.recommender_factory import get_recommender
from models.queries_recommend import record_interaction

logger = logging.getLogger(__name__)
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
    
    # Get the appropriate recommender
    recommender = get_recommender(recommendation_type)
    
    # Get recommendations
    recommended_items = recommender.get_recommendations(
        user_id=user_id,
        content_type=content_type,
        limit=limit,
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
    
    # Get content-based recommender
    recommender = get_recommender("content")
    
    similar_items = recommender.get_similar_recommendations(
        recipe_id=recipe_id,
        similarity_method=similarity_method,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in similar_items:
        items.append(RecommendationItem(
            id=str(item["id"]),
            content_type="recipe",
            title=item["title"],
            score=item.get("score", 0)
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
    
    # Validate recipe_id
    recipe_id = interaction.meal_id
    if not recipe_id:
        raise HTTPException(
            status_code=400,
            detail="Missing recipe_id parameter"
        )
    
    success = record_interaction(
        user_id=interaction.user_id,
        recipe_id=recipe_id,
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

@router.get("/recommend/cuisine/{cuisine_id}", response_model=RecommendationResponse)
def get_cuisine_recommendations_endpoint(
    cuisine_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items")
):
    """Get recipe recommendations for a specific cuisine or region."""
    start_time = time.time()
    
    # Get content-based recommender
    recommender = get_recommender("content")
    
    cuisine_items = recommender.get_cuisine_recommendations(
        cuisine_name=cuisine_id, 
        limit=limit
    )
    
    # Format the items
    items = []
    for item in cuisine_items:
        items.append(RecommendationItem(
            id=str(item["id"]),
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
    
    # Get content-based recommender
    recommender = get_recommender("content")
    
    dietary_items = recommender.get_dietary_recommendations(
        dietary_restriction=dietary_restriction,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in dietary_items:
        items.append(RecommendationItem(
            id=str(item["id"]),
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

@router.get("/recommend/quick", response_model=RecommendationResponse)
def get_quick_recipes(
    max_time: int = Query(30, description="Maximum total preparation time in minutes"),
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    cuisine: Optional[str] = None,
    dietary_restriction: Optional[str] = None
):
    """Get quick recipe recommendations based on preparation time."""
    start_time = time.time()
    
    # Get content-based recommender
    recommender = get_recommender("content")
    
    quick_items = recommender.get_quick_recommendations(
        max_time=max_time,
        limit=limit,
        cuisine=cuisine,
        dietary_restriction=dietary_restriction
    )
    
    # Format the items
    items = []
    for item in quick_items:
        items.append(RecommendationItem(
            id=str(item["id"]),
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
    
    # Get popularity-based recommender
    recommender = get_recommender("popularity")
    
    trending_items = recommender.get_trending_recommendations(
        time_window=time_window,
        limit=limit,
        cuisine=cuisine,
        dietary_restriction=dietary_restriction
    )
    
    # Format the items
    items = []
    for item in trending_items:
        items.append(RecommendationItem(
            id=str(item["id"]),
            content_type="recipe",
            title=item["title"],
            score=item.get("score", 0.5)
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return RecommendationResponse(
        items=items,
        count=len(items),
        execution_time_ms=round(execution_time, 2)
    )