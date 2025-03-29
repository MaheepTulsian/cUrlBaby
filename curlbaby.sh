 
mkdir -p curlbaby/target/classes
mkdir -p curlbaby/lib
 
if [ ! -f "curlbaby/lib/json-simple-1.1.1.jar" ] && [ -f "backup/client/lib/json-simple-1.1.1.jar" ]; then
  cp backup/client/lib/json-simple-1.1.1.jar curlbaby/lib/
fi
 
echo "Compiling CurlBaby application..."
javac -cp "curlbaby/lib/*" -d curlbaby/target/classes com/curlbaby/*.java
 
if [ $? -eq 0 ]; then
  echo "Compilation successful. Starting CurlBaby application..."
  echo ""
  java -cp "curlbaby/target/classes:curlbaby/lib/*" com.curlbaby.CurlBabyApp "$@"
else
  echo "Compilation failed. Please fix the errors."
  exit 1
fi
