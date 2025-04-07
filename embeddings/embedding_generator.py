"""
Embedding generator service.
Generates and stores vector embeddings for meals and recipes.
"""
from sentence_transformers import SentenceTransformer
from typing import List, Dict, Any, Optional
import time
import logging

from config.settings import EMBEDDING_MODEL
from data.repositories.content_embedding_repository import ContentEmbeddingRepository

logger = logging.getLogger(__name__)

class EmbeddingGenerator:
    """Service for generating and managing meal embeddings."""
    
    def __init__(self, model_name: str = EMBEDDING_MODEL):
        """
        Initialize the embedding generator.
        
        Args:
            model_name: The name of the sentence transformer model to use
        """
        self.model = SentenceTransformer(model_name)
        self.repository = ContentEmbeddingRepository()
    
    def generate_meal_embeddings(self, batch_size: int = 500) -> int:
        """
        Generate embeddings for meals that don't have them yet.
        
        Args:
            batch_size: Maximum number of meals to process at once
            
        Returns:
            Number of meals processed
        """
        meals = self.repository.get_content_without_embeddings('meal', batch_size)
        logger.info(f"Processing {len(meals)} meals for embedding generation")
        
        count = 0
        for meal_id, title, description in meals:
            # Get ingredients for this meal
            ingredients = self.repository.get_meal_ingredients(meal_id)
            ingredients_text = ", ".join([ing['name'] for ing in ingredients])
            
            # Generate text for embedding - include title, description, and ingredients
            text_for_embedding = f"{title} {description} Ingredients: {ingredients_text}"
            
            # Generate embedding
            embedding = self.model.encode(text_for_embedding)
            
            # Store embedding
            if self.repository.save_embedding(meal_id, 'meal', embedding.tolist()):
                count += 1
            
        logger.info(f"Generated embeddings for {count} meals")
        return count
    
    def generate_recipe_embeddings(self, batch_size: int = 500) -> int:
        """
        Generate embeddings for recipes that don't have them yet.
        
        Args:
            batch_size: Maximum number of recipes to process at once
            
        Returns:
            Number of recipes processed
        """
        recipes = self.repository.get_content_without_embeddings('recipe', batch_size)
        logger.info(f"Processing {len(recipes)} recipes for embedding generation")
        
        count = 0
        for recipe_id, name, instructions in recipes:
            # Get ingredients for this recipe
            ingredients = self.repository.get_recipe_ingredients(recipe_id)
            ingredients_text = ", ".join([ing['name'] for ing in ingredients])
            
            # Generate text for embedding - include name, instructions, and ingredients
            text_for_embedding = f"{name} {instructions} Ingredients: {ingredients_text}"
            
            # Generate embedding
            embedding = self.model.encode(text_for_embedding)
            
            # Store embedding
            if self.repository.save_embedding(recipe_id, 'recipe', embedding.tolist()):
                count += 1
            
        logger.info(f"Generated embeddings for {count} recipes")
        return count
    
    def generate_embedding_for_text(self, text: str) -> List[float]:
        """
        Generate an embedding for arbitrary text.
        
        Args:
            text: The text to encode
            
        Returns:
            The embedding vector
        """
        embedding = self.model.encode(text)
        return embedding.tolist()
    
    def generate_embedding_for_meal(self, meal_id: str, title: str, description: str, ingredients: List[str]) -> bool:
        """
        Generate and store an embedding for a specific meal.
        
        Args:
            meal_id: The ID of the meal
            title: The meal title
            description: The meal description
            ingredients: List of ingredient names
            
        Returns:
            Success status
        """
        ingredients_text = ", ".join(ingredients)
        text_for_embedding = f"{title} {description} Ingredients: {ingredients_text}"
        
        embedding = self.model.encode(text_for_embedding)
        return self.repository.save_embedding(meal_id, 'meal', embedding.tolist())
    
    def generate_all_embeddings(self) -> Dict[str, int]:
        """
        Generate embeddings for all content types.
        
        Returns:
            Dictionary with counts of processed items by type
        """
        meal_count = self.generate_meal_embeddings()
        recipe_count = self.generate_recipe_embeddings()
        
        return {
            "meals": meal_count,
            "recipes": recipe_count
        }


# For command-line execution
if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    generator = EmbeddingGenerator()
    result = generator.generate_all_embeddings()
    print(f"Generated embeddings: {result}")