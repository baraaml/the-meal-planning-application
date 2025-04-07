"""
SQL query constants for meal embedding operations.
Contains all the queries used by the ContentEmbeddingRepository.
"""

# Query to save or update a meal embedding
SAVE_EMBEDDING = """
INSERT INTO content_embeddings (meal_id, content_type, embedding)
VALUES (:meal_id, :content_type, :embedding)
ON CONFLICT (meal_id, content_type) 
DO UPDATE SET embedding = :embedding, updated_at = CURRENT_TIMESTAMP
"""

# Query to get the embedding for specific meal
GET_EMBEDDING = """
SELECT embedding
FROM content_embeddings
WHERE meal_id = :meal_id AND content_type = :content_type
"""

# Base query for finding similar meals (without filter clauses)
FIND_SIMILAR_CONTENT_BASE = """
SELECT ce.meal_id, ce.content_type,
       CASE WHEN ce.content_type = 'meal' THEN m.title
            WHEN ce.content_type = 'recipe' THEN r.name
       END as title,
       1 - (ce.embedding <=> :embedding) AS similarity
FROM content_embeddings ce
LEFT JOIN "Meal" m ON ce.meal_id = m.id AND ce.content_type = 'meal'
LEFT JOIN "Recipe" r ON ce.meal_id = r.id AND ce.content_type = 'recipe'
WHERE 1=1
{type_filter}
{exclude_clause}
ORDER BY similarity DESC
LIMIT :limit
"""

# Query to get meals that don't have embeddings yet
GET_MEALS_WITHOUT_EMBEDDINGS = """
SELECT m.id, m.title, m.description
FROM "Meal" m
LEFT JOIN content_embeddings ce ON ce.meal_id = m.id AND ce.content_type = 'meal'
WHERE ce.id IS NULL
LIMIT :limit
"""

# Query to get recipes that don't have embeddings yet
GET_RECIPES_WITHOUT_EMBEDDINGS = """
SELECT r.id, r.name, r.instructions
FROM "Recipe" r
LEFT JOIN content_embeddings ce ON ce.meal_id = r.id AND ce.content_type = 'recipe'
WHERE ce.id IS NULL
LIMIT :limit
"""

# Query to find meals by cuisine
FIND_MEALS_BY_CUISINE = """
SELECT m.id, m.title, ce.embedding
FROM "Meal" m
JOIN content_embeddings ce ON ce.meal_id = m.id AND ce.content_type = 'meal'
WHERE m.cuisine_id = :cuisine_id
LIMIT :limit
"""

# Query to find meals by dietary preferences
FIND_MEALS_BY_DIETARY_PREFERENCE = """
SELECT m.id, m.title, ce.embedding
FROM "Meal" m
JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
JOIN content_embeddings ce ON ce.meal_id = m.id AND ce.content_type = 'meal'
WHERE mdr.dietary_restriction_id = :dietary_restriction_id
LIMIT :limit
"""