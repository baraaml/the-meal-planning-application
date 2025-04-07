"""
Main FastAPI application module.
Integrates all API routes and serves as the entry point for the recommendation service.
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from config.settings import API_HOST, API_PORT, RELOAD
import user_recommendation_routes
import similar_content_routes
import trending_routes
import interactions_routes

def create_app():
    """Create and configure the FastAPI application."""
    app = FastAPI(
        title="Recommendation Service",
        description="API for personalized content recommendations",
        version="1.0.0"
    )
    
    # Add CORS middleware
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # Allow all origins in development
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    
    # Include all route modules
    app.include_router(user_recommendation_routes.router)
    app.include_router(similar_content_routes.router)
    app.include_router(trending_routes.router)
    app.include_router(interactions_routes.router)
    
    @app.get("/", tags=["status"])
    def read_root():
        """Root endpoint for API status check."""
        return {"status": "Recommendation service is running"}
    
    return app

app = create_app()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app:app", 
        host=API_HOST, 
        port=API_PORT,
        reload=RELOAD
    )