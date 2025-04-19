# MealFlow API Guide

This comprehensive guide documents all endpoints available in the MealFlow ecosystem, including both the Node.js Main Service and Python Recommendation Service.

## Table of Contents

- [Base URLs](#base-urls)
- [Authentication](#authentication)
- [Node.js Main Service](#nodejs-main-service)
  - [Authentication](#authentication-endpoints)
  - [User Management](#user-management-endpoints)
  - [Community](#community-endpoints)
  - [Meals (Node.js)](#meals-nodejs-endpoints)
  - [Upload](#upload-endpoints)
  - [System](#system-endpoints)
- [Python Recommendation Service](#python-recommendation-service)
  - [Recommendations](#recommendations-endpoints)
  - [Recipe Management](#recipe-management-endpoints)
  - [System Management](#system-management-endpoints)
- [Parameter Reference](#parameter-reference)

## Base URLs

- **Node.js Main Service**: `http://localhost:3000`
- **Python Recommendation Service**: `http://localhost:9999`

## Authentication

Most endpoints in the Node.js Main Service require authentication. Authentication is handled via JWT tokens:

- **Access Token**: Short-lived token (6 hours) used for API requests
- **Refresh Token**: Long-lived token (1 year) used to obtain new access tokens

Include the access token in the Authorization header:

```
Authorization: Bearer <access_token>
```

## Node.js Main Service

### Authentication Endpoints

#### Register User
- **Endpoint**: `POST /api/v1/users/register`
- **Description**: Register a new user
- **Body Parameters**:
  - `username`: String (3-30 characters, alphanumeric with underscores, must start with a letter)
  - `email`: String (valid email format)
  - `password`: String (8-64 characters, must include uppercase, lowercase, number, and special character)
- **Response**: Success message with instructions to verify email

#### Verify Email
- **Endpoint**: `POST /api/v1/users/verify-email`
- **Description**: Verify user email with OTP code
- **Body Parameters**:
  - `email`: String (email address)
  - `otp`: String (6-digit verification code)
- **Response**: Access token, refresh token, and user information

#### Resend Verification
- **Endpoint**: `POST /api/v1/users/resend-verification`
- **Description**: Resend verification OTP to user's email
- **Body Parameters**:
  - `email`: String (email address)
- **Response**: Success message

#### Login
- **Endpoint**: `POST /api/v1/users/login`
- **Description**: Login with email and password
- **Body Parameters**:
  - `email`: String (email address)
  - `password`: String (password)
- **Response**: Access token, refresh token, and user information

#### Logout
- **Endpoint**: `POST /api/v1/users/logout`
- **Description**: Logout and invalidate refresh token
- **Body Parameters**:
  - `refreshToken`: String (refresh token)
- **Response**: Success message

#### Quick Login
- **Endpoint**: `POST /api/v1/users/quick-login`
- **Description**: Quick login using refresh token
- **Body Parameters**:
  - `refreshToken`: String (refresh token)
- **Response**: Access token, refresh token, and user information

#### Refresh Token
- **Endpoint**: `GET /api/v1/users/refresh-token`
- **Description**: Get new access token using refresh token
- **Body Parameters**:
  - `refreshToken`: String (refresh token)
- **Response**: New access token

#### Forgot Password
- **Endpoint**: `POST /api/v1/users/forgot-password`
- **Description**: Send password reset link to email
- **Body Parameters**:
  - `email`: String (email address)
- **Response**: Success message and reset token

#### Reset Password
- **Endpoint**: `POST /api/v1/users/reset-password`
- **Description**: Reset password with token
- **Body Parameters**:
  - `token`: String (reset token)
  - `password`: String (new password, must meet password criteria)
- **Response**: Success message

### User Management Endpoints

#### Get User Profile
- **Endpoint**: `GET /api/v1/users/me`
- **Description**: Get current user profile
- **Authentication**: Required
- **Response**: User profile information

#### Update User Profile
- **Endpoint**: `PATCH /api/v1/users/me`
- **Description**: Update current user profile
- **Authentication**: Required
- **Body Parameters**: User profile fields to update
- **Response**: Updated user profile

#### Delete User Account
- **Endpoint**: `DELETE /api/v1/users/me`
- **Description**: Delete current user account
- **Authentication**: Required
- **Response**: Success message

### Community Endpoints

#### Create Community
- **Endpoint**: `POST /api/v1/community`
- **Description**: Create a new community
- **Authentication**: Required
- **Body Parameters**:
  - `name`: String (3-50 characters)
  - `description`: String (optional)
  - `privacy`: String (enum: "PUBLIC", "PRIVATE", "RESTRICTED")
  - `recipeCreationPermission`: String (enum: "ANY_MEMBER", "ADMIN_ONLY")
  - `categories`: Array of strings or JSON string (optional)
  - `image`: File (image file, optional)
- **Response**: Created community

#### Get All Communities
- **Endpoint**: `GET /api/v1/community`
- **Description**: Get all communities
- **Authentication**: Required
- **Response**: List of communities

#### Get Community by ID
- **Endpoint**: `GET /api/v1/community/:id`
- **Description**: Get community details by ID
- **Authentication**: Required
- **Path Parameters**:
  - `id`: String (community ID)
- **Response**: Community details

#### Join Community
- **Endpoint**: `POST /api/v1/community/:id/join`
- **Description**: Join a community
- **Authentication**: Required
- **Path Parameters**:
  - `id`: String (community ID)
- **Response**: Success message

#### Leave Community
- **Endpoint**: `DELETE /api/v1/community/:id/leave`
- **Description**: Leave a community
- **Authentication**: Required
- **Path Parameters**:
  - `id`: String (community ID)
- **Response**: Success message

#### Get Community Members
- **Endpoint**: `GET /api/v1/community/:id/members`
- **Description**: Get all members of a community
- **Authentication**: Required
- **Path Parameters**:
  - `id`: String (community ID)
- **Response**: List of community members

### Meals (Node.js) Endpoints

#### Get Recommended Meals
- **Endpoint**: `GET /api/v1/meal/recommended`
- **Description**: Get personalized meal recommendations
- **Query Parameters**:
  - `limit`: Number (default: 10, max: 100)
- **Response**: List of recommended meals

#### Get Trending Meals
- **Endpoint**: `GET /api/v1/meal/trending`
- **Description**: Get trending meals
- **Query Parameters**:
  - `limit`: Number (default: 10, max: 100)
  - `time_window`: String (enum: "day", "week", "month")
- **Response**: List of trending meals

#### Get Quick Meals
- **Endpoint**: `GET /api/v1/meal/quick`
- **Description**: Get meals that can be prepared quickly
- **Query Parameters**:
  - `max_time`: Number (minutes, default: 30)
  - `limit`: Number (default: 10, max: 100)
- **Response**: List of quick meals

#### Get Cuisine Recommendations
- **Endpoint**: `GET /api/v1/meal/cuisine/:cuisine_id`
- **Description**: Get recommendations for a specific cuisine
- **Path Parameters**:
  - `cuisine_id`: String (cuisine name)
- **Query Parameters**:
  - `limit`: Number (default: 10, max: 100)
- **Response**: List of cuisine-specific meals

#### Get Dietary Recommendations
- **Endpoint**: `GET /api/v1/meal/dietary/:dietary_restriction`
- **Description**: Get recommendations for a specific dietary restriction
- **Path Parameters**:
  - `dietary_restriction`: String (enum: "vegan", "pescetarian", "lacto_vegetarian")
- **Query Parameters**:
  - `limit`: Number (default: 10, max: 100)
- **Response**: List of dietary-specific meals

#### Get Meal By ID
- **Endpoint**: `GET /api/v1/meal/:id`
- **Description**: Get details of a specific meal
- **Path Parameters**:
  - `id`: String (meal ID)
- **Response**: Meal details

#### Get Similar Meals
- **Endpoint**: `GET /api/v1/meal/similar/:id`
- **Description**: Get meals similar to a specific meal
- **Path Parameters**:
  - `id`: String (meal ID)
- **Query Parameters**:
  - `limit`: Number (default: 5, max: 100)
- **Response**: List of similar meals

#### Get Filtered Meals
- **Endpoint**: `GET /api/v1/meal/filter`
- **Description**: Get meals filtered by various criteria
- **Query Parameters**:
  - `page`: Number (default: 1)
  - `limit`: Number (default: 20, max: 100)
  - `region`: String (cuisine/region name)
  - `min_calories`: Number (optional)
  - `max_calories`: Number (optional)
- **Response**: List of filtered meals

#### Get Meals By Calorie Range
- **Endpoint**: `GET /api/v1/meal/filter/calories`
- **Description**: Get meals within a specific calorie range
- **Query Parameters**:
  - `min`: Number (default: 0)
  - `max`: Number (default: 1000)
  - `page`: Number (default: 1)
  - `limit`: Number (default: 20, max: 100)
- **Response**: List of meals within calorie range

#### Advanced Search
- **Endpoint**: `GET /api/v1/meal/search/advanced`
- **Description**: Advanced search with multiple criteria
- **Query Parameters**:
  - `query`: String (search term)
  - `cuisines`: String (comma-separated list of cuisines)
  - `dietary`: String (comma-separated list of dietary restrictions)
  - `include_ingredients`: String (comma-separated list of ingredients to include)
  - `exclude_ingredients`: String (comma-separated list of ingredients to exclude)
  - `max_cook_time`: Number (maximum cooking time in minutes)
  - `sort_by`: String (enum: "relevance", "rating", "time", "calories")
  - `page`: Number (default: 1)
  - `limit`: Number (default: 20, max: 100)
- **Response**: List of search results

#### Record Meal Interaction
- **Endpoint**: `POST /api/v1/meal/interaction`
- **Description**: Record user interaction with a meal (like, save, cook, etc.)
- **Authentication**: Required
- **Body Parameters**:
  - `meal_id`: String (meal ID)
  - `interaction_type`: String (enum: "view", "like", "save", "cook", "rating")
  - `rating`: Number (optional, 1-5 for rating interactions)
- **Response**: Success message

### Upload Endpoints

#### Upload Image
- **Endpoint**: `POST /api/v1/upload`
- **Description**: Upload an image to the server
- **Authentication**: Required
- **Body Parameters**:
  - `image`: File (image file)
- **Response**: Upload details

### System Endpoints

#### Health Check
- **Endpoint**: `GET /api/v1/system/health`
- **Description**: Check system health status
- **Response**: Health status and configuration details

## Python Recommendation Service

### Recommendations Endpoints

#### Get User Recommendations
- **Endpoint**: `GET /api/v1/recommend/user/:user_id`
- **Description**: Get personalized recommendations for a user
- **Path Parameters**:
  - `user_id`: String (user ID, use "default_user" for non-authenticated)
- **Query Parameters**:
  - `limit`: Number (default: 10, max: 100)
  - `recommendation_type`: String (enum: "hybrid", "content", "collaborative")
  - `cuisine`: String (optional)
  - `dietary_restriction`: String (optional)
- **Response**: List of recommended recipes

#### Get Similar Recipes
- **Endpoint**: `GET /api/v1/recommend/similar/:recipe_id`
- **Description**: Get recipes similar to a specific recipe
- **Path Parameters**:
  - `recipe_id`: Number (recipe ID)
- **Query Parameters**:
  - `limit`: Number (default: 5, max: 100)
  - `similarity_method`: String (enum: "content", "ingredient")
- **Response**: List of similar recipes

#### Get Trending Recipes
- **Endpoint**: `GET /api/v1/trending`
- **Description**: Get trending recipes based on recent interactions
- **Query Parameters**:
  - `time_window`: String (enum: "day", "week", "month")
  - `limit`: Number (default: 10, max: 100)
  - `cuisine`: String (optional)
  - `dietary_restriction`: String (optional)
- **Response**: List of trending recipes

#### Get Cuisine Recommendations
- **Endpoint**: `GET /api/v1/recommend/cuisine/:cuisine_id`
- **Description**: Get recipe recommendations for a specific cuisine
- **Path Parameters**:
  - `cuisine_id`: String (cuisine name)
- **Query Parameters**:
  - `limit`: Number (default: 10, max: 100)
- **Response**: List of cuisine-specific recipes

#### Get Dietary Recommendations
- **Endpoint**: `GET /api/v1/recommend/dietary/:dietary_restriction`
- **Description**: Get recipe recommendations based on dietary restrictions
- **Path Parameters**:
  - `dietary_restriction`: String (enum: "vegan", "pescetarian", "lacto_vegetarian")
- **Query Parameters**:
  - `limit`: Number (default: 10, max: 100)
- **Response**: List of dietary-specific recipes

#### Get Quick Recipes
- **Endpoint**: `GET /api/v1/recommend/quick`
- **Description**: Get recipes that can be prepared quickly
- **Query Parameters**:
  - `max_time`: Number (minutes, default: 30)
  - `limit`: Number (default: 10, max: 100)
  - `cuisine`: String (optional)
  - `dietary_restriction`: String (optional)
- **Response**: List of quick recipes

#### Record Interaction
- **Endpoint**: `POST /api/v1/interactions`
- **Description**: Record a user interaction with a recipe
- **Body Parameters**:
  - `user_id`: String (user ID)
  - `meal_id`: String (recipe ID)
  - `interaction_type`: String (enum: "view", "like", "save", "cook", "rating")
  - `rating`: Number (optional, 1-5 for rating interactions)
- **Response**: Success message

### Recipe Management Endpoints

#### Get Recipe By ID
- **Endpoint**: `GET /api/v1/recipes/:recipe_id`
- **Description**: Get details of a specific recipe
- **Path Parameters**:
  - `recipe_id`: Number (recipe ID)
- **Response**: Recipe details

#### Get Recipes
- **Endpoint**: `GET /api/v1/recipes`
- **Description**: Get recipes with optional filtering
- **Query Parameters**:
  - `page`: Number (default: 1)
  - `limit`: Number (default: 20, max: 100)
  - `region`: String (cuisine/region name)
  - `sub_region`: String (sub-region name)
  - `min_calories`: Number (optional)
  - `max_calories`: Number (optional)
- **Response**: List of recipes

#### Create Recipe
- **Endpoint**: `POST /api/v1/recipes`
- **Description**: Create a new recipe
- **Body Parameters**:
  - `recipe_title`: String (required)
  - `region`: String (optional)
  - `sub_region`: String (optional)
  - `continent`: String (optional)
  - `source`: String (optional)
  - `image_url`: String (optional)
  - `cook_time`: Number (minutes, optional)
  - `prep_time`: Number (minutes, optional)
  - `total_time`: Number (minutes, optional)
  - `servings`: Number (optional)
  - `calories`: Number (optional)
- **Response**: Created recipe

#### Update Recipe
- **Endpoint**: `PUT /api/v1/recipes/:recipe_id`
- **Description**: Update an existing recipe
- **Path Parameters**:
  - `recipe_id`: Number (recipe ID)
- **Body Parameters**: Recipe fields to update
- **Response**: Updated recipe

#### Delete Recipe
- **Endpoint**: `DELETE /api/v1/recipes/:recipe_id`
- **Description**: Delete a recipe
- **Path Parameters**:
  - `recipe_id`: Number (recipe ID)
- **Response**: Success message

#### Filter Recipes By Calories
- **Endpoint**: `GET /api/v1/recipes/filter/calories`
- **Description**: Filter recipes by calorie range
- **Query Parameters**:
  - `min`: Number (default: 0)
  - `max`: Number (default: 1000)
  - `page`: Number (default: 1)
  - `limit`: Number (default: 20, max: 100)
- **Response**: List of recipes within calorie range

#### Advanced Recipe Search
- **Endpoint**: `GET /api/v1/recipes/search`
- **Description**: Advanced search for recipes with multiple criteria
- **Query Parameters**:
  - `query`: String (search term)
  - `cuisines`: String (comma-separated list of cuisines)
  - `dietary`: String (comma-separated list of dietary restrictions)
  - `include_ingredients`: String (comma-separated list of ingredients to include)
  - `exclude_ingredients`: String (comma-separated list of ingredients to exclude)
  - `max_prep_time`: Number (maximum preparation time in minutes)
  - `max_cook_time`: Number (maximum cooking time in minutes)
  - `sort_by`: String (enum: "relevance", "rating", "time", "calories")
  - `sort_order`: String (enum: "asc", "desc")
  - `page`: Number (default: 1)
  - `limit`: Number (default: 20, max: 100)
- **Response**: List of search results

### System Management Endpoints

#### Health Check
- **Endpoint**: `GET /api/v1/health`
- **Description**: Check the health status of the Python service
- **Response**: Health status

#### Embedding Stats
- **Endpoint**: `GET /api/v1/embeddings/stats`
- **Description**: Get statistics about recipe embeddings
- **Response**: Embedding statistics

#### Generate Embeddings
- **Endpoint**: `POST /api/v1/embeddings/generate`
- **Description**: Generate embeddings for recipes that don't have them yet
- **Query Parameters**:
  - `batch_size`: Number (default: 50)
- **Response**: Generation statistics

## Parameter Reference

### URL Configuration
- `base_url`: Default values:
  - Node.js: `http://localhost:3000`
  - Python: `http://localhost:9999/api/v1`

### User and Interaction Variables
- `user_id`: String identifier (use "default_user" for non-authenticated)
- `interaction_type`: Enum values:
  - `view`
  - `like`
  - `save`
  - `cook`
  - `rating`

### Recipe Filtering Variables

#### Geographical Filters
- `cuisine` / `region`: String values:
  - `Middle Eastern`
  - `Egyptian`
  - `Lebanese`
  - `Turkish`
  - `Saudi Arabian`
  - `Iraqi`
  - `Palestinian`

- `sub_region`: Same values as cuisine/region plus:
  - `Rest Middle Eastern`

- `continent`: String values:
  - `Africa`
  - Other continents (not in sample data)

#### Dietary Filters
- `dietary_restriction`: String values:
  - `vegan` (494 recipes in database)
  - `pescetarian` (539 recipes)
  - `lacto_vegetarian` (299 recipes)

#### Time Filters
- `max_time`: Any positive integer (minutes)
  - Default: 30

### Recommendation Variables
- `recommendation_type`: String values:
  - `hybrid` (default)
  - `content`
  - `collaborative`

- `similarity_method`: String values:
  - `content` (default)
  - `ingredient`

- `time_window`: String values:
  - `day`
  - `week`
  - `month`

### Pagination and Limits
- `limit`: 
  - Default: 10
  - Maximum: 100
  - Any positive integer

- `page`: 
  - Default: 1
  - Any positive integer

### Numeric Recipe Variables

#### Time
- `cook_time`: Range: 5-120 minutes (most recipes: < 15 minutes)
- `prep_time`: Range: 10-30 minutes (average: ~15 minutes)

#### Nutrition
- `calories`: Range: 45-934, Categories:
  - Low (< 300): 2,021 recipes
  - Medium (300-600): 1,227 recipes
  - High (601-900): 370 recipes
  - Very High (> 900): 286 recipes

- `rating`: Decimal between 1-5 (e.g., 4.5)

### Recipe Identification
- `recipe_id`: Numeric IDs (sample range: 2610-2619)
  - Total recipes: 3,905

### Community Variables
- `privacy`: Enum values:
  - `PUBLIC`
  - `PRIVATE`
  - `RESTRICTED`

- `recipeCreationPermission`: Enum values:
  - `ANY_MEMBER`
  - `ADMIN_ONLY`

### Categories
Common categories include:
- `Italian Cuisine`
- `French Cuisine`
- `Mexican Cuisine`
- `Middle Eastern Cuisine`
- `Asian Cuisine`
- `Vegan & Plant-Based`
- `Vegetarian`
- `Keto & Low-Carb`
- `Breakfast & Brunch`
- `Dinner & Main Courses`
- `Home Cooking`