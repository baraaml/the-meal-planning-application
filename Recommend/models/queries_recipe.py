"""
Recipe-specific database queries.
"""
import math
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

def filter_recipes_by_calories(min_calories, max_calories, limit=20, offset=0):
    """Filter recipes by calorie range."""
    query = """
    SELECT 
        recipe_id, recipe_title, region, sub_region
    FROM recipes
    WHERE calories >= %(min)s AND calories <= %(max)s
    ORDER BY recipe_id
    LIMIT %(limit)s OFFSET %(offset)s
    """
    
    params = {
        "min": min_calories,
        "max": max_calories,
        "limit": limit,
        "offset": offset
    }
    
    return execute_query(query, params)

def search_recipes(
    query=None, 
    cuisines=None, 
    dietary=None, 
    include_ingredients=None, 
    exclude_ingredients=None,
    min_calories=None, 
    max_calories=None, 
    max_prep_time=None, 
    max_cook_time=None, 
    max_total_time=None,
    sort_by="relevance", 
    sort_order="desc", 
    limit=20, 
    offset=0
):
    """
    Advanced search for recipes with multiple criteria.
    
    Args:
        query: Text search query
        cuisines: List of cuisines (or comma-separated string)
        dietary: List of dietary restrictions (or comma-separated string)
        include_ingredients: List of ingredients to include (or comma-separated string)
        exclude_ingredients: List of ingredients to exclude (or comma-separated string)
        min_calories: Minimum calories
        max_calories: Maximum calories
        max_prep_time: Maximum preparation time in minutes
        max_cook_time: Maximum cooking time in minutes
        max_total_time: Maximum total time in minutes
        sort_by: Field to sort by (relevance, rating, time, calories)
        sort_order: Sort order (asc, desc)
        limit: Maximum number of results
        offset: Offset for pagination
        
    Returns:
        Dict containing results and metadata
    """
    # Initialize SQL query parts
    select_clause = """
    SELECT 
        r.recipe_id, 
        r.recipe_title, 
        r.region, 
        r.sub_region, 
        r.continent,
        r.source,
        r.image_url,
        r.cook_time,
        r.prep_time,
        r.total_time,
        r.servings,
        r.url,
        r.calories
    """
    
    from_clause = """
    FROM recipes r
    """
    
    where_clauses = ["1=1"]
    join_clauses = []
    params = {}
    
    # Process text search
    if query:
        where_clauses.append("(r.recipe_title ILIKE %(query)s OR r.description ILIKE %(query)s)")
        params["query"] = f"%{query}%"
    
    # Process cuisine/region filters
    if cuisines:
        if isinstance(cuisines, str):
            cuisine_list = cuisines.split(',')
        else:
            cuisine_list = cuisines
            
        cuisine_placeholders = [f"%(cuisine{i})s" for i in range(len(cuisine_list))]
        where_clauses.append(f"(r.region IN ({','.join(cuisine_placeholders)}) OR r.sub_region IN ({','.join(cuisine_placeholders)}))")
        for i, cuisine in enumerate(cuisine_list):
            params[f"cuisine{i}"] = cuisine.strip()
    
    # Process dietary restrictions
    if dietary:
        if isinstance(dietary, str):
            dietary_list = dietary.split(',')
        else:
            dietary_list = dietary
            
        join_clauses.append("LEFT JOIN recipe_diet_attributes rda ON r.recipe_id = rda.recipe_id")
        
        dietary_conditions = []
        for i, diet in enumerate(dietary_list):
            diet = diet.strip().lower()
            if diet == "vegan":
                dietary_conditions.append("rda.vegan = TRUE")
            elif diet == "pescetarian":
                dietary_conditions.append("rda.pescetarian = TRUE")
            elif diet == "lacto_vegetarian":
                dietary_conditions.append("rda.lacto_vegetarian = TRUE")
            elif diet == "ovo_vegetarian":
                dietary_conditions.append("rda.ovo_vegetarian = TRUE")
            elif diet == "ovo_lacto_vegetarian":
                dietary_conditions.append("rda.ovo_lacto_vegetarian = TRUE")
        
        if dietary_conditions:
            where_clauses.append(f"({' OR '.join(dietary_conditions)})")
    
    # Process ingredient inclusion
    if include_ingredients:
        if isinstance(include_ingredients, str):
            ingredient_list = include_ingredients.split(',')
        else:
            ingredient_list = include_ingredients
            
        ing_inclusion_conditions = []
        
        for i, ingredient in enumerate(ingredient_list):
            params[f"include_ing{i}"] = f"%{ingredient.strip()}%"
            ing_inclusion_conditions.append(f"""
                EXISTS (
                    SELECT 1 FROM recipe_ingredients ri
                    JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
                    WHERE ri.recipe_id = r.recipe_id AND i.ingredient_name ILIKE %(include_ing{i})s
                )
            """)
        
        if ing_inclusion_conditions:
            where_clauses.append(f"({' AND '.join(ing_inclusion_conditions)})")
    
    # Process ingredient exclusion
    if exclude_ingredients:
        if isinstance(exclude_ingredients, str):
            ingredient_list = exclude_ingredients.split(',')
        else:
            ingredient_list = exclude_ingredients
            
        ing_exclusion_conditions = []
        
        for i, ingredient in enumerate(ingredient_list):
            params[f"exclude_ing{i}"] = f"%{ingredient.strip()}%"
            ing_exclusion_conditions.append(f"""
                NOT EXISTS (
                    SELECT 1 FROM recipe_ingredients ri
                    JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
                    WHERE ri.recipe_id = r.recipe_id AND i.ingredient_name ILIKE %(exclude_ing{i})s
                )
            """)
        
        if ing_exclusion_conditions:
            where_clauses.append(f"({' AND '.join(ing_exclusion_conditions)})")
    
    # Process numerical filters
    if min_calories is not None:
        where_clauses.append("r.calories >= %(min_calories)s")
        params["min_calories"] = min_calories
    
    if max_calories is not None:
        where_clauses.append("r.calories <= %(max_calories)s")
        params["max_calories"] = max_calories
    
    if max_prep_time is not None:
        where_clauses.append("(r.prep_time IS NULL OR r.prep_time <= %(max_prep_time)s)")
        params["max_prep_time"] = max_prep_time
    
    if max_cook_time is not None:
        where_clauses.append("(r.cook_time IS NULL OR r.cook_time <= %(max_cook_time)s)")
        params["max_cook_time"] = max_cook_time
    
    if max_total_time is not None:
        where_clauses.append("(r.total_time IS NULL OR r.total_time <= %(max_total_time)s)")
        params["max_total_time"] = max_total_time
    
    # Build the ORDER BY clause
    if sort_by == "relevance" and query:
        # Relevance sorting only makes sense with a query
        order_clause = """
        ORDER BY 
            CASE
                WHEN r.recipe_title ILIKE %(exact_query)s THEN 3  -- Exact match in title
                WHEN r.recipe_title ILIKE %(start_query)s THEN 2  -- Starts with query
                WHEN r.recipe_title ILIKE %(query)s THEN 1        -- Contains query
                ELSE 0
            END DESC
        """
        params["exact_query"] = query
        params["start_query"] = f"{query}%"
    elif sort_by == "rating":
        # We'll need to fetch ratings from the user_interactions table
        join_clauses.append("""
        LEFT JOIN (
            SELECT recipe_id, AVG(rating) as avg_rating
            FROM user_interactions
            WHERE interaction_type = 'rating'
            GROUP BY recipe_id
        ) ratings ON r.recipe_id = ratings.recipe_id
        """)
        order_direction = "DESC" if sort_order.upper() == "DESC" else "ASC"
        order_clause = f"ORDER BY ratings.avg_rating {order_direction} NULLS LAST"
    elif sort_by == "time":
        # Sort by total time, using prep_time + cook_time as fallback
        order_direction = "ASC" if sort_order.upper() == "ASC" else "DESC"  # Default to ASC for time
        order_clause = f"""
        ORDER BY 
            COALESCE(r.total_time, r.prep_time + COALESCE(r.cook_time, 0)) {order_direction},
            r.recipe_id
        """
    elif sort_by == "calories":
        order_direction = "ASC" if sort_order.upper() == "ASC" else "DESC"
        order_clause = f"ORDER BY r.calories {order_direction} NULLS LAST, r.recipe_id"
    else:
        # Default ordering by recipe_id
        order_direction = "DESC" if sort_order.upper() == "DESC" else "ASC"
        order_clause = f"ORDER BY r.recipe_id {order_direction}"
    
    # Add pagination
    pagination_clause = "LIMIT %(limit)s OFFSET %(offset)s"
    params["limit"] = limit
    params["offset"] = offset
    
    # Count total results without pagination
    count_query = f"""
    SELECT COUNT(*) as count
    {from_clause}
    {' '.join(join_clauses)}
    WHERE {' AND '.join(where_clauses)}
    """
    
    # Main query with all clauses
    main_query = f"""
    {select_clause}
    {from_clause}
    {' '.join(join_clauses)}
    WHERE {' AND '.join(where_clauses)}
    {order_clause}
    {pagination_clause}
    """
    
    # Execute count query
    count_result = execute_query_single(count_query, params)
    total_count = count_result["count"] if count_result else 0
    
    # Execute main query
    results = execute_query(main_query, params)
    
    # Get ingredients and instructions for each recipe
    for recipe in results:
        recipe_id = recipe["recipe_id"]
        
        # Get ingredients
        ing_query = """
        SELECT 
            i.ingredient_id, i.ingredient_name, 
            ri.quantity, ri.unit, ri.ingredient_state
        FROM recipe_ingredients ri
        JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
        WHERE ri.recipe_id = %(recipe_id)s
        """
        recipe["ingredients"] = execute_query(ing_query, {"recipe_id": recipe_id}) or []
        
        # Get instructions
        instr_query = """
        SELECT instructions
        FROM recipe_instructions
        WHERE recipe_id = %(recipe_id)s
        """
        instructions = execute_query_single(instr_query, {"recipe_id": recipe_id})
        recipe["instructions"] = instructions["instructions"] if instructions else None
    
    # Calculate total pages
    total_pages = math.ceil(total_count / limit) if total_count > 0 else 0
    
    # Return a structured response
    return {
        "results": results,
        "pagination": {
            "offset": offset,
            "limit": limit,
            "total_items": total_count,
            "total_pages": total_pages
        },
        "criteria": {
            "query": query,
            "cuisines": cuisines,
            "dietary": dietary,
            "include_ingredients": include_ingredients,
            "exclude_ingredients": exclude_ingredients,
            "min_calories": min_calories,
            "max_calories": max_calories,
            "max_prep_time": max_prep_time, 
            "max_cook_time": max_cook_time,
            "max_total_time": max_total_time,
            "sort_by": sort_by,
            "sort_order": sort_order
        }
    }