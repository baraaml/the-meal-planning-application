# Python Recommendation Service

A Python-based recommendation service designed to integrate with Node.js applications REST service without modifying existing Prisma schema which made by the node js application service.

## Overview

This recommendation service provides multiple recommendation strategies with fallbacks:
1. **User-based collaborative filtering** based on similar users' interactions on meals
2. **Item-based collaborative filtering** based on co-occurrence patterns 
3. **Content-based recommendations** using text embeddings of meals content 
4. **Popularity-based recommendations** as a last resort

The service is highly configurable and allows switching between recommendation algorithms based on your needs. In this project I will be mixing scores of different returned values of the above approaches

## System Architecture

The service is built with FastAPI and uses PostgreSQL with pgvector extension for vector similarity search. It stores embeddings and interaction data in separate tables without modifying your existing database schema.

## Features

### Multiple Recommendation Strategies

The service implements several recommendation algorithms:

- **Hybrid Recommender**: Combines multiple strategies with fallbacks for robust recommendations
- **User-Based Collaborative Filtering**: Finds users with similar tastes and recommends what they liked
- **Item-Based Collaborative Filtering**: Recommends items similar to those the user has interacted with
- **Content-Based Recommendations**: Uses vector embeddings to find similar content
- **Popularity-Based Recommendations**: Recommends trending content as a fallback

### Flexible API

- Choose which recommendation strategy to use via API parameters
- Filter by content type
- Adjust time windows for trending content
- Get category-based recommendations

### Background Processing

- Automatic embedding generation for new content
- Scheduled tasks for keeping recommendations fresh

## Setup Instructions

### Prerequisites

- Python 3.8+
- PostgreSQL with pgvector extension
- Node.js (for client integration)

### Installation

1. Install required Python packages:

```bash
pip install fastapi uvicorn sqlalchemy psycopg2-binary sentence-transformers pandas scikit-learn numpy python-dotenv schedule
```

2. Make the startup script executable:

```bash
chmod +x start_recommendation_service.sh
```

3. Update your .env file with the DATABASE_URL:

```
DATABASE_URL=postgresql://username:password@localhost:5432/yourdb
```

### Starting the Service

Use the provided startup script:

```bash
./start_recommendation_service.sh
```

This will:
- Check for and install required packages
- Create necessary database tables if they don't exist
- Generate initial embeddings for content
- Start the background scheduler
- Start the FastAPI server

## API Endpoints

### User Recommendations

```
GET /recommend/user/{user_id}?content_type=post&limit=10&recommendation_type=hybrid
```

Get personalized recommendations for a user with options to use hybrid, user-based, or item-based algorithms.

### Similar Content

```
GET /recommend/similar/{content_type}/{meal_id}?limit=10&similarity_method=content
```

Get content similar to the specified item. Choose between content-based similarity (using embeddings) or interaction-based similarity (based on co-occurrence patterns).

### Trending Content

```
GET /trending/{content_type}?time_window=day&limit=10
```

Get trending content based on recent interactions.

### Category Recommendations

```
GET /recommend/category/{category_id}?limit=10
```

Get communities in the specified category.

### Record Interactions

```
POST /interactions

{
  "user_id": "123",
  "meal_id": "456",
  "content_type": "post",
  "interaction_type": "view"
}
```

Record user interactions with content.

## Integration with Node.js

Use the provided recommendation-client.js:

```javascript
const recommendations = require('./recommendation-service/client/recommendation-client');

// Get user recommendations with hybrid algorithm
async function getUserFeed(userId) {
  const items = await recommendations.getUserRecommendations(userId, 'post', 20, 'hybrid');
  
  // Fetch additional data using Prisma if needed
  const posts = await prisma.post.findMany({
    where: {
      id: { in: items.map(item => item.id) }
    },
    include: {
      author: true,
      community: true
    }
  });
  
  return posts;
}

// Get similar content using item-based collaborative filtering
async function getSimilarPosts(postId) {
  return await recommendations.getSimilarContent('post', postId, 5, 'interaction');
}

// Record a view
async function recordView(userId, postId) {
  await recommendations.recordInteraction(userId, postId, 'post', 'view');
  // Other application logic...
}

// Get trending content
async function getTrendingPosts() {
  return await recommendations.getTrendingContent('post', 'week', 10);
}
```

## Customization

### Configuration Settings

Edit `config/settings.py` to customize:

- Database connection parameters
- API settings
- Embedding model and dimension
- Default recommendation limits
- Scheduler intervals

### Recommendation Algorithms

You can customize the recommendation algorithms by:

1. Adjusting parameters in the API requests
2. Modifying the implementation in the respective strategy files
3. Creating new recommender classes that implement the BaseRecommender interface

### Embedding Model

You can change the embedding model in `config/settings.py`:

```python
# Default model
EMBEDDING_MODEL = "all-MiniLM-L6-v2"

# For better multilingual support
# EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"

# For better semantic understanding but slower processing
# EMBEDDING_MODEL = "all-mpnet-base-v2"
```

## Performance Considerations

- The vector index is essential for fast similarity search
- Background embedding generation prevents API slowdowns
- Queries include appropriate indexes and limits

## Troubleshooting

### Connection Issues

If you encounter database connection issues:

```bash
python -m data.database
```

This will test the database connection and report any errors.

### Missing Embeddings

If content isn't showing up in recommendations:

```bash
python -m embeddings.embedding_generator
```

This will generate embeddings for any missing content.

### API Issues

You can check the API documentation at:

```
http://localhost:8000/docs
```

This provides an interactive OpenAPI interface for testing endpoints.