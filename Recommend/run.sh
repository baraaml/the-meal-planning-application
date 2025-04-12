#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
    set -a
    # shellcheck disable=SC1091
    source .env
    set +a
fi

# Determine reload flag
if [ "$RELOAD" = "True" ]; then
    RELOAD_FLAG="--reload"
else
    RELOAD_FLAG=""
fi

# Run the uvicorn server
./venv/bin/python -m uvicorn main:app \
    --host "$API_HOST" \
    --port "$API_PORT" \
    $RELOAD_FLAG