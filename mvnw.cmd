@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "MVNW_DIR=%~dp0"
set "MVNW_WRAPPER_DIR=%MVNW_DIR%.mvn\wrapper"
set "MVNW_VERSION=3.9.9"
set "MVNW_DIST_NAME=apache-maven-%MVNW_VERSION%-bin.zip"
set "MVNW_DIST_URL=https://archive.apache.org/dist/maven/maven-3/%MVNW_VERSION%/binaries/%MVNW_DIST_NAME%"
set "MVNW_CACHE_DIR=%MVNW_WRAPPER_DIR%\dist\apache-maven-%MVNW_VERSION%"
set "MVNW_ZIP=%MVNW_WRAPPER_DIR%\%MVNW_DIST_NAME%"
set "MVNW_MVN_CMD=%MVNW_CACHE_DIR%\bin\mvn.cmd"

if exist "%MVNW_MVN_CMD%" goto run

if not exist "%MVNW_WRAPPER_DIR%" mkdir "%MVNW_WRAPPER_DIR%"
if not exist "%MVNW_WRAPPER_DIR%\dist" mkdir "%MVNW_WRAPPER_DIR%\dist"

if not exist "%MVNW_ZIP%" (
  echo Downloading Maven %MVNW_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%MVNW_DIST_URL%' -OutFile '%MVNW_ZIP%'"
)

if not exist "%MVNW_CACHE_DIR%" (
  echo Expanding Maven %MVNW_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%MVNW_ZIP%' -DestinationPath '%MVNW_WRAPPER_DIR%\dist' -Force"
)

if not exist "%MVNW_MVN_CMD%" (
  echo Failed to prepare Maven wrapper distribution.
  exit /b 1
)

:run
set "MVNW_LAUNCHER=%MVNW_MVN_CMD%"
"%MVNW_LAUNCHER%" %*

