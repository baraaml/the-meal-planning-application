"""
Database connection module.
Provides database connection pool and transaction management.
"""
from sqlalchemy import create_engine, text
from contextlib import contextmanager
from typing import Generator

from config.settings import DATABASE_URL

# Create database engine with connection pooling
engine = create_engine(DATABASE_URL, pool_size=10, max_overflow=20)

@contextmanager
def get_connection():
    """Get a database connection from the pool."""
    connection = engine.connect()
    try:
        yield connection
    finally:
        connection.close()

@contextmanager
def get_transaction():
    """Get a database connection with transaction."""
    connection = engine.connect()
    transaction = connection.begin()
    try:
        yield connection
        transaction.commit()
    except Exception:
        transaction.rollback()
        raise
    finally:
        connection.close()

def execute_query(query, params=None, is_transaction=False):
    """Execute a database query with parameters."""
    manager = get_transaction if is_transaction else get_connection
    
    with manager() as conn:
        result = conn.execute(text(query), params or {})
        return result

def test_connection():
    """Test the database connection."""
    try:
        with get_connection() as conn:
            result = conn.execute(text("SELECT 1"))
            print("Database connection successful!")
            return True
    except Exception as e:
        print(f"Database connection failed: {e}")
        return False