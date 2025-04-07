"""
API middleware for the meal recommendation service.
Includes CORS, request timing, and error handling middleware.
"""
from fastapi import Request, Response
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
import time
import json
import logging
from typing import Callable

logger = logging.getLogger(__name__)

def setup_middleware(app):
    """
    Set up middleware for the FastAPI application.
    
    Args:
        app: The FastAPI application
    """
    # Add CORS middleware
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # Replace with specific origins in production
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    
    # Add GZip compression middleware
    app.add_middleware(GZipMiddleware, minimum_size=1000)
    
    # Add request timing middleware
    @app.middleware("http")
    async def add_process_time_header(request: Request, call_next: Callable):
        start_time = time.time()
        
        # Add request ID for tracking
        request_id = f"req-{int(start_time * 1000)}"
        request.state.request_id = request_id
        
        # Process the request
        try:
            response = await call_next(request)
            process_time = time.time() - start_time
            
            # Add timing headers
            response.headers["X-Process-Time"] = str(round(process_time * 1000, 2))
            response.headers["X-Request-ID"] = request_id
            
            # Add timing to JSON response
            if (
                response.headers.get("content-type") == "application/json" 
                and isinstance(response, JSONResponse)
            ):
                content = response.body.decode()
                try:
                    data = json.loads(content)
                    if isinstance(data, dict):
                        data["execution_time_ms"] = round(process_time * 1000, 2)
                        data["request_id"] = request_id
                        response.body = json.dumps(data).encode()
                except Exception as e:
                    logger.error(f"Error updating response: {e}")
            
            # Log request completion
            logger.info(
                f"Request {request_id} completed: {request.method} {request.url.path} "
                f"- {response.status_code} in {process_time:.3f}s"
            )
            
            return response
            
        except Exception as e:
            # Log any unhandled exceptions
            process_time = time.time() - start_time
            logger.error(
                f"Request {request_id} error: {request.method} {request.url.path} "
                f"- {str(e)} in {process_time:.3f}s"
            )
            
            # Create error response
            error_response = JSONResponse(
                status_code=500,
                content={
                    "detail": "Internal server error",
                    "request_id": request_id,
                    "execution_time_ms": round(process_time * 1000, 2)
                }
            )
            return error_response
    
    # Add error logging middleware
    @app.middleware("http")
    async def log_errors(request: Request, call_next: Callable):
        try:
            return await call_next(request)
        except Exception as e:
            # Log the error
            logger.exception(f"Unhandled exception: {str(e)}")
            
            # Return a consistent error response
            return JSONResponse(
                status_code=500,
                content={
                    "detail": "Internal server error",
                    "type": type(e).__name__,
                    "request_id": getattr(request.state, "request_id", "unknown")
                }
            )