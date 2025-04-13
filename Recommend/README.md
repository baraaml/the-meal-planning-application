# MealFlow Recommendation System

A sophisticated recipe and meal recommendation service built with FastAPI, PostgreSQL, and vector embeddings. Provides personalized recipe recommendations using a hybrid approach combining collaborative filtering, content-based analysis, and popularity metrics.

## Features

### 📊 Multiple Recommendation Strategies

- **Hybrid Recommender**: Combines multiple algorithms with intelligent fallbacks
- **Collaborative Filtering**: Recommends recipes based on similar users' preferences
- **Content-Based Analysis**: Uses vector embeddings to find similar recipes
- **Ingredient Similarity**: Suggests recipes with similar ingredient profiles
- **Popularity-Based**: Recommends trending recipes as a fallback

### 🔍 Flexible Filtering

- Filter by recipe category
- Filter by cuisine/region
- Filter by dietary preferences (vegan, vegetarian, etc.)
- Personalized recommendations based on user history

### 🚀 Optimized Architecture

- Direct database access for improved performance
- Efficient query organization by domain
- Background embedding generation
- Scalable vector search using pgvector

## Technical Architecture

The service is built using:

- **FastAPI**: High-performance API framework
- **PostgreSQL + pgvector**: Database with vector similarity search
- **SentenceTransformers**: For generating recipe embeddings
- **CF-Step**: For collaborative filtering and matrix factorization

## Quick Start

### Prerequisites

- Python == 3.12
- PostgreSQL 17.4 with pgvector extension
- Make utility (for using the Makefile)

### Fast Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/mealflow.git
cd mealflow

# Configure your PostgreSQL connection string in .env
cp .env.example .env
# Edit .env with your database credentials

# Run fast setup and start the service
python3.12 -m venv venv
make install
make setup-db
make load-data
make init-embeddings
make run
```

## API Documentation

The service provides the following API endpoints:

### User Recommendations

```
GET /api/v1/recommend/user/{user_id}
```

Get personalized recipe recommendations for a user.

**Parameters:**
- `limit` (optional): Maximum number of recommendations (default: 10)
- `recommendation_type` (optional): Algorithm to use (hybrid, content, collaborative)
- `cuisine` (optional): Filter by cuisine/region
- `dietary_restriction` (optional): Filter by dietary restriction

### Similar Recipes

```
GET /api/v1/recommend/similar/{recipe_id}
```

Find recipes similar to a specific item.

**Parameters:**
- `limit` (optional): Maximum number of similar items (default: 10)
- `similarity_method` (optional): Method to determine similarity (content, ingredient)

### Trending Recipes

```
GET /api/v1/trending
```

Get trending recipes based on recent interactions.

**Parameters:**
- `time_window` (optional): Time window for trending items (day, week, month)
- `limit` (optional): Maximum number of items (default: 10)

### Recipe Management

```
GET /api/v1/recipes/{recipe_id}
GET /api/v1/recipes
POST /api/v1/recipes
PUT /api/v1/recipes/{recipe_id}
DELETE /api/v1/recipes/{recipe_id}
```

Full CRUD operations for recipe management.

### Record Interactions

```
POST /api/v1/interactions
```

Record a user interaction with a recipe (view, like, save, cook).

**Body:**
```json
{
  "user_id": "user123",
  "meal_id": "1001",
  "content_type": "recipe",
  "interaction_type": "like"
}
```

## Data Structure

The system works with recipe information including:
- Basic recipe information (id, title, cuisine, etc.)
- Ingredient lists
- Cooking instructions
- Nutritional information
- User interactions and preferences

## Custom Embedding Generation

Adjust embedding generation parameters:

```python
from embedding.embeddings import EmbeddingGenerator

# Update batch size for faster processing
generator = EmbeddingGenerator()
generator.generate_all_embeddings(batch_size=64)

# Use a different model for better embeddings
generator = EmbeddingGenerator(model_name="sentence-transformers/all-mpnet-base-v2")
```

## Troubleshooting

### Common Issues

1. **Missing pgvector Extension**:
   - Error: `extension 'vector' does not exist`
   - Solution: Install the pgvector extension in your database
   ```sql
   CREATE EXTENSION vector;
   ```

## License

MIT License