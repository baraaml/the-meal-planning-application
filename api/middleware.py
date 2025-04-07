"""
Meal Recommendation Service main application.
This is the entry point for the service.
"""
import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.openapi.docs import get_swagger_ui_html
from contextlib import asynccontextmanager

from api.endpoints import router as api_router
from utils.scheduler import start_scheduler
from config import API_HOST, API_PORT, RELOAD
from setup import create_recommendation_tables
from embeddings.generator import EmbeddingGenerator

# Startup and shutdown events
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: Initialize database and run embedding generation
    create_recommendation_tables()
    
    # Run initial embedding generation
    generator = EmbeddingGenerator()
    generator.generate_all_embeddings()
    
    # Start the scheduler
    scheduler_thread = start_scheduler()
    
    yield
    
    # Shutdown: Nothing to clean up as scheduler runs in daemon thread

# Create FastAPI app
app = FastAPI(
    title="Meal Recommendation Service",
    description="API for personalized meal recommendations",
    version="1.0.0",
    lifespan=lifespan
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allow all origins in development
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include API router
app.include_router(api_router)

# Custom Swagger UI
@app.get("/docs", include_in_schema=False)
async def custom_swagger_ui_html():
    return get_swagger_ui_html(
        openapi_url=app.openapi_url,
        title=app.title + " - API Documentation",
        swagger_js_url="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5.9.0/swagger-ui-bundle.js",
        swagger_css_url="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5.9.0/swagger-ui.css",
    )

if __name__ == "__main__":
    uvicorn.run(
        "main:app", 
        host=API_HOST, 
        port=API_PORT,
        reload=RELOAD
    )