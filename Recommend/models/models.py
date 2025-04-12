"""
Pydantic models for API request and response validation.
"""
from typing import List, Dict, Any, Optional
from pydantic import BaseModel, Field

# Request models
class RecipeCreate(BaseModel):
    """Model for creating a recipe."""
    recipe_title: str
    region: Optional[str] = None
    sub_region: Optional[str] = None
    continent: Optional[str] = None
    source: Optional[str] = None
    image_url: Optional[str] = None
    cook_time: Optional[int] = None
    prep_time: Optional[int] = None
    total_time: Optional[int] = None
    servings: Optional[int] = None
    url: Optional[str] = None
    calories: Optional[float] = None

class RecipeUpdate(BaseModel):
    """Model for updating a recipe."""
    recipe_title: Optional[str] = None
    region: Optional[str] = None
    sub_region: Optional[str] = None
    continent: Optional[str] = None
    source: Optional[str] = None
    image_url: Optional[str] = None
    cook_time: Optional[int] = None
    prep_time: Optional[int] = None
    total_time: Optional[int] = None
    servings: Optional[int] = None
    url: Optional[str] = None
    calories: Optional[float] = None

class InteractionCreate(BaseModel):
    """Model for creating an interaction."""
    user_id: str
    meal_id: str
    content_type: str = "recipe"
    interaction_type: str
    rating: Optional[float] = None

class DietaryPreferenceCreate(BaseModel):
    """Model for creating a dietary preference."""
    dietary_restriction: str

# Response models
class RecipeBase(BaseModel):
    """Base model for recipe responses."""
    recipe_id: int
    recipe_title: str
    region: Optional[str] = None
    sub_region: Optional[str] = None

class RecipeDetail(RecipeBase):
    """Model for detailed recipe response."""
    continent: Optional[str] = None
    source: Optional[str] = None
    image_url: Optional[str] = None
    cook_time: Optional[int] = None
    prep_time: Optional[int] = None
    total_time: Optional[int] = None
    servings: Optional[int] = None
    url: Optional[str] = None
    calories: Optional[float] = None
    instructions: Optional[str] = None
    ingredients: List[Dict[str, Any]] = []

class RecommendationItem(BaseModel):
    """Model for a recommendation item."""
    id: str
    content_type: str = "recipe"
    title: str
    score: Optional[float] = None

class RecommendationResponse(BaseModel):
    """Model for recommendation responses."""
    items: List[RecommendationItem]
    count: int
    execution_time_ms: float = 0

class HealthResponse(BaseModel):
    """Model for health check response."""
    status: str
    database: str
    execution_time_ms: float = 0