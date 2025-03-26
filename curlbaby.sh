#!/bin/bash
mkdir -p curlbaby/target/classes
javac -cp "curlbaby/lib/*" -d curlbaby/target/classes curlbaby/src/main/java/com/curlbaby/CurlBabyApp.java
if [ $? -eq 0 ]; then
  java -cp "curlbaby/target/classes:curlbaby/lib/*" com.curlbaby.CurlBabyApp "$@"
else
  echo "Compilation failed. Please fix the errors."
fi
