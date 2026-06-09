@echo off

REM Example local run script for Windows.
REM Copy this file to run.bat and adjust the values if needed.

set "JAVA_HOME=C:\path\to\your\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "SPRING_PROFILES_ACTIVE=dev"

REM Available providers: mock, proxycheck
set "IP_INTELLIGENCE_PROVIDER=mock"
set "IP_INTELLIGENCE_BASE_URL=https://proxycheck.io/v2"
set "IP_INTELLIGENCE_API_KEY="
set "IP_INTELLIGENCE_CACHE_DURATION_MINUTES=60"

mvnw.cmd spring-boot:run