@REM Maven Wrapper startup script for Windows
@REM
@REM Required ENV vars:
@REM   JAVA_HOME - location of a JDK home dir

@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_OPTS=-Xmx512m

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if not exist %WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri %WRAPPER_URL% -OutFile %WRAPPER_JAR%"
)

set MAVEN_CMD_LINE_ARGS=%*

"%JAVA_HOME%\bin\java.exe" ^
    -jar %WRAPPER_JAR% ^
    %MAVEN_CMD_LINE_ARGS%

endlocal
