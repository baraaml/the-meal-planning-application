"""
SQL query constants for service layer operations.
Contains queries used by service classes.
"""

# Query for finding similar items based on co-occurrence patterns
FIND_SIMILAR_ITEMS = """
WITH users_who_interacted AS (
    SELECT DISTINCT user_id
    FROM recommendation_interactions
    WHERE meal_id = :meal_id
)
SELECT ri2.meal_id, ri2.content_type,
       CASE WHEN ri2.content_type = 'post' THEN p.title
            WHEN ri2.content_type = 'community' THEN c.name
       END as title,
       COUNT(DISTINCT ri2.user_id) as co_occurrence_count
FROM recommendation_interactions ri2
JOIN users_who_interacted uwi ON ri2.user_id = uwi.user_id
LEFT JOIN "Post" p ON ri2.meal_id = p.id AND ri2.content_type = 'post'
LEFT JOIN "Community" c ON ri2.meal_id = c.id AND ri2.content_type = 'community'
WHERE ri2.meal_id != :meal_id
{type_filter}
GROUP BY ri2.meal_id, ri2.content_type, p.title, c.name
ORDER BY co_occurrence_count DESC
LIMIT :limit
"""