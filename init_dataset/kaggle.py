import kagglehub

# Download latest version
path = kagglehub.dataset_download("kriishukla/recipe-db")

print("Path to dataset files:", path)