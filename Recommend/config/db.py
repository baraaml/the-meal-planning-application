"""
Database connection handling with connection pooling and transaction management.
"""
import logging
from typing import Any, Dict, Optional, List
from contextlib import contextmanager
import psycopg2
import psycopg2.extras
from psycopg2.pool import ThreadedConnectionPool
from config.config import DATABASE_URL

logger = logging.getLogger(__name__)

# Global connection pool
pool = None

def initialize_pool(min_conn=5, max_conn=20):
    """Initialize the connection pool."""
    global pool
    try:
        pool = ThreadedConnectionPool(
            min_conn, 
            max_conn, 
            DATABASE_URL,
            cursor_factory=psycopg2.extras.RealDictCursor
        )
        logger.info(f"Connection pool initialized with {min_conn}-{max_conn} connections")
    except Exception as e:
        logger.error(f"Failed to initialize connection pool: {e}")
        raise

@contextmanager
def get_connection():
    """Get a connection from the pool."""
    global pool
    if pool is None:
        initialize_pool()
    
    conn = None
    try:
        conn = pool.getconn()
        yield conn
    finally:
        if conn:
            pool.putconn(conn)

@contextmanager
def get_cursor():
    """Get a cursor using a connection from the pool."""
    with get_connection() as conn:
        cursor = conn.cursor()
        try:
            yield cursor
            conn.commit()
        except Exception as e:
            conn.rollback()
            logger.error(f"Database error: {e}")
            raise
        finally:
            cursor.close()

def execute_query(query: str, params: Optional[Dict[str, Any]] = None) -> List[Dict[str, Any]]:
    """Execute a query and return all results."""
    with get_cursor() as cursor:
        cursor.execute(query, params or {})
        if cursor.description:
            return cursor.fetchall()
        return []

def execute_query_single(query: str, params: Optional[Dict[str, Any]] = None) -> Optional[Dict[str, Any]]:
    """Execute a query and return a single result."""
    with get_cursor() as cursor:
        cursor.execute(query, params or {})
        if cursor.description:
            result = cursor.fetchone()
            return result
        return None

def execute_transaction(queries_params: List[tuple]):
    """
    Execute multiple queries in a transaction.
    
    Args:
        queries_params: List of (query, params) tuples to execute
    
    Returns:
        True if successful, False otherwise
    """
    with get_connection() as conn:
        try:
            with conn.cursor() as cursor:
                for query, params in queries_params:
                    cursor.execute(query, params or {})
            conn.commit()
            return True
        except Exception as e:
            conn.rollback()
            logger.error(f"Transaction error: {e}")
            raise

def check_connection() -> bool:
    """Test database connection."""
    try:
        with get_cursor() as cursor:
            cursor.execute("SELECT 1")
            return True
    except Exception as e:
        logger.error(f"Connection check failed: {e}")
        return False