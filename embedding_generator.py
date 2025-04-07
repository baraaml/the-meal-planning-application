# embedding_generator.py
from sentence_transformers import SentenceTransformer
from sqlalchemy import text
from db_utils import get_db_engine
import time

def generate_post_embeddings():
    engine = get_db_engine()
    model = SentenceTransformer('all-MiniLM-L6-v2')
    
    with engine.connect() as conn:
        # Get posts that don't have embeddings yet
        result = conn.execute(text("""
            SELECT p.id, p.title, p.content
            FROM "Post" p
            LEFT JOIN content_embeddings ce ON ce.meal_id = p.id AND ce.content_type = 'post'
            WHERE ce.id IS NULL
            LIMIT 500
        """))
        
        posts = result.fetchall()
        print(f"Processing {len(posts)} posts")
        
        for post in posts:
            post_id = post[0]
            title = post[1] or ''
            content = post[2] or ''
            
            # Generate text for embedding
            text_for_embedding = f"{title} {content}"
            
            # Generate embedding
            embedding = model.encode(text_for_embedding)
            
            # Store embedding
            conn.execute(text("""
                INSERT INTO content_embeddings (meal_id, content_type, embedding)
                VALUES (:meal_id, 'post', :embedding)
                ON CONFLICT (meal_id, content_type) 
                DO UPDATE SET embedding = :embedding, updated_at = CURRENT_TIMESTAMP
            """), {
                "meal_id": post_id,
                "embedding": embedding.tolist()
            })
            
            conn.commit()
            
    print("Post embeddings generated")

def generate_community_embeddings():
    engine = get_db_engine()
    model = SentenceTransformer('all-MiniLM-L6-v2')
    
    with engine.connect() as conn:
        # Get communities that don't have embeddings yet
        result = conn.execute(text("""
            SELECT c.id, c.name, c.description
            FROM "Community" c
            LEFT JOIN content_embeddings ce ON ce.meal_id = c.id AND ce.content_type = 'community'
            WHERE ce.id IS NULL
        """))
        
        communities = result.fetchall()
        print(f"Processing {len(communities)} communities")
        
        for community in communities:
            community_id = community[0]
            name = community[1] or ''
            description = community[2] or ''
            
            # Generate text for embedding
            text_for_embedding = f"{name} {description}"
            
            # Generate embedding
            embedding = model.encode(text_for_embedding)
            
            # Store embedding
            conn.execute(text("""
                INSERT INTO content_embeddings (meal_id, content_type, embedding)
                VALUES (:meal_id, 'community', :embedding)
                ON CONFLICT (meal_id, content_type) 
                DO UPDATE SET embedding = :embedding, updated_at = CURRENT_TIMESTAMP
            """), {
                "meal_id": community_id,
                "embedding": embedding.tolist()
            })
            
            conn.commit()
            
    print("Community embeddings generated")

if __name__ == "__main__":
    generate_post_embeddings()
    generate_community_embeddings()