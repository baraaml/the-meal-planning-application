"""
API endpoints for system management and monitoring.
"""
from fastapi import APIRouter, HTTPException
from typing import Dict, Any

from embedding.embeddings import EmbeddingGenerator
from models.models import HealthResponse

router = APIRouter(prefix="/api/v1", tags=["system"])

@router.get("/embeddings/stats")
def get_embedding_stats():
    """Get statistics about recipe embeddings."""
    generator = EmbeddingGenerator()
    stats = generator.get_embedding_stats()
    return stats

@router.post("/embeddings/generate")
def generate_embeddings(batch_size: int = 50):
    """Generate embeddings for recipes that don't have them yet."""
    generator = EmbeddingGenerator()
    count = generator.generate_all_embeddings(batch_size)
    
    return {
        "success": True,
        "generated_embeddings": count,
        "message": f"Successfully generated {count} embeddings"
    }