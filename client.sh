mkdir -p client/target/classes

echo "Compiling..."
javac -d client/target/classes client/src/main/java/com/client/CLIentApp.java

if [ $? -eq 0 ]; then
  echo "Compilation successful. Starting SimpleHTTP CLI..."
  java -cp "client/target/classes" com.client.CLIentApp "$@"
else
  echo "Compilation failed. Please fix the errors."
fi