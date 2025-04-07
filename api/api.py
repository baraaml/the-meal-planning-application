"""
Main FastAPI application module.
Integrates all API routes and serves as the entry point for the meal recommendation service.
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from config.settings import API_HOST, API_PORT, RELOAD
from api import user_recommendations, similar_meal_routes, trending_routes, interactions

def create_app():
    """Create and configure the FastAPI application."""
    app = FastAPI(
        title="Meal Recommendation Service",
        description="API for personalized meal recommendations",
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
    app.include_router(user_recommendations.router)
    app.include_router(similar_meal_routes.router)
    app.include_router(trending_routes.router)
    app.include_router(interactions.router)
    
    @app.get("/", tags=["status"])
    def read_root():
        """Root endpoint for API status check."""
        return {"status": "Meal recommendation service is running"}
    
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