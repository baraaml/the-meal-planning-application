.PHONY: setup fast-setup install clean run test lint load-data setup-db reset-db init-embeddings quick-embeddings test-db schema-only check-data freeze help

# Default Python interpreter
PYTHON = python3
PIP = pip3

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

# Default variables
DATABASE_URL ?= postgresql://postgres:postgres@localhost:5432/mealflow
API_PORT ?= 8000
API_HOST ?= 0.0.0.0
DATA_DIR ?= data

# Setup the development environment (full setup)
setup: install check-data setup-db load-data init-embeddings

# Fast setup - minimal required steps to get running
fast-setup: install check-data schema-only load-data quick-embeddings
	@echo "Fast setup complete. Use 'make run' to start the service."
	@echo "Note: Full embeddings will be generated in the background when the service starts."

# Install required packages
install:
	@echo "Installing required packages..."
	$(PIP) install -r requirements.txt
	@echo "✓ Installation complete"

# Clean up temporary files and artifacts
clean:
	@echo "Cleaning up temporary files and artifacts..."
	find . -type d -name __pycache__ -exec rm -rf {} +
	find . -type f -name "*.pyc" -delete
	find . -type f -name "*.pyo" -delete
	find . -type f -name "*.pyd" -delete
	find . -type f -name ".DS_Store" -delete
	find . -type d -name "*.egg-info" -exec rm -rf {} +
	find . -type d -name "*.egg" -exec rm -rf {} +
	find . -type d -name ".pytest_cache" -exec rm -rf {} +
	find . -type d -name ".coverage" -exec rm -rf {} +
	find . -type d -name "htmlcov" -exec rm -rf {} +
	find . -type d -name ".mypy_cache" -exec rm -rf {} +
	@echo "✓ Cleanup complete"

# Run the service
run:
	@echo "Starting the meal recommendation service..."
	uvicorn main:app --host $(API_HOST) --port $(API_PORT) --reload

# Run tests
test:
	@echo "Running tests..."
	pytest -xvs tests/

# Run linter
lint:
	@echo "Running linter..."
	flake8 .

# Check if data files exist
check-data:
	@echo "Checking data files..."
	@if [ ! -d "$(DATA_DIR)" ]; then \
		echo "Error: Data directory not found at $(DATA_DIR)"; \
		exit 1; \
	fi
	@missing_files=0; \
	for file in RecipeDB_general.csv RecipeDB_ingredient_phrase.csv RecipeDB_ingredient_flavor.csv RecipeDB_instructions.json; do \
		if [ ! -f "$(DATA_DIR)/$$file" ]; then \
			echo "Warning: File $$file not found in $(DATA_DIR)"; \
			missing_files=1; \
		fi; \
	done; \
	if [ $$missing_files -eq 0 ]; then \
		echo "✓ All required data files found"; \
	else \
		echo "Warning: Some data files are missing. See above for details."; \
	fi

# Load data from CSV and JSON files
load-data: check-data
	@echo "Loading data from CSV and JSON files..."
	$(PYTHON) -m data.loader
	@echo "✓ Data loading complete"

# Setup database tables for recommendation system
setup-db: test-db
	@echo "Setting up database tables for recommendation system..."
	$(PYTHON) -m setup
	@echo "✓ Database setup complete"

# Reset database (drop and recreate tables)
reset-db: test-db
	@echo "Resetting database tables..."
	$(PYTHON) -c "from sqlalchemy import create_engine, text; \
                engine = create_engine('$(DATABASE_URL)'); \
                with engine.connect() as conn: \
                    conn.execute(text('DROP SCHEMA public CASCADE')); \
                    conn.execute(text('CREATE SCHEMA public')); \
                    conn.execute(text('GRANT ALL ON SCHEMA public TO postgres')); \
                    conn.execute(text('GRANT ALL ON SCHEMA public TO public'));"
	@echo "Database reset complete. Run 'make setup-db' to create tables."

# Generate all initial embeddings
init-embeddings:
	@echo "Generating initial embeddings..."
	$(PYTHON) -c "from embeddings.generator import EmbeddingGenerator; \
				  generator = EmbeddingGenerator(); \
				  result = generator.generate_all_embeddings(); \
				  print(f'Initial embeddings generated: {result}')"
	@echo "✓ Initial embeddings generated"

# Quick embeddings - only process a small batch for fast startup
quick-embeddings:
	@echo "Generating minimal embeddings for fast startup..."
	$(PYTHON) -c "from embeddings.generator import EmbeddingGenerator; \
				  generator = EmbeddingGenerator(); \
				  meal_count = generator.generate_meal_embeddings(batch_size=50); \
				  recipe_count = generator.generate_recipe_embeddings(batch_size=50); \
				  print(f'Quick embeddings generated: {meal_count} meals, {recipe_count} recipes')"
	@echo "✓ Quick embeddings complete (remaining will be generated in the background)"

# Test database connection
test-db:
	@echo "Testing database connection..."
	@$(PYTHON) -c "from data.database import test_connection; connection_ok = test_connection(); exit(0 if connection_ok else 1)" || \
		(echo "Database connection failed. Please check your DATABASE_URL in .env file."; exit 1)
	@echo "✓ Database connection successful"

# Setup database schema only (no data import)
schema-only: test-db
	@echo "Setting up database schema only..."
	$(PYTHON) -m setup

# Generate requirements.txt file
freeze:
	@echo "Generating requirements.txt file..."
	$(PIP) freeze > requirements.txt
	@echo "✓ Requirements file updated"

# Help
help:
	@echo "╔════════════════════════════════════════════════════╗"
	@echo "║               MealFlow Makefile Help               ║"
	@echo "╚════════════════════════════════════════════════════╝"
	@echo "Available targets:"
	@echo "  fast-setup      - Quick setup for development (recommended)"
	@echo "  setup           - Complete setup (slower but thorough)"
	@echo "  run             - Start the recommendation service"
	@echo ""
	@echo "Database commands:"
	@echo "  test-db         - Test database connection"
	@echo "  setup-db        - Create database tables"
	@echo "  schema-only     - Create schema without importing data"
	@echo "  reset-db        - Reset database (drop and recreate)"
	@echo ""
	@echo "Data commands:"
	@echo "  check-data      - Verify data files exist"
	@echo "  load-data       - Import data from CSV/JSON files"
	@echo "  init-embeddings - Generate all embeddings"
	@echo "  quick-embeddings- Generate minimal embeddings for fast startup"
	@echo ""
	@echo "Development tools:"
	@echo "  install         - Install required packages"
	@echo "  clean           - Clean up temporary files"
	@echo "  test            - Run tests"
	@echo "  lint            - Run linter"
	@echo "  freeze          - Update requirements.txt"
	@echo ""
	@echo "Example usage:"
	@echo "  make fast-setup && make run"

# Default target
.DEFAULT_GOAL := help