"""
SQL query constants for user interaction operations.
Contains all the queries used by the InteractionRepository.
"""

# Query to record a user interaction
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

# Base query for getting trending content
GET_TRENDING_CONTENT_BASE = """
SELECT ri.meal_id, ri.content_type,
       CASE WHEN ri.content_type = 'post' THEN p.title
            WHEN ri.content_type = 'community' THEN c.name
       END as title,
       COUNT(*) as popularity
FROM recommendation_interactions ri
LEFT JOIN "Post" p ON ri.meal_id = p.id AND ri.content_type = 'post'
LEFT JOIN "Community" c ON ri.meal_id = c.id AND ri.content_type = 'community'
WHERE ri.created_at > {time_clause}
{type_filter}
GROUP BY ri.meal_id, ri.content_type, p.title, c.name
ORDER BY popularity DESC
LIMIT :limit
"""

# Query to find users with similar interaction patterns
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

# Base query for getting content from similar users
GET_CONTENT_FROM_SIMILAR_USERS_BASE = """
SELECT DISTINCT ri.meal_id, ri.content_type, COUNT(*) as interaction_count,
       CASE WHEN ri.content_type = 'post' THEN p.title
            WHEN ri.content_type = 'community' THEN c.name
       END as title
FROM recommendation_interactions ri
LEFT JOIN "Post" p ON ri.meal_id = p.id AND ri.content_type = 'post'
LEFT JOIN "Community" c ON ri.meal_id = c.id AND ri.content_type = 'community'
WHERE ri.user_id IN ({user_placeholders})
AND ri.meal_id NOT IN (
    SELECT meal_id 
    FROM recommendation_interactions 
    WHERE user_id = :user_id
)
{type_filter}
GROUP BY ri.meal_id, ri.content_type, p.title, c.name
ORDER BY interaction_count DESC
LIMIT :limit
"""

# Query to get communities in a specific category
GET_CATEGORY_COMMUNITIES = """
SELECT c.id, c.name
FROM "Community" c
JOIN "CommunityCategory" cc ON c.id = cc."communityId"
WHERE cc."categoryId" = :category_id
LIMIT :limit
"""