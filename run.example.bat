@echo off

REM Example local run script for Windows.
REM Copy this file to run.bat and adjust the values if needed.

set "JAVA_HOME=C:\path\to\your\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "SPRING_PROFILES_ACTIVE=dev"

REM Available providers: mock, proxycheck
REM Use "mock" for local testing without external API calls.
REM Use "proxycheck" for real IP intelligence analysis.
set "IP_INTELLIGENCE_PROVIDER=proxycheck"
set "IP_INTELLIGENCE_BASE_URL=https://proxycheck.io/v2"

REM Optional. Configure this for more stable usage and higher authenticated limits.
REM Do not commit real API keys.
set "IP_INTELLIGENCE_API_KEY="

set "IP_INTELLIGENCE_CACHE_DURATION_MINUTES=60"

mvnw.cmd spring-boot:run