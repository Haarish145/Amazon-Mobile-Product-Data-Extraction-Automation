@ECHO OFF
SETLOCAL
SETLOCAL EnableDelayedExpansion
SET "BASE_DIR=%~dp0"
IF "%BASE_DIR:~-1%"=="\" SET "BASE_DIR=%BASE_DIR:~0,-1%"
SET "WRAPPER_DIR=%BASE_DIR%\.mvn\wrapper"
SET "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
SET "WRAPPER_PROPS=%WRAPPER_DIR%\maven-wrapper.properties"
IF NOT EXIST "%WRAPPER_JAR%" (
  FOR /F "tokens=1,* delims==" %%A IN (%WRAPPER_PROPS%) DO (
    IF "%%A"=="wrapperUrl" SET WRAPPER_URL=%%B
  )
  powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -UseBasicParsing '!WRAPPER_URL!' -OutFile '%WRAPPER_JAR%'"
  IF ERRORLEVEL 1 EXIT /B 1
)
java "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
ENDLOCAL
ENDLOCAL
