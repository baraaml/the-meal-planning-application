import kagglehub
import os

# Download the dataset
path = kagglehub.dataset_download("kriishukla/recipe-db")

# Access meals.csv
file_path = os.path.join(path, "meals.csv")

print("Dataset Path:", path)
print("Meals File Path:", file_path)
