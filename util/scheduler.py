"""
Background task scheduler.
Runs periodic tasks for the recommendation system.
"""
import schedule
import time
import threading
import logging

from embeddings.embedding_generator import EmbeddingGenerator
from config.settings import EMBEDDING_GENERATION_INTERVAL, SCHEDULER_SLEEP_INTERVAL

logger = logging.getLogger(__name__)

def run_embedding_generation():
    """Generate embeddings for content that doesn't have them yet."""
    logger.info("Running scheduled embedding generation...")
    try:
        generator = EmbeddingGenerator()
        result = generator.generate_all_embeddings()
        logger.info(f"Embedding generation completed: {result}")
    except Exception as e:
        logger.error(f"Error during embedding generation: {e}")

def run_scheduler():
    """Run the scheduler loop to execute pending tasks."""
    while True:
        schedule.run_pending()
        time.sleep(SCHEDULER_SLEEP_INTERVAL)

def start_scheduler():
    """Configure and start the scheduler in a background thread."""
    # Schedule embedding generation job
    schedule.every(EMBEDDING_GENERATION_INTERVAL).minutes.do(run_embedding_generation)
    
    logger.info(f"Scheduled embedding generation every {EMBEDDING_GENERATION_INTERVAL} minutes")
    
    # Start scheduler in background thread
    scheduler_thread = threading.Thread(target=run_scheduler)
    scheduler_thread.daemon = True
    scheduler_thread.start()
    logger.info("Scheduler started in background thread")
    
    return scheduler_thread

if __name__ == "__main__":
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    # Run immediately on first execution
    run_embedding_generation()
    
    # Start scheduler
    thread = start_scheduler()
    
    logger.info("Scheduler running. Press Ctrl+C to exit.")
    try:
        # Keep the main thread alive
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        logger.info("Scheduler stopped.")