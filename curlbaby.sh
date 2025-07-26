#!/bin/bash
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

mkdir -p curlbaby/target/classes
mkdir -p curlbaby/lib

# Check Java installation
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java to run cUrlBaby"
    exit 1
fi

# Required JARs
REQUIRED_JARS=("json-simple-1.1.1.jar" "jfiglet-0.0.8.jar")
BACKUP_PATHS=("backup/client/lib" "../backup/client/lib" "lib")

# Auto-copy missing JARs
for JAR in "${REQUIRED_JARS[@]}"; do
    if [ ! -f "curlbaby/lib/$JAR" ]; then
        for backup_path in "${BACKUP_PATHS[@]}"; do
            if [ -f "$backup_path/$JAR" ]; then
                echo "Copying $JAR from $backup_path"
                cp "$backup_path/$JAR" curlbaby/lib/
                break
            fi
        done

        if [ ! -f "curlbaby/lib/$JAR" ]; then
            echo "Error: $JAR not found. Cannot proceed without it."
            exit 1
        fi
    fi
done

# Find Java source files
JAVA_FILES=$(find com/curlbaby -name "*.java" 2>/dev/null)

if [ -z "$JAVA_FILES" ]; then
    echo "Error: No Java source files found in com/curlbaby directory"
    exit 1
fi

# Determine classpath separator based on OS
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" || "$OSTYPE" == "cygwin" ]]; then
    CP_SEP=";"
else
    CP_SEP=":"
fi

# Compile
echo "Compiling cUrlBaby application..."
javac -cp "curlbaby/lib/*" -d curlbaby/target/classes $JAVA_FILES

# Run
echo "Compilation successful. Starting cUrlBaby application..."
echo ""
java -Djava.awt.headless=true -cp "curlbaby/target/classes${CP_SEP}curlbaby/lib/*" com.curlbaby.CurlBabyApp "$@"
