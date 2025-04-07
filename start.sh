#!/bin/bash
# start.sh
# Startup script for the meal recommendation service

# Set the directory to the script's location
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

# Function to check if a Python module exists
module_exists() {
    python3 -c "import $1" 2>/dev/null
    return $?
}

# Check for required Python packages
echo "Checking for required Python packages..."
required_packages=(
    "fastapi" 
    "uvicorn" 
    "sqlalchemy" 
    "psycopg2-binary" 
    "sentence-transformers" 
    "schedule" 
    "python-dotenv"
    "numpy"
)
missing_packages=()

for package in "${required_packages[@]}"; do
    if ! module_exists $package; then
        missing_packages+=($package)
    fi
done

# Install missing packages if any
if [ ${#missing_packages[@]} -ne 0 ]; then
    echo "Installing missing packages: ${missing_packages[*]}"
    pip install "${missing_packages[@]}"
fi

# Check if pgvector extension is installed in PostgreSQL
echo "Checking pgvector extension..."
python3 -c "
from data.database import execute_query
try:
    result = execute_query('SELECT COUNT(*) FROM pg_extension WHERE extname = \\'vector\\'')
    count = result.fetchone()[0]
    if count == 0:
        print('pgvector extension is not installed in the database.')
        print('You may need to run: CREATE EXTENSION vector;')
    else:
        print('pgvector extension is properly installed.')
except Exception as e:
    print(f'Error checking pgvector extension: {e}')
"

# Create necessary database tables
echo "Setting up database tables..."
python3 -m setup

# Generate initial embeddings
echo "Generating initial embeddings..."
python3 -c "
from embeddings.generator import EmbeddingGenerator
generator = EmbeddingGenerator()
result = generator.generate_all_embeddings()
print(f'Initial embeddings generated: {result}')
"

# Start the API server
echo "Starting API server..."
uvicorn main:app --host 0.0.0.0 --port 8000 --reload