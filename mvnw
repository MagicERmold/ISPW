#!/usr/bin/env sh
set -e

BASEDIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_PROPERTIES="$BASEDIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_VERSION="3.9.9"
MAVEN_DIST="${BASEDIR}/.mvn/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_ZIP="${MAVEN_DIST}/apache-maven-${MAVEN_VERSION}-bin.zip"
MAVEN_HOME="${MAVEN_DIST}/apache-maven-${MAVEN_VERSION}"
MAVEN_CMD="${MAVEN_HOME}/bin/mvn"

if [ ! -f "$WRAPPER_PROPERTIES" ]; then
  echo "Missing Maven wrapper properties: $WRAPPER_PROPERTIES" >&2
  exit 1
fi

if [ ! -x "$MAVEN_CMD" ]; then
  mkdir -p "$MAVEN_DIST"
  if [ ! -f "$MAVEN_ZIP" ]; then
    echo "Downloading Apache Maven ${MAVEN_VERSION}..."
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.zip" -o "$MAVEN_ZIP"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.zip" -O "$MAVEN_ZIP"
    else
      echo "Neither curl nor wget is available to download Maven." >&2
      exit 1
    fi
  fi
  echo "Unpacking Apache Maven ${MAVEN_VERSION}..."
  unzip -q -o "$MAVEN_ZIP" -d "$MAVEN_DIST"
fi

exec "$MAVEN_CMD" "$@"
