"""
Configuration settings for the meal recommendation service.
Centralizes all configuration parameters in a single file.
"""
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Database settings
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://postgres:postgres@localhost:5432/postgres")

# API settings
API_HOST = os.getenv("API_HOST", "0.0.0.0")
API_PORT = int(os.getenv("API_PORT", "8000"))
RELOAD = os.getenv("RELOAD", "True").lower() == "true"

# Embedding model settings
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "all-MiniLM-L6-v2")
EMBEDDING_DIMENSION = int(os.getenv("EMBEDDING_DIMENSION", "384"))  # Dimension depends on model

# Recommendation settings
DEFAULT_RECOMMENDATION_LIMIT = int(os.getenv("DEFAULT_RECOMMENDATION_LIMIT", "10"))
CONTENT_TYPES = ["meal", "recipe"]
ALLOWED_TRENDING_WINDOWS = ["day", "week", "month"]
INTERACTION_TYPES = ["view", "like", "save", "cook", "comment"]

# Scheduler settings
EMBEDDING_GENERATION_INTERVAL = int(os.getenv("EMBEDDING_GENERATION_INTERVAL", "60"))  # minutes
SCHEDULER_SLEEP_INTERVAL = int(os.getenv("SCHEDULER_SLEEP_INTERVAL", "60"))  # seconds

# Client settings
RECOMMENDATION_API_URL = os.getenv("RECOMMENDATION_API_URL", "http://localhost:8000")

# Cache settings
ENABLE_CACHE = os.getenv("ENABLE_CACHE", "False").lower() == "true"
CACHE_EXPIRATION = int(os.getenv("CACHE_EXPIRATION", "300"))  # seconds

# Meal-specific settings
CUISINE_TYPES = [
    "italian", "mexican", "chinese", "indian", "american",
    "french", "japanese", "mediterranean", "thai", "other"
]

DIETARY_RESTRICTIONS = [
    "vegetarian", "vegan", "gluten-free", "dairy-free", 
    "keto", "paleo", "low-carb", "low-fat"
]

# Similarity thresholds
MIN_SIMILARITY_SCORE = float(os.getenv("MIN_SIMILARITY_SCORE", "0.6"))
MIN_COMMON_ITEMS = int(os.getenv("MIN_COMMON_ITEMS", "2"))

# Logging configuration
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
LOG_FORMAT = os.getenv(
    "LOG_FORMAT", 
    "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)

# Setup logging
import logging
logging.basicConfig(level=getattr(logging, LOG_LEVEL), format=LOG_FORMAT)