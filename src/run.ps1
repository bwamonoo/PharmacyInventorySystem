Write-Host "[INFO] Cleaning old .class files..."
Get-ChildItem -Recurse -Filter *.class | Remove-Item -Force -ErrorAction SilentlyContinue

Write-Host "[INFO] Finding Java source files..."
$sources = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if ($sources.Count -eq 0) {
  Write-Host "[ERROR] No Java source files found."
  exit 1
}

Write-Host "[INFO] Compiling Java files..."
javac $sources

if ($LASTEXITCODE -eq 0) {
  Write-Host "[SUCCESS] Compilation successful."
  Write-Host "[INFO] Launching the application..."
  java cli.Main
}
else {
  Write-Host "[ERROR] Compilation failed. Fix the errors above."
}
