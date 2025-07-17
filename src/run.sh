#!/bin/bash

echo "🧹 Cleaning previous .class files..."
find . -name "*.class" -delete

echo "🔄 Compiling Java files..."
find . -name "*.java" > sources.txt
javac @sources.txt

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful."
    echo "🚀 Launching the application..."
    java cli.Main
else
    echo "❌ Compilation failed. Fix the errors above."
fi
