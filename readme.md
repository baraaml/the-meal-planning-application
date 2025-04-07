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

- Fast setup with minimal configuration
- Efficient database queries
- Background embedding generation
- Scalable vector search using pgvector

## Technical Architecture

The service is built using:

- **FastAPI**: High-performance API framework
- **PostgreSQL + pgvector**: Database with vector similarity search
- **SentenceTransformers**: For generating recipe embeddings
- **SQLAlchemy**: Database ORM and connection management
- **Pandas**: Data processing for CSV/JSON imports

## Quick Start

### Prerequisites

- Python 3.8+
- PostgreSQL 13+ with pgvector extension
- Make utility (for using the Makefile)

### Fast Setup

The fastest way to get up and running:

```bash
# Clone the repository
git clone https://github.com/your-username/mealflow.git
cd mealflow

# Copy your data files to the data directory
# Required files: RecipeDB_general.csv, RecipeDB_ingredient_phrase.csv, 
# RecipeDB_ingredient_flavor.csv, RecipeDB_instructions.json

# Configure your database in .env
cp .env.example .env
# Edit .env with your database credentials

# Run fast setup and start the service
make fast-setup && make run
```

This will:
1. Install all required packages
2. Set up the database schema
3. Import your recipe data
4. Generate initial embeddings for a small batch of recipes
5. Start the service with auto-reload enabled

The remaining embeddings will be generated in the background while the service runs.

### Manual Setup

If you prefer a step-by-step approach:

```bash
# Install requirements
pip install -r requirements.txt

# Set up database tables
python -m setup

# Import data
python -m data.loader

# Generate embeddings
python -c "from embeddings.generator import EmbeddingGenerator; generator = EmbeddingGenerator(); generator.generate_all_embeddings()"

# Start the server
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

## Available Commands

The project includes a Makefile with useful commands:

```bash
# Fast setup (recommended)
make fast-setup

# Complete setup (slower but thorough)
make setup

# Start the service
make run

# Test database connection
make test-db

# Reset database (drop and recreate)
make reset-db

# Import data from CSV/JSON files
make load-data

# Generate all embeddings
make init-embeddings

# See all available commands
make help
```

## API Documentation

The service provides the following API endpoints:

### User Recommendations

```
GET /recommend/user/{user_id}
```

Get personalized recipe recommendations for a user.

**Parameters:**
- `content_type` (optional): Filter by content type (meal, recipe)
- `limit` (optional): Maximum number of recommendations (default: 10)
- `recommendation_type` (optional): Algorithm to use (hybrid, item-based, user-based)
- `cuisine` (optional): Filter by cuisine/region
- `dietary_restriction` (optional): Filter by dietary restriction

### Similar Recipes

```
GET /recommend/similar/{content_type}/{meal_id}
```

Find recipes similar to a specific item.

**Parameters:**
- `limit` (optional): Maximum number of similar items (default: 10)
- `similarity_method` (optional): Method to determine similarity (content, interaction, ingredient)

### Trending Recipes

```
GET /trending/{content_type}
```

Get trending recipes based on recent interactions.

**Parameters:**
- `time_window` (optional): Time window for trending items (day, week, month)
- `limit` (optional): Maximum number of items (default: 10)

### Cuisine/Region Recommendations

```
GET /recommend/cuisine/{cuisine_id}
```

Get recipe recommendations for a specific cuisine or region.

### Dietary Recommendations

```
GET /recommend/dietary/{dietary_restriction}
```

Get recipe recommendations that match dietary preferences.

### Record Interactions

```
POST /interactions
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

### User Dietary Preferences

```
GET /user/{user_id}/dietary-preferences
POST /user/{user_id}/dietary-preferences
DELETE /user/{user_id}/dietary-preferences/{dietary_restriction}
```

Manage a user's dietary preferences.

## Data Structure

The system works with the following CSV and JSON files:

- **RecipeDB_general.csv**: Basic recipe information (id, title, cuisine, etc.)
- **RecipeDB_ingredient_phrase.csv**: Ingredient information for each recipe
- **RecipeDB_ingredient_flavor.csv**: Flavor profiles for ingredients
- **RecipeDB_instructions.json**: Step-by-step cooking instructions

The data follows the structure defined in the CSV header rows without requiring fixed enumerations for cuisines or dietary restrictions.

## Customizing the System

### Adding New Recommendation Strategies

1. Create a new class that extends `BaseRecommender`
2. Implement the `get_recommendations` method
3. Add your strategy to the `HybridRecommender` class

### Modifying Embedding Generation

Adjust embedding generation parameters in `EmbeddingGenerator`:

```python
# Update batch size for faster processing
generator.generate_meal_embeddings(batch_size=200)

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

2. **Embedding Model Download Issues**:
   - If you encounter issues downloading the embedding model, ensure you have internet access
   - You can pre-download models using Hugging Face's library

3. **Database Connection Errors**:
   - Verify your DATABASE_URL in the .env file
   - Check PostgreSQL is running
   - Test connection with `make test-db`

4. **Data Import Errors**:
   - Ensure your CSV files have the expected format
   - Check for encoding issues in your data files
   - Try importing with smaller batch sizes by editing BATCH_SIZE in .env

## Production Deployment

For production deployment, consider:

1. **Security**:
   - Add proper authentication
   - Restrict CORS settings
   - Use HTTPS

2. **Performance**:
   - Use connection pooling
   - Add caching for frequent queries
   - Deploy with multiple workers

3. **Monitoring**:
   - Add detailed logging
   - Set up performance monitoring
   - Implement health check endpoints

## License

MIT License