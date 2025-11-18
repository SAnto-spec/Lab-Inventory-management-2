<#
PowerShell helper to check Java and Maven, then run the JavaFX app.

Usage:
  Open PowerShell at the project root and run:
    powershell -ExecutionPolicy Bypass -File .\scripts\run-windows.ps1

Notes:
- This script does not install software automatically. If Java or Maven are missing
  it prints steps to install them on Windows.
#>

Write-Host "Checking Java (JDK) ..."
try {
    $javaVersion = & java -version 2>&1
    if ($LASTEXITCODE -ne 0) { throw "java-not-found" }
    Write-Host $javaVersion
} catch {
    Write-Host "\nJava (JDK) is not found in PATH. Please install a JDK 17+ and ensure 'java' is on PATH." -ForegroundColor Yellow
    Write-Host "Recommended options:" -ForegroundColor Yellow
    Write-Host " - Install Temurin (Adoptium): https://adoptium.net/" -ForegroundColor Yellow
    Write-Host " - Or use the Microsoft or Oracle JDK installers for Windows." -ForegroundColor Yellow
    Write-Host "After installing, re-open PowerShell and re-run this script." -ForegroundColor Yellow
    exit 1
}

Write-Host "\nChecking Maven ..."
try {
    $mvnVersion = & mvn -v 2>&1
    if ($LASTEXITCODE -ne 0) { throw "mvn-not-found" }
    Write-Host $mvnVersion
} catch {
    Write-Host "\nMaven is not found in PATH." -ForegroundColor Yellow
    Write-Host "Install options (pick one):" -ForegroundColor Yellow
    Write-Host " 1) Install via Chocolatey (requires admin):" -ForegroundColor Yellow
    Write-Host "      choco install maven" -ForegroundColor Yellow
    Write-Host " 2) Download binary and set PATH: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Write-Host " 3) Use SDKMAN on WSL or a Unix-like shell to manage Java + Maven." -ForegroundColor Yellow
    Write-Host "After installing, re-open PowerShell and re-run this script." -ForegroundColor Yellow
    exit 1
}

Write-Host "\nBuilding and running the JavaFX app (this will stream output)." -ForegroundColor Green

$projectArg = 'javaminiproject'
$cmd = "mvn -f `"$projectArg`" -DskipTests clean compile javafx:run"
Write-Host "Command: $cmd" -ForegroundColor Cyan

try {
    iex $cmd
} catch {
    Write-Host "\nThe command failed. Check the output above for errors." -ForegroundColor Red
    exit 1
}

Write-Host "\nDone." -ForegroundColor Green
