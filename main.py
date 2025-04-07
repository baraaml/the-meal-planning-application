"""
Meal Recommendation Service main application.
This is the entry point for the service.
"""
import uvicorn
import logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
import time

from api.endpoints import router as api_router
from utils.scheduler import start_scheduler
from setup import create_recommendation_tables
from embeddings.generator import EmbeddingGenerator
from config import API_HOST, API_PORT, RELOAD

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Request timing middleware
class TimingMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        start_time = time.time()
        response = await call_next(request)
        process_time = time.time() - start_time
        
        # Add timing header
        response.headers["X-Process-Time"] = f"{process_time:.4f}"
        
        return response

# Startup and shutdown events
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: Initialize database and run embedding generation
    logger.info("Starting application initialization...")
    
    try:
        # Setup database tables if needed
        create_recommendation_tables()
        logger.info("Database tables created/verified successfully")
        
        # Run initial embedding generation
        logger.info("Starting initial embedding generation...")
        generator = EmbeddingGenerator()
        result = generator.generate_all_embeddings()
        logger.info(f"Initial embedding generation complete: {result}")
        
        # Start the scheduler for background tasks
        logger.info("Starting background scheduler...")
        scheduler_thread = start_scheduler()
        logger.info("Background scheduler started successfully")
        
    except Exception as e:
        logger.error(f"Error during application initialization: {e}")
        # Continue startup even if there are errors to allow for troubleshooting
    
    logger.info("Application initialization complete")
    
    yield
    
    # Shutdown: Nothing to clean up as scheduler runs in daemon thread
    logger.info("Application shutting down...")

# Create FastAPI app with docs disabled
app = FastAPI(
    title="MealFlow Recommendation Service",
    description="API for personalized meal recommendations",
    version="1.0.0",
    docs_url=None,  # Disable Swagger UI
    redoc_url=None,  # Disable ReDoc
    lifespan=lifespan
)

# Add middleware
app.add_middleware(TimingMiddleware)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, replace with specific origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include API router
app.include_router(api_router)

if __name__ == "__main__":
    uvicorn.run(
        "main:app", 
        host=API_HOST, 
        port=API_PORT,
        reload=RELOAD
    )