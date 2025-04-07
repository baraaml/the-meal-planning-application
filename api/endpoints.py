"""
Meal Recommendation Service API endpoints.
All API routes are defined here in a modular, organized manner.
"""
from fastapi import APIRouter, Depends, Query, HTTPException, status, Request
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import List, Dict, Any, Optional
import time

from config import CONTENT_TYPES, DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS
from data.database import get_db
from services.hybrid import HybridRecommender
from services.item_based import ItemBasedRecommender
from services.collaborative import CollaborativeRecommender
from services.content_based import ContentBasedRecommender
from services.popularity import PopularityRecommender

# Request & Response Models
class InteractionCreate(BaseModel):
    """Request model for creating a meal interaction record."""
    user_id: str
    meal_id: str
    content_type: str
    interaction_type: str

class RecommendationItem(BaseModel):
    """Model for a recommendation item in responses."""
    id: str
    content_type: str
    title: str
    score: Optional[float] = None
    
class RecommendationResponse(BaseModel):
    """Standard response model for recommendation endpoints."""
    items: List[RecommendationItem]
    count: int
    execution_time_ms: float

# API Routers
router = APIRouter()

# Utility functions
def validate_content_type(content_type: str, allow_all: bool = False) -> str:
    """Validate the content type parameter."""
    valid_types = CONTENT_TYPES.copy()
    if allow_all:
        valid_types.append('all')
        
    if content_type not in valid_types:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Content type must be one of: {', '.join(valid_types)}"
        )
    
    return content_type

# Request timing middleware
@router.middleware("http")
async def add_process_time_header(request: Request, call_next):
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    
    # If it's a JSON response, we add the execution time to the response
    if isinstance(response, JSONResponse):
        content = response.body.decode()
        import json
        try:
            data = json.loads(content)
            if isinstance(data, dict):
                data["execution_time_ms"] = round(process_time * 1000, 2)
                response.body = json.dumps(data).encode()
        except:
            pass
    
    return response

# Endpoints
@router.get("/", tags=["status"])
def read_root():
    """Root endpoint for API status check."""
    return {"status": "Meal recommendation service is running"}

@router.get("/recommend/user/{user_id}", response_model=RecommendationResponse, tags=["recommendations"])
def get_user_recommendations(
    user_id: str,
    content_type: Optional[str] = Query(None, description="Filter by content type (meal, recipe)"),
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of recommendations"),
    recommendation_type: str = Query("hybrid", description="Recommendation algorithm to use (hybrid, item-based, user-based)"),
    cuisine: Optional[str] = Query(None, description="Filter by cuisine type"),
    dietary_restriction: Optional[str] = Query(None, description="Filter by dietary restriction"),
    db = Depends(get_db)
):
    """
    Get personalized meal recommendations for a user.
    
    Uses a hybrid approach with multiple recommendation strategies:
    1. Collaborative filtering based on similar users
    2. Content-based recommendations using recent interactions
    3. Popularity-based recommendations as fallback
    
    Parameters:
    - user_id: The ID of the user
    - content_type: Optional filter by content type ('meal', 'recipe')
    - limit: Maximum number of recommendations to return
    - recommendation_type: Algorithm to use (hybrid, item-based, user-based)
    - cuisine: Optional filter by cuisine type
    - dietary_restriction: Optional filter by dietary restriction
    
    Returns:
    - List of recommended meals
    """
    start_time = time.time()
    
    # Validate content type if provided
    if content_type:
        content_type = validate_content_type(content_type)
    
    # Choose the recommendation strategy based on the type parameter
    if recommendation_type == "hybrid":
        recommender = HybridRecommender()
    elif recommendation_type == "item-based":
        recommender = ItemBasedRecommender()
    elif recommendation_type == "user-based":
        recommender = CollaborativeRecommender()
    else:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid recommendation type. Must be 'hybrid', 'item-based', or 'user-based'."
        )
    
    # Get recommendations
    recommended_items = recommender.get_recommendations(
        user_id=user_id,
        content_type=content_type,
        limit=limit,
        cuisine=cuisine,
        dietary_restriction=dietary_restriction
    )
    
    # Format response
    items = []
    for item in recommended_items:
        items.append(RecommendationItem(
            id=item["id"],
            content_type=item["content_type"],
            title=item.get("title", ""),
            score=item.get("score")
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "items": items,
        "count": len(items),
        "execution_time_ms": round(execution_time, 2)
    }

@router.get("/recommend/similar/{content_type}/{meal_id}", response_model=RecommendationResponse, tags=["recommendations"])
def get_similar_meals(
    content_type: str,
    meal_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of similar meals"),
    similarity_method: str = Query("content", description="Method to determine similarity (content, interaction, ingredient)"),
    db = Depends(get_db)
):
    """
    Get meals similar to the specified item.
    
    Supports three similarity methods:
    - content: Uses vector embeddings to find similar meals based on textual features
    - interaction: Uses co-occurrence patterns to find meals that users interact with together
    - ingredient: Uses common ingredients to find similar meals
    
    Parameters:
    - content_type: Type of content ('meal' or 'recipe')
    - meal_id: ID of the meal
    - limit: Maximum number of similar meals to return
    - similarity_method: Method to determine similarity (content, interaction, ingredient)
    
    Returns:
    - List of similar meals with similarity scores
    """
    start_time = time.time()
    
    # Validate content type
    content_type = validate_content_type(content_type)
    
    # Choose similarity method
    if similarity_method == "content":
        # Content-based similarity using vector embeddings
        recommender = ContentBasedRecommender()
    elif similarity_method == "interaction":
        # Item-based collaborative filtering using interaction patterns
        recommender = ItemBasedRecommender()
    elif similarity_method == "ingredient":
        # Ingredient-based similarity
        recommender = ContentBasedRecommender()
        similar_items = recommender.get_similar_by_ingredients(
            meal_id=meal_id,
            content_type=content_type,
            limit=limit
        )
        
        # Format the items and return
        items = []
        for item in similar_items:
            items.append(RecommendationItem(
                id=item["id"],
                content_type=item.get("content_type", content_type),
                title=item.get("title", ""),
                score=item.get("similarity") or item.get("score")
            ))
        
        execution_time = (time.time() - start_time) * 1000
        
        return {
            "items": items,
            "count": len(items),
            "execution_time_ms": round(execution_time, 2)
        }
    else:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid similarity method. Must be 'content', 'interaction', or 'ingredient'."
        )
    
    # Get similar meals
    similar_items = recommender.get_recommendations(
        meal_id=meal_id,
        content_type=content_type,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in similar_items:
        items.append(RecommendationItem(
            id=item["id"],
            content_type=item["content_type"],
            title=item.get("title", ""),
            score=item.get("similarity") or item.get("score")
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "items": items,
        "count": len(items),
        "execution_time_ms": round(execution_time, 2)
    }

@router.get("/trending/{content_type}", response_model=RecommendationResponse, tags=["recommendations"])
def get_trending_meals(
    content_type: str,
    time_window: str = Query("day", description="Time window for trending meals"),
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db = Depends(get_db)
):
    """
    Get trending meals based on recent interactions.
    
    Parameters:
    - content_type: Type of content ('meal', 'recipe', or 'all')
    - time_window: Time window to consider ('day', 'week', 'month')
    - limit: Maximum number of meals to return
    
    Returns:
    - List of trending meals with popularity scores
    """
    start_time = time.time()
    
    # Validate content type (allowing 'all' as a valid option)
    content_type = validate_content_type(content_type, allow_all=True)
    
    # Validate time window
    if time_window not in ALLOWED_TRENDING_WINDOWS:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Time window must be one of: {', '.join(ALLOWED_TRENDING_WINDOWS)}"
        )
    
    # Get trending meals
    recommender = PopularityRecommender()
    trending_items = recommender.get_recommendations(
        content_type=content_type,
        time_window=time_window,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in trending_items:
        items.append(RecommendationItem(
            id=item["id"],
            content_type=item["content_type"],
            title=item.get("title", ""),
            score=item.get("popularity")
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "items": items,
        "count": len(items),
        "execution_time_ms": round(execution_time, 2)
    }

@router.get("/recommend/cuisine/{cuisine_id}", response_model=RecommendationResponse, tags=["recommendations"])
def get_cuisine_recommendations(
    cuisine_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db = Depends(get_db)
):
    """
    Get meal recommendations based on cuisine.
    
    Parameters:
    - cuisine_id: ID of the cuisine
    - limit: Maximum number of meals to return
    
    Returns:
    - List of meals in the specified cuisine
    """
    start_time = time.time()
    
    recommender = PopularityRecommender()
    cuisine_items = recommender.get_cuisine_recommendations(
        cuisine_id=cuisine_id,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in cuisine_items:
        items.append(RecommendationItem(
            id=item["id"],
            content_type=item.get("content_type", "meal"),
            title=item.get("title", ""),
            score=None
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "items": items,
        "count": len(items),
        "execution_time_ms": round(execution_time, 2)
    }

@router.get("/recommend/dietary/{dietary_restriction_id}", response_model=RecommendationResponse, tags=["recommendations"])
def get_dietary_recommendations(
    dietary_restriction_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db = Depends(get_db)
):
    """
    Get meal recommendations based on dietary restrictions.
    
    Parameters:
    - dietary_restriction_id: ID of the dietary restriction
    - limit: Maximum number of meals to return
    
    Returns:
    - List of meals that match the dietary restriction
    """
    start_time = time.time()
    
    recommender = PopularityRecommender()
    dietary_items = recommender.get_dietary_recommendations(
        dietary_restriction_id=dietary_restriction_id,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in dietary_items:
        items.append(RecommendationItem(
            id=item["id"],
            content_type=item.get("content_type", "meal"),
            title=item.get("title", ""),
            score=None
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "items": items,
        "count": len(items),
        "execution_time_ms": round(execution_time, 2)
    }

@router.post("/interactions", tags=["interactions"])
def record_interaction(interaction: InteractionCreate, db = Depends(get_db)):
    """
    Record a user interaction with a meal.
    
    Parameters:
    - user_id: ID of the user
    - meal_id: ID of the meal
    - content_type: Type of content ('meal' or 'recipe')
    - interaction_type: Type of interaction ('view', 'like', 'save', 'cook')
    
    Returns:
    - Status confirmation
    """
    start_time = time.time()
    
    # Validate content type
    try:
        validate_content_type(interaction.content_type)
    except HTTPException as e:
        raise e
    
    from data.repositories import InteractionRepository
    
    # Record the interaction
    repository = InteractionRepository()
    success = repository.record_interaction(
        user_id=interaction.user_id,
        meal_id=interaction.meal_id,
        content_type=interaction.content_type,
        interaction_type=interaction.interaction_type
    )
    
    if not success:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to record interaction"
        )
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "status": "recorded",
        "execution_time_ms": round(execution_time, 2)
    }

@router.get("/user/{user_id}/history", tags=["interactions"])
def get_user_meal_history(
    user_id: str,
    content_type: Optional[str] = None,
    limit: int = 10,
    db = Depends(get_db)
):
    """
    Get a user's meal interaction history.
    
    Parameters:
    - user_id: ID of the user
    - content_type: Optional filter by content type ('meal' or 'recipe')
    - limit: Maximum number of history items to return
    
    Returns:
    - List of user's recent meal interactions
    """
    start_time = time.time()
    
    # Validate content type if provided
    if content_type:
        content_type = validate_content_type(content_type)
    
    from data.repositories import InteractionRepository
    
    repository = InteractionRepository()
    history = repository.get_user_recent_interactions(
        user_id=user_id,
        content_type=content_type,
        limit=limit
    )
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "history": history,
        "count": len(history),
        "execution_time_ms": round(execution_time, 2)
    }

@router.get("/user/{user_id}/dietary-preferences", tags=["user"])
def get_user_dietary_preferences(
    user_id: str,
    db = Depends(get_db)
):
    """
    Get a user's dietary preferences.
    
    Parameters:
    - user_id: ID of the user
    
    Returns:
    - List of user's dietary preferences
    """
    start_time = time.time()
    
    from data.repositories import InteractionRepository
    
    repository = InteractionRepository()
    preferences = repository.get_user_dietary_preferences(user_id)
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "dietary_preferences": preferences,
        "count": len(preferences),
        "execution_time_ms": round(execution_time, 2)
    }

@router.post("/user/{user_id}/dietary-preferences", tags=["user"])
def set_user_dietary_preference(
    user_id: str,
    dietary_restriction_id: int,
    db = Depends(get_db)
):
    """
    Add a dietary preference for a user.
    
    Parameters:
    - user_id: ID of the user
    - dietary_restriction_id: ID of the dietary restriction
    
    Returns:
    - Status confirmation
    """
    start_time = time.time()
    
    from data.repositories import InteractionRepository
    
    repository = InteractionRepository()
    success = repository.add_user_dietary_preference(
        user_id=user_id,
        dietary_restriction_id=dietary_restriction_id
    )
    
    if not success:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to add dietary preference"
        )
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "status": "added",
        "execution_time_ms": round(execution_time, 2)
    }