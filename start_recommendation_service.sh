#!/bin/bash
# start_recommendation_service.sh
# Startup script for the recommendation service

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
required_packages=("fastapi" "uvicorn" "sqlalchemy" "sentence-transformers" "schedule" "python-dotenv")
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

# Create necessary database tables
echo "Setting up database tables..."
python3 -m setup.setup_tables

# Generate initial embeddings
echo "Generating initial embeddings..."
python3 -m embeddings.embedding_generator

# Start the scheduler in the background
echo "Starting background scheduler..."
python3 -m scheduler.scheduler &
SCHEDULER_PID=$!

# Register a trap to kill the scheduler when the script exits
trap "echo 'Stopping scheduler...'; kill $SCHEDULER_PID 2>/dev/null" EXIT

# Start the API server
echo "Starting API server..."
python3 -m api.app

echo "Recommendation service stopped."