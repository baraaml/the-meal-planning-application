# db_utils.py
import os
import sqlalchemy
from sqlalchemy import create_engine
from dotenv import load_dotenv

# Load environment variables from your existing Node.js .env file
load_dotenv()

def get_db_engine():
    db_url = os.getenv("DATABASE_URL")
    engine = create_engine(db_url)
    return engine

def test_connection():
    engine = get_db_engine()
    try:
        with engine.connect() as conn:
            result = conn.execute(sqlalchemy.text("SELECT 1"))
            print("Database connection successful!")
            return True
    except Exception as e:
        print(f"Database connection failed: {e}")
        return False

if __name__ == "__main__":
    test_connection()