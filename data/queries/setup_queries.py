"""
SQL query constants for database setup.
Contains all the queries used for creating tables and indexes for meal recommendations.
"""

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