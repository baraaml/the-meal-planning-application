"""
User interaction API routes.
Endpoints for tracking user interactions with content.
"""
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from typing import Optional

from api.base import get_db, validate_content_type
from data.repositories.interaction_repository import InteractionRepository

router = APIRouter(tags=["interactions"])

class InteractionCreate(BaseModel):
    """Request model for creating an interaction record."""
    user_id: str
    content_id: str
    content_type: str
    interaction_type: str

@router.post("/interactions")
def record_interaction(
    interaction: InteractionCreate,
    db=Depends(get_db)
):
    """
    Record a user interaction with content.
    
    Parameters:
    - user_id: ID of the user
    - content_id: ID of the content
    - content_type: Type of content ('post', 'community', 'comment')
    - interaction_type: Type of interaction ('view', 'click', 'vote', etc.)
    
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
        content_id=interaction.content_id,
        content_type=interaction.content_type,
        interaction_type=interaction.interaction_type
    )
    
    if not success:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to record interaction"
        )
    
    return {"status": "recorded"}