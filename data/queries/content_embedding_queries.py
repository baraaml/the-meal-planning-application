"""
SQL query constants for content embedding operations.
Contains all the queries used by the ContentEmbeddingRepository.
"""

# Query to save or update a content embedding
SAVE_EMBEDDING = """
INSERT INTO content_embeddings (meal_id, content_type, embedding)
VALUES (:meal_id, :content_type, :embedding)
ON CONFLICT (meal_id, content_type) 
DO UPDATE SET embedding = :embedding, updated_at = CURRENT_TIMESTAMP
"""

# Query to get the embedding for specific content
GET_EMBEDDING = """
SELECT embedding
FROM content_embeddings
WHERE meal_id = :meal_id AND content_type = :content_type
"""

# Base query for finding similar content (without filter clauses)
FIND_SIMILAR_CONTENT_BASE = """
SELECT ce.meal_id, ce.content_type,
       CASE WHEN ce.content_type = 'post' THEN p.title
            WHEN ce.content_type = 'community' THEN c.name
       END as title,
       1 - (ce.embedding <=> :embedding) AS similarity
FROM content_embeddings ce
LEFT JOIN "Post" p ON ce.meal_id = p.id AND ce.content_type = 'post'
LEFT JOIN "Community" c ON ce.meal_id = c.id AND ce.content_type = 'community'
WHERE 1=1
{type_filter}
{exclude_clause}
ORDER BY similarity DESC
LIMIT :limit
"""

# Query to get posts that don't have embeddings yet
GET_POSTS_WITHOUT_EMBEDDINGS = """
SELECT p.id, p.title, p.content
FROM "Post" p
LEFT JOIN content_embeddings ce ON ce.meal_id = p.id AND ce.content_type = 'post'
WHERE ce.id IS NULL
LIMIT :limit
"""

# Query to get communities that don't have embeddings yet
GET_COMMUNITIES_WITHOUT_EMBEDDINGS = """
SELECT c.id, c.name, c.description
FROM "Community" c
LEFT JOIN content_embeddings ce ON ce.meal_id = c.id AND ce.content_type = 'community'
WHERE ce.id IS NULL
LIMIT :limit
"""