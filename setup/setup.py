"""
Database setup script.
Creates necessary database tables and indexes for the recommendation system.
"""
import logging
from sqlalchemy import text

from data.database import execute_query, get_transaction, test_connection
from config import EMBEDDING_DIMENSION
from data.queries import (
    ENABLE_PGVECTOR,
    CREATE_CONTENT_EMBEDDINGS_TABLE,
    CREATE_EMBEDDINGS_INDEX,
    CREATE_INTERACTIONS_TABLE,
    CREATE_INTERACTIONS_INDEXES,
    CREATE_USER_DIETARY_PREFERENCE_TABLE,
    ENABLE_TRIGRAM
)

logger = logging.getLogger(__name__)

def create_recommendation_tables():
    """Create all necessary tables and indexes for the recommendation system."""
    logger.info("Creating recommendation database tables and indexes...")
    
    try:
        with get_transaction() as conn:
            # Enable pgvector extension
            conn.execute(text(ENABLE_PGVECTOR))
            
            # Enable trigram extension for text search
            conn.execute(text(ENABLE_TRIGRAM))
            
            # Create a separate table for content embeddings
            # Format the query with the embedding dimension
            formatted_embeddings_table = CREATE_CONTENT_EMBEDDINGS_TABLE.format(
                embedding_dimension=EMBEDDING_DIMENSION
            )
            conn.execute(text(formatted_embeddings_table))
            
            # Create index for vector search
            conn.execute(text(CREATE_EMBEDDINGS_INDEX))
            
            # Create interactions table
            conn.execute(text(CREATE_INTERACTIONS_TABLE))
            
            # Create indexes for quick lookups
            conn.execute(text(CREATE_INTERACTIONS_INDEXES))
            
            # Create user dietary preferences table
            conn.execute(text(CREATE_USER_DIETARY_PREFERENCE_TABLE))
        
        logger.info("Database tables and indexes created successfully")
        return True
    except Exception as e:
        logger.error(f"Error creating database tables: {e}")
        return False

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    
    # Test database connection
    if test_connection():
        # Create tables
        success = create_recommendation_tables()
        if success:
            logger.info("Database setup completed successfully")
        else:
            logger.error("Database setup failed")
    else:
        logger.error("Database connection test failed. Check your DATABASE_URL in .env file.")