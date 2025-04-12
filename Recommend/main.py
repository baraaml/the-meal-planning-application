"""
Main application file for the MealFlow recommendation service.
Sets up FastAPI app, routes, middleware, and database connection.
"""
import uvicorn
import logging
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse, RedirectResponse
import time

from config.config import API_HOST, API_PORT, RELOAD, LOG_LEVEL, LOG_FORMAT
from config.db import check_connection, initialize_pool
from endpoints.middleware import setup_middleware
from endpoints.recipes import router as recipe_router
from endpoints.recommendations import router as recommendation_router
from models.models import HealthResponse

# Configure logging
logging.basicConfig(
    level=getattr(logging, LOG_LEVEL),
    format=LOG_FORMAT
)
logger = logging.getLogger(__name__)

# Create FastAPI app
app = FastAPI(
    title="MealFlow Recommendation Service",
    description="API for personalized meal recommendations",
    version="1.0.0"
)

# Set up middleware
setup_middleware(app)

# Include API routers
app.include_router(recipe_router)
app.include_router(recommendation_router)

# Add error handling for 500 errors
@app.exception_handler(Exception)
async def general_exception_handler(request: Request, exc: Exception):
    """Global exception handler for unhandled exceptions."""
    logger.error(f"Unhandled exception: {str(exc)}", exc_info=True)
    return JSONResponse(
        status_code=500,
        content={
            "detail": "An unexpected error occurred. Please try again later.",
            "error_type": type(exc).__name__,
            "path": request.url.path
        }
    )

# Add health check endpoint
@app.get("/health", response_model=HealthResponse)
async def health_check():
    """
    Simple health check endpoint.
    Returns database connection status and uptime.
    """
    start_time = time.time()
    
    # Test database connection
    db_status = "connected" if check_connection() else "disconnected"
    
    execution_time = time.time() - start_time
    
    return HealthResponse(
        status="healthy",
        database=db_status,
        execution_time_ms=round(execution_time * 1000, 2)
    )

# Add API documentation redirection
@app.get("/", include_in_schema=False)
async def redirect_to_docs():
    """Redirect root endpoint to API documentation."""
    return RedirectResponse(url="/docs")

# Initialize database connection pool
@app.on_event("startup")
async def startup_event():
    """Initialize resources on startup."""
    logger.info("Initializing application...")
    initialize_pool()
    logger.info("Application initialization complete")

# Cleanup on shutdown
@app.on_event("shutdown")
async def shutdown_event():
    """Clean up resources on shutdown."""
    logger.info("Application shutting down...")
    # Any cleanup code would go here

if __name__ == "__main__":
    uvicorn.run(
        "main:app", 
        host=API_HOST, 
        port=API_PORT,
        reload=RELOAD
    )