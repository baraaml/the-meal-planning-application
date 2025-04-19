# PostgreSQL Configuration
DB_USER=mealflow
DB_PASSWORD=mealflow
DB_NAME=mealflow
DB_HOST=localhost
DB_PORT=5432

.PHONY: db-setup-linux db-drop-linux db-setup-windows db-drop-windows db-migrate git-login git-pull git-push git-connect git-init git-create-branch git-pull-dev

# Setup Database and User (Linux)
db-setup-linux:
	@echo "Setting up database and user for Linux..."
	sudo -u postgres psql -tc "SELECT 1 FROM pg_roles WHERE rolname='$(DB_USER)'" | grep -q 1 || sudo -u postgres psql -c "CREATE USER $(DB_USER) WITH ENCRYPTED PASSWORD '$(DB_PASSWORD)';"
	sudo -u postgres psql -c "ALTER USER $(DB_USER) WITH CREATEDB;"
	sudo -u postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='$(DB_NAME)'" | grep -q 1 || sudo -u postgres psql -c "CREATE DATABASE $(DB_NAME) OWNER $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON DATABASE $(DB_NAME) TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $(DB_USER);"
	@echo "Database setup completed!"

# Drop Database and User (Linux)
db-drop-linux:
	@echo "Dropping database and user..."
	sudo -u postgres psql -c "DROP DATABASE IF EXISTS $(DB_NAME);"
	sudo -u postgres psql -c "DROP USER IF EXISTS $(DB_USER);"
	@echo "Database and user dropped!"

# Setup Database and User (Windows)
db-setup-windows:
	@echo "Setting up database and user for Windows..."
	psql -tc "SELECT 1 FROM pg_roles WHERE rolname='$(DB_USER)'" | grep -q 1 || psql -c "CREATE USER $(DB_USER) WITH ENCRYPTED PASSWORD '$(DB_PASSWORD)';"
	psql -c "ALTER USER $(DB_USER) WITH CREATEDB;"
	psql -tc "SELECT 1 FROM pg_database WHERE datname='$(DB_NAME)'" | grep -q 1 || psql -c "CREATE DATABASE $(DB_NAME) OWNER $(DB_USER);"
	psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON DATABASE $(DB_NAME) TO $(DB_USER);"
	psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $(DB_USER);"
	psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $(DB_USER);"
	psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $(DB_USER);"
	psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $(DB_USER);"
	@echo "Database setup completed!"

# Drop Database and User (Windows)
db-drop-windows:
	@echo "Dropping database and user..."
	psql -c "DROP DATABASE IF EXISTS $(DB_NAME);"
	psql -c "DROP USER IF EXISTS $(DB_USER);"
	@echo "Database and user dropped!"

# Migrate Database
db-migrate:
	@echo "Running Prisma migrations..."
	npx prisma migrate dev --name init
	@echo "Migrations applied!"

# GitHub Login
git-login:
	@echo "Logging into GitHub..."
	gh auth login --web
	@echo "GitHub login successful!"

# GitHub Initialize Repository
git-init:
	@if [ ! -d .git ]; then \
		echo "Initializing Git repository..."; \
		git init; \
	fi

# GitHub Connect to Remote Repository
git-connect: git-init
	@read -p "Enter the repository owner: " REPO_OWNER; \
	read -p "Enter the repository name: " REPO_NAME; \
	REPO_URL="https://github.com/$${REPO_OWNER}/$${REPO_NAME}.git"; \
	if git remote | grep -q origin; then \
		echo "Remote 'origin' already exists. Updating..."; \
		git remote set-url origin $${REPO_URL}; \
	else \
		git remote add origin $${REPO_URL}; \
	fi; \
	echo "Connected to remote: $${REPO_URL}"

# GitHub Pull Latest Changes (detects main/master branch)
git-pull:
	@echo "Pulling latest changes..."
	@if git branch -r | grep -q "origin/main"; then \
		git pull origin main; \
	elif git branch -r | grep -q "origin/master"; then \
		git pull origin master; \
	else \
		echo "No remote branch found (main or master)"; \
	fi
	@echo "Pull completed!"

# GitHub Pull Latest Changes for dev Branch
git-pull-dev:
	@echo "Pulling latest changes for the dev branch..."
	@if git branch -r | grep -q "origin/dev"; then \
		git pull --rebase origin dev; \
	else \
		echo "No remote branch found (dev)"; \
	fi
	@echo "Pull completed!"



# GitHub Push Changes (handles non-existent remote branch)
git-push:
	@echo "Pushing changes..."
	@read -p "Enter the branch name to push: " BRANCH_NAME; \
	if git branch -r | grep -q "origin/$$BRANCH_NAME"; then \
		git pull origin $$BRANCH_NAME; \
		git add .; \
		git commit -m "Auto commit" || echo "No changes to commit"; \
		git push origin $$BRANCH_NAME --force; \
		echo "Push completed!"; \
	else \
		echo "Branch '$$BRANCH_NAME' does not exist on remote. Creating and pushing..."; \
		git push -u origin $$BRANCH_NAME; \
		echo "Branch '$$BRANCH_NAME' created and pushed to remote."; \
	fi

# Create a New Git Branch
git-create-branch:
	@echo "Creating a new branch..."
	@read -p "Enter new branch name: " BRANCH_NAME; \
	if git show-ref --verify --quiet "refs/heads/$$BRANCH_NAME"; then \
		echo "Branch '$$BRANCH_NAME' already exists locally."; exit 1; \
	else \
		git checkout -b "$$BRANCH_NAME"; \
		echo "Created branch: $$BRANCH_NAME"; \
	fi