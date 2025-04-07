"""
Similar meal API routes.
Endpoints for finding meals similar to a given item.
"""
from fastapi import APIRouter, Depends, Query, HTTPException, status
from typing import Optional

from base import get_db, validate_content_type
from services.content_based_recommender import ContentBasedRecommender
from services.item_based_recommender import ItemBasedRecommender
from config.settings import DEFAULT_RECOMMENDATION_LIMIT

router = APIRouter(prefix="/recommend", tags=["recommendations"])

@router.get("/similar/{content_type}/{meal_id}")
def get_similar_meals(
    content_type: str,
    meal_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of similar meals"),
    similarity_method: str = Query("content", description="Method to determine similarity (content, interaction, ingredient)"),
    db=Depends(get_db)
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
        return recommender.get_similar_by_ingredients(
            meal_id=meal_id,
            content_type=content_type,
            limit=limit
        )
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
    
    return recommender.format_recommendations(similar_items)