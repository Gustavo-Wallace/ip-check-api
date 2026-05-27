@echo off

REM Configure JAVA_HOME if needed
REM set "JAVA_HOME=C:\Path\To\Your\JDK"
REM set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Optional: set external IP intelligence API key
REM set "IP_INTELLIGENCE_API_KEY=your-api-key-here"

echo Starting Spring Boot...
call .\mvnw.cmd spring-boot:run

pause