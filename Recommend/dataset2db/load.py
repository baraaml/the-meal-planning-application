import pandas as pd
import json
import re
import os
from sqlalchemy import create_engine, text
import logging

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    filename='mealflow_import.log'
)
console = logging.StreamHandler()
console.setLevel(logging.INFO)
logging.getLogger('').addHandler(console)
logger = logging.getLogger(__name__)

# Database connection
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://mealflow:mealflow@localhost:5432/meals")
engine = create_engine(DATABASE_URL)

def clean_value(value):
    """Clean values for database insertion"""
    if pd.isna(value) or value == '':
        return None
    return value

def parse_time(time_str):
    """Parse various time formats"""
    if pd.isna(time_str) or time_str == '':
        return None
        
    # Simple numeric
    if str(time_str).isdigit():
        return int(time_str)
        
    # Format: "1Day20" (1 day, 20 minutes)
    day_match = re.match(r'(\d+)Day(\d+)', str(time_str))
    if day_match:
        days = int(day_match.group(1))
        minutes = int(day_match.group(2))
        return days * 24 * 60 + minutes
    
    # Format: "30-45" (take average)
    range_match = re.match(r'(\d+)\s*-\s*(\d+)', str(time_str))
    if range_match:
        try:
            min_time = int(range_match.group(1))
            max_time = int(range_match.group(2))
            return (min_time + max_time) // 2
        except:
            pass
        
    return None

def parse_servings(servings):
    """Parse serving values including fractional formats"""
    if pd.isna(servings) or servings == '':
        return None
        
    # Simple numeric
    if str(servings).isdigit():
        return int(servings)
        
    # Format: "01-Apr" (1/4)
    fraction_match = re.match(r'(\d+)-(\w+)', str(servings))
    if fraction_match:
        try:
            numerator = int(fraction_match.group(1))
            month_name = fraction_match.group(2)
            
            month_map = {
                'Jan': 1, 'Feb': 2, 'Mar': 3, 'Apr': 4,
                'May': 5, 'Jun': 6, 'Jul': 7, 'Aug': 8,
                'Sep': 9, 'Oct': 10, 'Nov': 11, 'Dec': 12
            }
            
            if month_name in month_map:
                denominator = month_map[month_name]
                return float(numerator) / float(denominator)
        except:
            pass
    
    # Format: "4-6" (take lower bound)
    range_match = re.match(r'(\d+)\s*-\s*(\d+)', str(servings))
    if range_match:
        try:
            return int(range_match.group(1))
        except:
            pass
            
    return None

def execute_query(query, params=None):
    """Execute a database query with robust error handling"""
    try:
        with engine.connect() as conn:
            if params:
                result = conn.execute(text(query), params)
            else:
                result = conn.execute(text(query))
            conn.commit()
            return result
    except Exception as e:
        logger.error(f"Database error: {e}")
        return None

def get_valid_ids(table, id_column):
    """Get set of valid IDs from a table"""
    query = f"SELECT {id_column} FROM {table}"
    result = execute_query(query)
    if result:
        return {row[0] for row in result}
    return set()

def import_ingredients(file_path):
    """Import ingredients from merged.csv"""
    logger.info(f"Importing ingredients from {file_path}")
    
    try:
        df = pd.read_csv(file_path, low_memory=False)
        df = df.fillna('')
        
        success = 0
        total = len(df)
        
        for _, row in df.iterrows():
            if pd.isna(row.get('IngID', '')) or row.get('IngID', '') == '':
                continue
                
            try:
                # Insert ingredient
                params = {
                    'id': row.get('IngID'),
                    'name': row.get('ingredient', ''),
                    'generic': row.get('generic_name', ''),
                    'frequency': clean_value(row.get('frequency')),
                    'wiki_link': row.get('wikilink', ''),
                    'wiki_image': row.get('wikiimage', ''),
                    'flavor_cat': row.get('FlavorDB_Category', ''),
                    'dietrx_cat': row.get('Dietrx_Category', ''),
                    'flavor_link': row.get('Flavor_DB_Link', ''),
                    'diet_link': row.get('Diet_rx_link', ''),
                    'raw': row.get('ingredient_raw', '')
                }
                
                query = """
                INSERT INTO ingredients (
                    ingredient_id, ingredient_name, generic_name, frequency, 
                    wiki_link, wiki_image, flavor_db_category, 
                    dietrx_category, flavor_db_link, diet_rx_link, ingredient_raw
                ) VALUES (
                    :id, :name, :generic, :frequency, 
                    :wiki_link, :wiki_image, :flavor_cat, 
                    :dietrx_cat, :flavor_link, :diet_link, :raw
                ) ON CONFLICT (ingredient_id) DO UPDATE SET
                    ingredient_name = EXCLUDED.ingredient_name,
                    generic_name = EXCLUDED.generic_name
                """
                
                execute_query(query, params)
                
                # Insert nutrition if applicable
                serving_size = row.get('serving size (g)')
                if not pd.isna(serving_size) and serving_size != '':
                    nutr_params = {
                        'id': row.get('IngID'),
                        'serving': clean_value(serving_size),
                        'energy': clean_value(row.get('energy (kcal)')),
                        'protein': clean_value(row.get('protein (g)')),
                        'carbs': clean_value(row.get('carbohydrate (g)')),
                        'fat': clean_value(row.get('total fat (g)')),
                        'sugar': clean_value(row.get('total sugar (g)'))
                    }
                    
                    nutr_query = """
                    INSERT INTO ingredient_nutrition (
                        ingredient_id, serving_size, energy_kcal, 
                        protein_g, carbohydrate_g, total_fat_g, total_sugar_g
                    ) VALUES (
                        :id, :serving, :energy, 
                        :protein, :carbs, :fat, :sugar
                    ) ON CONFLICT (ingredient_id) DO UPDATE SET
                        serving_size = EXCLUDED.serving_size
                    """
                    
                    execute_query(nutr_query, nutr_params)
                
                success += 1
                if success % 1000 == 0:
                    logger.info(f"Processed {success}/{total} ingredients")
                
            except Exception as e:
                logger.error(f"Error with ingredient {row.get('IngID')}: {e}")
        
        logger.info(f"Imported {success}/{total} ingredients successfully")
        
    except Exception as e:
        logger.error(f"Failed to import ingredients: {e}")

def import_recipes(file_path):
    """Import recipes from RecipeDB_general.csv"""
    logger.info(f"Importing recipes from {file_path}")
    
    try:
        df = pd.read_csv(file_path, low_memory=False)
        
        success = 0
        total = len(df)
        
        for _, row in df.iterrows():
            # Skip rows where region does not contain 'egypt' or 'middle' as substring
            region = str(row.get('Region', '')).lower()
            if not ('egypt' in region or 'middle' in region):
                continue
            
            recipe_id = row.get('Recipe_id')
            if pd.isna(recipe_id) or recipe_id == '':
                continue
                
            try:
                # Parse time values
                cook_time = parse_time(row.get('cook_time'))
                prep_time = parse_time(row.get('prep_time'))
                total_time = parse_time(row.get('total_time'))
                servings = parse_servings(row.get('servings'))
                
                # Insert recipe
                params = {
                    'id': recipe_id,
                    'title': row.get('Recipe_title', ''),
                    'region': clean_value(row.get('Region')),
                    'sub_region': clean_value(row.get('Sub_region')),
                    'continent': clean_value(row.get('Continent')),
                    'source': clean_value(row.get('Source')),
                    'img_url': clean_value(row.get('img_url')),
                    'cook_time': cook_time,
                    'prep_time': prep_time,
                    'total_time': total_time,
                    'servings': servings,
                    'url': clean_value(row.get('url')),
                    'calories': clean_value(row.get('Calories'))
                }
                
                query = """
                INSERT INTO recipes (
                    recipe_id, recipe_title, region, sub_region, 
                    continent, source, image_url, cook_time, 
                    prep_time, total_time, servings, url, calories
                ) VALUES (
                    :id, :title, :region, :sub_region, 
                    :continent, :source, :img_url, :cook_time, 
                    :prep_time, :total_time, :servings, :url, :calories
                ) ON CONFLICT (recipe_id) DO UPDATE SET
                    recipe_title = EXCLUDED.recipe_title
                """
                
                execute_query(query, params)
                
                # Insert diet attributes (optional)
                try:
                    diet_params = {
                        'id': recipe_id,
                        'vegan': bool(clean_value(row.get('vegan', 0))),
                        'pescetarian': bool(clean_value(row.get('pescetarian', 0))),
                        'ovo_veg': bool(clean_value(row.get('ovo_vegetarian', 0))),
                        'lacto_veg': bool(clean_value(row.get('lacto_vegetarian', 0))),
                        'ovo_lacto_veg': bool(clean_value(row.get('ovo_lacto_vegetarian', 0)))
                    }
                    
                    diet_query = """
                    INSERT INTO recipe_diet_attributes (
                        recipe_id, vegan, pescetarian, ovo_vegetarian, 
                        lacto_vegetarian, ovo_lacto_vegetarian
                    ) VALUES (
                        :id, :vegan, :pescetarian, :ovo_veg, 
                        :lacto_veg, :ovo_lacto_veg
                    ) ON CONFLICT (recipe_id) DO UPDATE SET
                        vegan = EXCLUDED.vegan
                    """
                    
                    execute_query(diet_query, diet_params)
                except Exception:
                    pass
                
                # Insert utensils (optional)
                utensils = row.get('Utensils')
                if isinstance(utensils, str) and utensils:
                    for utensil in utensils.split('||'):
                        try:
                            # First check if the utensil already exists for this recipe
                            check_query = """
                            SELECT 1 FROM recipe_utensils 
                            WHERE recipe_id = :id AND utensil = :utensil
                            """
                            result = execute_query(check_query, {'id': recipe_id, 'utensil': utensil})
                            
                            # If not exists, insert
                            if not result or not result.fetchone():
                                utensil_query = """
                                INSERT INTO recipe_utensils (recipe_id, utensil)
                                VALUES (:id, :utensil)
                                """
                                execute_query(utensil_query, {'id': recipe_id, 'utensil': utensil})
                        except Exception as e:
                            logger.error(f"Error inserting utensil '{utensil}' for recipe {recipe_id}: {e}")
                
                # Insert processes (optional)
                processes = row.get('Processes')
                if isinstance(processes, str) and processes:
                    for idx, process in enumerate(processes.split('||'), 1):
                        try:
                            # First check if the process already exists for this recipe
                            check_query = """
                            SELECT 1 FROM recipe_processes 
                            WHERE recipe_id = :id AND process_step = :process
                            """
                            result = execute_query(check_query, {'id': recipe_id, 'process': process})
                            
                            # If not exists, insert
                            if not result or not result.fetchone():
                                process_query = """
                                INSERT INTO recipe_processes (recipe_id, process_step, step_order)
                                VALUES (:id, :process, :order)
                                """
                                execute_query(process_query, {
                                    'id': recipe_id, 
                                    'process': process, 
                                    'order': idx
                                })
                        except Exception as e:
                            logger.error(f"Error inserting process '{process}' for recipe {recipe_id}: {e}")
                
                success += 1
                if success % 1000 == 0:
                    logger.info(f"Processed {success}/{total} recipes")
                
            except Exception as e:
                logger.error(f"Error with recipe {recipe_id}: {e}")
        
        logger.info(f"Imported {success}/{total} recipes successfully")
        
    except Exception as e:
        logger.error(f"Failed to import recipes: {e}")

def parse_fraction(fraction_str):
    """Parse fraction strings (like '1/2', '2/3') to float values"""
    if pd.isna(fraction_str) or fraction_str == '':
        return None
        
    # Handle simple fractions like "1/2", "1/4", etc.
    if '/' in str(fraction_str):
        try:
            num, denom = map(int, str(fraction_str).split('/'))
            return float(num) / float(denom)
        except (ValueError, ZeroDivisionError):
            pass
            
    # Handle ranges like "1 -2" or "1-2"
    range_match = re.match(r'(\d+)\s*-\s*(\d+)', str(fraction_str))
    if range_match:
        try:
            min_val = int(range_match.group(1))
            return float(min_val)  # Just use the lower bound
        except ValueError:
            pass
            
    # Try converting directly if it's a number
    try:
        return float(fraction_str)
    except (ValueError, TypeError):
        return None

def import_ingredient_phrases(file_path):
    """Import ingredient phrases from RecipeDB_ingredient_phrase.csv"""
    logger.info(f"Importing ingredient phrases from {file_path}")
    
    try:
        df = pd.read_csv(file_path, low_memory=False)
        
        # Get valid IDs
        valid_recipes = get_valid_ids('recipes', 'recipe_id')
        valid_ingredients = get_valid_ids('ingredients', 'ingredient_id')
        
        logger.info(f"Found {len(valid_recipes)} valid recipes and {len(valid_ingredients)} valid ingredients")
        
        success = 0
        total = len(df)
        
        for _, row in df.iterrows():
            recipe_id = row.get('recipe_no')
            ingredient_id = row.get('ing_id')
            
            if (pd.isna(recipe_id) or pd.isna(ingredient_id) or 
                recipe_id not in valid_recipes or 
                ingredient_id not in valid_ingredients):
                continue
            
            try:
                # Parse quantity as float if it's a fraction
                quantity_str = row.get('quantity')
                quantity_float = parse_fraction(quantity_str)
                
                params = {
                    'recipe_id': recipe_id,
                    'ingredient_id': ingredient_id,
                    'phrase': clean_value(row.get('ingredient_Phrase')),
                    'quantity': quantity_float,  # Use parsed float value
                    'unit': clean_value(row.get('unit')),
                    'state': clean_value(row.get('state')),
                    'size': clean_value(row.get('size'))
                }
                
                query = """
                INSERT INTO recipe_ingredients (
                    recipe_id, ingredient_id, ingredient_phrase, 
                    quantity, unit, ingredient_state, size
                ) VALUES (
                    :recipe_id, :ingredient_id, :phrase, 
                    :quantity, :unit, :state, :size
                ) ON CONFLICT (recipe_id, ingredient_id) DO UPDATE SET
                    ingredient_phrase = EXCLUDED.ingredient_phrase
                """
                
                execute_query(query, params)
                
                success += 1
                if success % 5000 == 0:
                    logger.info(f"Processed {success}/{total} ingredient phrases")
                
            except Exception as e:
                logger.error(f"Error with ingredient phrase {recipe_id}/{ingredient_id}: {e}")
        
        logger.info(f"Imported {success}/{total} ingredient phrases successfully")
        
    except Exception as e:
        logger.error(f"Failed to import ingredient phrases: {e}")

def import_instructions(file_path):
    """Import recipe instructions from RecipeDB_instructions.json"""
    logger.info(f"Importing recipe instructions from {file_path}")
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            instructions_data = json.load(f)
        
        # Get valid recipe IDs
        valid_recipes = get_valid_ids('recipes', 'recipe_id')
        logger.info(f"Found {len(valid_recipes)} valid recipes")
        
        success = 0
        total = len(instructions_data)
        
        # Process list of instruction objects
        for instruction_obj in instructions_data:
            if not isinstance(instruction_obj, dict):
                continue
                
            try:
                recipe_id_str = instruction_obj.get('recipe_id')
                if not recipe_id_str:
                    continue
                    
                # Convert string ID to int
                recipe_id = int(recipe_id_str)
                
                # Skip if recipe doesn't exist
                if recipe_id not in valid_recipes:
                    continue
                
                # Get steps
                steps = instruction_obj.get('steps', '')
                if not steps:  # Skip empty instructions
                    continue
                
                query = """
                INSERT INTO recipe_instructions (recipe_id, instructions)
                VALUES (:recipe_id, :instructions)
                ON CONFLICT (recipe_id) DO UPDATE SET
                    instructions = EXCLUDED.instructions
                """
                
                execute_query(query, {
                    'recipe_id': recipe_id,
                    'instructions': steps
                })
                
                success += 1
                if success % 1000 == 0:
                    logger.info(f"Processed {success}/{total} instructions")
                    
            except Exception as e:
                logger.error(f"Error with instruction for recipe {recipe_id_str}: {e}")
        
        logger.info(f"Imported {success}/{total} recipe instructions successfully")
        
    except Exception as e:
        logger.error(f"Failed to import recipe instructions: {e}")
        import traceback
        logger.error(traceback.format_exc())

def import_ingredient_flavors(file_path):
    """Import ingredient flavor profiles from RecipeDB_ingredient_flavor.csv"""
    logger.info(f"Importing ingredient flavors from {file_path}")
    
    try:
        df = pd.read_csv(file_path, low_memory=False)
        
        # Get valid ingredient IDs
        valid_ingredients = get_valid_ids('ingredients', 'ingredient_id')
        logger.info(f"Found {len(valid_ingredients)} valid ingredients")
        
        success = 0
        total = len(df)
        
        for _, row in df.iterrows():
            ingredient_id = row.get('ingredient_id')
            
            if pd.isna(ingredient_id) or ingredient_id == '' or ingredient_id not in valid_ingredients:
                continue
            
            try:
                params = {
                    'ingredient_id': ingredient_id,
                    'sweet': clean_value(row.get('sweet', 0)),
                    'salty': clean_value(row.get('salty', 0)),
                    'sour': clean_value(row.get('sour', 0)),
                    'bitter': clean_value(row.get('bitter', 0)),
                    'umami': clean_value(row.get('umami', 0)),
                    'spicy': clean_value(row.get('spicy', 0))
                }
                
                query = """
                INSERT INTO ingredient_flavors (
                    ingredient_id, sweet, salty, sour, bitter, umami, spicy
                ) VALUES (
                    :ingredient_id, :sweet, :salty, :sour, :bitter, :umami, :spicy
                ) ON CONFLICT (ingredient_id) DO UPDATE SET
                    sweet = EXCLUDED.sweet,
                    salty = EXCLUDED.salty,
                    sour = EXCLUDED.sour,
                    bitter = EXCLUDED.bitter,
                    umami = EXCLUDED.umami,
                    spicy = EXCLUDED.spicy
                """
                
                execute_query(query, params)
                
                success += 1
                if success % 1000 == 0:
                    logger.info(f"Processed {success}/{total} ingredient flavors")
                
            except Exception as e:
                logger.error(f"Error with ingredient flavor {ingredient_id}: {e}")
        
        logger.info(f"Imported {success}/{total} ingredient flavors successfully")
        
    except Exception as e:
        logger.error(f"Failed to import ingredient flavors: {e}")

def main():
    # Update file paths to your actual file locations
    file_paths = {
        'ingredients': 'dataset2db/merged.csv',
        'recipes': 'dataset2db/RecipeDB_general.csv',
        'ingredient_phrases': 'dataset2db/RecipeDB_ingredient_phrase.csv',
        'instructions': 'dataset2db/RecipeDB_instructions.json',
        'ingredient_flavors': 'dataset2db/RecipeDB_ingredient_flavor.csv'
    }
    
    logger.info("Starting MealFlow database import")
    
    # Import in order to respect foreign key constraints
    import_ingredients(file_paths['ingredients'])
    import_recipes(file_paths['recipes'])
    import_ingredient_phrases(file_paths['ingredient_phrases'])
    import_instructions(file_paths['instructions'])
    import_ingredient_flavors(file_paths['ingredient_flavors'])
    
    logger.info("MealFlow database import completed")

if __name__ == "__main__":
    main()