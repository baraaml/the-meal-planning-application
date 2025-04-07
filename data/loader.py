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

    # Tables based directly on the CSV files
    tables = {
        "Recipe": {
            "file": RECIPE_GENERAL_FILE,
            "id_column": "Recipe_id",
            "columns": None  # Will be populated from CSV headers
        },
        "RecipeIngredient": {
            "file": RECIPE_INGREDIENT_PHRASE_FILE,
            "id_column": None,  # Will be auto-generated
            "columns": None  # Will be populated from CSV headers
        },
        "RecipeIngredientFlavor": {
            "file": RECIPE_INGREDIENT_FLAVOR_FILE,
            "id_column": "IngID",
            "columns": None  # Will be populated from CSV headers
        }
    }
    
    # Create tables based on CSV structure
    for table_name, table_info in tables.items():
        if not check_table_exists(table_name):
            # Read CSV headers to get column names
            file_path = os.path.join(DATA_DIR, table_info["file"])
            if os.path.exists(file_path):
                df = pd.read_csv(file_path, nrows=0)
                columns = df.columns.tolist()
                
                # Create columns for the table
                table_columns = []
                for col in columns:
                    # Use the id column as primary key if specified
                    if table_info["id_column"] and col == table_info["id_column"]:
                        table_columns.append(Column(col, String, primary_key=True))
                    else:
                        # Determine column type based on CSV column name patterns
                        if any(substr in col.lower() for substr in ["time", "date", "created", "updated"]):
                            table_columns.append(Column(col, String))  # Use String for time/date fields
                        elif any(substr in col.lower() for substr in ["id", "_id", "ingid"]):
                            table_columns.append(Column(col, String))  # Use String for ID fields
                        elif any(substr in col.lower() for substr in ["count", "number", "qty", "quantity"]):
                            table_columns.append(Column(col, Float))  # Use Float for numeric fields
                        else:
                            table_columns.append(Column(col, String))  # Default to String
                
                # Add auto-increment ID if no primary key
                if not table_info["id_column"]:
                    table_columns.insert(0, Column("id", Integer, primary_key=True))
                
                # Create the table
                Table(table_name, metadata, *table_columns)
                logger.info(f"Created table definition for {table_name}")
            else:
                logger.warning(f"File not found: {file_path}. Skipping table {table_name}")
    
    # Create the Recipe Instructions table if not already created
    if not check_table_exists("RecipeInstruction"):
        RecipeInstruction = Table(
            "RecipeInstruction",
            metadata,
            Column("id", Integer, primary_key=True),
            Column("recipe_id", String),
            Column("step_number", Integer),
            Column("instruction", String)
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

def load_ingredient_flavors():
    """Load ingredient flavor data from CSV."""
    file_path = os.path.join(DATA_DIR, RECIPE_INGREDIENT_FLAVOR_FILE)
    logger.info(f"Loading ingredient flavor data from {file_path}")
    
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
                batch.to_sql('RecipeIngredientFlavor', conn, if_exists='append', index=False,
                            method='multi', chunksize=BATCH_SIZE)
                logger.info(f"Inserted batch {i//BATCH_SIZE + 1} of {(len(df)//BATCH_SIZE) + 1} into RecipeIngredientFlavor table")
        
        logger.info(f"Loaded {len(df)} ingredient flavors into RecipeIngredientFlavor table")
        
    except Exception as e:
        logger.error(f"Error loading ingredient flavor data: {e}")

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

def main():
    """Main execution function."""
    start_time = time.time()
    logger.info("Starting data import...")
    
    # Create tables
    create_tables()
    
    # Load data
    load_recipe_general()
    load_recipe_ingredients()
    load_ingredient_flavors()
    load_recipe_instructions()
    
    elapsed_time = time.time() - start_time
    logger.info(f"Data import completed in {elapsed_time:.2f} seconds")

if __name__ == "__main__":
    main()