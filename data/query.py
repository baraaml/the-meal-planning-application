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
# This offers better performance for larger databases compared to simple index
CREATE_EMBEDDINGS_INDEX = """
CREATE INDEX IF NOT EXISTS content_embeddings_vector_idx 
ON content_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

CREATE INDEX IF NOT EXISTS content_embeddings_content_type_idx
ON content_embeddings (content_type);

CREATE INDEX IF NOT EXISTS content_embeddings_meal_id_idx
ON content_embeddings (meal_id);
"""

# Optimized interactions table with partitioning hints for high-volume data
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

-- Composite index for user history queries
CREATE INDEX IF NOT EXISTS rec_interactions_user_created_idx
ON recommendation_interactions(user_id, created_at DESC);

-- Composite index for trending content queries
CREATE INDEX IF NOT EXISTS rec_interactions_content_created_idx
ON recommendation_interactions(content_type, created_at DESC);
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
);

CREATE INDEX IF NOT EXISTS user_dietary_pref_user_idx
ON "UserDietaryPreference"(user_id);
"""

# Query to create meal dietary restrictions table
CREATE_MEAL_DIETARY_RESTRICTION_TABLE = """
CREATE TABLE IF NOT EXISTS "MealDietaryRestriction" (
    id SERIAL PRIMARY KEY,
    meal_id TEXT NOT NULL,
    dietary_restriction_id INTEGER NOT NULL REFERENCES "DietaryRestriction"(id),
    UNIQUE(meal_id, dietary_restriction_id)
);

CREATE INDEX IF NOT EXISTS meal_dietary_meal_idx
ON "MealDietaryRestriction"(meal_id);

CREATE INDEX IF NOT EXISTS meal_dietary_restriction_idx
ON "MealDietaryRestriction"(dietary_restriction_id);
"""

# Query to create ingredients table
CREATE_INGREDIENT_TABLE = """
CREATE TABLE IF NOT EXISTS "Ingredient" (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS ingredient_name_idx
ON "Ingredient" USING gin(name gin_trgm_ops);
"""

# Query to create meal ingredients table
CREATE_MEAL_INGREDIENT_TABLE = """
CREATE TABLE IF NOT EXISTS "MealIngredient" (
    id SERIAL PRIMARY KEY,
    meal_id TEXT NOT NULL,
    ingredient_id INTEGER NOT NULL REFERENCES "Ingredient"(id),
    amount TEXT,
    UNIQUE(meal_id, ingredient_id)
);

CREATE INDEX IF NOT EXISTS meal_ingredient_meal_idx
ON "MealIngredient"(meal_id);

CREATE INDEX IF NOT EXISTS meal_ingredient_ingredient_idx
ON "MealIngredient"(ingredient_id);
"""

# Query to enable trigram indexing for text search
ENABLE_TRIGRAM = """
CREATE EXTENSION IF NOT EXISTS pg_trgm
"""

# Query to insert default cuisines
INSERT_DEFAULT_CUISINES = """
INSERT INTO "Cuisine" (name) VALUES
('italian'), ('mexican'), ('chinese'), ('indian'), ('american'),
('french'), ('japanese'), ('mediterranean'), ('thai'), ('other'),
('middle eastern'), ('korean'), ('vietnamese'), ('spanish'), ('greek'),
('caribbean'), ('brazilian'), ('german'), ('british'), ('african')
ON CONFLICT (name) DO NOTHING
"""

# Query to insert default dietary restrictions
INSERT_DEFAULT_DIETARY_RESTRICTIONS = """
INSERT INTO "DietaryRestriction" (name) VALUES
('vegetarian'), ('vegan'), ('gluten-free'), ('dairy-free'),
('keto'), ('paleo'), ('low-carb'), ('low-fat'), ('pescatarian'),
('nut-free'), ('egg-free'), ('soy-free'), ('halal'), ('kosher')
ON CONFLICT (name) DO NOTHING
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
    LIMIT :limit * 2
)
SELECT meal_id, content_type, title, similarity
FROM candidate_embeddings
WHERE similarity > 0.5
ORDER BY similarity DESC
LIMIT :limit
"""

# Query to get meals that don't have embeddings yet (optimized with limit)
GET_MEALS_WITHOUT_EMBEDDINGS = """
SELECT m.id, m.title, m.description
FROM "Meal" m
LEFT JOIN content_embeddings ce ON ce.meal_id = m.id AND ce.content_type = 'meal'
WHERE ce.id IS NULL
LIMIT :limit
"""

# Query to get recipes that don't have embeddings yet (optimized with limit)
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
    (SELECT COUNT(*) FROM "Meal" m 
     LEFT JOIN content_embeddings ce ON ce.meal_id = m.id AND ce.content_type = 'meal' 
     WHERE ce.id IS NULL) as meals_without_embeddings,
     
    (SELECT COUNT(*) FROM "Recipe" r 
     LEFT JOIN content_embeddings ce ON ce.meal_id = r.id AND ce.content_type = 'recipe' 
     WHERE ce.id IS NULL) as recipes_without_embeddings,
     
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

# Query to get recent interactions for a user (optimized with index hint)
GET_USER_RECENT_INTERACTIONS_BASE = """
/*+ INDEX(recommendation_interactions rec_interactions_user_created_idx) */
SELECT meal_id, content_type, interaction_type, created_at
FROM recommendation_interactions
WHERE user_id = :user_id
{type_filter}
ORDER BY created_at DESC
LIMIT :limit
"""

# Base query for getting trending meals (optimized with window function)
GET_TRENDING_MEALS_BASE = """
WITH trending AS (
    SELECT ri.meal_id, ri.content_type,
           CASE WHEN ri.content_type = 'meal' THEN m.title
                WHEN ri.content_type = 'recipe' THEN r.name
           END as title,
           COUNT(*) as popularity,
           ROW_NUMBER() OVER (PARTITION BY ri.content_type ORDER BY COUNT(*) DESC) as rank
    FROM recommendation_interactions ri
    LEFT JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ri.meal_id = r.id AND ri.content_type = 'recipe'
    WHERE ri.created_at > {time_clause}
    {type_filter}
    GROUP BY ri.meal_id, ri.content_type, m.title, r.name
)
SELECT meal_id, content_type, title, popularity
FROM trending
WHERE rank <= :limit
ORDER BY popularity DESC
"""

# Query to find users with similar meal preferences (optimized with CTE)
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

# Base query for getting meals from similar users (optimized with NOT EXISTS)
GET_MEALS_FROM_SIMILAR_USERS_BASE = """
WITH user_recommendations AS (
    SELECT ri.meal_id, ri.content_type, COUNT(*) as interaction_count,
           CASE WHEN ri.content_type = 'meal' THEN m.title
                WHEN ri.content_type = 'recipe' THEN r.name
           END as title,
           ROW_NUMBER() OVER (ORDER BY COUNT(*) DESC) as rank
    FROM recommendation_interactions ri
    LEFT JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ri.meal_id = r.id AND ri.content_type = 'recipe'
    WHERE ri.user_id IN ({user_placeholders})
    AND NOT EXISTS (
        SELECT 1 
        FROM recommendation_interactions ri2
        WHERE ri2.user_id = :user_id
        AND ri2.meal_id = ri.meal_id
        AND ri2.content_type = ri.content_type
    )
    {type_filter}
    GROUP BY ri.meal_id, ri.content_type, m.title, r.name
)
SELECT meal_id, content_type, interaction_count, title
FROM user_recommendations
WHERE rank <= :limit
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

# Query for finding similar meals based on co-occurrence patterns (optimized with window function)
FIND_SIMILAR_ITEMS = """
WITH users_who_interacted AS (
    SELECT DISTINCT user_id
    FROM recommendation_interactions
    WHERE meal_id = :meal_id
),
similar_items AS (
    SELECT ri2.meal_id, ri2.content_type,
           CASE WHEN ri2.content_type = 'meal' THEN m.title
                WHEN ri2.content_type = 'recipe' THEN r.name
           END as title,
           COUNT(DISTINCT ri2.user_id) as co_occurrence_count,
           ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT ri2.user_id) DESC) as rank
    FROM recommendation_interactions ri2
    JOIN users_who_interacted uwi ON ri2.user_id = uwi.user_id
    LEFT JOIN "Meal" m ON ri2.meal_id = m.id AND ri2.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ri2.meal_id = r.id AND ri2.content_type = 'recipe'
    WHERE ri2.meal_id != :meal_id
    {type_filter}
    GROUP BY ri2.meal_id, ri2.content_type, m.title, r.name
)
SELECT meal_id, content_type, title, co_occurrence_count
FROM similar_items
WHERE rank <= :limit
"""

# Query for finding meals with similar ingredients (optimized with CTE and Jaccard similarity)
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
        m.id, 
        m.title, 
        COUNT(mi.ingredient_id) AS common_ingredients,
        (SELECT count FROM source_count) AS source_ingredient_count,
        COUNT(mi.ingredient_id)::float / 
        ((SELECT count FROM source_count) + 
         (SELECT COUNT(*) FROM "MealIngredient" WHERE meal_id = m.id) - 
         COUNT(mi.ingredient_id))::float AS jaccard_similarity
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
    jaccard_similarity AS similarity_score
FROM ingredient_counts
ORDER BY jaccard_similarity DESC, common_ingredients DESC
LIMIT :limit
"""

# Query for getting meals by cuisine (with improved joining)
GET_CUISINE_MEALS = """
SELECT m.id, m.title
FROM "Meal" m
JOIN "Cuisine" c ON m.cuisine_id = c.id
WHERE c.id = :cuisine_id
ORDER BY m.ratings DESC NULLS LAST
LIMIT :limit
"""

# Query for getting meals by dietary preference (optimized with direct join)
GET_DIETARY_PREFERENCE_MEALS = """
SELECT m.id, m.title
FROM "Meal" m
JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
WHERE mdr.dietary_restriction_id = :dietary_restriction_id
ORDER BY m.ratings DESC NULLS LAST
LIMIT :limit
"""

# Query for getting a user's dietary preferences
GET_USER_DIETARY_PREFERENCES = """
SELECT dr.id, dr.name
FROM "UserDietaryPreference" udp
JOIN "DietaryRestriction" dr ON udp.dietary_restriction_id = dr.id
WHERE udp.user_id = :user_id
"""

# Query for getting similar meals based on ingredients (with normalized similarity score)
GET_SIMILAR_MEALS_BY_INGREDIENTS = """
WITH meal_ingredients AS (
    SELECT ingredient_id
    FROM "MealIngredient"
    WHERE meal_id = :meal_id
),
similarity_scores AS (
    SELECT 
        m1.id, 
        m1.title, 
        COUNT(mi1.ingredient_id) as ingredient_match_count,
        COUNT(mi1.ingredient_id)::float / 
        (SELECT COUNT(*) FROM "MealIngredient" WHERE meal_id = :meal_id)::float as similarity
    FROM "Meal" m1
    JOIN "MealIngredient" mi1 ON m1.id = mi1.meal_id
    JOIN meal_ingredients mi2 ON mi1.ingredient_id = mi2.ingredient_id
    WHERE m1.id != :meal_id
    GROUP BY m1.id, m1.title
)
SELECT id, title, ingredient_match_count, similarity
FROM similarity_scores
WHERE similarity > 0.2
ORDER BY similarity DESC, ingredient_match_count DESC
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

# Query for getting personalized meal recommendations based on dietary preferences (optimized)
GET_MEALS_BY_USER_DIETARY_PREFERENCES = """
WITH user_restrictions AS (
    SELECT dietary_restriction_id
    FROM "UserDietaryPreference"
    WHERE user_id = :user_id
),
eligible_meals AS (
    SELECT 
        m.id, 
        m.title, 
        m.description,
        COUNT(DISTINCT mdr.dietary_restriction_id) as matching_restrictions,
        ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT mdr.dietary_restriction_id) DESC, RANDOM()) as rank
    FROM "Meal" m
    JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
    JOIN user_restrictions ur ON mdr.dietary_restriction_id = ur.dietary_restriction_id
    GROUP BY m.id, m.title, m.description
)
SELECT id, title, description
FROM eligible_meals
WHERE rank <= :limit
"""

# Query for getting meals with high popularity (optimized with window function)
GET_POPULAR_MEALS = """
WITH popular AS (
    SELECT 
        m.id, 
        m.title, 
        COUNT(ri.id) as popularity,
        ROW_NUMBER() OVER (ORDER BY COUNT(ri.id) DESC) as rank
    FROM "Meal" m
    JOIN recommendation_interactions ri ON m.id = ri.meal_id AND ri.content_type = 'meal'
    WHERE ri.created_at > NOW() - INTERVAL :time_window
    GROUP BY m.id, m.title
    HAVING COUNT(ri.id) > :min_interactions
)
SELECT id, title, popularity
FROM popular
WHERE rank <= :limit
"""

# Query for getting meals by time of day (optimized with user preferences)
GET_MEALS_BY_TIME_OF_DAY = """
WITH user_preferred_cuisines AS (
    SELECT m.cuisine_id, COUNT(*) as interaction_count
    FROM recommendation_interactions ri
    JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
    WHERE ri.user_id = :user_id
    GROUP BY m.cuisine_id
    ORDER BY interaction_count DESC
    LIMIT 3
),
meals_by_type AS (
    SELECT 
        m.id, 
        m.title, 
        m.description,
        CASE 
            WHEN m.cuisine_id IN (SELECT cuisine_id FROM user_preferred_cuisines) THEN 0
            ELSE 1
        END as preference_rank,
        ROW_NUMBER() OVER (
            PARTITION BY (CASE WHEN m.cuisine_id IN (SELECT cuisine_id FROM user_preferred_cuisines) THEN 0 ELSE 1 END)
            ORDER BY RANDOM()
        ) as rand_rank
    FROM "Meal" m
    WHERE m.meal_type = :meal_type
)
SELECT id, title, description
FROM meals_by_type
ORDER BY preference_rank, rand_rank
LIMIT :limit
"""

# Query for getting low-carb or low-calorie meals (optimized)
GET_HEALTHY_MEALS = """
WITH healthy_meals AS (
    SELECT 
        m.id, 
        m.title, 
        m.calories,
        CASE 
            WHEN m.id IN (
                SELECT meal_id FROM "MealDietaryRestriction" 
                WHERE dietary_restriction_id IN (
                    SELECT id FROM "DietaryRestriction" WHERE name IN ('low-carb', 'keto')
                )
            ) THEN 0
            ELSE 1
        END as diet_rank,
        ROW_NUMBER() OVER (
            PARTITION BY (CASE 
                WHEN m.id IN (
                    SELECT meal_id FROM "MealDietaryRestriction" 
                    WHERE dietary_restriction_id IN (
                        SELECT id FROM "DietaryRestriction" WHERE name IN ('low-carb', 'keto')
                    )
                ) THEN 0
                ELSE 1
            END)
            ORDER BY COALESCE(m.calories, 9999)
        ) as calorie_rank
    FROM "Meal" m
    WHERE 
        (m.calories IS NULL OR m.calories < :max_calories)
        AND (:low_carb = FALSE OR m.id IN (
            SELECT meal_id FROM "MealDietaryRestriction" 
            WHERE dietary_restriction_id IN (
                SELECT id FROM "DietaryRestriction" WHERE name IN ('low-carb', 'keto')
            )
        ))
)
SELECT id, title, calories
FROM healthy_meals
ORDER BY diet_rank, calorie_rank
LIMIT :limit
"""

# Query for finding meals suitable for meal planning (optimized with CTEs)
GET_MEAL_PLAN_RECOMMENDATIONS = """
WITH user_favorite_cuisines AS (
    SELECT m.cuisine_id, COUNT(*) as interaction_count
    FROM recommendation_interactions ri
    JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
    WHERE ri.user_id = :user_id
    GROUP BY m.cuisine_id
    ORDER BY interaction_count DESC
    LIMIT 3
),
user_diet_restrictions AS (
    SELECT dietary_restriction_id
    FROM "UserDietaryPreference"
    WHERE user_id = :user_id
),
eligible_meals AS (
    SELECT 
        m.id, 
        m.title, 
        m.meal_type, 
        m.prep_time, 
        m.cuisine_id,
        CASE 
            WHEN EXISTS (
                SELECT 1 
                FROM "MealDietaryRestriction" mdr
                JOIN user_diet_restrictions udr ON mdr.dietary_restriction_id = udr.dietary_restriction_id
                WHERE mdr.meal_id = m.id
            ) THEN 0
            ELSE 1
        END as diet_match,
        CASE 
            WHEN m.cuisine_id IN (SELECT cuisine_id FROM user_favorite_cuisines) THEN 0
            ELSE 1
        END as cuisine_match,
        ROW_NUMBER() OVER (
            PARTITION BY CASE 
                WHEN EXISTS (
                    SELECT 1 
                    FROM "MealDietaryRestriction" mdr
                    JOIN user_diet_restrictions udr ON mdr.dietary_restriction_id = udr.dietary_restriction_id
                    WHERE mdr.meal_id = m.id
                ) THEN 0
                ELSE 1
            END,
            CASE 
                WHEN m.cuisine_id IN (SELECT cuisine_id FROM user_favorite_cuisines) THEN 0
                ELSE 1
            END
            ORDER BY RANDOM()
        ) as rank
    FROM "Meal" m
    WHERE 
        (m.meal_type = :meal_type OR :meal_type IS NULL)
        AND (m.cuisine_id IN (SELECT cuisine_id FROM user_favorite_cuisines) OR :ignore_cuisine = TRUE)
        AND (EXISTS (
            SELECT 1 
            FROM "MealDietaryRestriction" mdr
            JOIN user_diet_restrictions udr ON mdr.dietary_restriction_id = udr.dietary_restriction_id
            WHERE mdr.meal_id = m.id
        ) OR :ignore_preferences = TRUE)
        AND (m.prep_time <= :max_prep_time OR :max_prep_time IS NULL)
)
SELECT id, title, meal_type, prep_time, cuisine_id
FROM eligible_meals
WHERE rank <= :limit
ORDER BY diet_match, cuisine_match, rank
"""

# Query for optimized meal search with multiple criteria
SEARCH_MEALS = """
WITH search_results AS (
    SELECT 
        m.id, 
        m.title, 
        m.description, 
        m.cuisine_id,
        c.name as cuisine_name,
        m.ratings,
        m.calories,
        m.prep_time,
        m.cook_time,
        m.total_time,
        CASE 
            WHEN m.title ILIKE :search_term THEN 3
            WHEN m.description ILIKE :search_term THEN 2
            ELSE 1
        END as relevance_score,
        ts_rank(
            to_tsvector('english', COALESCE(m.title, '') || ' ' || COALESCE(m.description, '')), 
            plainto_tsquery('english', :search_term)
        ) as text_rank,
        ROW_NUMBER() OVER (
            ORDER BY 
                CASE WHEN m.title ILIKE :search_term THEN 3
                     WHEN m.description ILIKE :search_term THEN 2
                     ELSE 1
                END DESC,
                ts_rank(
                    to_tsvector('english', COALESCE(m.title, '') || ' ' || COALESCE(m.description, '')), 
                    plainto_tsquery('english', :search_term)
                ) DESC,
                m.ratings DESC NULLS LAST
        ) as rank
    FROM "Meal" m
    LEFT JOIN "Cuisine" c ON m.cuisine_id = c.id
    WHERE 
        (
            m.title ILIKE '%' || :search_term || '%' OR 
            m.description ILIKE '%' || :search_term || '%' OR
            c.name ILIKE '%' || :search_term || '%'
        )
        AND (:cuisine_id IS NULL OR m.cuisine_id = :cuisine_id)
        AND (:max_prep_time IS NULL OR m.prep_time <= :max_prep_time)
        AND (:min_rating IS NULL OR m.ratings >= :min_rating)
        AND (
            :dietary_restriction_id IS NULL OR 
            m.id IN (
                SELECT meal_id 
                FROM "MealDietaryRestriction" 
                WHERE dietary_restriction_id = :dietary_restriction_id
            )
        )
)
SELECT 
    id, 
    title, 
    description, 
    cuisine_id, 
    cuisine_name, 
    ratings, 
    calories, 
    prep_time, 
    cook_time,
    total_time
FROM search_results
WHERE rank <= :limit
ORDER BY relevance_score DESC, text_rank DESC, ratings DESC NULLS LAST
"""

# Query for retrieving detailed meal information
GET_MEAL_DETAILS = """
SELECT 
    m.id,
    m.title,
    m.description,
    m.prep_time,
    m.cook_time,
    m.total_time,
    m.servings,
    m.calories,
    m.ratings,
    c.id as cuisine_id,
    c.name as cuisine_name,
    (
        SELECT json_agg(json_build_object(
            'id', dr.id,
            'name', dr.name
        ))
        FROM "MealDietaryRestriction" mdr
        JOIN "DietaryRestriction" dr ON mdr.dietary_restriction_id = dr.id
        WHERE mdr.meal_id = m.id
    ) as dietary_restrictions,
    (
        SELECT json_agg(json_build_object(
            'id', i.id,
            'name', i.name,
            'amount', mi.amount
        ))
        FROM "MealIngredient" mi
        JOIN "Ingredient" i ON mi.ingredient_id = i.id
        WHERE mi.meal_id = m.id
    ) as ingredients,
    (
        SELECT COUNT(*) 
        FROM recommendation_interactions 
        WHERE meal_id = m.id AND content_type = 'meal'
    ) as interaction_count
FROM "Meal" m
LEFT JOIN "Cuisine" c ON m.cuisine_id = c.id
WHERE m.id = :meal_id
"""

# Query for getting user recommendations with advanced personalization
GET_PERSONALIZED_RECOMMENDATIONS = """
WITH user_interactions AS (
    SELECT meal_id, content_type, interaction_type, created_at,
        ROW_NUMBER() OVER (PARTITION BY meal_id, content_type ORDER BY created_at DESC) as row_num
    FROM recommendation_interactions
    WHERE user_id = :user_id
),
recent_interactions AS (
    SELECT meal_id, content_type, interaction_type, created_at
    FROM user_interactions
    WHERE row_num = 1
),
user_dietary_preferences AS (
    SELECT dietary_restriction_id
    FROM "UserDietaryPreference"
    WHERE user_id = :user_id
),
user_cuisine_preferences AS (
    SELECT 
        m.cuisine_id, 
        COUNT(*) as interaction_count,
        ROW_NUMBER() OVER (ORDER BY COUNT(*) DESC) as rank
    FROM recommendation_interactions ri
    JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
    WHERE ri.user_id = :user_id
    GROUP BY m.cuisine_id
    HAVING COUNT(*) > 1
),
content_based_candidates AS (
    SELECT 
        ce2.meal_id,
        ce2.content_type,
        1 - (ce2.embedding <=> ce1.embedding) as similarity_score,
        ROW_NUMBER() OVER (PARTITION BY ce1.meal_id ORDER BY 1 - (ce2.embedding <=> ce1.embedding) DESC) as rank
    FROM recent_interactions ri
    JOIN content_embeddings ce1 ON ri.meal_id = ce1.meal_id AND ri.content_type = ce1.content_type
    JOIN content_embeddings ce2 ON ce1.content_type = ce2.content_type
    WHERE ce2.meal_id != ri.meal_id
    AND NOT EXISTS (
        SELECT 1 FROM user_interactions ui 
        WHERE ui.meal_id = ce2.meal_id AND ui.content_type = ce2.content_type
    )
),
collaborative_candidates AS (
    SELECT 
        ri2.meal_id,
        ri2.content_type,
        COUNT(DISTINCT ri2.user_id) as user_count,
        ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT ri2.user_id) DESC) as rank
    FROM recent_interactions ri
    JOIN recommendation_interactions ri2 ON ri.meal_id = ri2.meal_id AND ri.content_type = ri2.content_type
    JOIN recommendation_interactions ri3 ON ri3.user_id = ri2.user_id AND ri3.user_id != :user_id
    WHERE NOT EXISTS (
        SELECT 1 FROM user_interactions ui 
        WHERE ui.meal_id = ri3.meal_id AND ui.content_type = ri3.content_type
    )
    AND ri3.meal_id != ri.meal_id
    GROUP BY ri3.meal_id, ri3.content_type
),
final_candidates AS (
    -- Content-based candidates
    SELECT 
        c.meal_id,
        c.content_type,
        CASE 
            WHEN c.content_type = 'meal' THEN m.title
            WHEN c.content_type = 'recipe' THEN r.name
        END as title,
        c.similarity_score * 0.8 as score,
        1 as source_type
    FROM content_based_candidates c
    LEFT JOIN "Meal" m ON c.meal_id = m.id AND c.content_type = 'meal'
    LEFT JOIN "Recipe" r ON c.meal_id = r.id AND c.content_type = 'recipe'
    WHERE c.rank <= :limit * 2
    
    UNION ALL
    
    -- Collaborative candidates
    SELECT 
        c.meal_id,
        c.content_type,
        CASE 
            WHEN c.content_type = 'meal' THEN m.title
            WHEN c.content_type = 'recipe' THEN r.name
        END as title,
        c.user_count / (SELECT MAX(user_count) FROM collaborative_candidates) * 0.7 as score,
        2 as source_type
    FROM collaborative_candidates c
    LEFT JOIN "Meal" m ON c.meal_id = m.id AND c.content_type = 'meal'
    LEFT JOIN "Recipe" r ON c.meal_id = r.id AND c.content_type = 'recipe'
    WHERE c.rank <= :limit * 2
    
    UNION ALL
    
    -- Popular items from user's preferred cuisines
    SELECT 
        m.id as meal_id,
        'meal' as content_type,
        m.title,
        0.6 - (0.1 * ucp.rank) as score,
        3 as source_type
    FROM "Meal" m
    JOIN user_cuisine_preferences ucp ON m.cuisine_id = ucp.cuisine_id
    WHERE NOT EXISTS (
        SELECT 1 FROM user_interactions ui 
        WHERE ui.meal_id = m.id AND ui.content_type = 'meal'
    )
    AND ucp.rank <= 3
    
    UNION ALL
    
    -- Items matching user's dietary preferences
    SELECT 
        m.id as meal_id,
        'meal' as content_type,
        m.title,
        0.5 as score,
        4 as source_type
    FROM "Meal" m
    JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
    JOIN user_dietary_preferences udp ON mdr.dietary_restriction_id = udp.dietary_restriction_id
    WHERE NOT EXISTS (
        SELECT 1 FROM user_interactions ui 
        WHERE ui.meal_id = m.id AND ui.content_type = 'meal'
    )
    
    UNION ALL
    
    -- Generally popular items as fallback
    SELECT 
        m.id as meal_id,
        'meal' as content_type,
        m.title,
        0.4 as score,
        5 as source_type
    FROM "Meal" m
    JOIN (
        SELECT meal_id, COUNT(*) as count
        FROM recommendation_interactions
        WHERE content_type = 'meal'
        GROUP BY meal_id
        ORDER BY count DESC
        LIMIT :limit
    ) popular ON m.id = popular.meal_id
    WHERE NOT EXISTS (
        SELECT 1 FROM user_interactions ui 
        WHERE ui.meal_id = m.id AND ui.content_type = 'meal'
    )
),
ranked_candidates AS (
    SELECT 
        meal_id,
        content_type,
        title,
        score,
        source_type,
        ROW_NUMBER() OVER (
            PARTITION BY meal_id, content_type 
            ORDER BY score DESC
        ) as dedupe_rank
    FROM final_candidates
    WHERE (:content_type IS NULL OR content_type = :content_type)
)
SELECT meal_id as id, content_type, title, score
FROM ranked_candidates
WHERE dedupe_rank = 1
ORDER BY score DESC
LIMIT :limit
"""

# Query for getting trending content by category
GET_TRENDING_BY_CATEGORY = """
WITH trending_by_category AS (
    SELECT 
        ri.meal_id,
        ri.content_type,
        c.name as category,
        CASE 
            WHEN ri.content_type = 'meal' THEN m.title
            WHEN ri.content_type = 'recipe' THEN r.name
        END as title,
        COUNT(*) as interaction_count,
        ROW_NUMBER() OVER (
            PARTITION BY c.name
            ORDER BY COUNT(*) DESC
        ) as category_rank
    FROM recommendation_interactions ri
    JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
    LEFT JOIN "Recipe" r ON ri.meal_id = r.id AND ri.content_type = 'recipe'
    JOIN "Cuisine" c ON m.cuisine_id = c.id
    WHERE ri.created_at > CURRENT_TIMESTAMP - INTERVAL :time_window
    GROUP BY ri.meal_id, ri.content_type, c.name, m.title, r.name
)
SELECT meal_id as id, content_type, category, title, interaction_count as popularity
FROM trending_by_category
WHERE category_rank <= :items_per_category
ORDER BY category, category_rank
"""

# Query for getting recently added content
GET_RECENTLY_ADDED = """
WITH recent_content AS (
    SELECT 
        id as meal_id,
        'meal' as content_type,
        title,
        created_at,
        ROW_NUMBER() OVER (ORDER BY created_at DESC) as rank
    FROM "Meal" 
    WHERE created_at IS NOT NULL
    
    UNION ALL
    
    SELECT 
        id as meal_id,
        'recipe' as content_type,
        name as title,
        created_at,
        ROW_NUMBER() OVER (ORDER BY created_at DESC) as rank
    FROM "Recipe"
    WHERE created_at IS NOT NULL
)
SELECT meal_id as id, content_type, title
FROM recent_content
WHERE rank <= :limit
ORDER BY created_at DESC
"""