#!/usr/bin/env sh
set -e

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$ROOT_DIR"

if [ ! -x "$ROOT_DIR/mvnw" ]; then
  chmod +x "$ROOT_DIR/mvnw" 2>/dev/null || true
fi

exec "$ROOT_DIR/mvnw" -q -f "$ROOT_DIR/StockTrack/pom.xml" javafx:run
