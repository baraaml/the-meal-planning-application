"""
SQL query constants for database setup.
Contains all the queries used for creating tables and indexes.
"""

# Query to enable pgvector extension
ENABLE_PGVECTOR = """
CREATE EXTENSION IF NOT EXISTS vector
"""

# Query to create content embeddings table
CREATE_CONTENT_EMBEDDINGS_TABLE = """
CREATE TABLE IF NOT EXISTS content_embeddings (
    id SERIAL PRIMARY KEY,
    content_id TEXT NOT NULL,
    content_type TEXT NOT NULL,  -- 'post' or 'community'
    embedding vector({embedding_dimension}),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(content_id, content_type)
)
"""

# Query to create index for vector search
CREATE_EMBEDDINGS_INDEX = """
CREATE INDEX IF NOT EXISTS content_embeddings_idx 
ON content_embeddings USING ivfflat (embedding vector_cosine_ops)
"""

# Query to create recommendation interactions table
CREATE_INTERACTIONS_TABLE = """
CREATE TABLE IF NOT EXISTS recommendation_interactions (
    id SERIAL PRIMARY KEY,
    user_id TEXT NOT NULL,
    content_id TEXT NOT NULL,
    content_type TEXT NOT NULL,  -- 'post', 'community', 'comment'
    interaction_type TEXT NOT NULL,  -- 'view', 'click', 'vote', etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
"""

# Query to create indexes for quick lookups
CREATE_INTERACTIONS_INDEXES = """
CREATE INDEX IF NOT EXISTS rec_interactions_user_idx ON recommendation_interactions(user_id);
CREATE INDEX IF NOT EXISTS rec_interactions_content_idx ON recommendation_interactions(content_id, content_type);
CREATE INDEX IF NOT EXISTS rec_interactions_type_idx ON recommendation_interactions(interaction_type);
CREATE INDEX IF NOT EXISTS rec_interactions_created_idx ON recommendation_interactions(created_at);
"""