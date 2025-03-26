 
mkdir -p curlbaby/target/classes
 
echo "Compiling..."
javac -d curlbaby/target/classes curlbaby/src/main/java/com/curlbaby/CurlBabyApp.java
 
if [ $? -eq 0 ]; then
  echo "Compilation successful. Starting Curl Baby..."
  java -cp "curlbaby/target/classes" com.curlbaby.CurlBabyApp "$@"
else
  echo "Compilation failed. Please fix the errors."
fi