#!/bin/sh
set -e
BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
WRAPPER_PROPS="$WRAPPER_DIR/maven-wrapper.properties"
if [ ! -f "$WRAPPER_JAR" ]; then
  if command -v curl >/dev/null 2>&1; then
    URL=$(grep '^wrapperUrl=' "$WRAPPER_PROPS" | cut -d'=' -f2-)
    curl -fsSL "$URL" -o "$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    URL=$(grep '^wrapperUrl=' "$WRAPPER_PROPS" | cut -d'=' -f2-)
    wget -q "$URL" -O "$WRAPPER_JAR"
  else
    echo "curl or wget is required to download maven-wrapper.jar" >&2
    exit 1
  fi
fi
exec java -classpath "$WRAPPER_JAR" -Dmaven.multiModuleProjectDirectory="$BASE_DIR" org.apache.maven.wrapper.MavenWrapperMain "$@"
