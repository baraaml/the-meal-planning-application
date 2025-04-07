"""
Embedding generator service.
Generates and stores vector embeddings for different content types.
"""
from sentence_transformers import SentenceTransformer
from typing import List, Dict, Any, Optional
import time
import logging

from config.settings import EMBEDDING_MODEL
from data.repositories.content_embedding_repository import ContentEmbeddingRepository

logger = logging.getLogger(__name__)

class EmbeddingGenerator:
    """Service for generating and managing content embeddings."""
    
    def __init__(self, model_name: str = EMBEDDING_MODEL):
        """
        Initialize the embedding generator.
        
        Args:
            model_name: The name of the sentence transformer model to use
        """
        self.model = SentenceTransformer(model_name)
        self.repository = ContentEmbeddingRepository()
    
    def generate_post_embeddings(self, batch_size: int = 500) -> int:
        """
        Generate embeddings for posts that don't have them yet.
        
        Args:
            batch_size: Maximum number of posts to process at once
            
        Returns:
            Number of posts processed
        """
        posts = self.repository.get_content_without_embeddings('post', batch_size)
        logger.info(f"Processing {len(posts)} posts for embedding generation")
        
        count = 0
        for post_id, title, content in posts:
            # Generate text for embedding
            text_for_embedding = f"{title} {content}"
            
            # Generate embedding
            embedding = self.model.encode(text_for_embedding)
            
            # Store embedding
            if self.repository.save_embedding(post_id, 'post', embedding.tolist()):
                count += 1
            
        logger.info(f"Generated embeddings for {count} posts")
        return count
    
    def generate_community_embeddings(self, batch_size: int = 500) -> int:
        """
        Generate embeddings for communities that don't have them yet.
        
        Args:
            batch_size: Maximum number of communities to process at once
            
        Returns:
            Number of communities processed
        """
        communities = self.repository.get_content_without_embeddings('community', batch_size)
        logger.info(f"Processing {len(communities)} communities for embedding generation")
        
        count = 0
        for community_id, name, description in communities:
            # Generate text for embedding
            text_for_embedding = f"{name} {description}"
            
            # Generate embedding
            embedding = self.model.encode(text_for_embedding)
            
            # Store embedding
            if self.repository.save_embedding(community_id, 'community', embedding.tolist()):
                count += 1
            
        logger.info(f"Generated embeddings for {count} communities")
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
    
    def generate_embedding_for_content(self, meal_id: str, content_type: str, text: str) -> bool:
        """
        Generate and store an embedding for specific content.
        
        Args:
            meal_id: The ID of the content
            content_type: The type of content
            text: The text to encode
            
        Returns:
            Success status
        """
        embedding = self.model.encode(text)
        return self.repository.save_embedding(meal_id, content_type, embedding.tolist())
    
    def generate_all_embeddings(self) -> Dict[str, int]:
        """
        Generate embeddings for all content types.
        
        Returns:
            Dictionary with counts of processed items by type
        """
        post_count = self.generate_post_embeddings()
        community_count = self.generate_community_embeddings()
        
        return {
            "posts": post_count,
            "communities": community_count
        }


# For command-line execution
if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    generator = EmbeddingGenerator()
    result = generator.generate_all_embeddings()
    print(f"Generated embeddings: {result}")