@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Spear Tech 2.0 Builder
chcp 65001 >nul 2>&1

echo ========================================
echo         Spear Tech 2.0 Builder
echo ========================================
echo.

if not exist "gradlew.bat" goto :missing_project
if not exist "build.gradle.kts" goto :missing_project
if not exist "gradle\wrapper\gradle-wrapper.properties" goto :missing_project

rem GitHub source releases may omit the binary Gradle wrapper JAR.
rem Bootstrap the official Gradle 9.6.1 wrapper JAR when it is missing.
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo [INFO] Gradle wrapper JAR is missing. Downloading the official Gradle 9.6.1 wrapper...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -UseBasicParsing 'https://raw.githubusercontent.com/gradle/gradle/v9.6.1/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
    if errorlevel 1 goto :wrapper_failed
    if not exist "gradle\wrapper\gradle-wrapper.jar" goto :wrapper_failed
    echo [OK] Gradle wrapper downloaded.
    echo.
)

set "JAVA_CMD=java.exe"
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        echo [INFO] JAVA_HOME: %JAVA_HOME%
    ) else (
        echo [WARN] JAVA_HOME is set but invalid:
        echo        %JAVA_HOME%
        echo [WARN] Falling back to Java from PATH.
        set "JAVA_HOME="
    )
)

if /i "%JAVA_CMD%"=="java.exe" (
    where java.exe >nul 2>&1
    if errorlevel 1 goto :no_java
)

set "JAVA_INFO_FILE=%TEMP%\speartech-java-%RANDOM%-%RANDOM%.txt"
"%JAVA_CMD%" -version > "%JAVA_INFO_FILE%" 2>&1
if errorlevel 1 goto :java_failed

set "JAVA_VERSION="
for /f "tokens=3" %%A in ('findstr /i /c:"version" "%JAVA_INFO_FILE%"') do if not defined JAVA_VERSION set "JAVA_VERSION=%%~A"
del /q "%JAVA_INFO_FILE%" >nul 2>&1

if not defined JAVA_VERSION (
    echo [ERROR] Could not detect the Java version.
    goto :fail
)

for /f "tokens=1 delims=.-+" %%A in ("%JAVA_VERSION%") do set "JAVA_MAJOR=%%A"

echo [INFO] Java version: %JAVA_VERSION%
if not "%JAVA_MAJOR%"=="25" (
    echo.
    echo [ERROR] This project requires JDK 25.
    echo [ERROR] Gradle is currently using Java %JAVA_VERSION%.
    echo.
    echo Fix JAVA_HOME or install JDK 25, then run BUILD.bat again.
    goto :fail
)

if defined JAVA_HOME (
    if not exist "%JAVA_HOME%\bin\javac.exe" (
        echo.
        echo [ERROR] JAVA_HOME does not point to a full JDK ^(javac.exe is missing^).
        goto :fail
    )
) else (
    where javac.exe >nul 2>&1
    if errorlevel 1 (
        echo.
        echo [ERROR] javac.exe was not found. Install JDK 25, not just a JRE.
        goto :fail
    )
)

echo [INFO] Project folder: %CD%
echo [INFO] Starting Gradle build...
echo.

call "%CD%\gradlew.bat" --no-daemon clean build
set "BUILD_EXIT=%ERRORLEVEL%"

if not "%BUILD_EXIT%"=="0" (
    echo.
    echo ========================================
    echo [ERROR] BUILD FAILED ^(exit code %BUILD_EXIT%^)
    echo ========================================
    echo.
    echo The important Gradle error is directly above this message.
    goto :fail
)

echo.
echo ========================================
echo [OK] BUILD COMPLETE
echo ========================================
echo.
if exist "build\libs" (
    echo Generated files:
    dir /b "build\libs\*.jar" 2>nul
) else (
    echo [WARN] Gradle succeeded but build\libs was not found.
)
echo.
pause
exit /b 0

:wrapper_failed
echo [ERROR] Could not download gradle-wrapper.jar.
echo Check your Internet connection and run BUILD.bat again.
goto :fail

:missing_project
echo [ERROR] The project files are missing next to BUILD.bat.
echo Make sure you downloaded/extracted the whole repository.
goto :fail

:no_java
echo [ERROR] Java was not found.
echo Install JDK 25 and reopen BUILD.bat.
goto :fail

:java_failed
echo [ERROR] Java exists but could not be started.
goto :fail

:fail
echo.
echo Press any key to close this window.
pause >nul
exit /b 1
