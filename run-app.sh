#!/usr/bin/env bash

#
# Cross-platform command line runner for the Credit Suisse Trial app
# Compatible with: Windows Git Bash, WSL Ubuntu, Pop OS
#

# Detect platform
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    PLATFORM="windows"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    PLATFORM="linux"
else
    PLATFORM="unix"
fi

# Initialize SDKMAN with cross-platform paths
if [[ "$PLATFORM" == "windows" ]]; then
    export SDKMAN_DIR="$USERPROFILE/.sdkman"
else
    export SDKMAN_DIR="$HOME/.sdkman"
fi

[[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"

# Use Java 24 if available, fallback to system Java
if command -v sdk &> /dev/null; then
    sdk use java 24.fx-zulu 2>/dev/null || echo "Java 24 not found, using system Java"
else
    echo "SDKMAN not found, using system Java"
fi

function cleanup() {
    if [ ! -z "$SERVER_PID" ]; then
        echo "Stopping server (PID: $SERVER_PID)..."
        kill -9 ${SERVER_PID} 2>/dev/null
    fi
}

trap cleanup EXIT

echo "Building and testing..."
mvn compile test -q

echo "Starting Spring Boot server..."
mvn spring-boot:run -q & SERVER_PID=$!

echo "Waiting for server to start..."
WAIT_COUNT=0

# Cross-platform server check
check_server() {
    if command -v curl &> /dev/null; then
        curl -s "http://localhost:8080/" > /dev/null 2>&1
    elif command -v wget &> /dev/null; then
        wget -q --spider "http://localhost:8080/" 2>/dev/null
    else
        # Fallback using netcat if available
        if command -v nc &> /dev/null; then
            nc -z localhost 8080 2>/dev/null
        else
            return 1
        fi
    fi
}

while ! check_server; do
    if [ $WAIT_COUNT -eq 0 ]; then
        echo -n "Starting"
    fi
    echo -n "."
    sleep 3
    WAIT_COUNT=$((WAIT_COUNT + 1))
    if [ $WAIT_COUNT -gt 20 ]; then
        echo ""
        echo "Server failed to start after 60 seconds. Exiting."
        exit 1
    fi
done

echo ""
echo "Server started! Application is running at: http://localhost:8080/"
echo "Press Ctrl+C to stop the server"
wait $SERVER_PID