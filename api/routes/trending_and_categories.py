"""
Trending and category recommendation API routes.
Endpoints for trending content and category-based recommendations.
"""
from fastapi import APIRouter, Depends, Query, HTTPException, status
from typing import Optional

from api.base import get_db, validate_content_type
from services.popularity_recommender import PopularityRecommender
from config.settings import DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS

router = APIRouter(tags=["recommendations"])

@router.get("/trending/{content_type}")
def get_trending_content(
    content_type: str,
    time_window: str = Query("day", description="Time window for trending content"),
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db=Depends(get_db)
):
    """
    Get trending content based on recent interactions.
    
    Parameters:
    - content_type: Type of content ('post', 'community', or 'all')
    - time_window: Time window to consider ('day', 'week', 'month')
    - limit: Maximum number of items to return
    
    Returns:
    - List of trending content items with popularity scores
    """
    # Validate content type (allowing 'all' as a valid option)
    content_type = validate_content_type(content_type, allow_all=True)
    
    # Validate time window
    if time_window not in ALLOWED_TRENDING_WINDOWS:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Time window must be one of: {', '.join(ALLOWED_TRENDING_WINDOWS)}"
        )
    
    # Get trending content
    recommender = PopularityRecommender()
    trending_items = recommender.get_recommendations(
        content_type=content_type,
        time_window=time_window,
        limit=limit
    )
    
    return recommender.format_recommendations(trending_items)

@router.get("/recommend/category/{category_id}")
def get_category_recommendations(
    category_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db=Depends(get_db)
):
    """
    Get community recommendations based on category.
    
    Parameters:
    - category_id: ID of the category
    - limit: Maximum number of items to return
    
    Returns:
    - List of communities in the specified category
    """
    recommender = PopularityRecommender()
    category_items = recommender.get_category_recommendations(
        category_id=category_id,
        limit=limit
    )
    
    return recommender.format_recommendations(category_items)