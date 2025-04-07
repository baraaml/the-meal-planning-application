"""
User meal interaction API routes.
Endpoints for tracking user interactions with meals.
"""
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from typing import Optional

from base import get_db, validate_content_type
from data.repositories.interaction_repository import InteractionRepository

router = APIRouter(tags=["interactions"])

class InteractionCreate(BaseModel):
    """Request model for creating a meal interaction record."""
    user_id: str
    meal_id: str
    content_type: str
    interaction_type: str

@router.post("/interactions")
def record_interaction(
    interaction: InteractionCreate,
    db=Depends(get_db)
):
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
    # Validate content type
    try:
        validate_content_type(interaction.content_type)
    except HTTPException as e:
        raise e
    
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
    
    return {"status": "recorded"}

@router.get("/user/{user_id}/history")
def get_user_meal_history(
    user_id: str,
    content_type: Optional[str] = None,
    limit: int = 10,
    db=Depends(get_db)
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
    # Validate content type if provided
    if content_type:
        content_type = validate_content_type(content_type)
    
    repository = InteractionRepository()
    history = repository.get_user_recent_interactions(
        user_id=user_id,
        content_type=content_type,
        limit=limit
    )
    
    return {"history": history}

@router.get("/user/{user_id}/dietary-preferences")
def get_user_dietary_preferences(
    user_id: str,
    db=Depends(get_db)
):
    """
    Get a user's dietary preferences.
    
    Parameters:
    - user_id: ID of the user
    
    Returns:
    - List of user's dietary preferences
    """
    repository = InteractionRepository()
    preferences = repository.get_user_dietary_preferences(user_id)
    
    return {"dietary_preferences": preferences}

@router.post("/user/{user_id}/dietary-preferences")
def set_user_dietary_preference(
    user_id: str,
    dietary_restriction_id: int,
    db=Depends(get_db)
):
    """
    Add a dietary preference for a user.
    
    Parameters:
    - user_id: ID of the user
    - dietary_restriction_id: ID of the dietary restriction
    
    Returns:
    - Status confirmation
    """
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
    
    return {"status": "added"}