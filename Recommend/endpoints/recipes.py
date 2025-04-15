"""
API endpoints for recipe management.
"""
from fastapi import APIRouter, HTTPException, Query, status
from typing import Dict, Optional, List

from models.models import RecipeCreate, RecipeUpdate, RecipeBase, RecipeDetail
from models.queries_recipe import (
    get_recipe, get_recipes, create_recipe, update_recipe, delete_recipe
)
from embedding.embeddings import EmbeddingGenerator
from config.db import execute_query  # Add this import

router = APIRouter(prefix="/api/v1", tags=["recipes"])

@router.get("/recipes/{recipe_id}", response_model=RecipeDetail)
def get_recipe_endpoint(recipe_id: int):
    """Get a recipe by ID."""
    recipe = get_recipe(recipe_id)
    if not recipe:
        raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
    return recipe


@router.get("/recipes", response_model=List[RecipeBase])
def get_recipes_endpoint(
    page: int = Query(1, ge=1),
    limit: int = Query(20, ge=1, le=100),
    region: Optional[str] = None,
    sub_region: Optional[str] = None,
    min_calories: Optional[int] = None,
    max_calories: Optional[int] = None
):
    """Get a list of recipes with optional filtering."""
    # Calculate offset based on page and limit
    offset = (page - 1) * limit
    
    # Prepare filters
    filters = {}
    if region:
        filters["region"] = region
    if sub_region:
        filters["sub_region"] = sub_region
        
    # Add calorie filters to the query parameters
    calories_filter = {}
    if min_calories is not None:
        calories_filter["min_calories"] = min_calories
    if max_calories is not None:
        calories_filter["max_calories"] = max_calories
    
    recipes = get_recipes(limit=limit, offset=offset, filters=filters, calories_filter=calories_filter)
    return recipes

@router.post("/recipes", response_model=RecipeDetail, status_code=status.HTTP_201_CREATED)
def create_recipe_endpoint(recipe: RecipeCreate):
    """Create a new recipe."""
    recipe_id = create_recipe(recipe.dict())
    if not recipe_id:
        raise HTTPException(status_code=500, detail="Failed to create recipe")
    
    # Generate embedding in background (could be moved to a background task)
    generator = EmbeddingGenerator()
    generator.update_recipe_embedding(recipe_id)
    
    return get_recipe(recipe_id)

@router.put("/recipes/{recipe_id}", response_model=RecipeDetail)
def update_recipe_endpoint(recipe_id: int, recipe: RecipeUpdate):
    """Update a recipe."""
    # Check if recipe exists
    existing_recipe = get_recipe(recipe_id)
    if not existing_recipe:
        raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
    
    # Update recipe
    success = update_recipe(recipe_id, recipe.dict(exclude_unset=True))
    if not success:
        raise HTTPException(status_code=500, detail="Failed to update recipe")
    
    # Update embedding if recipe content changed
    if recipe.recipe_title:
        generator = EmbeddingGenerator()
        generator.update_recipe_embedding(recipe_id)
    
    return get_recipe(recipe_id)

@router.delete("/recipes/{recipe_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_recipe_endpoint(recipe_id: int):
    """Delete a recipe."""
    # Check if recipe exists
    existing_recipe = get_recipe(recipe_id)
    if not existing_recipe:
        raise HTTPException(status_code=404, detail=f"Recipe with ID {recipe_id} not found")
    
    # Delete recipe
    success = delete_recipe(recipe_id)
    if not success:
        raise HTTPException(status_code=500, detail="Failed to delete recipe")
    
    return None


@router.get("/recipes/filter/calories", response_model=List[RecipeBase])
def filter_recipes_by_calories(
    min: float = Query(0, ge=0),
    max: float = Query(1000, ge=0),
    page: int = Query(1, ge=1),
    limit: int = Query(20, ge=1, le=100)
):
    """Filter recipes by calorie range."""
    # Calculate offset based on page and limit
    offset = (page - 1) * limit
    
    query = """
    SELECT 
        recipe_id, recipe_title, region, sub_region
    FROM recipes
    WHERE calories >= %(min)s AND calories <= %(max)s
    ORDER BY recipe_id
    LIMIT %(limit)s OFFSET %(offset)s
    """
    
    params = {
        "min": min,
        "max": max,
        "limit": limit,
        "offset": offset
    }
    
    return execute_query(query, params)

# Add this to your endpoints/recipes.py file

@router.get("/recipes/search", response_model=Dict[str, Any])
def search_recipes(
    query: Optional[str] = None,
    cuisines: Optional[str] = None,
    dietary: Optional[str] = None,
    include_ingredients: Optional[str] = None,
    exclude_ingredients: Optional[str] = None,
    min_calories: Optional[float] = None,
    max_calories: Optional[float] = None,
    max_prep_time: Optional[int] = None,
    max_cook_time: Optional[int] = None,
    max_total_time: Optional[int] = None,
    sort_by: str = "relevance",
    sort_order: str = "desc",
    page: int = Query(1, ge=1),
    limit: int = Query(20, ge=1, le=100)
):
    """
    Advanced search for recipes with multiple criteria.
    
    Parameters:
    - query: Text search query for recipe titles and descriptions
    - cuisines: Comma-separated list of cuisines/regions
    - dietary: Comma-separated list of dietary restrictions
    - include_ingredients: Comma-separated list of ingredients that must be included
    - exclude_ingredients: Comma-separated list of ingredients to exclude
    - min_calories: Minimum calories
    - max_calories: Maximum calories
    - max_prep_time: Maximum preparation time in minutes
    - max_cook_time: Maximum cooking time in minutes
    - max_total_time: Maximum total time in minutes
    - sort_by: Field to sort by (relevance, rating, time, calories)
    - sort_order: Sort order (asc, desc)
    - page: Page number
    - limit: Items per page
    """
    start_time = time.time()
    
    # Calculate offset based on page and limit
    offset = (page - 1) * limit
    
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
        cuisine_list = cuisines.split(',')
        cuisine_placeholders = [f"%(cuisine{i})s" for i in range(len(cuisine_list))]
        where_clauses.append(f"(r.region IN ({','.join(cuisine_placeholders)}) OR r.sub_region IN ({','.join(cuisine_placeholders)}))")
        for i, cuisine in enumerate(cuisine_list):
            params[f"cuisine{i}"] = cuisine.strip()
    
    # Process dietary restrictions
    if dietary:
        dietary_list = dietary.split(',')
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
        ingredient_list = include_ingredients.split(',')
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
        ingredient_list = exclude_ingredients.split(',')
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
    
    # Calculate execution time
    execution_time = (time.time() - start_time) * 1000
    
    # Return a structured response
    return {
        "results": results,
        "pagination": {
            "page": page,
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
        },
        "execution_time_ms": round(execution_time, 2)
    }