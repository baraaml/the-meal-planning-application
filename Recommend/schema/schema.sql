-- Ingredients Master Table
CREATE TABLE ingredients (
    ingredient_id SERIAL PRIMARY KEY,
    ingredient_name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    frequency INTEGER,
    wiki_link TEXT,
    wiki_image TEXT,
    flavor_db_category VARCHAR(100),
    dietrx_category VARCHAR(100),
    flavor_db_link TEXT,
    diet_rx_link TEXT,
    ingredient_raw VARCHAR(255)
);

-- Nutrition Information Table
CREATE TABLE ingredient_nutrition (
    ingredient_id INTEGER PRIMARY KEY,
    serving_size DECIMAL(10,2),
    energy_kcal DECIMAL(10,2),
    protein_g DECIMAL(10,2),
    carbohydrate_g DECIMAL(10,2),
    total_fat_g DECIMAL(10,2),
    total_sugar_g DECIMAL(10,2),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
);

-- Recipes Master Table
CREATE TABLE recipes (
    recipe_id SERIAL PRIMARY KEY,
    recipe_title TEXT NOT NULL,
    region VARCHAR(100),
    sub_region VARCHAR(100),
    continent VARCHAR(50),
    source VARCHAR(100),
    image_url TEXT,
    cook_time INTEGER,
    prep_time INTEGER,
    total_time INTEGER,
    servings INTEGER,
    url TEXT,
    calories DECIMAL(10,2)
);

-- Recipe Vegetarian Attributes
CREATE TABLE recipe_diet_attributes (
    recipe_id INTEGER PRIMARY KEY,
    vegan BOOLEAN DEFAULT FALSE,
    pescetarian BOOLEAN DEFAULT FALSE,
    ovo_vegetarian BOOLEAN DEFAULT FALSE,
    lacto_vegetarian BOOLEAN DEFAULT FALSE,
    ovo_lacto_vegetarian BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
);

-- Recipe Cooking Process
CREATE TABLE recipe_processes (
    recipe_id INTEGER,
    process_step TEXT,
    step_order INTEGER,
    PRIMARY KEY (recipe_id, step_order),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
);

-- Recipe Utensils Used
CREATE TABLE recipe_utensils (
    recipe_id INTEGER,
    utensil VARCHAR(100),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
);

-- Recipe Ingredient Composition
CREATE TABLE recipe_ingredients (
    recipe_id INTEGER,
    ingredient_id INTEGER,
    ingredient_phrase TEXT,
    quantity DECIMAL(10,2),
    unit VARCHAR(50),
    ingredient_state VARCHAR(100),
    size VARCHAR(50),
    PRIMARY KEY (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
);

-- Recipe Instructions
CREATE TABLE recipe_instructions (
    recipe_id INTEGER PRIMARY KEY,
    instructions TEXT,
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)
);

-- Index for performance optimization
CREATE INDEX idx_ingredient_name ON ingredients(ingredient_name);
CREATE INDEX idx_recipe_title ON recipes(recipe_title);
CREATE INDEX idx_recipe_region ON recipes(region, sub_region);

-- Enable the pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Recipe Embeddings Table - streamlined for just recipes
CREATE TABLE IF NOT EXISTS recipe_embeddings (
    embedding_id SERIAL PRIMARY KEY,
    recipe_id INTEGER NOT NULL REFERENCES recipes(recipe_id),
    embedding VECTOR(384),  -- Explicitly set dimension to match EMBEDDING_DIMENSION
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(recipe_id)
);

-- Create an index for efficient vector similarity searches
CREATE INDEX IF NOT EXISTS idx_recipe_embeddings ON recipe_embeddings USING ivfflat (embedding vector_cosine_ops);

-- Function to find similar recipes using vector similarity
CREATE OR REPLACE FUNCTION get_similar_recipes(
    query_embedding VECTOR(384),  -- Explicitly set dimension
    similarity_threshold FLOAT DEFAULT 0.6,
    max_results INTEGER DEFAULT 10
)
RETURNS TABLE (
    recipe_id INTEGER,
    recipe_title TEXT,
    similarity FLOAT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        r.recipe_id,
        r.recipe_title,
        1 - (re.embedding <=> query_embedding) AS similarity
    FROM 
        recipe_embeddings re
    JOIN 
        recipes r ON re.recipe_id = r.recipe_id
    WHERE 
        1 - (re.embedding <=> query_embedding) > similarity_threshold
    ORDER BY 
        similarity DESC
    LIMIT 
        max_results;
END;
$$ LANGUAGE plpgsql;

-- Matrix Factorization Models Table
CREATE TABLE IF NOT EXISTS matrix_factorization_models (
    model_id SERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL UNIQUE,
    user_map JSONB NOT NULL, -- Serialized mapping of user_id to index
    item_map JSONB NOT NULL, -- Serialized mapping of item_id to index
    n_factors INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Interactions Table
CREATE TABLE IF NOT EXISTS user_interactions (
    interaction_id SERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    recipe_id INTEGER NOT NULL REFERENCES recipes(recipe_id),
    interaction_type VARCHAR(50) NOT NULL, -- 'view', 'like', 'save', 'cook', 'rating'
    rating DECIMAL(3,1), -- Optional rating value (1-5)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id, interaction_type)
);

-- Indices for fast querying
CREATE INDEX IF NOT EXISTS idx_interactions_user_id ON user_interactions(user_id);
CREATE INDEX IF NOT EXISTS idx_interactions_recipe_id ON user_interactions(recipe_id);
CREATE INDEX IF NOT EXISTS idx_interactions_type ON user_interactions(interaction_type);
CREATE INDEX IF NOT EXISTS idx_interactions_timestamp ON user_interactions(created_at);

-- Add this script to the schema.sql file
ALTER TABLE recipe_diet_attributes ALTER COLUMN vegan SET DEFAULT FALSE;
ALTER TABLE recipe_diet_attributes ALTER COLUMN pescetarian SET DEFAULT FALSE;
ALTER TABLE recipe_diet_attributes ALTER COLUMN ovo_vegetarian SET DEFAULT FALSE;
ALTER TABLE recipe_diet_attributes ALTER COLUMN lacto_vegetarian SET DEFAULT FALSE;
ALTER TABLE recipe_diet_attributes ALTER COLUMN ovo_lacto_vegetarian SET DEFAULT FALSE;

-- User Interactions Table (updated to accept string recipe_id)
CREATE TABLE IF NOT EXISTS user_interactions (
    interaction_id SERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    recipe_id VARCHAR(100) NOT NULL, -- Using VARCHAR type
    interaction_type VARCHAR(50) NOT NULL, -- 'view', 'like', 'save', 'cook', 'rating'
    rating DECIMAL(3,1), -- Optional rating value (1-5)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id, interaction_type)
);

-- User Dietary Preferences Table
CREATE TABLE IF NOT EXISTS user_dietary_preference (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    dietary_restriction VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, dietary_restriction)
);