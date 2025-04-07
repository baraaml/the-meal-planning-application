"""
SQL query constants for user meal interaction operations.
Contains all the queries used by the InteractionRepository.
"""

# Query to record a user interaction with a meal
RECORD_INTERACTION = """
INSERT INTO recommendation_interactions
(user_id, meal_id, content_type, interaction_type)
VALUES (:user_id, :meal_id, :content_type, :interaction_type)
"""

# Query to get recent interactions for a user
GET_USER_RECENT_INTERACTIONS_BASE = """
SELECT meal_id, content_type, interaction_type, created_at
FROM recommendation_interactions
WHERE user_id = :user_id
{type_filter}
ORDER BY created_at DESC
LIMIT :limit
"""

# Base query for getting trending meals
GET_TRENDING_MEALS_BASE = """
SELECT ri.meal_id, ri.content_type,
       CASE WHEN ri.content_type = 'meal' THEN m.title
            WHEN ri.content_type = 'recipe' THEN r.name
       END as title,
       COUNT(*) as popularity
FROM recommendation_interactions ri
LEFT JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
LEFT JOIN "Recipe" r ON ri.meal_id = r.id AND ri.content_type = 'recipe'
WHERE ri.created_at > {time_clause}
{type_filter}
GROUP BY ri.meal_id, ri.content_type, m.title, r.name
ORDER BY popularity DESC
LIMIT :limit
"""

# Query to find users with similar meal preferences
FIND_SIMILAR_USERS = """
SELECT DISTINCT ri2.user_id
FROM recommendation_interactions ri1
JOIN recommendation_interactions ri2 ON ri1.meal_id = ri2.meal_id 
                                  AND ri1.content_type = ri2.content_type
WHERE ri1.user_id = :user_id
AND ri2.user_id != :user_id
GROUP BY ri2.user_id
HAVING COUNT(DISTINCT ri1.meal_id) > :min_common_items
LIMIT :limit
"""

# Base query for getting meals from similar users
GET_MEALS_FROM_SIMILAR_USERS_BASE = """
SELECT DISTINCT ri.meal_id, ri.content_type, COUNT(*) as interaction_count,
       CASE WHEN ri.content_type = 'meal' THEN m.title
            WHEN ri.content_type = 'recipe' THEN r.name
       END as title
FROM recommendation_interactions ri
LEFT JOIN "Meal" m ON ri.meal_id = m.id AND ri.content_type = 'meal'
LEFT JOIN "Recipe" r ON ri.meal_id = r.id AND ri.content_type = 'recipe'
WHERE ri.user_id IN ({user_placeholders})
AND ri.meal_id NOT IN (
    SELECT meal_id 
    FROM recommendation_interactions 
    WHERE user_id = :user_id
)
{type_filter}
GROUP BY ri.meal_id, ri.content_type, m.title, r.name
ORDER BY interaction_count DESC
LIMIT :limit
"""

# Query to get meals by cuisine
GET_CUISINE_MEALS = """
SELECT m.id, m.title
FROM "Meal" m
WHERE m.cuisine_id = :cuisine_id
LIMIT :limit
"""

# Query to get meals by dietary preference
GET_DIETARY_PREFERENCE_MEALS = """
SELECT m.id, m.title
FROM "Meal" m
JOIN "MealDietaryRestriction" mdr ON m.id = mdr.meal_id
WHERE mdr.dietary_restriction_id = :dietary_restriction_id
LIMIT :limit
"""

# Query to get user's dietary preferences
GET_USER_DIETARY_PREFERENCES = """
SELECT dietary_restriction_id
FROM "UserDietaryPreference"
WHERE user_id = :user_id
"""

# Query to get similar meals based on ingredients
GET_SIMILAR_MEALS_BY_INGREDIENTS = """
SELECT m1.id, m1.title, COUNT(mi1.ingredient_id) as ingredient_match_count
FROM "Meal" m1
JOIN "MealIngredient" mi1 ON m1.id = mi1.meal_id
JOIN "MealIngredient" mi2 ON mi1.ingredient_id = mi2.ingredient_id
WHERE mi2.meal_id = :meal_id
AND m1.id != :meal_id
GROUP BY m1.id, m1.title
ORDER BY ingredient_match_count DESC
LIMIT :limit
"""