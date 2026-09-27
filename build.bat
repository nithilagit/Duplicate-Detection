@echo off
title Build AI Question Detection System
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot
set PATH=%JAVA_HOME%\bin;C:\Users\divya\.gemini\antigravity-ide\scratch\tools\apache-maven-3.9.6\bin;%PATH%

echo Building and testing AI-Based Duplicate Question Detection System...
mvn clean package
pause
