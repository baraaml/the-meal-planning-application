"""
Recipe-specific database queries.
"""
from config.db import execute_query, execute_query_single, execute_transaction

def get_recipe(recipe_id: int):
    """Get a recipe by ID with ingredients and instructions."""
    query = """
    SELECT 
        r.recipe_id, r.recipe_title, r.region, r.sub_region, 
        r.continent, r.source, r.image_url, r.cook_time, 
        r.prep_time, r.total_time, r.servings, r.url, r.calories
    FROM recipes r
    WHERE r.recipe_id = %(recipe_id)s
    """
    recipe = execute_query_single(query, {"recipe_id": recipe_id})
    
    if not recipe:
        return None
    
    # Get ingredients
    ing_query = """
    SELECT 
        i.ingredient_id, i.ingredient_name, 
        ri.quantity, ri.unit, ri.ingredient_state
    FROM recipe_ingredients ri
    JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
    WHERE ri.recipe_id = %(recipe_id)s
    """
    ingredients = execute_query(ing_query, {"recipe_id": recipe_id})
    
    # Get instructions
    instr_query = """
    SELECT instructions
    FROM recipe_instructions
    WHERE recipe_id = %(recipe_id)s
    """
    instructions = execute_query_single(instr_query, {"recipe_id": recipe_id})
    
    # Add to recipe
    recipe["ingredients"] = ingredients or []
    recipe["instructions"] = instructions["instructions"] if instructions else None
    
    return recipe

def get_recipes(limit=20, offset=0, filters=None, calories_filter=None):
    """Get a list of recipes with optional filtering."""
    query = """
    SELECT 
        recipe_id, recipe_title, region, sub_region
    FROM recipes
    WHERE 1=1
    """
    
    params = {
        "limit": limit,
        "offset": offset
    }
    
    # Add filters if provided
    if filters:
        for key, value in filters.items():
            if value:
                query += f" AND {key} = %({key})s"
                params[key] = value
    
    # Add calorie filters if provided
    if calories_filter:
        if "min_calories" in calories_filter:
            query += " AND calories >= %(min_calories)s"
            params["min_calories"] = calories_filter["min_calories"]
        if "max_calories" in calories_filter:
            query += " AND calories <= %(max_calories)s"
            params["max_calories"] = calories_filter["max_calories"]
    
    # Add ORDER BY and LIMIT clauses
    query += """
    ORDER BY recipe_id
    LIMIT %(limit)s OFFSET %(offset)s
    """
    
    return execute_query(query, params)

def create_recipe(recipe_data):
    """Create a new recipe."""
    query = """
    INSERT INTO recipes (
        recipe_title, region, sub_region, continent, source,
        image_url, cook_time, prep_time, total_time, servings,
        url, calories
    ) VALUES (
        %(recipe_title)s, %(region)s, %(sub_region)s, %(continent)s, %(source)s,
        %(image_url)s, %(cook_time)s, %(prep_time)s, %(total_time)s, %(servings)s,
        %(url)s, %(calories)s
    )
    RETURNING recipe_id
    """
    
    result = execute_query_single(query, recipe_data)
    return result["recipe_id"] if result else None

def update_recipe(recipe_id, recipe_data):
    """Update an existing recipe."""
    # Build the SET clause dynamically
    set_clauses = []
    params = {"recipe_id": recipe_id}
    
    for key, value in recipe_data.items():
        if key != "recipe_id" and value is not None:
            set_clauses.append(f"{key} = %({key})s")
            params[key] = value
    
    if not set_clauses:
        return False
    
    set_clause = ", ".join(set_clauses)
    query = f"""
    UPDATE recipes
    SET {set_clause}
    WHERE recipe_id = %(recipe_id)s
    """
    
    execute_query(query, params)
    return True

def delete_recipe(recipe_id):
    """Delete a recipe and its related data."""
    # Create a list of queries to execute in a transaction
    queries = [
        ("DELETE FROM recipe_embeddings WHERE recipe_id = %(recipe_id)s", {"recipe_id": recipe_id}),
        ("DELETE FROM recipe_ingredients WHERE recipe_id = %(recipe_id)s", {"recipe_id": recipe_id}),
        ("DELETE FROM recipe_instructions WHERE recipe_id = %(recipe_id)s", {"recipe_id": recipe_id}),
        ("DELETE FROM recipe_diet_attributes WHERE recipe_id = %(recipe_id)s", {"recipe_id": recipe_id}),
        ("DELETE FROM recipes WHERE recipe_id = %(recipe_id)s", {"recipe_id": recipe_id})
    ]
    
    try:
        execute_transaction(queries)
        return True
    except:
        return False

def get_recipes_without_embeddings(limit=50):
    """Get recipes that don't have embeddings yet."""
    query = """
    SELECT r.recipe_id, r.recipe_title
    FROM recipes r
    LEFT JOIN recipe_embeddings re ON r.recipe_id = re.recipe_id
    WHERE re.embedding_id IS NULL
    LIMIT %(limit)s
    """
    
    return execute_query(query, {"limit": limit})