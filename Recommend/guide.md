# MealFlow API Variable Guide

## URL Configuration
- `base_url`: 
  - Default: `http://localhost:9999/api/v1`
  - Replace with actual service host

## User and Interaction Variables
- `user_id`: 
  - Unique string identifier
  - Currently limited to single user in database

- `interaction_type`: 
  - `view`
  - `like`
  - `save`
  - `cook`
  - `rating`

## Recipe Filtering Variables
### Geographical Filters
- `cuisine` / `region`: 
  - `Middle Eastern`
  - `Egyptian`
  - `Lebanese`
  - `Turkish`
  - `Saudi Arabian`
  - `Iraqi`
  - `Palestinian`
  - `Israeli`

- `sub_region`: 
  - Same values as cuisine/region
  - `Rest Middle Eastern`

- `continent`:
  - `Africa`
  - Other continents not in sample data

### Dietary Filters
- `dietary_restriction`: 
  - `vegan` (494 recipes)
  - `pescetarian` (539 recipes)
  - `lacto_vegetarian` (299 recipes)

## Recommendation Variables
- `recommendation_type`: 
  - `hybrid` (default)
  - `content`
  - `collaborative`

- `similarity_method`: 
  - `content` (default)
  - `ingredient`

- `time_window`: 
  - `day`
  - `week`
  - `month`

## Pagination and Limits
- `limit`: 
  - Default: `10`
  - Maximum: `100`
  - Any positive integer

- `page`: 
  - Default: `1`
  - Any positive integer

## Numeric Recipe Variables
### Time
- `cook_time`: 
  - Range: 5-120 minutes
  - Most recipes: < 15 minutes

- `prep_time`: 
  - Range: 10-30 minutes
  - Average: ~15 minutes

### Nutrition
- `calories`: 
  - Range: 45-934
  - Categories:
    - Low (< 300): 2,021 recipes
    - Medium (300-600): 1,227 recipes
    - High (601-900): 370 recipes
    - Very High (> 900): 286 recipes

- `rating`: 
  - Decimal between 1-5
  - Example: `4.5`

## Recipe Identification
- `recipe_id`: 
  - Numeric IDs (sample range: 2610-2619)
  - Total recipes: 3,905

## Naming
- `recipe_title`: Free-text recipe name
- Example: `"Egyptian Lentil Soup"`

## Notes
- Values derived from database exploration
- Actual values may expand with future data imports

# MealFlow API Variable Guide

## URL Configuration
- `base_url`: 
  - Default: `http://localhost:9999/api/v1`
  - Replace with actual service host

## User and Interaction Variables
- `user_id`: 
  - Unique string identifier
  - Currently limited to single user in database

- `interaction_type`: 
  - `view`
  - `like`
  - `save`
  - `cook`
  - `rating`

## Recipe Filtering Variables
### Geographical Filters
- `cuisine` / `region`: 
  - `Middle Eastern`
  - `Egyptian`
  - `Lebanese`
  - `Turkish`
  - `Saudi Arabian`
  - `Iraqi`
  - `Palestinian`
  - `Israeli`

- `sub_region`: 
  - Same values as cuisine/region
  - `Rest Middle Eastern`

- `continent`:
  - `Africa`
  - Other continents not in sample data

### Dietary Filters
- `dietary_restriction`: 
  - `vegan` (494 recipes)
  - `pescetarian` (539 recipes)
  - `lacto_vegetarian` (299 recipes)

### Time Filters
- `max_time`:
  - For quick recipes endpoint
  - Any positive integer (minutes)
  - Default: 30

## Recommendation Variables
- `recommendation_type`: 
  - `hybrid` (default)
  - `content`
  - `collaborative`

- `similarity_method`: 
  - `content` (default)
  - `ingredient`

- `time_window`: 
  - `day`
  - `week`
  - `month`

## Pagination and Limits
- `limit`: 
  - Default: `10`
  - Maximum: `100`
  - Any positive integer

- `page`: 
  - Default: `1`
  - Any positive integer

## Numeric Recipe Variables
### Time
- `cook_time`: 
  - Range: 5-120 minutes
  - Most recipes: < 15 minutes

- `prep_time`: 
  - Range: 10-30 minutes
  - Average: ~15 minutes

### Nutrition
- `calories`: 
  - Range: 45-934
  - Categories:
    - Low (< 300): 2,021 recipes
    - Medium (300-600): 1,227 recipes
    - High (601-900): 370 recipes
    - Very High (> 900): 286 recipes

- `rating`: 
  - Decimal between 1-5
  - Example: `4.5`

## Recipe Identification
- `recipe_id`: 
  - Numeric IDs (sample range: 2610-2619)
  - Total recipes: 3,905

## Naming
- `recipe_title`: Free-text recipe name
- Example: `"Egyptian Lentil Soup"`

## API Endpoints
### Quick Recommendations
```
GET /api/v1/recommend/quick
```
Get recipes that can be prepared quickly.

**Parameters:**
- `max_time` (optional): Maximum preparation time in minutes (default: 30)
- `limit` (optional): Maximum number of recipes (default: 10)
- `cuisine` (optional): Filter by cuisine/region
- `dietary_restriction` (optional): Filter by dietary restriction

## Notes
- Values derived from database exploration
- Actual values may expand with future data imports