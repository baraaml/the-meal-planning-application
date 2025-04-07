# Meal Recommendation Service

A streamlined, comprehensive meal recommendation service built with FastAPI and PostgreSQL with pgvector for efficient similarity searches. This service provides personalized meal recommendations using multiple recommendation strategies.

## Features

### Multiple Recommendation Strategies

- **Hybrid Recommender**: Combines multiple strategies with fallbacks for robust recommendations
- **User-Based Collaborative Filtering**: Finds users with similar tastes and recommends what they liked
- **Item-Based Collaborative Filtering**: Recommends items similar to those the user has interacted with
- **Content-Based Recommendations**: Uses vector embeddings to find similar content
- **Ingredient-Based Recommendations**: Suggests meals with similar ingredients
- **Popularity-Based Recommendations**: Recommends trending content as a fallback

### Flexible API

- Choose which recommendation strategy to use via API parameters
- Filter by meal type or recipe
- Filter by cuisine or dietary restrictions
- Adjust time windows for trending content
- Get meal recommendations based on time of day

### Efficient Architecture

- Simplified folder structure for better maintainability
- Consolidated modules (no deep nesting of folders)
- Optimized database queries
- Background processing for embedding generation
- Consistent error handling and response formats

## System Architecture

The service is built with FastAPI and uses PostgreSQL with pgvector extension for vector similarity search. It stores embeddings and interaction data in separate tables without modifying your existing database schema.

### Folder Structure

```
meal-recommendation-service/
├── api/
│   ├── __init__.py
│   ├── endpoints.py        # All API routes consolidated here
│   └── middleware.py       # CORS and other middleware
├── services/
│   ├── __init__.py
│   ├── base_recommender.py
│   ├── collaborative.py
│   ├── content_based.py
│   ├── hybrid.py
│   ├── item_based.py
│   └── popularity.py
├── data/
│   ├── __init__.py
│   ├── database.py         # Connection handling
│   ├── queries.py          # SQL queries in one file
│   └── repositories.py     # All repositories consolidated
├── embeddings/
│   ├── __init__.py
│   └── generator.py
├── utils/
│   ├── __init__.py
│   └── scheduler.py
├── config.py               # All settings in one file
├── setup.py                # Database setup
├── main.py                 # Entry point
└── start.sh                # Startup script
```

## Setup Instructions

### Prerequisites

- Python 3.8+
- PostgreSQL with pgvector extension
- Node.js (for client integration, optional)

### Installation

1. Clone this repository:

```bash
git clone https://github.com/yourusername/meal-recommendation-service.git
cd meal-recommendation-service
```

2. Create a virtual environment (recommended):

```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

3. Install required Python packages:

```bash
pip install fastapi uvicorn sqlalchemy psycopg2-binary sentence-transformers pandas scikit-learn numpy python-dotenv schedule
```

4. Make the startup script executable:

```bash
chmod +x start.sh
```

5. Create a `.env` file with your database connection string:

```
DATABASE_URL=postgresql://username:password@localhost:5432/yourdb
```

### Starting the Service

Use the provided startup script:

```bash
./start.sh
```

This will:
- Check for and install required packages
- Create necessary database tables if they don't exist
- Generate initial embeddings for content
- Start the background scheduler
- Start the FastAPI server

## API Documentation

Once the service is running, you can access the API documentation at:

```
http://localhost:8000/docs
```

### Key Endpoints

#### User Recommendations

```
GET /recommend/user/{user_id}?content_type=meal&limit=10&recommendation_type=hybrid
```

Get personalized meal recommendations for a user with options to use hybrid, user-based, or item-based algorithms.

#### Similar Meals

```
GET /recommend/similar/{content_type}/{meal_id}?limit=10&similarity_method=content
```

Get meals similar to the specified item. Choose between content-based similarity (using embeddings), interaction-based similarity (based on co-occurrence patterns), or ingredient-based similarity.

#### Trending Meals

```
GET /trending/{content_type}?time_window=day&limit=10
```

Get trending meals based on recent interactions.

#### Cuisine Recommendations

```
GET /recommend/cuisine/{cuisine_id}?limit=10
```

Get meals in the specified cuisine.

#### Dietary Restriction Recommendations

```
GET /recommend/dietary/{dietary_restriction_id}?limit=10
```

Get meals that conform to specific dietary restrictions.

#### Record Interactions

```
POST /interactions

{
  "user_id": "123",
  "meal_id": "456",
  "content_type": "meal",
  "interaction_type": "view"
}
```

Record user interactions with meals.

## Postman Collection

A Postman collection is included in the repository for testing the API. To use it:

1. Import `meal-recommendations.postman_collection.json` into Postman
2. Set the `baseUrl` environment variable to your API server (default: http://localhost:8000)
3. Use the test data IDs or replace them with your own data

## Customization

### Configuration Settings

Edit `config.py` to customize:

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

You can change the embedding model in `config.py`:

```python
# Default model (lightweight)
EMBEDDING_MODEL = "all-MiniLM-L6-v2"

# For better multilingual support
# EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM