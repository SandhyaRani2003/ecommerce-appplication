#!/bin/sh
set -eu

MVNW_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MVNW_WRAPPER_DIR="$MVNW_DIR/.mvn/wrapper"
MVNW_VERSION="3.9.9"
MVNW_DIST_NAME="apache-maven-${MVNW_VERSION}-bin.zip"
MVNW_DIST_URL="https://archive.apache.org/dist/maven/maven-3/${MVNW_VERSION}/binaries/${MVNW_DIST_NAME}"
MVNW_CACHE_DIR="$MVNW_WRAPPER_DIR/dist/apache-maven-${MVNW_VERSION}"
MVNW_ZIP="$MVNW_WRAPPER_DIR/$MVNW_DIST_NAME"
MVNW_MVN="$MVNW_CACHE_DIR/bin/mvn"

if [ -x "$MVNW_MVN" ]; then
  exec "$MVNW_MVN" "$@"
fi

mkdir -p "$MVNW_WRAPPER_DIR/dist"

if [ ! -f "$MVNW_ZIP" ]; then
  echo "Downloading Maven ${MVNW_VERSION}..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$MVNW_DIST_URL" -o "$MVNW_ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$MVNW_ZIP" "$MVNW_DIST_URL"
  else
    echo "curl or wget is required to download Maven." >&2
    exit 1
  fi
fi

if [ ! -x "$MVNW_MVN" ]; then
  echo "Expanding Maven ${MVNW_VERSION}..."
  if command -v unzip >/dev/null 2>&1; then
    unzip -q "$MVNW_ZIP" -d "$MVNW_WRAPPER_DIR/dist"
  else
    echo "unzip is required to expand Maven." >&2
    exit 1
  fi
fi

if [ ! -x "$MVNW_MVN" ]; then
  echo "Failed to prepare Maven wrapper distribution." >&2
  exit 1
fi

exec "$MVNW_MVN" "$@"

