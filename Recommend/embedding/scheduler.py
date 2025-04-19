"""
Background scheduler for automatic embedding generation.
"""
import threading
import time
import logging
from datetime import datetime
from embedding.embeddings import EmbeddingGenerator
from config.config import EMBEDDING_GENERATION_INTERVAL, SCHEDULER_SLEEP_INTERVAL

logger = logging.getLogger(__name__)

def embedding_generation_task():
    """Background task to periodically generate embeddings for new recipes."""
    generator = EmbeddingGenerator()
    last_run_time = datetime.now()
    
    while True:
        try:
            current_time = datetime.now()
            time_since_last_run = (current_time - last_run_time).total_seconds() / 60
            
            # Run if the specified interval has passed
            if time_since_last_run >= EMBEDDING_GENERATION_INTERVAL:
                logger.info("Starting scheduled embedding generation")
                count = generator.generate_all_embeddings(batch_size=50)
                logger.info(f"Scheduled embedding generation completed: {count} embeddings created")
                last_run_time = current_time
            
            # Sleep for the specified interval
            time.sleep(SCHEDULER_SLEEP_INTERVAL)
            
        except Exception as e:
            logger.error(f"Error in embedding generation task: {e}")
            time.sleep(SCHEDULER_SLEEP_INTERVAL)

def start_embedding_scheduler():
    """Start the embedding generation scheduler in a background thread."""
    scheduler_thread = threading.Thread(target=embedding_generation_task, daemon=True)
    scheduler_thread.start()
    logger.info("Embedding generation scheduler started")