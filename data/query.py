"""
SQL Queries for the meal recommendation service.
Optimized for production use with improved indexing and query performance.
"""
from config import EMBEDDING_DIMENSION

#######################
# SETUP QUERIES       #
#######################

# Query to enable pgvector extension
ENABLE_PGVECTOR = """
CREATE EXTENSION IF NOT EXISTS vector
"""

# Query to create meal embeddings table with optimized index
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

# Create optimized index for vector search with IVFFlat indexing
CREATE_EMBEDDINGS_INDEX = """
CREATE INDEX IF NOT EXISTS content_embeddings_vector_idx 
ON content_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

CREATE INDEX IF NOT EXISTS content_embeddings_content_type_idx
ON content_embeddings (content_type);

CREATE INDEX IF NOT EXISTS content_embeddings_meal_id_idx
ON content_embeddings (meal_id);
"""

# Optimized interactions table
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

# Create optimized indexes for interactions
CREATE_INTERACTIONS_INDEXES = """
-- Index for user lookups (most common query pattern)
CREATE INDEX IF NOT EXISTS rec_interactions_user_idx ON recommendation_interactions(user_id);

-- Composite index for meal+content lookups
CREATE INDEX IF NOT EXISTS rec_interactions_meal_content_idx 
ON recommendation_interactions(meal_id, content_type);

-- Index for filtering by interaction type
CREATE INDEX IF NOT EXISTS rec_interactions_type_idx ON recommendation_interactions(interaction_type);

-- Index for time-based queries with BRIN for better performance on timestamp data
CREATE INDEX IF NOT EXISTS rec_interactions_created_brin_idx 
ON recommendation_interactions USING BRIN (created_at);
"""

# Query to create user dietary preferences table
CREATE_USER_DIETARY_PREFERENCE_TABLE = """
CREATE TABLE IF NOT EXISTS "UserDietaryPreference" (
    id SERIAL PRIMARY KEY,
    user_id TEXT NOT NULL,
    dietary_restriction TEXT NOT NULL,
    UNIQUE(user_id, dietary_restriction)
);

CREATE INDEX IF NOT EXISTS user_dietary_pref_user_idx
ON "UserDietaryPreference"(user_id);
"""

# Enable trigram indexing for text search
ENABLE_TRIGRAM = """
CREATE EXTENSION IF NOT EXISTS pg_trgm
"""

#######################
# EMBEDDING QUERIES   #
#######################

# Query to save or update a meal embedding with optimized upsert
SAVE_EMBEDDING = """
INSERT INTO content_embeddings (meal_id, content_type, embedding)
VALUES (:meal_id, :content_type, :embedding)
ON CONFLICT (meal_id, content_type) 
DO UPDATE SET 
    embedding = :embedding, 
    updated_at = CURRENT_TIMESTAMP
"""

# Query to get the embedding for specific meal
GET_EMBEDDING = """
SELECT embedding
FROM content_embeddings
WHERE meal_id = :meal_id AND content_type = :content_type
"""

# Base query for finding similar meals (optimized with CTE)
FIND_SIMILAR_CONTENT_BASE = """
WITH candidate_embeddings AS (
    SELECT ce.meal_id, ce.content_type,
           CASE WHEN ce.content_type = 'meal' THEN m.Recipe_title
                WHEN ce.content_type = 'recipe' THEN r.Recipe_title
           END as title,
           1 - (ce.embedding <=> :embedding) AS similarity
    FROM content_embeddings ce
    LEFT JOIN "Meal" m ON ce.meal_id = m.Recipe_id AND ce.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ce.meal_id = r.Recipe_id AND ce.content_type = 'recipe'
    WHERE 1=1
    {type_filter}
    {exclude_clause}
    ORDER BY similarity DESC
    LIMIT :limit * 2
)
SELECT meal_id, content_type, title, similarity
FROM candidate_embeddings
WHERE similarity > 0.5
ORDER BY similarity DESC
LIMIT :limit
"""

# Query to get meals that don't have embeddings yet
GET_MEALS_WITHOUT_EMBEDDINGS = """
SELECT m.Recipe_id, m.Recipe_title, m.description
FROM "Meal" m
LEFT JOIN content_embeddings ce ON ce.meal_id = m.Recipe_id AND ce.content_type = 'meal'
WHERE ce.id IS NULL
LIMIT :limit
"""

# Query to get recipes that don't have embeddings yet
GET_RECIPES_WITHOUT_EMBEDDINGS = """
SELECT r.Recipe_id, r.Recipe_title, r.instructions
FROM "Recipe" r
LEFT JOIN content_embeddings ce ON ce.meal_id = r.Recipe_id AND ce.content_type = 'recipe'
WHERE ce.id IS NULL
LIMIT :limit
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
WITH trending AS (
    SELECT ri.meal_id, ri.content_type,
           CASE WHEN ri.content_type = 'meal' THEN m.Recipe_title
                WHEN ri.content_type = 'recipe' THEN r.Recipe_title
           END as title,
           COUNT(*) as popularity,
           ROW_NUMBER() OVER (PARTITION BY ri.content_type ORDER BY COUNT(*) DESC) as rank
    FROM recommendation_interactions ri
    LEFT JOIN "Meal" m ON ri.meal_id = m.Recipe_id AND ri.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ri.meal_id = r.Recipe_id AND ri.content_type = 'recipe'
    WHERE ri.created_at > {time_clause}
    {type_filter}
    GROUP BY ri.meal_id, ri.content_type, m.Recipe_title, r.Recipe_title
)
SELECT meal_id, content_type, title, popularity
FROM trending
WHERE rank <= :limit
ORDER BY popularity DESC
"""

# Query to find users with similar meal preferences
FIND_SIMILAR_USERS = """
WITH user_items AS (
    SELECT meal_id, content_type
    FROM recommendation_interactions
    WHERE user_id = :user_id
),
similar_users AS (
    SELECT ri.user_id,
           COUNT(DISTINCT ri.meal_id) as common_items,
           ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT ri.meal_id) DESC) as rank
    FROM recommendation_interactions ri
    JOIN user_items ui ON ri.meal_id = ui.meal_id AND ri.content_type = ui.content_type
    WHERE ri.user_id != :user_id
    GROUP BY ri.user_id
    HAVING COUNT(DISTINCT ri.meal_id) > :min_common_items
)
SELECT user_id
FROM similar_users
WHERE rank <= :limit
"""

# Base query for getting meals from similar users
GET_MEALS_FROM_SIMILAR_USERS_BASE = """
WITH user_recommendations AS (
    SELECT ri.meal_id, ri.content_type, COUNT(*) as interaction_count,
           CASE WHEN ri.content_type = 'meal' THEN m.Recipe_title
                WHEN ri.content_type = 'recipe' THEN r.Recipe_title
           END as title,
           ROW_NUMBER() OVER (ORDER BY COUNT(*) DESC) as rank
    FROM recommendation_interactions ri
    LEFT JOIN "Meal" m ON ri.meal_id = m.Recipe_id AND ri.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ri.meal_id = r.Recipe_id AND ri.content_type = 'recipe'
    WHERE ri.user_id IN ({user_placeholders})
    AND NOT EXISTS (
        SELECT 1 
        FROM recommendation_interactions ri2
        WHERE ri2.user_id = :user_id
        AND ri2.meal_id = ri.meal_id
        AND ri2.content_type = ri.content_type
    )
    {type_filter}
    GROUP BY ri.meal_id, ri.content_type, m.Recipe_title, r.Recipe_title
)
SELECT meal_id, content_type, interaction_count, title
FROM user_recommendations
WHERE rank <= :limit
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
),
similar_items AS (
    SELECT ri2.meal_id, ri2.content_type,
           CASE WHEN ri2.content_type = 'meal' THEN m.Recipe_title
                WHEN ri2.content_type = 'recipe' THEN r.Recipe_title
           END as title,
           COUNT(DISTINCT ri2.user_id) as co_occurrence_count,
           ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT ri2.user_id) DESC) as rank
    FROM recommendation_interactions ri2
    JOIN users_who_interacted uwi ON ri2.user_id = uwi.user_id
    LEFT JOIN "Meal" m ON ri2.meal_id = m.Recipe_id AND ri2.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ri2.meal_id = r.Recipe_id AND ri2.content_type = 'recipe'
    WHERE ri2.meal_id != :meal_id
    {type_filter}
    GROUP BY ri2.meal_id, ri2.content_type, m.Recipe_title, r.Recipe_title
)
SELECT meal_id, content_type, title, co_occurrence_count
FROM similar_items
WHERE rank <= :limit
"""

# Query for finding meals with similar ingredients
FIND_MEALS_WITH_SIMILAR_INGREDIENTS = """
WITH meal_ingredients AS (
    SELECT ingredient_id
    FROM "MealIngredient"
    WHERE meal_id = :meal_id
),
source_count AS (
    SELECT COUNT(*) AS count
    FROM meal_ingredients
),
ingredient_counts AS (
    SELECT 
        m.Recipe_id, 
        m.Recipe_title, 
        COUNT(mi.ingredient_id) AS common_ingredients,
        (SELECT count FROM source_count) AS source_ingredient_count,
        COUNT(mi.ingredient_id)::float / 
        ((SELECT count FROM source_count) + 
         (SELECT COUNT(*) FROM "MealIngredient" WHERE meal_id = m.Recipe_id) - 
         COUNT(mi.ingredient_id))::float AS jaccard_similarity
    FROM "Meal" m
    JOIN "MealIngredient" mi ON m.Recipe_id = mi.meal_id
    JOIN meal_ingredients src ON mi.ingredient_id = src.ingredient_id
    WHERE m.Recipe_id != :meal_id
    GROUP BY m.Recipe_id, m.Recipe_title
)
SELECT 
    Recipe_id, 
    Recipe_title, 
    common_ingredients,
    jaccard_similarity AS similarity_score
FROM ingredient_counts
ORDER BY jaccard_similarity DESC, common_ingredients DESC
LIMIT :limit
"""

# Query for getting meals by region/cuisine
GET_CUISINE_MEALS = """
SELECT r.Recipe_id, r.Recipe_title
FROM "Recipe" r
WHERE r.Region = :cuisine_name OR r.Sub_region = :cuisine_name
ORDER BY r.ratings DESC NULLS LAST
LIMIT :limit
"""

# Query for getting meals by dietary preference
GET_DIETARY_PREFERENCE_MEALS = """
SELECT r.Recipe_id, r.Recipe_title
FROM "Recipe" r
WHERE 
    CASE 
        WHEN :dietary_restriction = 'vegan' THEN r.vegan > 0
        WHEN :dietary_restriction = 'pescetarian' THEN r.pescetarian > 0
        WHEN :dietary_restriction = 'ovo_vegetarian' THEN r.ovo_vegetarian > 0
        WHEN :dietary_restriction = 'lacto_vegetarian' THEN r.lacto_vegetarian > 0
        WHEN :dietary_restriction = 'ovo_lacto_vegetarian' THEN r.ovo_lacto_vegetarian > 0
        ELSE TRUE
    END
ORDER BY r.ratings DESC NULLS LAST
LIMIT :limit
"""

# Query for getting a user's dietary preferences
GET_USER_DIETARY_PREFERENCES = """
SELECT dietary_restriction
FROM "UserDietaryPreference"
WHERE user_id = :user_id
"""

# Query for getting similar meals based on ingredients
GET_SIMILAR_MEALS_BY_INGREDIENTS = """
WITH recipe_ingredients AS (
    SELECT ingredient_id
    FROM "RecipeIngredient"
    WHERE recipe_no = :meal_id
),
similarity_scores AS (
    SELECT 
        r.Recipe_id, 
        r.Recipe_title, 
        COUNT(ri.ingredient_id) as ingredient_match_count,
        COUNT(ri.ingredient_id)::float / 
        (SELECT COUNT(*) FROM "RecipeIngredient" WHERE recipe_no = :meal_id)::float as similarity
    FROM "Recipe" r
    JOIN "RecipeIngredient" ri ON r.Recipe_id = ri.recipe_no
    JOIN recipe_ingredients i ON ri.ingredient_id = i.ingredient_id
    WHERE r.Recipe_id != :meal_id
    GROUP BY r.Recipe_id, r.Recipe_title
)
SELECT Recipe_id, Recipe_title, ingredient_match_count, similarity
FROM similarity_scores
WHERE similarity > 0.2
ORDER BY similarity DESC, ingredient_match_count DESC
LIMIT :limit
"""

# Query for getting meal ingredients
GET_MEAL_INGREDIENTS = """
SELECT i.IngID, i.ingredient, ri.quantity
FROM "RecipeIngredient" ri
JOIN "RecipeIngredientFlavor" i ON ri.ing_id = i.IngID
WHERE ri.recipe_no = :meal_id
"""

# Query for getting recipe ingredients
GET_RECIPE_INGREDIENTS = """
SELECT i.IngID, i.ingredient, ri.quantity
FROM "RecipeIngredient" ri
JOIN "RecipeIngredientFlavor" i ON ri.ing_id = i.IngID
WHERE ri.recipe_no = :recipe_id
"""

# Query for getting meal details
GET_MEAL_DETAILS = """
SELECT 
    r.Recipe_id,
    r.Recipe_title,
    r.url,
    r.cook_time,
    r.prep_time,
    r.total_time,
    r.servings,
    r.Calories,
    r.ratings,
    r.Region,
    r.Sub_region,
    r.Continent,
    r.img_url,
    r.vegan,
    r.pescetarian,
    r.ovo_vegetarian,
    r.lacto_vegetarian,
    r.ovo_lacto_vegetarian,
    (
        SELECT json_agg(json_build_object(
            'id', i.IngID,
            'name', i.ingredient,
            'quantity', ri.quantity,
            'unit', ri.unit,
            'state', ri.state
        ))
        FROM "RecipeIngredient" ri
        JOIN "RecipeIngredientFlavor" i ON ri.ing_id = i.IngID
        WHERE ri.recipe_no = r.Recipe_id
    ) as ingredients,
    (
        SELECT COUNT(*) 
        FROM recommendation_interactions 
        WHERE meal_id = r.Recipe_id
    ) as interaction_count
FROM "Recipe" r
WHERE r.Recipe_id = :meal_id
"""

# Query for getting trending content by category
GET_TRENDING_BY_CATEGORY = """
WITH trending_by_category AS (
    SELECT 
        ri.meal_id,
        ri.content_type,
        r.Region as category,
        CASE 
            WHEN ri.content_type = 'meal' THEN m.Recipe_title
            WHEN ri.content_type = 'recipe' THEN r.Recipe_title
        END as title,
        COUNT(*) as interaction_count,
        ROW_NUMBER() OVER (
            PARTITION BY r.Region
            ORDER BY COUNT(*) DESC
        ) as category_rank
    FROM recommendation_interactions ri
    JOIN "Recipe" r ON ri.meal_id = r.Recipe_id
    LEFT JOIN "Meal" m ON ri.meal_id = m.Recipe_id AND ri.content_type = 'meal'
    WHERE ri.created_at > CURRENT_TIMESTAMP - INTERVAL :time_window
    AND r.Region IS NOT NULL
    GROUP BY ri.meal_id, ri.content_type, r.Region, m.Recipe_title, r.Recipe_title
)
SELECT meal_id as id, content_type, category, title, interaction_count as popularity
FROM trending_by_category
WHERE category_rank <= :items_per_category
ORDER BY category, category_rank
"""