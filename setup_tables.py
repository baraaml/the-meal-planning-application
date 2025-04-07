# setup_tables.py
from sqlalchemy import text
from db_utils import get_db_engine

def create_recommendation_tables():
    engine = get_db_engine()
    
    with engine.connect() as conn:
        # Enable pgvector extension
        conn.execute(text("CREATE EXTENSION IF NOT EXISTS vector"))
        conn.commit()
        
        # Create a separate table for content embeddings (doesn't modify your schema)
        conn.execute(text("""
            CREATE TABLE IF NOT EXISTS content_embeddings (
                id SERIAL PRIMARY KEY,
                content_id TEXT NOT NULL,
                content_type TEXT NOT NULL,  -- 'post' or 'community'
                embedding vector(768),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(content_id, content_type)
            )
        """))
        
        # Create index for vector search
        conn.execute(text("""
            CREATE INDEX IF NOT EXISTS content_embeddings_idx 
            ON content_embeddings USING ivfflat (embedding vector_cosine_ops)
        """))
        
        # Create a table for tracking user interactions (if you don't want to use your existing tables)
        conn.execute(text("""
            CREATE TABLE IF NOT EXISTS recommendation_interactions (
                id SERIAL PRIMARY KEY,
                user_id TEXT NOT NULL,
                content_id TEXT NOT NULL,
                content_type TEXT NOT NULL,  -- 'post', 'community', 'comment'
                interaction_type TEXT NOT NULL,  -- 'view', 'click', 'vote', etc.
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """))
        
        # Create indexes for quick lookups
        conn.execute(text("""
            CREATE INDEX IF NOT EXISTS rec_interactions_user_idx ON recommendation_interactions(user_id);
            CREATE INDEX IF NOT EXISTS rec_interactions_content_idx ON recommendation_interactions(content_id, content_type);
            CREATE INDEX IF NOT EXISTS rec_interactions_type_idx ON recommendation_interactions(interaction_type);
        """))
        
        conn.commit()
        print("Recommendation tables created successfully")

if __name__ == "__main__":
    create_recommendation_tables()