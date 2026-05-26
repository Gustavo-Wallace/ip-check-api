@echo off

set "JAVA_HOME=C:\Users\gustavo.santos3\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.2.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Optional: set external IP intelligence API key for local testing
REM set "IP_INTELLIGENCE_API_KEY=your-api-key-here"

echo Using Java:
java -version

echo.
echo Using Javac:
javac -version

echo.
echo Starting Spring Boot...
call .\mvnw.cmd spring-boot:run

pause