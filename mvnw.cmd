@echo off
setlocal

set "BASEDIR=%~dp0"
set "WRAPPER_PROPERTIES=%BASEDIR%.mvn\wrapper\maven-wrapper.properties"
set "MAVEN_VERSION=3.9.9"
set "MAVEN_DIST=%BASEDIR%.mvn\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MAVEN_ZIP=%MAVEN_DIST%\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_HOME=%MAVEN_DIST%\apache-maven-%MAVEN_VERSION%"
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if not exist "%WRAPPER_PROPERTIES%" (
  echo Missing Maven wrapper properties: %WRAPPER_PROPERTIES%
  exit /b 1
)

if not exist "%MAVEN_CMD%" (
  if not exist "%MAVEN_DIST%" mkdir "%MAVEN_DIST%"
  if not exist "%MAVEN_ZIP%" (
    echo Downloading Apache Maven %MAVEN_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -UseBasicParsing -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%MAVEN_ZIP%'; if (-not (Test-Path -LiteralPath '%MAVEN_ZIP%')) { exit 1 }"
    if errorlevel 1 exit /b 1
    if not exist "%MAVEN_ZIP%" exit /b 1
  )
  echo Unpacking Apache Maven %MAVEN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Expand-Archive -LiteralPath '%MAVEN_ZIP%' -DestinationPath '%MAVEN_DIST%' -Force"
  if errorlevel 1 exit /b 1
)

call "%MAVEN_CMD%" %*
endlocal
