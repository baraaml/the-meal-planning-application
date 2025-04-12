.PHONY: setup install clean run test load-data setup-db reset-db drop-db create-db init-embeddings test-db train-mf-model help

# Python from virtual environment
PYTHON = ./venv/bin/python
PIP = ./venv/bin/pip

# Default environment file
ENV_FILE = .env

# Check if environment file exists
ifeq (,$(wildcard $(ENV_FILE)))
    $(warning $(ENV_FILE) file not found. Using default values.)
endif

# Load environment variables if file exists
ifneq (,$(wildcard $(ENV_FILE)))
    include $(ENV_FILE)
    export
endif

# Setup the development environment (full setup)
setup: install setup-db load-data init-embeddings

# Install required packages
install:
	@echo "Installing required packages..."
	$(PIP) install -r requirements.txt
	@echo "✓ Installation complete"

# Clean up temporary files and artifacts
clean:
	@echo "Cleaning up temporary files and artifacts..."
	find . -name "__pycache__" -type d -exec rm -rf {} +
	find . -name "*.py[cod]" -exec rm -f {} +
	find . -name ".DS_Store" -exec rm -f {} +
	find . -name "*.egg-info" -type d -exec rm -rf {} +
	find . -name "*.egg" -exec rm -f {} +
	find . -name ".pytest_cache" -type d -exec rm -rf {} +
	find . -name ".coverage" -exec rm -f {} +
	find . -name "htmlcov" -type d -exec rm -rf {} +
	find . -name ".mypy_cache" -type d -exec rm -rf {} +
	@echo "✓ Cleanup complete"

# Run the service
run:
	@echo "Starting the meal recommendation service..."
	./run.sh

# Run tests
test:
	@echo "Running tests..."
	pytest -xvs tests/


# Load data from CSV and JSON files
load-data:
	@echo "Loading data from CSV and JSON files..."
	$(PYTHON) ./dataset2db/load.py
	@echo "✓ Data loading complete"

# Setup database tables for recommendation system
setup-db: test-db
	@echo "Setting up database tables for recommendation system..."
	psql $(DATABASE_URL) < ./schema/schema.sql
	@echo "✓ Database setup complete"

# Reset database (drop and recreate tables)
reset-db: drop-db setup-db
	@echo "Database reset complete."

# Drop database
drop-db:
	@echo "Dropping database schema..."
	psql $(DATABASE_URL) -c "DROP SCHEMA public CASCADE;"
	psql $(DATABASE_URL) -c "CREATE SCHEMA public;"
	psql $(DATABASE_URL) -c "GRANT ALL ON SCHEMA public TO postgres;"
	psql $(DATABASE_URL) -c "GRANT ALL ON SCHEMA public TO public;"
	@echo "✓ Database dropped"

# Generate all initial embeddings
init-embeddings:
	@echo "Generating initial embeddings..."
	PYTHONPATH=. ./venv/bin/python ./embeddings/generator.py

# Create database
create-db:
	@echo "Creating database..."
	createdb -h localhost -U mealflow meals
	@echo "✓ Database created"

# Test database connection
test-db:
	@echo "Testing database connection..."
	@psql $(DATABASE_URL) -c "SELECT 1" >/dev/null 2>&1 || \
		(echo "Database connection failed. Please check your DATABASE_URL in .env file."; exit 1)
	@echo "✓ Database connection successful"


# Train matrix factorization model
train-mf-model:
	@echo "Training matrix factorization model..."
	curl -X POST "http://$(API_HOST):$(API_PORT)/models/matrix-factorization/train" \
		 -H "Content-Type: application/json" \
		 -d '{"n_factors": 32, "learning_rate": 0.05, "batch_size": 512}'
	@echo "✓ Matrix factorization model training started"



# Help
help:
	@echo "╔════════════════════════════════════════════════════╗"
	@echo "║               MealFlow Makefile Help               ║"
	@echo "╚════════════════════════════════════════════════════╝"
	@echo "Available targets:"
	@echo "  setup           - Complete setup"
	@echo "  run             - Start the recommendation service"
	@echo ""
	@echo "Database commands:"
	@echo "  test-db         - Test database connection"
	@echo "  create-db       - Create the database"
	@echo "  setup-db        - Create database tables"
	@echo "  drop-db         - Drop database schema"
	@echo "  reset-db        - Reset database (drop and recreate)"
	@echo ""
	@echo "Data commands:"
	@echo "  load-data       - Import data from CSV/JSON files"
	@echo "  init-embeddings - Generate all embeddings"
	@echo ""
	@echo "Development tools:"
	@echo "  install         - Install required packages"
	@echo "  clean           - Clean up temporary files"
	@echo "  test            - Run tests"
	@echo ""
	@echo "Example usage:"
	@echo "  make setup && make run"

# Default target
.DEFAULT_GOAL := help