"""
Background task scheduler for meal recommendation service.
Manages periodic tasks for embedding generation and other maintenance operations.
"""
import schedule
import time
import threading
import logging
import signal
import sys
from typing import Callable, Dict, List
from datetime import datetime, timedelta

from embeddings.generator import EmbeddingGenerator
from config import (
    EMBEDDING_GENERATION_INTERVAL,
    SCHEDULER_SLEEP_INTERVAL,
    LOG_LEVEL,
    LOG_FORMAT
)

logger = logging.getLogger(__name__)

# Track running tasks
_running_tasks: Dict[str, bool] = {}
_task_last_run: Dict[str, datetime] = {}
_task_durations: Dict[str, List[float]] = {}
_scheduler_thread: threading.Thread = None
_stop_event = threading.Event()

def timed_task(task_name: str) -> Callable:
    """
    Decorator to time tasks and track their execution.
    
    Args:
        task_name: Name of the task to track
        
    Returns:
        Decorated function
    """
    def decorator(func: Callable) -> Callable:
        def wrapper(*args, **kwargs):
            # Mark task as running
            _running_tasks[task_name] = True
            _task_last_run[task_name] = datetime.now()
            
            start_time = time.time()
            logger.info(f"Starting task: {task_name}")
            
            try:
                result = func(*args, **kwargs)
                end_time = time.time()
                duration = end_time - start_time
                
                # Track task duration
                if task_name not in _task_durations:
                    _task_durations[task_name] = []
                
                _task_durations[task_name].append(duration)
                
                # Keep only the last 10 durations for average calculation
                if len(_task_durations[task_name]) > 10:
                    _task_durations[task_name] = _task_durations[task_name][-10:]
                
                avg_duration = sum(_task_durations[task_name]) / len(_task_durations[task_name])
                
                logger.info(f"Task {task_name} completed in {duration:.2f}s (avg: {avg_duration:.2f}s)")
                return result
            except Exception as e:
                logger.error(f"Error in task {task_name}: {e}")
                raise
            finally:
                # Mark task as not running
                _running_tasks[task_name] = False
        return wrapper
    return decorator

@timed_task("embedding_generation")
def run_embedding_generation():
    """
    Generate embeddings for meals and recipes that don't have them yet.
    This task runs periodically to ensure all content has embeddings.
    """
    try:
        generator = EmbeddingGenerator()
        result = generator.generate_all_embeddings()
        
        meals_count = result.get('meals', 0)
        recipes_count = result.get('recipes', 0)
        total_count = meals_count + recipes_count
        
        if total_count > 0:
            logger.info(f"Generated embeddings for {meals_count} meals and {recipes_count} recipes")
        else:
            logger.info("No new content requiring embeddings")
        
        return result
    except Exception as e:
        logger.error(f"Error during embedding generation: {e}")
        return {"error": str(e)}

def get_task_status() -> Dict:
    """
    Get the status of scheduled tasks.
    
    Returns:
        Dictionary with task status information
    """
    status = {}
    
    for task_name in _task_last_run:
        status[task_name] = {
            "running": _running_tasks.get(task_name, False),
            "last_run": _task_last_run[task_name].isoformat() if task_name in _task_last_run else None,
            "average_duration": None
        }
        
        if task_name in _task_durations and _task_durations[task_name]:
            avg_duration = sum(_task_durations[task_name]) / len(_task_durations[task_name])
            status[task_name]["average_duration"] = f"{avg_duration:.2f}s"
            
        if task_name in _task_last_run:
            time_since_last_run = datetime.now() - _task_last_run[task_name]
            status[task_name]["time_since_last_run"] = f"{time_since_last_run.total_seconds():.0f}s"
    
    return status

def run_scheduler():
    """
    Run the scheduler loop to execute pending tasks.
    This function runs in a separate thread and periodically checks
    for tasks that need to be executed.
    """
    while not _stop_event.is_set():
        schedule.run_pending()
        time.sleep(SCHEDULER_SLEEP_INTERVAL)

def stop_scheduler():
    """Stop the scheduler thread."""
    logger.info("Stopping scheduler...")
    _stop_event.set()
    
    if _scheduler_thread and _scheduler_thread.is_alive():
        _scheduler_thread.join(timeout=5)
        
    logger.info("Scheduler stopped")

def handle_exit_signal(signum, frame):
    """Handle exit signals to gracefully stop the scheduler."""
    logger.info(f"Received signal {signum}, shutting down gracefully...")
    stop_scheduler()
    sys.exit(0)

def start_scheduler():
    """
    Configure and start the scheduler in a background thread.
    Sets up signal handlers for graceful shutdown.
    
    Returns:
        The scheduler thread
    """
    global _scheduler_thread
    
    # Register signal handlers for graceful shutdown
    signal.signal(signal.SIGINT, handle_exit_signal)
    signal.signal(signal.SIGTERM, handle_exit_signal)
    
    # Clear any existing schedules
    schedule.clear()
    
    # Schedule embedding generation job
    schedule.every(EMBEDDING_GENERATION_INTERVAL).minutes.do(run_embedding_generation)
    logger.info(f"Scheduled embedding generation every {EMBEDDING_GENERATION_INTERVAL} minutes")
    
    # Initialize tracking dictionaries
    _running_tasks["embedding_generation"] = False
    _task_last_run["embedding_generation"] = datetime.now() - timedelta(days=1)  # Set to past time
    
    # Reset stop event
    _stop_event.clear()
    
    # Start scheduler in background thread
    _scheduler_thread = threading.Thread(target=run_scheduler, name="SchedulerThread")
    _scheduler_thread.daemon = True
    _scheduler_thread.start()
    
    logger.info("Scheduler started in background thread")
    return _scheduler_thread

if __name__ == "__main__":
    # Configure logging
    logging.basicConfig(
        level=getattr(logging, LOG_LEVEL),
        format=LOG_FORMAT
    )
    
    # Run immediately on first execution
    logger.info("Running initial embedding generation")
    run_embedding_generation()
    
    # Start scheduler
    thread = start_scheduler()
    
    logger.info("Scheduler running. Press Ctrl+C to exit.")
    try:
        # Keep the main thread alive
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        stop_scheduler()
        logger.info("Scheduler stopped.")