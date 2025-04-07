"""
User meal recommendations API routes.
Endpoints for personalized meal recommendations.
"""
from fastapi import APIRouter, Depends, Query, HTTPException, status
from typing import Optional, List, Dict, Any

from base import get_db, validate_content_type
from services.hybrid_recommender import HybridRecommender
from services.item_based_recommender import ItemBasedRecommender
from services.collaborative_recommender import CollaborativeRecommender
from config.settings import DEFAULT_RECOMMENDATION_LIMIT, CONTENT_TYPES

router = APIRouter(prefix="/recommend", tags=["recommendations"])

@router.get("/user/{user_id}")
def get_user_recommendations(
    user_id: str,
    content_type: Optional[str] = Query(None, description="Filter by content type (meal, recipe)"),
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of recommendations"),
    recommendation_type: str = Query("hybrid", description="Recommendation algorithm to use (hybrid, item-based, user-based)"),
    cuisine: Optional[str] = Query(None, description="Filter by cuisine type"),
    dietary_restriction: Optional[str] = Query(None, description="Filter by dietary restriction"),
    db=Depends(get_db)
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
    
    return recommender.format_recommendations(recommended_items)