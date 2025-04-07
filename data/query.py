"""
SQL Queries for the meal recommendation service.
All queries are consolidated in this module for easier maintenance.
"""
from config import EMBEDDING_DIMENSION

#######################
# SETUP QUERIES       #
#######################

# Query to enable pgvector extension
ENABLE_PGVECTOR = """
CREATE EXTENSION IF NOT EXISTS vector
"""

# Query to create meal embeddings table
CREATE_CONTENT_EMBEDDINGS_TABLE = """
CREATE TABLE IF NOT EXISTS content_embeddings (
    id SERIAL PRIMARY KEY,
    meal_id TEXT NOT NULL,
    content_type TEXT NOT NULL,  -- 'meal' or 'recipe'
    embedding vector({embedding_dimension}),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(meal_id, content_type)
)
"""

# Query to create index for vector search
CREATE_EMBEDDINGS_INDEX = """
CREATE INDEX IF NOT EXISTS content_embeddings_idx 
ON content_embeddings USING ivfflat (embedding vector_cosine_ops)
"""

# Query to create meal interactions table
CREATE_INTERACTIONS_TABLE = """
CREATE TABLE IF NOT EXISTS recommendation_interactions (
    id SERIAL PRIMARY KEY,
    user_id TEXT NOT NULL,
    meal_id TEXT NOT NULL,
    content_type TEXT NOT NULL,  -- 'meal', 'recipe'
    interaction_type TEXT NOT NULL,  -- 'view', 'like', 'save', 'cook'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
"""

# Query to create indexes for quick lookups
CREATE_INTERACTIONS_INDEXES = """
CREATE INDEX IF NOT EXISTS rec_interactions_user_idx ON recommendation_interactions(user_id);
CREATE INDEX IF NOT EXISTS rec_interactions_meal_idx ON recommendation_interactions(meal_id, content_type);
CREATE INDEX IF NOT EXISTS rec_interactions_type_idx ON recommendation_interactions(interaction_type);
CREATE INDEX IF NOT EXISTS rec_interactions_created_idx ON recommendation_interactions(created_at);
"""

# Query to create cuisine reference table
CREATE_CUISINE_TABLE = """
CREATE TABLE IF NOT EXISTS "Cuisine" (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
)
"""

# Query to create dietary restriction reference table
CREATE_DIETARY_RESTRICTION_TABLE = """
CREATE TABLE IF NOT EXISTS "DietaryRestriction" (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
)
"""

# Query to create user dietary preferences table
CREATE_USER_DIETARY_PREFERENCE_TABLE = """
CREATE TABLE IF NOT EXISTS "UserDietaryPreference" (
    id SERIAL PRIMARY KEY,
    user_id TEXT NOT NULL,
    dietary_restriction_id INTEGER NOT NULL REFERENCES "DietaryRestriction"(id),
    UNIQUE(user_id, dietary_restriction_id)
)
"""

# Query to create meal dietary restrictions table
CREATE_MEAL_DIETARY_RESTRICTION_TABLE = """
CREATE TABLE IF NOT EXISTS "MealDietaryRestriction" (
    id SERIAL PRIMARY KEY,
    meal_id TEXT NOT NULL,
    dietary_restriction_id INTEGER NOT NULL REFERENCES "DietaryRestriction"(id),
    UNIQUE(meal_id, dietary_restriction_id)
)
"""

# Query to create ingredients table
CREATE_INGREDIENT_TABLE = """
CREATE TABLE IF NOT EXISTS "Ingredient" (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
)
"""

# Query to create meal ingredients table
CREATE_MEAL_INGREDIENT_TABLE = """
CREATE TABLE IF NOT EXISTS "MealIngredient" (
    id SERIAL PRIMARY KEY,
    meal_id TEXT NOT NULL,
    ingredient_id INTEGER NOT NULL REFERENCES "Ingredient"(id),
    amount TEXT,
    UNIQUE(meal_id, ingredient_id)
)
"""

# Query to insert default cuisines
INSERT_DEFAULT_CUISINES = """
INSERT INTO "Cuisine" (name) VALUES
('italian'), ('mexican'), ('chinese'), ('indian'), ('american'),
('french'), ('japanese'), ('mediterranean'), ('thai'), ('other')
ON CONFLICT (name) DO NOTHING
"""

# Query to insert default dietary restrictions
INSERT_DEFAULT_DIETARY_RESTRICTIONS = """
INSERT INTO "DietaryRestriction" (name) VALUES
('vegetarian'), ('vegan'), ('gluten-free'), ('dairy-free'),
('keto'), ('paleo'), ('low-carb'), ('low-fat')
ON CONFLICT (name) DO NOTHING
"""

#######################
# EMBEDDING QUERIES   #
#######################

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

# Query to find meals by cuisine for embedding
FIND_MEALS_BY_CUISINE_FOR_EMBEDDING = """
SELECT m.id, m.title, m.description
FROM "Meal" m
WHERE m.cuisine_id = :cuisine_id
AND m.id NOT IN (
    SELECT meal_id FROM content_embeddings WHERE content_type = 'meal'
)
LIMIT :limit
"""

# Query to get total counts of items without embeddings
GET_EMBEDDING_STATS = """
SELECT 
    (SELECT COUNT(*) FROM "Meal" m LEFT JOIN content_embeddings ce 
     ON ce.meal_id = m.id AND ce.content_type = 'meal' WHERE ce.id IS NULL) as meals_without_embeddings,
     
    (SELECT COUNT(*) FROM "Recipe" r LEFT JOIN content_embeddings ce 
     ON ce.meal_id = r.id AND ce.content_type = 'recipe' WHERE ce.id IS NULL) as recipes_without_embeddings,
     
    (SELECT COUNT(*) FROM content_embeddings) as total_embeddings
"""

#######################
# INTERACTION QUERIES #
#######################

# Query to record a user interaction with a meal
RECORD_INTERACTION = """
INSERT INTO recommendation_interactions
(user_id, meal_id, content_type, interaction_type)
VALUES (:user_id, :meal_id, :content_type, :interaction_type)
"""

# Query to get recent interactions for a user
GET_USER_RECENT_INTERACTIONS_BASE = """
SELECT meal_id, content_type, interaction_type, created_at
FROM recommendation_interactions
WHERE user_id = :user_id
{type_filter}
ORDER BY created_at DESC
LIMIT :limit
"""

# Base query for getting trending meals
GET_TRENDING_MEALS_BASE = """
SELECT ri.meal_id, ri.content_type,
       CASE WHEN ri.content_type = 'meal' THEN m.title
            WHEN ri.content_type = 'recipe' THEN r.name
       END as title,
       COUNT(*) as popularity
FROM recommendation_interactions ri
LEFT JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
LEFT JOIN "Recipe" r ON ri.meal_id = r.id AND ri.content_type = 'recipe'
WHERE ri.created_at > {time_clause}
{type_filter}
GROUP BY ri.meal_id, ri.content_type, m.title, r.name
ORDER BY popularity DESC
LIMIT :limit
"""

# Query to find users with similar meal preferences
FIND_SIMILAR_USERS = """
SELECT DISTINCT ri2.user_id
FROM recommendation_interactions ri1
JOIN recommendation_interactions ri2 ON ri1.meal_id = ri2.meal_id 
                                  AND ri1.content_type = ri2.content_type
WHERE ri1.user_id = :user_id
AND ri2.user_id != :user_id
GROUP BY ri2.user_id
HAVING COUNT(DISTINCT ri1.meal_id) > :min_common_items
LIMIT :limit
"""

# Base query for getting meals from similar users
GET_MEALS_FROM_SIMILAR_USERS_BASE = """
SELECT DISTINCT ri.meal_id, ri.content_type, COUNT(*) as interaction_count,
       CASE WHEN ri.content_type = 'meal' THEN m.title
            WHEN ri.content_type = 'recipe' THEN r.name
       END as title
FROM recommendation_interactions ri
LEFT JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
LEFT JOIN "Recipe" r ON ri.meal_id = r.id AND ri.content_type = 'recipe'
WHERE ri.user_id IN ({user_placeholders})
AND ri.meal_id NOT IN (
    SELECT meal_id 
    FROM recommendation_interactions 
    WHERE user_id = :user_id
)
{type_filter}
GROUP BY ri.meal_id, ri.content_type, m.title, r.name
ORDER BY interaction_count DESC
LIMIT :limit
"""

# Query to get user interaction statistics
GET_USER_INTERACTION_STATS = """
SELECT 
    interaction_type,
    COUNT(*) as count,
    MAX(created_at) as latest
FROM recommendation_interactions
WHERE user_id = :user_id
GROUP BY interaction_type
ORDER BY count DESC
"""

# Query to get interaction stats by content type
GET_CONTENT_TYPE_STATS = """
SELECT 
    content_type,
    COUNT(*) as interaction_count,
    COUNT(DISTINCT meal_id) as unique_items,
    COUNT(DISTINCT user_id) as unique_users
FROM recommendation_interactions
GROUP BY content_type
"""

#######################
# RECOMMENDATION QUERIES #
#######################

# Query for finding similar meals based on co-occurrence patterns
FIND_SIMILAR_ITEMS = """
WITH users_who_interacted AS (
    SELECT DISTINCT user_id
    FROM recommendation_interactions
    WHERE meal_id = :meal_id
)
SELECT ri2.meal_id, ri2.content_type,
       CASE WHEN ri2.content_type = 'meal' THEN m.title
            WHEN ri2.content_type = 'recipe' THEN r.name
       END as title,
       COUNT(DISTINCT ri2.user_id) as co_occurrence_count
FROM recommendation_interactions ri2
JOIN users_who_interacted uwi ON ri2.user_id = uwi.user_id
LEFT JOIN "Meal" m ON ri2.meal_id = m.id AND ri2.content_type = 'meal'
LEFT JOIN "Recipe" r ON ri2.meal_id = r.id AND ri2.content_type = 'recipe'
WHERE ri2.meal_id != :meal_id
{type_filter}
GROUP BY ri2.meal_id, ri2.content_type, m.title, r.name
ORDER BY co_occurrence_count DESC
LIMIT :limit
"""

# Query for finding meals with similar ingredients
FIND_MEALS_WITH_SIMILAR_INGREDIENTS = """
WITH meal_ingredients AS (
    SELECT ingredient_id
    FROM "MealIngredient"
    WHERE meal_id = :meal_id
),
ingredient_counts AS (
    SELECT 
        m.id, 
        m.title, 
        COUNT(mi.ingredient_id) AS common_ingredients,
        (SELECT COUNT(*) FROM "MealIngredient" WHERE meal_id = :meal_id) AS source_ingredient_count
    FROM "Meal" m
    JOIN "MealIngredient" mi ON m.id = mi.meal_id
    JOIN meal_ingredients src ON mi.ingredient_id = src.ingredient_id
    WHERE m.id != :meal_id
    GROUP BY m.id, m.title
)
SELECT 
    id, 
    title, 
    common_ingredients,
    common_ingredients::float / source_ingredient_count::float AS similarity_score
FROM ingredient_counts
ORDER BY similarity_score DESC, common_ingredients DESC
LIMIT :limit
"""

# Query for getting meals by cuisine
GET_CUISINE_MEALS = """
SELECT m.id, m.title
FROM "Meal" m
WHERE m.cuisine_id = :cuisine_id
LIMIT :limit
"""

# Query for getting meals by dietary preference
GET_DIETARY_PREFERENCE_MEALS = """
SELECT m.id, m.title
FROM "Meal" m
JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
WHERE mdr.dietary_restriction_id = :dietary_restriction_id
LIMIT :limit
"""

# Query for getting a user's dietary preferences
GET_USER_DIETARY_PREFERENCES = """
SELECT dr.id, dr.name
FROM "UserDietaryPreference" udp
JOIN "DietaryRestriction" dr ON udp.dietary_restriction_id = dr.id
WHERE udp.user_id = :user_id
"""

# Query for getting similar meals based on ingredients
GET_SIMILAR_MEALS_BY_INGREDIENTS = """
SELECT m1.id, m1.title, COUNT(mi1.ingredient_id) as ingredient_match_count
FROM "Meal" m1
JOIN "MealIngredient" mi1 ON m1.id = mi1.meal_id
JOIN "MealIngredient" mi2 ON mi1.ingredient_id = mi2.ingredient_id
WHERE mi2.meal_id = :meal_id
AND m1.id != :meal_id
GROUP BY m1.id, m1.title
ORDER BY ingredient_match_count DESC
LIMIT :limit
"""

# Query for getting meal ingredients
GET_MEAL_INGREDIENTS = """
SELECT i.id, i.name, mi.amount
FROM "MealIngredient" mi
JOIN "Ingredient" i ON mi.ingredient_id = i.id
WHERE mi.meal_id = :meal_id
"""

# Query for getting recipe ingredients
GET_RECIPE_INGREDIENTS = """
SELECT i.id, i.name, ri.amount
FROM "RecipeIngredient" ri
JOIN "Ingredient" i ON ri.ingredient_id = i.id
WHERE ri.recipe_id = :recipe_id
"""

# Query for getting personalized meal recommendations based on dietary preferences
GET_MEALS_BY_USER_DIETARY_PREFERENCES = """
SELECT DISTINCT m.id, m.title, m.description
FROM "Meal" m
JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
JOIN "UserDietaryPreference" udp ON mdr.dietary_restriction_id = udp.dietary_restriction_id
WHERE udp.user_id = :user_id
ORDER BY RANDOM()
LIMIT :limit
"""

# Query for getting meals with high popularity
GET_POPULAR_MEALS = """
SELECT m.id, m.title, COUNT(ri.id) as popularity
FROM "Meal" m
JOIN recommendation_interactions ri ON m.id = ri.meal_id AND ri.content_type = 'meal'
WHERE ri.created_at > NOW() - INTERVAL :time_window
GROUP BY m.id, m.title
HAVING COUNT(ri.id) > :min_interactions
ORDER BY popularity DESC
LIMIT :limit
"""

# Query for getting meals by time of day
GET_MEALS_BY_TIME_OF_DAY = """
SELECT m.id, m.title, m.description
FROM "Meal" m
WHERE m.meal_type = :meal_type
ORDER BY 
    CASE WHEN m.cuisine_id = :preferred_cuisine_id THEN 0 ELSE 1 END,
    RANDOM()
LIMIT :limit
"""

# Query for getting low-carb or low-calorie meals
GET_HEALTHY_MEALS = """
SELECT m.id, m.title, m.calories
FROM "Meal" m
WHERE 
    (m.calories IS NULL OR m.calories < :max_calories)
    AND (:low_carb = FALSE OR m.id IN (
        SELECT meal_id FROM "MealDietaryRestriction" 
        WHERE dietary_restriction_id IN (
            SELECT id FROM "DietaryRestriction" WHERE name IN ('low-carb', 'keto')
        )
    ))
ORDER BY RANDOM()
LIMIT :limit
"""

# Query for finding meals suitable for meal planning
GET_MEAL_PLAN_RECOMMENDATIONS = """
WITH user_favorite_cuisines AS (
    SELECT DISTINCT m.cuisine_id
    FROM recommendation_interactions ri
    JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
    WHERE ri.user_id = :user_id
    GROUP BY m.cuisine_id
    ORDER BY COUNT(*) DESC
    LIMIT 3
)
SELECT m.id, m.title, m.meal_type, m.prep_time, m.cuisine_id
FROM "Meal" m
LEFT JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
LEFT JOIN "UserDietaryPreference" udp ON mdr.dietary_restriction_id = udp.dietary_restriction_id AND udp.user_id = :user_id
WHERE 
    (m.meal_type = :meal_type OR :meal_type IS NULL)
    AND (m.cuisine_id IN (SELECT cuisine_id FROM user_favorite_cuisines) OR :ignore_cuisine = TRUE)
    AND (udp.id IS NOT NULL OR :ignore_preferences = TRUE)
    AND (m.prep_time <= :max_prep_time OR :max_prep_time IS NULL)
ORDER BY 
    CASE WHEN udp.id IS NOT NULL THEN 0 ELSE 1 END,
    CASE WHEN m.cuisine_id IN (SELECT cuisine_id FROM user_favorite_cuisines) THEN 0 ELSE 1 END,
    RANDOM()
LIMIT :limit
"""