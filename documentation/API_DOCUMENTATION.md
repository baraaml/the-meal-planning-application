# Recommendation Service API Documentation

This guide provides instructions for using the Recommendation Service API and testing it with Postman.

## Overview

The Recommendation Service API provides several types of recommendations:

- **User Recommendations**: Personalized content recommendations for users
- **Similar Content**: Finding content similar to a specific item
- **Trending Content**: Popular content based on recent interactions
- **Category Recommendations**: Communities within specific categories
- **Interaction Tracking**: Recording user interactions with content

## Getting Started with Postman

### Importing the Collection

1. Open Postman
2. Click "Import" in the top left
3. Select the `recommendation_service.postman_collection.json` file from this directory
4. The collection will appear in your Postman workspace

### Setting Environment Variables

The collection uses several variables that you can customize:

- `baseUrl`: The base URL of the API (default: `http://localhost:8000`)
- `userId`: The user ID for testing user recommendations
- `contentId`: The content ID for testing similar content recommendations
- `contentType`: The content type (post, community) for testing
- `categoryId`: The category ID for testing category recommendations

You can either:

1. Edit these in the collection variables section
2. Create a Postman environment with these variables
3. Manually replace them in each request

## API Endpoints

### Service Status

```
GET /
```

Checks if the recommendation service is running.

### User Recommendations

```
GET /recommend/user/{userId}?recommendation_type={type}&content_type={contentType}&limit={limit}
```

Parameters:
- `userId`: The ID of the user
- `recommendation_type`: Algorithm to use (hybrid, item-based, user-based)
- `content_type` (optional): Filter by content type (post, community)
- `limit` (optional): Maximum number of recommendations

#### Recommendation Types

1. **hybrid**: Combines multiple strategies with fallbacks
   - Starts with collaborative filtering
   - Falls back to content-based recommendations
   - Finally uses popularity-based recommendations if needed

2. **item-based**: Item-based collaborative filtering
   - Finds items similar to those the user has interacted with
   - Based on co-occurrence patterns in user interactions

3. **user-based**: User-based collaborative filtering
   - Finds users with similar tastes
   - Recommends content they interacted with

### Similar Content

```
GET /recommend/similar/{contentType}/{contentId}?similarity_method={method}&limit={limit}
```

Parameters:
- `contentType`: The type of content (post, community)
- `contentId`: The ID of the content
- `similarity_method`: Method to determine similarity (content, interaction)
- `limit` (optional): Maximum number of similar items

#### Similarity Methods

1. **content**: Content-based similarity
   - Uses vector embeddings to find similar content
   - Based on text features and semantic similarity

2. **interaction**: Interaction-based similarity
   - Uses co-occurrence patterns to find similar content
   - Based on how users interact with content

### Trending Content

```
GET /trending/{contentType}?time_window={window}&limit={limit}
```

Parameters:
- `contentType`: The type of content (post, community, all)
- `time_window` (optional): Time window (day, week, month)
- `limit` (optional): Maximum number of items

### Category Recommendations

```
GET /recommend/category/{categoryId}?limit={limit}
```

Parameters:
- `categoryId`: The ID of the category
- `limit` (optional): Maximum number of communities to return

### Record Interactions

```
POST /interactions
```

Request body:
```json
{
  "user_id": "123",
  "content_id": "post-456",
  "content_type": "post",
  "interaction_type": "view"
}
```

Parameters:
- `user_id`: The ID of the user
- `content_id`: The ID of the content
- `content_type`: The type of content (post, community, comment)
- `interaction_type`: The type of interaction (view, click, vote, etc.)

## Testing Strategies

### Testing User Recommendations

1. Start by recording some interactions for a user
2. Then request recommendations for that user
3. Try different recommendation types to compare results

### Testing Similar Content

1. Select a piece of content with known features or interactions
2. Request similar content using both similarity methods
3. Compare the results to understand the differences

### Testing Trending Content

1. Record multiple interactions for different content
2. Try different time windows to see how trends change

## Example Workflow

1. Record interactions for user "123" with several posts
2. Get hybrid recommendations for user "123"
3. Get similar content to a post the user interacted with
4. Compare with trending content

## Common Issues

- If no recommendations are returned, it may be because:
  - The user has no recorded interactions
  - No similar users or content could be found
  - The content type filter is too restrictive
  
- If similarity scores are low, it may be because:
  - The embedding model needs more data
  - The content is genuinely dissimilar

## Advanced Usage

### Customizing Recommendation Behavior

You can modify the following files to customize the recommendation behavior:

- `services/hybrid_recommender.py`: Change the fallback strategy order
- `services/content_based_recommender.py`: Adjust similarity thresholds
- `services/collaborative_recommender.py`: Change the minimum common items threshold
- `config/settings.py`: Adjust global settings like limits and embedding dimensions