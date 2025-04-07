"""
Configuration settings for the recommendation service.
Centralizes all configuration parameters.
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
EMBEDDING_DIMENSION = int(os.getenv("EMBEDDING_DIMENSION", "768"))

# Recommendation settings
DEFAULT_RECOMMENDATION_LIMIT = int(os.getenv("DEFAULT_RECOMMENDATION_LIMIT", "10"))
CONTENT_TYPES = ["post", "community", "comment"]
ALLOWED_TRENDING_WINDOWS = ["day", "week", "month"]

# Scheduler settings
EMBEDDING_GENERATION_INTERVAL = int(os.getenv("EMBEDDING_GENERATION_INTERVAL", "60"))  # minutes
SCHEDULER_SLEEP_INTERVAL = int(os.getenv("SCHEDULER_SLEEP_INTERVAL", "60"))  # seconds

# Client settings
RECOMMENDATION_API_URL = os.getenv("RECOMMENDATION_API_URL", "http://localhost:8000")