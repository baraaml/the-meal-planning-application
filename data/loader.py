"""
Data loader for MealFlow recommendation system.
Imports datasets from CSV and JSON files into the PostgreSQL database.
"""
import os
import json
import pandas as pd
import logging
import time
from typing import Dict, List, Optional, Any
from dotenv import load_dotenv
from sqlalchemy import create_engine, text, Table, Column, Integer, String, Float, MetaData, inspect

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Load environment variables
load_dotenv()

# Get database connection string
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://postgres:postgres@localhost:5432/mealflow")

# Get data file paths
DATA_DIR = os.getenv("DATA_DIR", "data")
RECIPE_GENERAL_FILE = os.getenv("RECIPE_GENERAL_FILE", "RecipeDB_general.csv")
RECIPE_INGREDIENT_PHRASE_FILE = os.getenv("RECIPE_INGREDIENT_PHRASE_FILE", "RecipeDB_ingredient_phrase.csv")
RECIPE_INGREDIENT_FLAVOR_FILE = os.getenv("RECIPE_INGREDIENT_FLAVOR_FILE", "RecipeDB_ingredient_flavor.csv")
RECIPE_INSTRUCTIONS_FILE = os.getenv("RECIPE_INSTRUCTIONS_FILE", "RecipeDB_instructions.json")
MERGED_RECIPES_FILE = os.getenv("MERGED_RECIPES_FILE", "merged.csv")
BATCH_SIZE = int(os.getenv("BATCH_SIZE", 1000))

# Connect to database
engine = create_engine(DATABASE_URL)
metadata = MetaData()

def check_table_exists(table_name: str) -> bool:
    """Check if a table exists in the database."""
    inspector = inspect(engine)
    return table_name in inspector.get_table_names()

def create_tables():
    """Create all necessary tables if they don't exist."""
    logger.info("Creating tables if they don't exist...")

    # Recipe table
    if not check_table_exists("Recipe"):
        Recipe = Table(
            "Recipe", 
            metadata,
            Column("id", String, primary_key=True),
            Column("name", String),
            Column("cuisine", String),
            Column("prep_time", Integer),
            Column("cook_time", Integer),
            Column("total_time", Integer),
            Column("servings", Integer),
            Column("calories", Float),
            Column("ratings", Float),
            Column("url", String),
            Column("region", String),
            Column("sub_region", String),
            Column("continent", String),
            Column("source", String),
            Column("img_url", String),
            Column("carbohydrate", Float),
            Column("energy", Float),
            Column("protein", Float),
            Column("fat", Float),
            Column("utensils", String),
            Column("processes", String),
            Column("vegan", Float),
            Column("pescetarian", Float),
            Column("ovo_vegetarian", Float),
            Column("lacto_vegetarian", Float),
            Column("ovo_lacto_vegetarian", Float)
        )
        
    # Ingredient table
    if not check_table_exists("Ingredient"):
        Ingredient = Table(
            "Ingredient",
            metadata,
            Column("id", Integer, primary_key=True),
            Column("name", String, unique=True),
            Column("generic_name", String),
            Column("wiki_link", String),
            Column("wiki_image", String),
            Column("flavordb_category", String),
            Column("dietrx_category", String),
            Column("flavor_db_link", String),
            Column("flavordb_id", Integer),
            Column("diet_rx_link", String)
        )

    # RecipeIngredient table
    if not check_table_exists("RecipeIngredient"):
        RecipeIngredient = Table(
            "RecipeIngredient",
            metadata,
            Column("id", Integer, primary_key=True),
            Column("recipe_id", String),
            Column("ingredient_id", Integer),
            Column("quantity", String),
            Column("unit", String),
            Column("preparation_method", String)
        )

    # Merged table
    if not check_table_exists("MergedRecipes"):
        MergedRecipes = Table(
            "MergedRecipes",
            metadata,
            Column("recipe_id", String, primary_key=True),
            Column("recipe_name", String),
            Column("cuisine", String),
            Column("prep_time", Integer),
            Column("cook_time", Integer),
            Column("total_time", Integer),
            Column("servings", Integer),
            Column("calories", Float),
            Column("ratings", Float),
            Column("ingredient_count", Integer),
            Column("complexity_score", Integer),
            Column("dietary_flags", String)
        )
        
    # Recipe Instructions table
    if not check_table_exists("RecipeInstruction"):
        RecipeInstruction = Table(
            "RecipeInstruction",
            metadata,
            Column("id", Integer, primary_key=True),
            Column("recipe_id", String),
            Column("step_number", Integer),
            Column("instruction", String)
        )

    # Cuisine table if not already created
    if not check_table_exists("Cuisine"):
        Cuisine = Table(
            "Cuisine",
            metadata,
            Column("id", Integer, primary_key=True),
            Column("name", String, unique=True)
        )

    # Create all tables
    metadata.create_all(engine)
    logger.info("Tables created successfully")

def clean_dataframe(df: pd.DataFrame) -> pd.DataFrame:
    """Clean a dataframe by stripping whitespace, removing quotes, handling NaN values."""
    # Replace missing values with None
    df = df.where(pd.notnull(df), None)
    
    # Strip whitespace from string columns
    for col in df.select_dtypes(include=['object']).columns:
        if df[col].dtype == 'object':
            df[col] = df[col].str.strip() if df[col].dtype == 'object' else df[col]
            
            # Remove quotes from string columns
            df[col] = df[col].str.replace('"', '') if df[col].dtype == 'object' else df[col]
            
    return df

def load_recipe_general():
    """Load recipe general data from CSV."""
    file_path = os.path.join(DATA_DIR, RECIPE_GENERAL_FILE)
    logger.info(f"Loading recipe general data from {file_path}")
    
    if not os.path.exists(file_path):
        logger.error(f"File not found: {file_path}")
        return
    
    try:
        # Read the CSV file
        df = pd.read_csv(file_path)
        df = clean_dataframe(df)
        
        # Rename column to match our schema
        df = df.rename(columns={
            'Recipe_id': 'id',
            'Recipe_title': 'name'
        })
        
        # Insert data into database in batches
        with engine.begin() as conn:
            for i in range(0, len(df), BATCH_SIZE):
                batch = df.iloc[i:i+BATCH_SIZE]
                batch.to_sql('Recipe', conn, if_exists='append', index=False, 
                            method='multi', chunksize=BATCH_SIZE)
                logger.info(f"Inserted batch {i//BATCH_SIZE + 1} of {(len(df)//BATCH_SIZE) + 1} into Recipe table")
        
        logger.info(f"Loaded {len(df)} recipes into Recipe table")
        
    except Exception as e:
        logger.error(f"Error loading recipe general data: {e}")

def load_ingredients():
    """Load ingredient data from CSV."""
    file_path = os.path.join(DATA_DIR, RECIPE_INGREDIENT_FLAVOR_FILE)
    logger.info(f"Loading ingredient data from {file_path}")
    
    if not os.path.exists(file_path):
        logger.error(f"File not found: {file_path}")
        return
    
    try:
        # Read the CSV file
        df = pd.read_csv(file_path)
        df = clean_dataframe(df)
        
        # Rename columns to match our schema
        df = df.rename(columns={
            'IngID': 'id',
            'ingredient': 'name',
            'generic_name': 'generic_name',
            'wikilink': 'wiki_link',
            'wikiimage': 'wiki_image',
            'FlavorDB_Category': 'flavordb_category',
            'Dietrx_Category': 'dietrx_category',
            'Flavor_DB_Link': 'flavor_db_link',
            'flavordb_id': 'flavordb_id',
            'Diet_rx_link': 'diet_rx_link'
        })
        
        # Insert data into database in batches
        with engine.begin() as conn:
            for i in range(0, len(df), BATCH_SIZE):
                batch = df.iloc[i:i+BATCH_SIZE]
                batch.to_sql('Ingredient', conn, if_exists='append', index=False,
                            method='multi', chunksize=BATCH_SIZE)
                logger.info(f"Inserted batch {i//BATCH_SIZE + 1} of {(len(df)//BATCH_SIZE) + 1} into Ingredient table")
        
        logger.info(f"Loaded {len(df)} ingredients into Ingredient table")
        
    except Exception as e:
        logger.error(f"Error loading ingredient data: {e}")

def load_recipe_ingredients():
    """Load recipe ingredient relationships from CSV."""
    file_path = os.path.join(DATA_DIR, RECIPE_INGREDIENT_PHRASE_FILE)
    logger.info(f"Loading recipe ingredient data from {file_path}")
    
    if not os.path.exists(file_path):
        logger.error(f"File not found: {file_path}")
        return
    
    try:
        # Read the CSV file
        df = pd.read_csv(file_path)
        df = clean_dataframe(df)
        
        # Rename columns to match our schema
        df = df.rename(columns={
            'recipe_no': 'recipe_id',
            'ing_id': 'ingredient_id',
            'state': 'preparation_method'
        })
        
        # Select relevant columns
        df = df[['recipe_id', 'ingredient_id', 'quantity', 'unit', 'preparation_method']]
        
        # Insert data into database in batches
        with engine.begin() as conn:
            for i in range(0, len(df), BATCH_SIZE):
                batch = df.iloc[i:i+BATCH_SIZE]
                batch.to_sql('RecipeIngredient', conn, if_exists='append', index=True,
                            method='multi', chunksize=BATCH_SIZE)
                logger.info(f"Inserted batch {i//BATCH_SIZE + 1} of {(len(df)//BATCH_SIZE) + 1} into RecipeIngredient table")
        
        logger.info(f"Loaded {len(df)} recipe-ingredient relationships into RecipeIngredient table")
        
    except Exception as e:
        logger.error(f"Error loading recipe ingredient data: {e}")

def load_merged_recipes():
    """Load merged recipe data from CSV."""
    file_path = os.path.join(DATA_DIR, MERGED_RECIPES_FILE)
    logger.info(f"Loading merged recipe data from {file_path}")
    
    if not os.path.exists(file_path):
        logger.error(f"File not found: {file_path}")
        return
    
    try:
        # Read the CSV file
        df = pd.read_csv(file_path)
        df = clean_dataframe(df)
        
        # Insert data into database in batches
        with engine.begin() as conn:
            for i in range(0, len(df), BATCH_SIZE):
                batch = df.iloc[i:i+BATCH_SIZE]
                batch.to_sql('MergedRecipes', conn, if_exists='append', index=False,
                            method='multi', chunksize=BATCH_SIZE)
                logger.info(f"Inserted batch {i//BATCH_SIZE + 1} of {(len(df)//BATCH_SIZE) + 1} into MergedRecipes table")
        
        logger.info(f"Loaded {len(df)} merged recipes into MergedRecipes table")
        
    except Exception as e:
        logger.error(f"Error loading merged recipe data: {e}")

def load_recipe_instructions():
    """Load recipe instructions from JSON file."""
    file_path = os.path.join(DATA_DIR, RECIPE_INSTRUCTIONS_FILE)
    logger.info(f"Loading recipe instructions from {file_path}")
    
    if not os.path.exists(file_path):
        logger.error(f"File not found: {file_path}")
        return
    
    try:
        # Read the JSON file
        with open(file_path, 'r') as f:
            data = json.load(f)
        
        # Extract instructions
        instructions = []
        for recipe in data.get('recipes', []):
            recipe_id = recipe.get('recipe_id')
            for step in recipe.get('steps', []):
                instructions.append({
                    'recipe_id': recipe_id,
                    'step_number': step.get('step_number'),
                    'instruction': step.get('instruction')
                })
        
        # Convert to dataframe
        df = pd.DataFrame(instructions)
        
        # Insert data into database in batches
        with engine.begin() as conn:
            for i in range(0, len(df), BATCH_SIZE):
                batch = df.iloc[i:i+BATCH_SIZE]
                batch.to_sql('RecipeInstruction', conn, if_exists='append', index=True,
                            method='multi', chunksize=BATCH_SIZE)
                logger.info(f"Inserted batch {i//BATCH_SIZE + 1} of {(len(df)//BATCH_SIZE) + 1} into RecipeInstruction table")
        
        logger.info(f"Loaded {len(df)} recipe instructions into RecipeInstruction table")
        
    except Exception as e:
        logger.error(f"Error loading recipe instructions: {e}")

def populate_cuisines():
    """Extract and populate unique cuisines from recipe data."""
    logger.info("Populating cuisines from recipe data")
    
    try:
        # Extract unique cuisines from Recipe table
        query = text("""
            INSERT INTO "Cuisine" (name)
            SELECT DISTINCT cuisine FROM "Recipe" WHERE cuisine IS NOT NULL
            ON CONFLICT (name) DO NOTHING
        """)
        
        with engine.begin() as conn:
            conn.execute(query)
        
        # Count the number of cuisines
        query = text('SELECT COUNT(*) FROM "Cuisine"')
        with engine.connect() as conn:
            count = conn.execute(query).scalar()
        
        logger.info(f"Populated {count} cuisines into Cuisine table")
        
    except Exception as e:
        logger.error(f"Error populating cuisines: {e}")

def update_foreign_keys():
    """Update foreign key references in tables."""
    logger.info("Updating foreign key references")
    
    try:
        # Add cuisine_id to Recipe table
        if not check_column_exists("Recipe", "cuisine_id"):
            query = text("""
                ALTER TABLE "Recipe" ADD COLUMN cuisine_id INTEGER;
                UPDATE "Recipe" r
                SET cuisine_id = c.id
                FROM "Cuisine" c
                WHERE r.cuisine = c.name;
            """)
            
            with engine.begin() as conn:
                conn.execute(query)
            
            logger.info("Added and updated cuisine_id in Recipe table")
        
    except Exception as e:
        logger.error(f"Error updating foreign keys: {e}")

def check_column_exists(table_name: str, column_name: str) -> bool:
    """Check if a column exists in a table."""
    inspector = inspect(engine)
    columns = [c['name'] for c in inspector.get_columns(table_name)]
    return column_name in columns

def main():
    """Main execution function."""
    start_time = time.time()
    logger.info("Starting data import...")
    
    # Create tables
    create_tables()
    
    # Load data
    load_recipe_general()
    load_ingredients()
    load_recipe_ingredients()
    load_merged_recipes()
    load_recipe_instructions()
    
    # Post-processing
    populate_cuisines()
    update_foreign_keys()
    
    elapsed_time = time.time() - start_time
    logger.info(f"Data import completed in {elapsed_time:.2f} seconds")

if __name__ == "__main__":
    main()