"""
Embedding generation for recipes using SentenceTransformers.
"""
import logging
from typing import List, Dict, Any, Optional
from sentence_transformers import SentenceTransformer
import numpy as np

from config.db import execute_query, execute_query_single
from config.config import EMBEDDING_MODEL, EMBEDDING_DIMENSION
from models.queries_recipe import get_recipe, get_recipes_without_embeddings

logger = logging.getLogger(__name__)

class EmbeddingGenerator:
    """Generator for recipe embeddings using SentenceTransformers."""
    
    def __init__(self, model_name: str = EMBEDDING_MODEL):
        """Initialize the embedding generator with the specified model."""
        try:
            logger.info(f"Loading model: {model_name}")
            self.model = SentenceTransformer(model_name)
            logger.info(f"Model loaded successfully")
        except Exception as e:
            logger.error(f"Failed to load model {model_name}: {e}")
            logger.info("Falling back to default model: all-MiniLM-L6-v2")
            self.model = SentenceTransformer('all-MiniLM-L6-v2')
        
        self.embedding_dimension = EMBEDDING_DIMENSION
    
    def generate_recipe_embedding(self, recipe_data: Dict[str, Any]) -> Optional[List[float]]:
        """Generate an embedding for a single recipe."""
        try:
            # Create a comprehensive text representation of the recipe
            title = recipe_data.get("recipe_title", "")
            instructions = recipe_data.get("instructions", "")
            ingredients = [ing.get("ingredient_name", "") for ing in recipe_data.get("ingredients", [])]
            
            ingredients_text = ", ".join(ingredients)
            text_for_embedding = f"{title}. {instructions} Ingredients: {ingredients_text}"
            
            # Generate embedding
            embedding = self.model.encode(text_for_embedding)
            
            # Convert to list for storage
            return embedding.tolist()
            
        except Exception as e:
            logger.error(f"Error generating embedding: {e}")
            return None
    
    def save_recipe_embedding(self, recipe_id: int, embedding: List[float]) -> bool:
        """Save a recipe embedding to the database."""
        try:
            # Convert embedding to PostgreSQL vector format
            pg_vector = str(embedding).replace('[', '{').replace(']', '}')
            
            query = """
            INSERT INTO recipe_embeddings (recipe_id, embedding)
            VALUES (%(recipe_id)s, %(embedding)s::vector)
            ON CONFLICT (recipe_id) 
            DO UPDATE SET 
                embedding = %(embedding)s::vector, 
                updated_at = CURRENT_TIMESTAMP
            """
            
            execute_query(query, {"recipe_id": recipe_id, "embedding": pg_vector})
            return True
            
        except Exception as e:
            logger.error(f"Error saving embedding for recipe {recipe_id}: {e}")
            return False
    
    def update_recipe_embedding(self, recipe_id: int) -> bool:
        """Update the embedding for a specific recipe."""
        try:
            # Get recipe data
            recipe_data = get_recipe(recipe_id)
            if not recipe_data:
                return False
            
            # Generate new embedding
            embedding = self.generate_recipe_embedding(recipe_data)
            if not embedding:
                return False
            
            # Save embedding
            return self.save_recipe_embedding(recipe_id, embedding)
            
        except Exception as e:
            logger.error(f"Error updating embedding for recipe {recipe_id}: {e}")
            return False
    
    def generate_all_embeddings(self, batch_size: int = 50) -> int:
        """Generate embeddings for recipes that don't have them yet."""
        count = 0
        
        try:
            # Get recipes without embeddings
            recipes = get_recipes_without_embeddings(batch_size)
            
            if not recipes:
                logger.info("No recipes found without embeddings")
                return 0
            
            logger.info(f"Generating embeddings for {len(recipes)} recipes")
            
            # Process recipes
            for recipe in recipes:
                recipe_id = recipe["recipe_id"]
                
                # Get complete recipe data
                recipe_data = get_recipe(recipe_id)
                if not recipe_data:
                    continue
                
                # Generate embedding
                embedding = self.generate_recipe_embedding(recipe_data)
                if not embedding:
                    continue
                
                # Save embedding
                if self.save_recipe_embedding(recipe_id, embedding):
                    count += 1
            
            logger.info(f"Generated embeddings for {count} recipes")
            return count
            
        except Exception as e:
            logger.error(f"Error generating embeddings: {e}")
            return count
    
    def get_embedding_stats(self) -> Dict[str, Any]:
        """Get statistics about recipe embeddings."""
        try:
            query = """
            SELECT
                (SELECT COUNT(*) FROM recipes) as total_recipes,
                (SELECT COUNT(*) FROM recipe_embeddings) as total_embeddings
            """
            
            stats = execute_query_single(query)
            
            # Get most recent embeddings
            recent_query = """
            SELECT recipe_id, created_at, updated_at
            FROM recipe_embeddings
            ORDER BY updated_at DESC
            LIMIT 5
            """
            
            recent_embeddings = execute_query(recent_query)
            
            # Format timestamps to strings
            if recent_embeddings:
                for embedding in recent_embeddings:
                    if "created_at" in embedding and embedding["created_at"]:
                        embedding["created_at"] = embedding["created_at"].isoformat()
                    if "updated_at" in embedding and embedding["updated_at"]:
                        embedding["updated_at"] = embedding["updated_at"].isoformat()
            
            total_recipes = stats.get("total_recipes", 0)
            total_embeddings = stats.get("total_embeddings", 0)
            
            return {
                "total_recipes": total_recipes,
                "total_embeddings": total_embeddings,
                "coverage_percentage": round((total_embeddings / total_recipes * 100), 2) if total_recipes > 0 else 0,
                "recent_embeddings": recent_embeddings or []
            }
            
        except Exception as e:
            logger.error(f"Error getting embedding stats: {e}")
            return {
                "error": str(e)
            }