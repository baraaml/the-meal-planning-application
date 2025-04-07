"""
Trending meals and cuisine-based recommendation API routes.
Endpoints for trending meals and cuisine-based recommendations.
"""
from fastapi import APIRouter, Depends, Query, HTTPException, status
from typing import Optional

from base import get_db, validate_content_type
from services.popularity_recommender import PopularityRecommender
from config.settings import DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS

router = APIRouter(tags=["recommendations"])

@router.get("/trending/{content_type}")
def get_trending_meals(
    content_type: str,
    time_window: str = Query("day", description="Time window for trending meals"),
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db=Depends(get_db)
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
    
    return recommender.format_recommendations(trending_items)

@router.get("/recommend/cuisine/{cuisine_id}")
def get_cuisine_recommendations(
    cuisine_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db=Depends(get_db)
):
    """
    Get meal recommendations based on cuisine.
    
    Parameters:
    - cuisine_id: ID of the cuisine
    - limit: Maximum number of meals to return
    
    Returns:
    - List of meals in the specified cuisine
    """
    recommender = PopularityRecommender()
    cuisine_items = recommender.get_cuisine_recommendations(
        cuisine_id=cuisine_id,
        limit=limit
    )
    
    return recommender.format_recommendations(cuisine_items)

@router.get("/recommend/dietary/{dietary_restriction_id}")
def get_dietary_recommendations(
    dietary_restriction_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db=Depends(get_db)
):
    """
    Get meal recommendations based on dietary restrictions.
    
    Parameters:
    - dietary_restriction_id: ID of the dietary restriction
    - limit: Maximum number of meals to return
    
    Returns:
    - List of meals that match the dietary restriction
    """
    recommender = PopularityRecommender()
    dietary_items = recommender.get_dietary_recommendations(
        dietary_restriction_id=dietary_restriction_id,
        limit=limit
    )
    
    return recommender.format_recommendations(dietary_items)
"""