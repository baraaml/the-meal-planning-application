# MealFlow Recommendation Service

A comprehensive meal recommendation service built with FastAPI and PostgreSQL with pgvector for efficient similarity searches. This service provides personalized meal and recipe recommendations using multiple recommendation strategies.

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
- Consolidated modules for easier updates
- Optimized database queries
- Background processing for embedding generation
- Consistent error handling and response formats

## System Architecture

The service is built with FastAPI and uses PostgreSQL with pgvector extension for vector similarity search. It stores embeddings and interaction data in separate tables to work alongside your existing database schema.

## Setup Instructions

### Prerequisites

- Python 3.8+
- PostgreSQL 13+ with pgvector extension
- Make (optional, for using the Makefile)

### Installation

1. Clone this repository:

```bash
git clone https://github.com/yourusername/mealflow-recommendation-service.git
cd mealflow-recommendation-service
```

2. Create a virtual environment (recommended):

```bash
python -m data.loader

This will:
- Create necessary database tables if they don't exist
- Import all recipe data from CSV and JSON files
- Extract and populate cuisine information
- Update foreign key references

You can customize the import settings in your `.env` file, including:
- Data directory path
- File names
- Batch size for efficient imports

## API Documentation

Once the service is running, you can access the interactive API documentation at:

```
http://localhost:8000/docs
```

### Key Endpoints

#### User Recommendations

```
GET /recommend/user/{user_id}
```

Get personalized meal recommendations for a user with options to filter by content type, cuisine, dietary restrictions, and more.

#### Similar Meals

```
GET /recommend/similar/{content_type}/{meal_id}
```

Get meals similar to the specified item with various similarity methods (content-based, interaction-based, ingredient-based).

#### Trending Meals

```
GET /trending/{content_type}
```

Get trending meals based on recent interactions, with adjustable time windows.

#### Cuisine Recommendations

```
GET /recommend/cuisine/{cuisine_id}
```

Get recommended meals from a specific cuisine.

#### Dietary Restriction Recommendations

```
GET /recommend/dietary/{dietary_restriction_id}
```

Get meal recommendations that match specific dietary requirements.

#### Record Interactions

```
POST /interactions
```

Record user interactions with meals (views, likes, saves, etc.) to improve future recommendations.

## Project Structure

```
mealflow-recommendation-service/
├── api/                   # API endpoints and middleware
├── config/                # Configuration settings
├── data/                  # Data access and management
│   ├── loader.py          # Data import script
│   ├── database.py        # Database connection utilities
│   ├── queries.py         # SQL queries
│   └── repositories.py    # Data access repositories
├── embeddings/            # Vector embedding generation
├── services/              # Recommendation services
│   ├── base_recommender.py        # Base interface
│   ├── collaborative_recommender.py
│   ├── content_based_recommender.py
│   ├── hybrid_recommender.py
│   ├── item_based_recommender.py
│   └── popularity_recommender.py
├── setup/                 # Database setup scripts
├── utils/                 # Utility functions
├── main.py                # Application entry point
├── .env                   # Environment variables (not in version control)
├── .env.example           # Example environment file
├── Makefile               # Task automation
└── requirements.txt       # Python dependencies
```

## Production Deployment Considerations

When deploying to production, consider the following:

1. **Database Optimization**:
   - Create appropriate indexes for frequent queries
   - Consider partitioning large tables
   - Set up regular database maintenance

2. **Security**:
   - Use proper authentication for API endpoints
   - Restrict CORS settings to allowed domains
   - Use environment variables for sensitive information

3. **Performance**:
   - Run embedding generation as a separate background process
   - Implement caching for frequent queries
   - Consider using a connection pool for database access

4. **Scalability**:
   - Deploy behind a load balancer
   - Set up multiple worker processes
   - Consider containerization with Docker

5. **Monitoring**:
   - Add detailed logging
   - Set up performance monitoring
   - Implement health check endpoints

## Maintenance and Updates

### Adding New Recommendation Strategies

1. Create a new class that implements the `BaseRecommender` interface
2. Implement the `get_recommendations` method
3. Add the new strategy to the `HybridRecommender` if needed

### Updating Data Models

If you need to update data models:

1. Modify the corresponding repository class
2. Update the database tables as needed
3. Update any affected queries

## Troubleshooting

### Common Issues

1. **Missing pgvector Extension**:
   - Error: "extension 'vector' does not exist"
   - Solution: Install the pgvector extension in your database

2. **Embedding Generation Errors**:
   - Check that the `sentence-transformers` package is properly installed
   - Ensure you have enough memory for the embedding model

3. **Database Connection Issues**:
   - Verify your DATABASE_URL in the .env file
   - Check that PostgreSQL is running
   - Test connection with `make test-db`

4. **Dataset Import Errors**:
   - Ensure your CSV files have the expected format
   - Check for encoding issues in your data files
   - Try importing with smaller batch sizes

## License

MIT License

## Acknowledgments

- SentenceTransformers for the embedding models
- pgvector for PostgreSQL vector similarity search
- FastAPI for the API framework venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

3. Install required Python packages:

```bash
pip install -r requirements.txt
```

4. Configure your environment by creating a `.env` file based on `.env.example`:

```bash
cp .env.example .env
# Edit .env with your database credentials and settings
```

5. Install the pgvector extension in your PostgreSQL database:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

### Using the Makefile

The project includes a Makefile to simplify common tasks:

```bash
# Set up the complete development environment
make setup

# Test database connection
make test-db

# Load data from CSV and JSON files
make load-data

# Set up database tables only (without data import)
make schema-only

# Generate initial embeddings
make init-embeddings

# Run the service
make run

# Clean up temporary files
make clean

# Show available make commands
make help
```

### Manual Setup

If you prefer not to use the Makefile, you can perform each step manually:

1. Set up database tables:

```bash
python -m setup
```

2. Load data from CSV and JSON files:

```bash
python -m data.loader
```

3. Generate initial embeddings:

```bash
python -c "from embeddings.generator import EmbeddingGenerator; generator = EmbeddingGenerator(); generator.generate_all_embeddings()"
```

4. Start the server:

```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

## Importing Data

The system includes a data loader script that imports the following datasets:

- `RecipeDB_general.csv`: Basic recipe metadata
- `RecipeDB_ingredient_phrase.csv`: Recipe-ingredient relationships
- `RecipeDB_ingredient_flavor.csv`: Ingredient metadata and flavor profiles
- `RecipeDB_instructions.json`: Step-by-step recipe instructions
- `merged.csv`: Merged recipe data with additional metrics

To import your data:

1. Place your data files in the `data` directory
2. Run the data loader:

```bash
python -m