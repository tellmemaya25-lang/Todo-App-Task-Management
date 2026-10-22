#!/bin/bash
# Fix missing gradle-wrapper.jar
set -e
WRAPPER_URL="https://github.com/gradle/gradle/raw/v8.11.1/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PATH="gradle/wrapper/gradle-wrapper.jar"

echo "Downloading gradle-wrapper.jar..."
mkdir -p gradle/wrapper

if command -v curl >/dev/null 2>&1; then
    curl -L -o "$WRAPPER_PATH" "$WRAPPER_URL"
elif command -v wget >/dev/null 2>&1; then
    wget -O "$WRAPPER_PATH" "$WRAPPER_URL"
elif command -v gradle >/dev/null 2>&1; then
    echo "curl/wget not found, using 'gradle wrapper'"
    gradle wrapper --gradle-version 8.11.1
else
    echo "Please download manually from $WRAPPER_URL to $WRAPPER_PATH"
    exit 1
fi

echo "Done: $WRAPPER_PATH ($(wc -c < "$WRAPPER_PATH") bytes)"
echo "Now try: ./gradlew assembleDebug"
