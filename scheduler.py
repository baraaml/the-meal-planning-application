# scheduler.py
import schedule
import time
import subprocess
import threading

def run_embedding_generation():
    print("Running embedding generation...")
    subprocess.run(["python", "embedding_generator.py"])

# Schedule jobs
schedule.every(1).hours.do(run_embedding_generation)

def run_scheduler():
    while True:
        schedule.run_pending()
        time.sleep(60)

if __name__ == "__main__":
    # Run immediately on first execution
    run_embedding_generation()
    
    # Start scheduler in background thread
    scheduler_thread = threading.Thread(target=run_scheduler)
    scheduler_thread.daemon = True
    scheduler_thread.start()
    
    print("Scheduler started. Press Ctrl+C to exit.")
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("Scheduler stopped.")