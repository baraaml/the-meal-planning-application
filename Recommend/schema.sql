-- Extension setup for pgvector
CREATE EXTENSION IF NOT EXISTS vector;

-- Recipe-related tables
CREATE TABLE IF NOT EXISTS recipes (
    recipe_id SERIAL PRIMARY KEY,
    recipe_title VARCHAR(255) NOT NULL,
    region VARCHAR(100),
    sub_region VARCHAR(100),
    continent VARCHAR(100),
    source VARCHAR(255),
    image_url TEXT,
    cook_time INTEGER,
    prep_time INTEGER,
    total_time INTEGER,
    servings INTEGER,
    url TEXT,
    calories FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ingredients (
    ingredient_id SERIAL PRIMARY KEY,
    ingredient_name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recipe_ingredients (
    recipe_ingredient_id SERIAL PRIMARY KEY,
    recipe_id INTEGER REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    ingredient_id INTEGER REFERENCES ingredients(ingredient_id),
    quantity FLOAT,
    unit VARCHAR(50),
    ingredient_state VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recipe_instructions (
    instruction_id SERIAL PRIMARY KEY,
    recipe_id INTEGER REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    instructions TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recipe_diet_attributes (
    attribute_id SERIAL PRIMARY KEY,
    recipe_id INTEGER REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    vegan BOOLEAN DEFAULT FALSE,
    vegetarian BOOLEAN DEFAULT FALSE,
    pescetarian BOOLEAN DEFAULT FALSE,
    gluten_free BOOLEAN DEFAULT FALSE,
    dairy_free BOOLEAN DEFAULT FALSE,
    low_carb BOOLEAN DEFAULT FALSE,
    keto BOOLEAN DEFAULT FALSE,
    paleo BOOLEAN DEFAULT FALSE,
    lacto_vegetarian BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vector storage for recipe embeddings
CREATE TABLE IF NOT EXISTS recipe_embeddings (
    embedding_id SERIAL PRIMARY KEY,
    recipe_id INTEGER REFERENCES recipes(recipe_id) ON DELETE CASCADE UNIQUE,
    embedding vector(384) NOT NULL,  -- Dimension matches EMBEDDING_DIMENSION in config
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for fast similarity search
CREATE INDEX IF NOT EXISTS recipe_embedding_idx ON recipe_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- User interaction tables
CREATE TABLE IF NOT EXISTS user_interactions (
    interaction_id SERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    recipe_id VARCHAR(100) NOT NULL,
    interaction_type VARCHAR(50) NOT NULL,
    rating FLOAT,
    interaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS user_interactions_user_idx ON user_interactions(user_id);
CREATE INDEX IF NOT EXISTS user_interactions_recipe_idx ON user_interactions(recipe_id);
CREATE INDEX IF NOT EXISTS user_interactions_type_idx ON user_interactions(interaction_type);
CREATE INDEX IF NOT EXISTS user_interactions_time_idx ON user_interactions(interaction_time);

-- Matrix factorization model storage
CREATE TABLE IF NOT EXISTS matrix_factorization_models (
    model_id SERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL UNIQUE,
    model_data BYTEA NOT NULL,
    user_map JSONB NOT NULL,
    item_map JSONB NOT NULL,
    n_factors INTEGER NOT NULL,
    regularization FLOAT NOT NULL,
    global_mean FLOAT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- View for trending recipes
CREATE OR REPLACE VIEW trending_recipes AS
SELECT 
    r.recipe_id as id,
    r.recipe_title as title,
    COUNT(ui.interaction_id) as interaction_count,
    AVG(CASE WHEN ui.interaction_type = 'rating' THEN ui.rating ELSE NULL END) as avg_rating,
    COUNT(CASE WHEN ui.interaction_type = 'like' THEN 1 ELSE NULL END) as like_count,
    COUNT(CASE WHEN ui.interaction_type = 'save' THEN 1 ELSE NULL END) as save_count,
    COUNT(CASE WHEN ui.interaction_type = 'cook' THEN 1 ELSE NULL END) as cook_count,
    MAX(ui.interaction_time) as last_interaction
FROM recipes r
JOIN user_interactions ui ON r.recipe_id::text = ui.recipe_id
WHERE ui.interaction_time > (CURRENT_TIMESTAMP - INTERVAL '7 days')
GROUP BY r.recipe_id, r.recipe_title
ORDER BY interaction_count DESC;

-- Sample query to find similar recipes using vector similarity
/*
SELECT 
    r.recipe_id,
    r.recipe_title,
    1 - (re.embedding <=> '[0.1, 0.2, ..., 0.3]'::vector) as similarity_score
FROM recipe_embeddings re
JOIN recipes r ON re.recipe_id = r.recipe_id
WHERE 1 - (re.embedding <=> '[0.1, 0.2, ..., 0.3]'::vector) > 0.6  -- Minimum similarity threshold
ORDER BY similarity_score DESC
LIMIT 10;
*/