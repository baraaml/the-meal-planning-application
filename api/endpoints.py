"""
Modified version of API endpoints to work with CSV schema and avoid fixed enumerations.
This shows only the relevant methods that need changes - the full file would include all endpoints.
"""
from fastapi import APIRouter, Depends, Query, HTTPException, status
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import List, Dict, Any, Optional
import time

from config import CONTENT_TYPES, DEFAULT_RECOMMENDATION_LIMIT, ALLOWED_TRENDING_WINDOWS
from data.database import get_db
from services.hybrid import HybridRecommender
from services.popularity import PopularityRecommender
from data.queries import execute_query

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

class DietaryPreferenceCreate(BaseModel):
    """Request model for setting dietary preference."""
    dietary_restriction: str

# Modified endpoint for getting dietary recommendations
@router.get("/recommend/dietary/{dietary_restriction}", response_model=RecommendationResponse)
def get_dietary_recommendations(
    dietary_restriction: str,
    limit: int = Query(DEFAULT_RECOMMENDATION_LIMIT, description="Maximum number of items"),
    db = Depends(get_db)
):
    """Get meal recommendations based on dietary restrictions."""
    start_time = time.time()
    
    recommender = PopularityRecommender()
    dietary_items = recommender.get_dietary_recommendations(
        dietary_restriction=dietary_restriction,
        limit=limit
    )
    
    # Format the items
    items = []
    for item in dietary_items:
        items.append(RecommendationItem(
            id=item["id"],
            content_type=item.get("content_type", "recipe"),
            title=item.get("title", ""),
            score=None
        ))
    
    execution_time = (time.time() - start_time) * 1000
    
    return {
        "items": items,
        "count": len(items),
        "execution_time_ms": round(execution_time, 2)
    }

# Modified endpoint for getting user dietary preferences
@router.get("/user/{user_id}/dietary-preferences")
def get_user_dietary_preferences(
    user_id: str,
    db = Depends(get_db)
):
    """Get a user's dietary preferences."""
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

# Modified endpoint for setting user dietary preference
@router.post("/user/{user_id}/dietary-preferences")
def set_user_dietary_preference(
    user_id: str,
    preference: DietaryPreferenceCreate,
    db = Depends(get_db)
):
    """Add a dietary preference for a user."""
    start_time = time.time()
    
    try:
        execute_query(
            """
            INSERT INTO "UserDietaryPreference" (user_id, dietary_restriction)
            VALUES (:user_id, :dietary_restriction)
            ON CONFLICT (user_id, dietary_restriction) DO NOTHING
            """,
            {
                "user_id": user_id,
                "dietary_restriction": preference.dietary_restriction
            },
            is_transaction=True
        )
        
        execution_time = (time.time() - start_time) * 1000
        
        return {
            "status": "added",
            "dietary_restriction": preference.dietary_restriction,
            "execution_time_ms": round(execution_time, 2)
        }
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to add dietary preference: {str(e)}"
        )

# Modified endpoint for removing user dietary preference
@router.delete("/user/{user_id}/dietary-preferences/{dietary_restriction}")
def remove_user_dietary_preference(
    user_id: str,
    dietary_restriction: str,
    db = Depends(get_db)
):
    """Remove a dietary preference for a user."""
    start_time = time.time()
    
    try:
        result = execute_query(
            """
            DELETE FROM "UserDietaryPreference"
            WHERE user_id = :user_id AND dietary_restriction = :dietary_restriction
            """,
            {
                "user_id": user_id,
                "dietary_restriction": dietary_restriction
            },
            is_transaction=True
        )
        
        execution_time = (time.time() - start_time) * 1000
        
        return {
            "status": "removed",
            "dietary_restriction": dietary_restriction,
            "execution_time_ms": round(execution_time, 2)
        }
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to remove dietary preference: {str(e)}"
        )