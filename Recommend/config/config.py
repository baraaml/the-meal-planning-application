"""
Configuration settings loaded from environment variables.
"""
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Database settings
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://mealflow:mealflow@localhost:5432/meals")

# API settings
API_HOST = os.getenv("API_HOST", "127.0.0.1")
API_PORT = int(os.getenv("API_PORT", "9999"))
RELOAD = os.getenv("RELOAD", "True").lower() in ("true", "1", "t")

# Embedding settings
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "all-MiniLM-L6-v2")
EMBEDDING_DIMENSION = int(os.getenv("EMBEDDING_DIMENSION", "384"))

# Recommendation settings
DEFAULT_RECOMMENDATION_LIMIT = int(os.getenv("DEFAULT_RECOMMENDATION_LIMIT", "10"))
ALLOWED_TRENDING_WINDOWS = ["day", "week", "month"]
INTERACTION_TYPES = ["view", "like", "save", "cook", "rating","ignore"]

# Search settings
MIN_SIMILARITY_SCORE = float(os.getenv("MIN_SIMILARITY_SCORE", "0.6"))
MIN_COMMON_ITEMS = int(os.getenv("MIN_COMMON_ITEMS", "2"))

# Logging
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
LOG_FORMAT = os.getenv("LOG_FORMAT", "%(asctime)s - %(name)s - %(levelname)s - %(message)s")