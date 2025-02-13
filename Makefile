# PostgreSQL Configuration
DB_USER=mealflow
DB_PASSWORD=mealflow
DB_NAME=mealflow
DB_HOST=localhost
DB_PORT=5432

# Create PostgreSQL User and Database
.PHONY: db-setup-linux db-drop-linux db-setup-windows db-drop-windows db-migrate git-login git-pull git-push git-connect git-init git-branch git-push-branch

# Setup Database and User (Linux)
db-setup-linux:
	sudo -u postgres psql -c "CREATE USER $(DB_USER) WITH ENCRYPTED PASSWORD '$(DB_PASSWORD)';"
	sudo -u postgres psql -c "ALTER USER $(DB_USER) WITH CREATEDB;"
	sudo -u postgres psql -c "CREATE DATABASE $(DB_NAME) OWNER $(DB_USER);"
	sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE $(DB_NAME) TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON SCHEMA public TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $(DB_USER);"
	sudo -u postgres psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $(DB_USER);"

# Drop Database and User (Linux)
db-drop-linux:
	sudo -u postgres psql -c "DROP DATABASE IF EXISTS $(DB_NAME);"
	sudo -u postgres psql -c "DROP USER IF EXISTS $(DB_USER);"

# Setup Database and User (Windows)
db-setup-windows:
	psql -c "CREATE USER $(DB_USER) WITH ENCRYPTED PASSWORD '$(DB_PASSWORD)';"
	psql -c "ALTER USER $(DB_USER) WITH CREATEDB;"
	psql -c "CREATE DATABASE $(DB_NAME) OWNER $(DB_USER);"
	psql -c "GRANT ALL PRIVILEGES ON DATABASE $(DB_NAME) TO $(DB_USER);"
	psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $(DB_USER);"
	psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $(DB_USER);"
	psql -d $(DB_NAME) -c "GRANT ALL PRIVILEGES ON SCHEMA public TO $(DB_USER);"
	psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $(DB_USER);"
	psql -d $(DB_NAME) -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $(DB_USER);"

# Drop Database and User (Windows)
db-drop-windows:
	psql -c "DROP DATABASE IF EXISTS $(DB_NAME);"
	psql -c "DROP USER IF EXISTS $(DB_USER);"

# Migrate Database
db-migrate:
	npx prisma migrate dev --name init

# GitHub Login
git-login:
	gh auth login --web

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
		git remote remove origin; \
	fi; \
	git remote add origin $${REPO_URL}; \
	echo "Connected to remote: $${REPO_URL}"

# GitHub Pull Latest Changes (detects main/master branch)
git-pull:
	@if git branch -r | grep -q "origin/main"; then \
		git pull origin main; \
	elif git branch -r | grep -q "origin/master"; then \
		git pull origin master; \
	else \
		echo "No remote branch found (main or master)"; \
	fi

# GitHub Push Changes (detects main/master branch)
git-push:
	@if git branch -r | grep -q "origin/main"; then \
		BRANCH=main; \
	elif git branch -r | grep -q "origin/master"; then \
		BRANCH=master; \
	else \
		echo "No remote branch found (main or master)"; exit 1; \
	fi; \
	git add .; \
	git commit -m "Auto commit" || echo "No changes to commit"; \
	git push origin $${BRANCH}

# Create a New Git Branch
git-branch:
	@read -p "Enter new branch name: " BRANCH_NAME; \
	git checkout -b "$$BRANCH_NAME"; \
	git add .; \
	git commit -m "Initial commit on $$BRANCH_NAME"; \
	git push -u origin "$$BRANCH_NAME"; \
	echo "Pushed branch: $$BRANCH_NAME"

git-push-branch:
	@read -p "Enter the branch name to push: " BRANCH_NAME; \
	git push origin "$$BRANCH_NAME"; \
	echo "Pushed branch: $$BRANCH_NAME"