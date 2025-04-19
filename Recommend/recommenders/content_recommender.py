"""
Content-based recommender implementation using vector embeddings.
"""
import logging
from typing import List, Dict, Any, Optional

from recommenders.base_recommender import BaseRecommender
from models.queries_search import (
    find_similar_content, get_similar_by_ingredients, 
    get_cuisine_recommendations, get_dietary_recommendations,
    get_quick_recipes
)
from models.queries_recipe import get_recipe
from embedding.embeddings import EmbeddingGenerator

logger = logging.getLogger(__name__)

class ContentRecommender(BaseRecommender):
    """Content-based recommendation strategy using recipe embeddings."""
    
    def get_recommendations(self, user_id: Optional[str] = None, content_type: Optional[str] = None, 
                           limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get content-based recommendations.
        
        If user_id is provided, uses their most recent interaction to find similar recipes.
        Otherwise, returns trending recipes as a fallback.
        
        Args:
            user_id: Optional user ID
            content_type: Optional content type filter
            limit: Maximum number of recommendations
            kwargs: Additional parameters like cuisine and dietary_restriction
            
        Returns:
            List of recommended items
        """
        # If user_id is provided, get their recent interactions
        if user_id:
            from models.queries_recommend import get_user_recent_interactions
            
            recent_interactions = get_user_recent_interactions(
                user_id=user_id,
                limit=1,
                exclude_types=["ignore"]
            )
            
            if recent_interactions:
                # Get the most recent interaction
                recent = recent_interactions[0]
                recipe_id = recent["recipe_id"]
                
                try:
                    # Convert string ID to integer if necessary
                    if isinstance(recipe_id, str):
                        recipe_id = int(recipe_id)
                    
                    # Get recommendations similar to this recipe
                    return self.get_similar_recommendations(
                        recipe_id=recipe_id,
                        limit=limit,
                        **kwargs
                    )
                except (ValueError, TypeError) as e:
                    logger.error(f"Invalid recipe_id: {recipe_id}, error: {e}")
        
        # Fallback: Get recommendations based on constraints
        cuisine = kwargs.get('cuisine')
        dietary_restriction = kwargs.get('dietary_restriction')
        
        if cuisine:
            return self.get_cuisine_recommendations(cuisine, limit)
        elif dietary_restriction:
            return self.get_dietary_recommendations(dietary_restriction, limit)
        
        # Final fallback: trending recipes
        from recommenders.popularity_recommender import PopularityRecommender
        popularity = PopularityRecommender()
        return popularity.get_trending_recommendations(limit=limit)
    
    def get_similar_recommendations(self, recipe_id: int, similarity_method: str = "content", limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get recommendations similar to a specific recipe.
        
        Args:
            recipe_id: ID of the reference recipe
            similarity_method: Method to determine similarity ("content" or "ingredient")
            limit: Maximum number of recommendations
            
        Returns:
            List of similar recipes
        """
        # Get recipe details
        recipe = get_recipe(recipe_id)
        if not recipe:
            logger.error(f"Recipe with ID {recipe_id} not found")
            return []
        
        # Choose similarity method
        if similarity_method == "ingredient":
            similar_items = get_similar_by_ingredients(recipe_id, limit)
            return [
                {
                    "id": str(item["id"]),
                    "title": item["title"],
                    "content_type": "recipe",
                    "score": item.get("score", 0)
                }
                for item in similar_items
            ]
        else:  # Default to content-based using embeddings
            # Try to get existing embedding from database
            query = """
            SELECT embedding FROM recipe_embeddings WHERE recipe_id = %(recipe_id)s
            """
            
            from config.db import execute_query_single
            embedding_result = execute_query_single(query, {"recipe_id": recipe_id})
            
            if embedding_result and "embedding" in embedding_result:
                # Use existing embedding from database
                embedding = embedding_result["embedding"]
                logger.info(f"Using existing embedding for recipe {recipe_id}")
            else:
                # Generate new embedding
                logger.info(f"No embedding found for recipe {recipe_id}, generating one")
                generator = EmbeddingGenerator()
                embedding = generator.generate_recipe_embedding(recipe)
                
                if embedding:
                    # Save the generated embedding
                    generator.save_recipe_embedding(recipe_id, embedding)
                else:
                    logger.error(f"Failed to generate embedding for recipe {recipe_id}")
                    return []
            
            # Find similar recipes using vector similarity
            similar_items = find_similar_content(embedding, exclude_ids=[recipe_id], limit=limit)
            
            return [
                {
                    "id": str(item["id"]),
                    "title": item["title"],
                    "content_type": "recipe",
                    "score": item.get("score", 0)
                }
                for item in similar_items
            ]
    
    def get_cuisine_recommendations(self, cuisine_name: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        Get recommendations for a specific cuisine.
        
        Args:
            cuisine_name: Name of the cuisine or region
            limit: Maximum number of recommendations
            
        Returns:
            List of cuisine-specific recipes
        """
        cuisine_items = get_cuisine_recommendations(cuisine_name, limit)
        return [
            {
                "id": str(item["id"]),
                "title": item["title"],
                "content_type": "recipe"
            }
            for item in cuisine_items
        ]
    
    def get_dietary_recommendations(self, dietary_restriction: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        Get recommendations for a specific dietary restriction.
        
        Args:
            dietary_restriction: Type of dietary restriction (vegan, pescetarian, etc.)
            limit: Maximum number of recommendations
            
        Returns:
            List of dietary-specific recipes
        """
        dietary_items = get_dietary_recommendations(dietary_restriction, limit)
        return [
            {
                "id": str(item["id"]),
                "title": item["title"],
                "content_type": "recipe"
            }
            for item in dietary_items
        ]
    
    def get_quick_recommendations(self, max_time: int = 30, limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get quick recipe recommendations based on preparation time.
        
        Args:
            max_time: Maximum preparation time in minutes
            limit: Maximum number of recommendations
            kwargs: Additional filters like cuisine and dietary_restriction
            
        Returns:
            List of quick recipes
        """
        cuisine = kwargs.get('cuisine')
        dietary_restriction = kwargs.get('dietary_restriction')
        
        quick_items = get_quick_recipes(max_time, limit, cuisine, dietary_restriction)
        return [
            {
                "id": str(item["id"]),
                "title": item["title"],
                "content_type": "recipe"
            }
            for item in quick_items
        ]
    
    def get_recommendations_by_text(self, query_text: str, limit: int = 10, **kwargs) -> List[Dict[str, Any]]:
        """
        Get recommendations based on a text query using vector similarity.
        
        Args:
            query_text: Text to find similar recipes for
            limit: Maximum number of recommendations
            kwargs: Additional filters
            
        Returns:
            List of similar recipes
        """
        # Generate embedding for query text
        generator = EmbeddingGenerator()
        embedding = generator.get_embedding_from_text(query_text)
        
        if not embedding:
            logger.error(f"Failed to generate embedding for query text: {query_text}")
            return []
        
        # Find similar recipes
        filters = {}
        cuisine = kwargs.get('cuisine')
        if cuisine:
            filters["cuisine"] = cuisine
            
        dietary = kwargs.get('dietary')
        if dietary:
            filters["dietary"] = dietary
            
        from models.queries_search import search_by_text_embedding
        similar_items = search_by_text_embedding(
            query_text=query_text,
            limit=limit,
            filters=filters
        )
        
        return [
            {
                "id": str(item["id"]),
                "title": item["title"],
                "content_type": "recipe",
                "score": item.get("score", 0)
            }
            for item in similar_items
        ]