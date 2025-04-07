.PHONY: setup install clean run test lint load-data setup-db init-embeddings

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

# Setup the development environment
setup: install setup-db load-data init-embeddings

# Install required packages
install:
	@echo "Installing required packages..."
	$(PIP) install -r requirements.txt

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

# Load data from CSV and JSON files
load-data:
	@echo "Loading data from CSV and JSON files..."
	$(PYTHON) -m data.loader

# Setup database tables for recommendation system
setup-db:
	@echo "Setting up database tables for recommendation system..."
	$(PYTHON) -m setup

# Generate initial embeddings
init-embeddings:
	@echo "Generating initial embeddings..."
	$(PYTHON) -c "from embeddings.generator import EmbeddingGenerator; \
				  generator = EmbeddingGenerator(); \
				  result = generator.generate_all_embeddings(); \
				  print(f'Initial embeddings generated: {result}')"

# Test database connection
test-db:
	@echo "Testing database connection..."
	$(PYTHON) -c "from data.database import test_connection; test_connection()"

# Setup database schema only (no data import)
schema-only:
	@echo "Setting up database schema only..."
	$(PYTHON) -m setup

# Generate requirements.txt file
freeze:
	@echo "Generating requirements.txt file..."
	$(PIP) freeze > requirements.txt

# Help
help:
	@echo "Available targets:"
	@echo "  setup          - Set up development environment (install, setup-db, load-data, init-embeddings)"
	@echo "  install        - Install required packages"
	@echo "  clean          - Clean up temporary files and artifacts"
	@echo "  run            - Run the meal recommendation service"
	@echo "  test           - Run tests"
	@echo "  lint           - Run linter"
	@echo "  load-data      - Load data from CSV and JSON files"
	@echo "  setup-db       - Setup database tables for recommendation system"
	@echo "  init-embeddings - Generate initial embeddings"
	@echo "  test-db        - Test database connection"
	@echo "  schema-only    - Setup database schema only (no data import)"
	@echo "  freeze         - Generate requirements.txt file"
	@echo "  help           - Show this help"