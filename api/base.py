"""
API base module.
Contains common API dependencies and configurations for meal recommendations.
"""
from fastapi import Depends, HTTPException, status
from sqlalchemy import text
from typing import Generator

from data.database import get_connection
from config.settings import CONTENT_TYPES

def get_db():
    """Database connection dependency."""
    with get_connection() as conn:
        yield conn

def validate_content_type(content_type: str, allow_all: bool = False) -> str:
    """
    Validate meal content type parameter.
    
    Args:
        content_type: The content type to validate
        allow_all: Whether to allow 'all' as a valid content type
        
    Returns:
        The validated content type
        
    Raises:
        HTTPException: If the content type is invalid
    """
    valid_types = CONTENT_TYPES.copy()
    if allow_all:
        valid_types.append('all')
        
    if content_type not in valid_types:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Content type must be one of: {', '.join(valid_types)}"
        )
    
    return content_type