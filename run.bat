@echo off
title AI-Based Duplicate Question Detection System
echo ===================================================================
echo AI-Based Duplicate Question Detection System
echo Starting Application...
echo ===================================================================

set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot
set PATH=%JAVA_HOME%\bin;C:\Users\divya\.gemini\antigravity-ide\scratch\tools\apache-maven-3.9.6\bin;%PATH%

echo Java Environment:
java -version
echo.

if not exist "target\duplicate-question-detection-1.0.0.jar" (
    echo Compiling and packaging JAR file...
    call mvn clean package -DskipTests
)

echo Starting Spring Boot Web Server on http://localhost:8080 ...
echo The login page will open in your browser automatically in a few seconds.
echo.
echo Demo Credentials:
echo   Email:      faculty@eec.srmrmp.edu.in
echo   Password:   Admin@123
echo   Department: Artificial Intelligence and Data Science
echo ===================================================================

start "" cmd /c "timeout /t 6 >nul && start http://localhost:8080"
java -jar target\duplicate-question-detection-1.0.0.jar
pause
