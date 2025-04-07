"""
SQL query constants for meal recommendation service operations.
Contains queries used by service classes.
"""

# Query for finding similar meals based on co-occurrence patterns
FIND_SIMILAR_ITEMS = """
WITH users_who_interacted AS (
    SELECT DISTINCT user_id
    FROM recommendation_interactions
    WHERE meal_id = :meal_id
)
SELECT ri2.meal_id, ri2.content_type,
       CASE WHEN ri2.content_type = 'meal' THEN m.title
            WHEN ri2.content_type = 'recipe' THEN r.name
       END as title,
       COUNT(DISTINCT ri2.user_id) as co_occurrence_count
FROM recommendation_interactions ri2
JOIN users_who_interacted uwi ON ri2.user_id = uwi.user_id
LEFT JOIN "Meal" m ON ri2.meal_id = m.id AND ri2.content_type = 'meal'
LEFT JOIN "Recipe" r ON ri2.meal_id = r.id AND ri2.content_type = 'recipe'
WHERE ri2.meal_id != :meal_id
{type_filter}
GROUP BY ri2.meal_id, ri2.content_type, m.title, r.name
ORDER BY co_occurrence_count DESC
LIMIT :limit
"""

# Query for finding meals filtered by user dietary preferences
FIND_MEALS_WITH_DIETARY_PREFERENCES = """
SELECT m.id, m.title, m.description
FROM "Meal" m
JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
JOIN "UserDietaryPreference" udp ON mdr.dietary_restriction_id = udp.dietary_restriction_id
WHERE udp.user_id = :user_id
GROUP BY m.id, m.title, m.description
LIMIT :limit
"""

# Query for finding meals with similar ingredients
FIND_MEALS_BY_INGREDIENT_SIMILARITY = """
WITH meal_ingredients AS (
    SELECT mi.ingredient_id
    FROM "MealIngredient" mi
    WHERE mi.meal_id = :meal_id
)
SELECT m.id, m.title, m.description,
       COUNT(mi.ingredient_id) AS matching_ingredients,
       (COUNT(mi.ingredient_id) * 100.0 / ingredient_count.total) AS match_percentage
FROM "Meal" m
JOIN "MealIngredient" mi ON m.id = mi.meal_id
JOIN meal_ingredients source_ingredients ON mi.ingredient_id = source_ingredients.ingredient_id
JOIN (
    SELECT meal_id, COUNT(ingredient_id) AS total
    FROM "MealIngredient"
    GROUP BY meal_id
) ingredient_count ON m.id = ingredient_count.meal_id
WHERE m.id != :meal_id
GROUP BY m.id, m.title, m.description, ingredient_count.total
ORDER BY matching_ingredients DESC, match_percentage DESC
LIMIT :limit
"""

# Query for getting popular meals by cuisine
GET_POPULAR_MEALS_BY_CUISINE = """
SELECT m.id, m.title, m.description, COUNT(ri.id) AS popularity
FROM "Meal" m
LEFT JOIN recommendation_interactions ri ON m.id = ri.meal_id AND ri.content_type = 'meal'
WHERE m.cuisine_id = :cuisine_id
GROUP BY m.id, m.title, m.description
ORDER BY popularity DESC
LIMIT :limit
"""

# Query for getting meal recommendations based on time of day
GET_MEALS_BY_TIME_OF_DAY = """
SELECT m.id, m.title, m.description
FROM "Meal" m
WHERE m.meal_type = :meal_type  -- 'breakfast', 'lunch', 'dinner', 'snack'
ORDER BY 
    CASE WHEN m.cuisine_id = :preferred_cuisine_id THEN 0 ELSE 1 END,
    RANDOM()
LIMIT :limit
"""

# Query for getting recommended meal pairings
GET_MEAL_PAIRINGS = """
WITH meal_pairs AS (
    SELECT ri1.meal_id AS meal1_id, ri2.meal_id AS meal2_id, COUNT(*) AS pair_count
    FROM recommendation_interactions ri1
    JOIN recommendation_interactions ri2 
        ON ri1.user_id = ri2.user_id 
        AND ri1.meal_id != ri2.meal_id
        AND ri1.content_type = 'meal'
        AND ri2.content_type = 'meal'
    WHERE ri1.meal_id = :meal_id
    GROUP BY ri1.meal_id, ri2.meal_id
    ORDER BY pair_count DESC
)
SELECT mp.meal2_id, m.title, m.description, mp.pair_count
FROM meal_pairs mp
JOIN "Meal" m ON mp.meal2_id = m.id
LIMIT :limit
"""