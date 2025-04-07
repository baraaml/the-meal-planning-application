"""
Similar content API routes.
Endpoints for finding content similar to a given item.
"""
from fastapi import APIRouter, Depends, Query, HTTPException, status
from typing import Optional

from api.base import get_db, validate_content_type
from services.content_based_recommender import ContentBasedRecommender
from services.item_based_recommender import ItemBasedRecommender
from config.settings import DEFAULT_RECOMMENDATION_LIMIT

router = APIRouter(prefix="/recommend", tags=["recommendations"])

@router.get("/similar/{content_type}/{content_id}")
def get_similar_content(
    content_type: str,
    content_id: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of similar items"),
    similarity_method: str = Query("content", description="Method to determine similarity (content, interaction)"),
    db=Depends(get_db)
):
    """
    Get content similar to the specified item.
    
    Supports two similarity methods:
    - content: Uses vector embeddings to find similar content based on textual features
    - interaction: Uses co-occurrence patterns to find items that users interact with together
    
    Parameters:
    - content_type: Type of content ('post' or 'community')
    - content_id: ID of the content item
    - limit: Maximum number of similar items to return
    - similarity_method: Method to determine similarity (content, interaction)
    
    Returns:
    - List of similar content items with similarity scores
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
    else:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid similarity method. Must be 'content' or 'interaction'."
        )
    
    # Get similar content
    similar_items = recommender.get_recommendations(
        content_id=content_id,
        content_type=content_type,
        limit=limit
    )
    
    return recommender.format_recommendations(similar_items)