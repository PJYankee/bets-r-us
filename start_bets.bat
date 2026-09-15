@echo off
setlocal
cd /d "%~dp0"

set "JAVA_HOME=C:\TrustedApps\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Java 25 was not found at %JAVA_HOME%
    echo Update the JAVA_HOME path in start_bets.bat if your JDK install location is different.
    exit /b 1
)

REM Start local MongoDB before launching the app if it is not already running
sc query MongoDB >nul 2>&1
if errorlevel 1 (
    if exist "C:\TrustedApps\MongoDB\bin\mongod.exe" (
        echo Starting MongoDB service...
        "C:\TrustedApps\MongoDB\bin\mongod.exe" --dbpath "C:\TrustedApps\MongoDB\data" --logpath "C:\TrustedApps\MongoDB\log\mongod.log" --install
        net start MongoDB
    ) else (
        echo MongoDB was not found at C:\TrustedApps\MongoDB\bin\mongod.exe
        exit /b 1
    )
) else (
    echo MongoDB is already running.
)

java -jar "%~dp0spring-boot-0.0.1-SNAPSHOT.jar"

cmd /k


