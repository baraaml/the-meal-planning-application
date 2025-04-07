"""
Configuration module for the meal recommendation service.
Exports all configuration values from settings.py.
"""
from config.settings import (
    # Database settings
    DATABASE_URL,
    
    # API settings
    API_HOST,
    API_PORT,
    RELOAD,
    
    # Embedding model settings
    EMBEDDING_MODEL,
    EMBEDDING_DIMENSION,
    
    # Recommendation settings
    DEFAULT_RECOMMENDATION_LIMIT,
    CONTENT_TYPES,
    ALLOWED_TRENDING_WINDOWS,
    INTERACTION_TYPES,
    
    # Scheduler settings
    EMBEDDING_GENERATION_INTERVAL,
    SCHEDULER_SLEEP_INTERVAL,
    
    # Client settings
    RECOMMENDATION_API_URL,
    
    # Cache settings
    ENABLE_CACHE,
    CACHE_EXPIRATION,
    
    # Meal-specific settings
    CUISINE_TYPES,
    DIETARY_RESTRICTIONS,
    
    # Similarity thresholds
    MIN_SIMILARITY_SCORE,
    MIN_COMMON_ITEMS,
    
    # Logging configuration
    LOG_LEVEL,
    LOG_FORMAT
)