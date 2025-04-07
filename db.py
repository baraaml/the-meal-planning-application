"""
Database connection module.
Provides database connection pool and transaction management.
"""
from sqlalchemy import create_engine, text
from contextlib import contextmanager
from typing import Generator
import logging

from config import DATABASE_URL

logger = logging.getLogger(__name__)

# Create database engine with connection pooling
engine = create_engine(
    DATABASE_URL, 
    pool_size=10, 
    max_overflow=20,
    pool_pre_ping=True,  # Check connection validity before using
    pool_recycle=300,    # Recycle connections after 5 minutes
)

@contextmanager
def get_db():
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
    except Exception as e:
        transaction.rollback()
        logger.error(f"Transaction failed: {e}")
        raise
    finally:
        connection.close()

def execute_query(query, params=None, is_transaction=False):
    """
    Execute a database query with parameters.
    
    Args:
        query: SQL query text
        params: Query parameters
        is_transaction: Whether to execute within a transaction
        
    Returns:
        Query result
    """
    manager = get_transaction if is_transaction else get_db
    
    with manager() as conn:
        result = conn.execute(text(query), params or {})
        return result

def test_connection():
    """
    Test the database connection.
    
    Returns:
        Boolean indicating connection success
    """
    try:
        with get_db() as conn:
            result = conn.execute(text("SELECT 1"))
            logger.info("Database connection successful!")
            return True
    except Exception as e:
        logger.error(f"Database connection failed: {e}")
        return False