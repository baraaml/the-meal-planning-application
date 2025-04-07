# main.py
from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy import text
from typing import List, Optional, Dict
from pydantic import BaseModel
from db_utils import get_db_engine
from sentence_transformers import SentenceTransformer
import uvicorn

app = FastAPI(title="Recommendation Service")
model = SentenceTransformer('all-MiniLM-L6-v2')

def get_db():
    engine = get_db_engine()
    db = engine.connect()
    try:
        yield db
    finally:
        db.close()

class ContentItem(BaseModel):
    id: str
    title: Optional[str] = None
    content_type: str
    similarity: Optional[float] = None

class ContentList(BaseModel):
    items: List[ContentItem]

@app.get("/")
def read_root():
    return {"status": "Recommendation service is running"}

@app.get("/recommend/similar/{content_type}/{content_id}", response_model=ContentList)
def get_similar_content(
    content_type: str,
    content_id: str,
    limit: int = 10,
    db = Depends(get_db)
):
    # Validate content type
    if content_type not in ['post', 'community']:
        raise HTTPException(status_code=400, detail="Content type must be 'post' or 'community'")
    
    # Get embedding for the source content
    result = db.execute(
        text("""
            SELECT embedding
            FROM content_embeddings
            WHERE content_id = :content_id AND content_type = :content_type
        """),
        {"content_id": content_id, "content_type": content_type}
    )
    
    item = result.fetchone()
    if not item:
        raise HTTPException(status_code=404, detail=f"{content_type} not found or embedding not generated")
    
    embedding = item[0]
    
    # Find similar content
    result = db.execute(
        text(f"""
            SELECT ce.content_id, ce.content_type,
                   CASE WHEN ce.content_type = 'post' THEN p.title
                        WHEN ce.content_type = 'community' THEN c.name
                   END as title,
                   1 - (ce.embedding <=> :embedding) AS similarity
            FROM content_embeddings ce
            LEFT JOIN "Post" p ON ce.content_id = p.id AND ce.content_type = 'post'
            LEFT JOIN "Community" c ON ce.content_id = c.id AND ce.content_type = 'community'
            WHERE ce.content_id != :content_id
            ORDER BY similarity DESC
            LIMIT :limit
        """),
        {"embedding": embedding, "content_id": content_id, "limit": limit}
    )
    
    items = []
    for row in result:
        items.append({
            "id": row[0],
            "content_type": row[1],
            "title": row[2],
            "similarity": row[3]
        })
    
    return {"items": items}

@app.get("/recommend/user/{user_id}", response_model=ContentList)
def get_user_recommendations(
    user_id: str,
    content_type: Optional[str] = None,
    limit: int = 10,
    db = Depends(get_db)
):
    # Get user's interactions
    type_filter = ""
    params = {"user_id": user_id, "limit": limit}
    
    if content_type:
        if content_type not in ['post', 'community']:
            raise HTTPException(status_code=400, detail="Content type must be 'post' or 'community'")
        type_filter = "AND content_type = :content_type"
        params["content_type"] = content_type
    
    # First try collaborative filtering based on what similar users interacted with
    result = db.execute(
        text(f"""
            -- Find users with similar interaction patterns
            WITH similar_users AS (
                SELECT DISTINCT ri2.user_id
                FROM recommendation_interactions ri1
                JOIN recommendation_interactions ri2 ON ri1.content_id = ri2.content_id 
                                                  AND ri1.content_type = ri2.content_type
                WHERE ri1.user_id = :user_id
                AND ri2.user_id != :user_id
                GROUP BY ri2.user_id
                HAVING COUNT(DISTINCT ri1.content_id) > 2
                LIMIT 10
            ),
            -- Get content these similar users interacted with
            candidate_content AS (
                SELECT DISTINCT ri.content_id, ri.content_type, COUNT(*) as interaction_count
                FROM recommendation_interactions ri
                JOIN similar_users su ON ri.user_id = su.user_id
                WHERE ri.content_id NOT IN (
                    SELECT content_id 
                    FROM recommendation_interactions 
                    WHERE user_id = :user_id
                )
                {type_filter}
                GROUP BY ri.content_id, ri.content_type
                ORDER BY interaction_count DESC
                LIMIT :limit
            )
            -- Get content details
            SELECT cc.content_id, cc.content_type,
                   CASE WHEN cc.content_type = 'post' THEN p.title
                        WHEN cc.content_type = 'community' THEN c.name
                   END as title
            FROM candidate_content cc
            LEFT JOIN "Post" p ON cc.content_id = p.id AND cc.content_type = 'post'
            LEFT JOIN "Community" c ON cc.content_id = c.id AND cc.content_type = 'community'
        """),
        params
    )
    
    items = []
    for row in result:
        items.append({
            "id": row[0],
            "content_type": row[1],
            "title": row[2]
        })
    
    # If we didn't get enough recommendations from collaborative filtering,
    # fall back to content-based recommendations
    if len(items) < limit:
        remaining = limit - len(items)
        
        # Get user's most recent interactions
        result = db.execute(
            text("""
                SELECT content_id, content_type
                FROM recommendation_interactions
                WHERE user_id = :user_id
                ORDER BY created_at DESC
                LIMIT 1
            """),
            {"user_id": user_id}
        )
        
        recent = result.fetchone()
        
        # If user has recent interactions, get similar content
        if recent:
            content_id, content_type = recent
            
            # Get similar content embedding
            result = db.execute(
                text("""
                    SELECT embedding
                    FROM content_embeddings
                    WHERE content_id = :content_id AND content_type = :content_type
                """),
                {"content_id": content_id, "content_type": content_type}
            )
            
            embedding_row = result.fetchone()
            
            if embedding_row:
                # Exclude already recommended items
                existing_ids = [item["id"] for item in items]
                exclude_clause = ""
                exclude_params = {}
                
                if existing_ids:
                    placeholder_list = ','.join([f':exclude_{i}' for i in range(len(existing_ids))])
                    exclude_clause = f"AND ce.content_id NOT IN ({placeholder_list})"
                    for i, id_val in enumerate(existing_ids):
                        exclude_params[f"exclude_{i}"] = id_val
                
                type_filter = ""
                if content_type:
                    type_filter = "AND ce.content_type = :content_type"
                
                result = db.execute(
                    text(f"""
                        SELECT ce.content_id, ce.content_type,
                               CASE WHEN ce.content_type = 'post' THEN p.title
                                    WHEN ce.content_type = 'community' THEN c.name
                               END as title,
                               1 - (ce.embedding <=> :embedding) AS similarity
                        FROM content_embeddings ce
                        LEFT JOIN "Post" p ON ce.content_id = p.id AND ce.content_type = 'post'
                        LEFT JOIN "Community" c ON ce.content_id = c.id AND ce.content_type = 'community'
                        WHERE ce.content_id != :content_id
                        {exclude_clause}
                        {type_filter}
                        ORDER BY similarity DESC
                        LIMIT :limit
                    """),
                    {
                        "embedding": embedding_row[0], 
                        "content_id": content_id, 
                        "content_type": content_type,
                        "limit": remaining,
                        **exclude_params
                    }
                )
                
                for row in result:
                    items.append({
                        "id": row[0],
                        "content_type": row[1],
                        "title": row[2],
                        "similarity": row[3]
                    })
    
    # If still not enough recommendations, get popular content
    if len(items) < limit:
        remaining = limit - len(items)
        existing_ids = [item["id"] for item in items]
        
        exclude_clause = ""
        exclude_params = {}
        
        if existing_ids:
            placeholder_list = ','.join([f':exclude_{i}' for i in range(len(existing_ids))])
            exclude_clause = f"AND content_id NOT IN ({placeholder_list})"
            for i, id_val in enumerate(existing_ids):
                exclude_params[f"exclude_{i}"] = id_val
        
        type_filter = ""
        if content_type:
            type_filter = "AND content_type = :content_type"
            
        result = db.execute(
            text(f"""
                SELECT ri.content_id, ri.content_type,
                       CASE WHEN ri.content_type = 'post' THEN p.title
                            WHEN ri.content_type = 'community' THEN c.name
                       END as title,
                       COUNT(*) as popularity
                FROM recommendation_interactions ri
                LEFT JOIN "Post" p ON ri.content_id = p.id AND ri.content_type = 'post'
                LEFT JOIN "Community" c ON ri.content_id = c.id AND ri.content_type = 'community'
                WHERE 1=1
                {exclude_clause}
                {type_filter}
                GROUP BY ri.content_id, ri.content_type, p.title, c.name
                ORDER BY popularity DESC
                LIMIT :limit
            """),
            {"limit": remaining, **exclude_params, **({"content_type": content_type} if content_type else {})}
        )
        
        for row in result:
            items.append({
                "id": row[0],
                "content_type": row[1],
                "title": row[2]
            })
    
    return {"items": items}

class Interaction(BaseModel):
    user_id: str
    content_id: str
    content_type: str
    interaction_type: str

@app.post("/interactions")
def record_interaction(interaction: Interaction, db = Depends(get_db)):
    # Validate content type
    if interaction.content_type not in ['post', 'community', 'comment']:
        raise HTTPException(status_code=400, detail="Content type must be 'post', 'community', or 'comment'")
    
    # Record the interaction
    db.execute(
        text("""
            INSERT INTO recommendation_interactions
            (user_id, content_id, content_type, interaction_type)
            VALUES (:user_id, :content_id, :content_type, :interaction_type)
        """),
        {
            "user_id": interaction.user_id,
            "content_id": interaction.content_id,
            "content_type": interaction.content_type,
            "interaction_type": interaction.interaction_type
        }
    )
    db.commit()
    
    return {"status": "recorded"}

@app.get("/trending/{content_type}", response_model=ContentList)
def get_trending_content(
    content_type: str,
    time_window: str = "day",  # day, week, month
    limit: int = 10,
    db = Depends(get_db)
):
    # Validate content type
    if content_type not in ['post', 'community', 'all']:
        raise HTTPException(status_code=400, detail="Content type must be 'post', 'community', or 'all'")
    
    # Determine time interval
    time_clause = "NOW() - INTERVAL '1 day'"
    if time_window == "week":
        time_clause = "NOW() - INTERVAL '7 days'"
    elif time_window == "month":
        time_clause = "NOW() - INTERVAL '30 days'"
    
    type_filter = ""
    if content_type != 'all':
        type_filter = "AND ri.content_type = :content_type"
    
    # Get trending content based on recent interactions
    result = db.execute(
        text(f"""
            SELECT ri.content_id, ri.content_type,
                   CASE WHEN ri.content_type = 'post' THEN p.title
                        WHEN ri.content_type = 'community' THEN c.name
                   END as title,
                   COUNT(*) as popularity
            FROM recommendation_interactions ri
            LEFT JOIN "Post" p ON ri.content_id = p.id AND ri.content_type = 'post'
            LEFT JOIN "Community" c ON ri.content_id = c.id AND ri.content_type = 'community'
            WHERE ri.created_at > {time_clause}
            {type_filter}
            GROUP BY ri.content_id, ri.content_type, p.title, c.name
            ORDER BY popularity DESC
            LIMIT :limit
        """),
        {"limit": limit, **({"content_type": content_type} if content_type != 'all' else {})}
    )
    
    items = []
    for row in result:
        items.append({
            "id": row[0],
            "content_type": row[1],
            "title": row[2],
            "popularity": row[3]
        })
    
    return {"items": items}

@app.get("/recommend/category/{category_id}", response_model=ContentList)
def get_category_recommendations(
    category_id: str,
    limit: int = 10,
    db = Depends(get_db)
):
    # Get communities in this category
    result = db.execute(
        text("""
            SELECT c.id, c.name
            FROM "Community" c
            JOIN "CommunityCategory" cc ON c.id = cc."communityId"
            WHERE cc."categoryId" = :category_id
            LIMIT :limit
        """),
        {"category_id": category_id, "limit": limit}
    )
    
    items = []
    for row in result:
        items.append({
            "id": row[0],
            "content_type": "community",
            "title": row[1]
        })
    
    return {"items": items}

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)