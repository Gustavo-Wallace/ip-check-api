# IP Check API

REST API for detecting VPN, proxy, Tor, datacenter and anonymized IP addresses.

## About

IP Check API is a Spring Boot project created to analyze IP addresses and identify possible anonymity indicators, such as VPN, proxy, Tor usage and datacenter/hosting origin.

The project currently uses internal rules and a mock intelligence client, but it is structured to support future integration with external IP intelligence APIs.

## Features

- Register IP addresses
- List registered IP addresses
- Validate IPv4 and IPv6 addresses
- Analyze IP addresses
- Simulate manual IP analysis
- Detect anonymity indicators:
  - VPN
  - Proxy
  - Tor
  - Datacenter
  - Anonymous
- Classify IP risk level
- Store analysis history
- Generate reports by risk level
- Generate reports by anonymity indicators
- Global error handling

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- Lombok
- H2 Database
- Maven

## Project Structure

```text
src/main/java/br/com/gustavo/ip_check_api/
├── clients/
├── config/
├── controllers/
├── dtos/
├── enums/
├── exceptions/
├── models/
├── repositories/
├── services/
└── utils/
```

## Environment Variables

The project supports external API keys through environment variables.

| Variable | Description |
|---|---|
| IP_INTELLIGENCE_API_KEY | API key used by the configured IP intelligence provider |

Example on Windows CMD:

```bat
set IP_INTELLIGENCE_API_KEY=your-api-key-here
mvnw.cmd spring-boot:run
```
The API key is not exposed by the configuration endpoint. The endpoint only indicates whether it is configured.

## Risk Levels

| Level     | Description                              |
| --------- | ---------------------------------------- |
| LOW       | No relevant anonymity indicator detected |
| ATTENTION | Datacenter or hosting provider detected  |
| MEDIUM    | VPN or proxy detected                    |
| HIGH      | Strong anonymity combination detected    |
| CRITICAL  | Tor detected                             |

## Risk Level Calculation

The risk level is calculated using anonymity indicators and the external risk score.

| Condition | Risk Level |
|---|---|
| Tor detected or external risk score >= 90 | CRITICAL |
| VPN + Proxy, Anonymous + Proxy, or external risk score >= 70 | HIGH |
| VPN, Proxy, or external risk score >= 40 | MEDIUM |
| Datacenter detected or external risk score >= 20 | ATTENTION |
| No relevant indicator and external risk score below 20 | LOW |

## Analysis Sources

| Source            | Description                         |
| ----------------- | ----------------------------------- |
| INTERNAL_RULES    | Analysis based on internal rules    |
| MANUAL_SIMULATION | Manual simulation for testing       |
| EXTERNAL_API      | Mocked external intelligence client |

## IP Intelligence Cache

The project can reuse recent analyses to avoid unnecessary external API calls.

```properties
ip-intelligence.cache-duration-minutes=60
```

If the same IP was analyzed within the configured time window, the latest analysis is reused.

To disable cache:

```properties
ip-intelligence.cache-duration-minutes=0
```

## Endpoints

### Health Check
```http
GET /health
```
Response:
```
IP Check API is running
```

### Register IP Address
```http
Post /ips
```
Request:
```JSON
{
  "address": "8.8.8.8",
  "description": "Google DNS"
}
```
Response:
```JSON
{
  "id": 1,
  "address": "8.8.8.8",
  "description": "Google DNS",
  "active": true,
  "createdAt": "2026-05-20T13:20:17.8335225"
}
```

### Deactivate IP Address
```http
PATCH /ips/{id}/deactivate
```
Example:
```bash
curl -X PATCH http://localhost:8080/ips/1/deactivate
```

### Activate IP Address
```http
PATCH /ips/{id}/activate
```
Example:
```bash
curl -X PATCH http://localhost:8080/ips/1/activate
```

### List IP Addresses
```http
GET /ips
```

### List Active IP Addresses
```http
GET /ips/active
```
Example:
```bash
curl http://localhost:8080/ips/active
```

### Find IP Address by ID
```http
Get /ips/{id}
```
Example:
```bash
curl http://localhost:8080/ips/1
```

### List IP Addresses With Pagination
```http
GET /ips/page?page=0&size=10
```
Example:
```bash
curl "http://localhost:8080/ips/page?page=0&size=5"
```
Example with sorting:
```bash
curl "http://localhost:8080/ips/page?page=0&size=5&sort=createdAt,desc"
```

### Analyze IP Address
```http
POST /ips/{address}/analyze`
```
Example:
```bash
curl -X POST http://localhost:8080/ips/8.8.8.8/analyze
```
Response:
```JSON
{
  "address": "8.8.8.8",
  "analyzedAt": "2026-05-27T17:04:00.1765442",
  "anonymous": false,
  "asn": "AS15169",
  "city": "Mountain View",
  "country": "United States",
  "datacenter": false,
  "externalProvider": "Google LLC",
  "externalRiskScore": 0,
  "externalType": "Business",
  "hostname": "dns.google",
  "id": 1,
  "networkRange": "8.8.8.0/24",
  "proxy": false,
  "riskLevel": "LOW",
  "source": "EXTERNAL_API",
  "tor": false,
  "vpn": false
}
```

### Manual Analysis Simulation
```http
POST /ips/{address}/analyze/manual
```
Request:
```JSON
{
  "vpn": true,
  "proxy": false,
  "tor": false,
  "datacenter": false
}
```
Response:
```JSON
{
  "id": 2,
  "address": "8.8.8.8",
  "vpn": true,
  "proxy": false,
  "tor": false,
  "datacenter": false,
  "anonymous": true,
  "riskLevel": "MEDIUM",
  "source": "MANUAL_SIMULATION",
  "analyzedAt": "2026-05-20T13:45:00"
}
```

### Analyze All Active IP Addresses
```http
POST /ips/active/analyze
```
Example:
```bash
curl -X POST http://localhost:8080/ips/active/analyze
```
Response:
```JSON
{
  "totalProcessed": 2,
  "successCount": 2,
  "errorCount": 0,
  "analyses": [],
  "errors": []
}
```
This endpoint analyzes all active registered IP addresses, stores the analysis history and returns a batch processing summary.

### List Analyses by IP
```http
GET /ips/{address}/analyses
```

### List All Analyses
```http
Get /analyses
```

### List Analyses by risk Level
```http
GET /analyses/risk-level/{riskLevel}
```
Example:
```bash
curl http://localhost:8080/analyses/risk-level/CRITICAL
```
Available risk levels:

 - LOW
 - ATTENTION
 - MEDIUM
 - HIGH
 - CRITICAL
 Accepted values are case-insensitive.

### Analysis Summary Report
```http
GET /analyses/report/summary
```
Example:
```bash
curl http://localhost:8080/analyses/report/summary
```
Response:
```JSON
{
  "totalAnalyses": 2,
  "anonymousCount": 1,
  "vpnCount": 0,
  "proxyCount": 0,
  "torCount": 1,
  "datacenterCount": 1,
  "highestRiskLevel": "CRITICAL"
}
```

### Risk Level Report
```http
GET /analyses/report/risk-level
```
Response:
```JSON
{
  "LOW": 1,
  "ATTENTION": 1,
  "MEDIUM": 1,
  "HIGH": 0,
  "CRITICAL": 1
}
```

### Anonymity Indicators Report
```http
GET /analyses/report/anonymity
```
Response:
```JSON
{
  "vpn": 1,
  "proxy": 1,
  "tor": 1,
  "datacenter": 1,
  "anonymous": 3
}
```

### Filter Analyses by Anonymity Indicators
```http
GET /analyses/filter?vpn=true&proxy=false&tor=false&datacenter=true&anonymous=false
```
Example:
```bash
curl "http://localhost:8080/analyses/filter?tor=true"
```
Available optional query parameters
 - vpn
 - proxy
 - tor
 - datacenter
 - anonymous

### List Analyses With Pagination
```http
GET /analyses/page?page=0&size=10
```
Example:
```bash
curl "http://localhost:8080/analyses/page?page=0&size=5"
```
Example with sorting:
```bash
curl "http://localhost:8080/analyses/page?page=0&size=5&sort=analyzedAt,desc"
```

### IP Intelligence Configuration
```http
GET /ip-intelligence/config
```
Example:
```bash
curl http://localhost:8080/ip-intelligence/config
```
Response:
```JSON
{
  "provider": "mock",
  "baseUrlConfigured": "false",
  "apiKeyConfigured": "false"
}
```

## Running the Project

On machines with Java properly configured:
```bash
./mvnw spring-boot:run
```
On Windows:
```bash
mvnw.cmd spring-boot:run
```
If the machine has multiple Java versions, configure `JAVA_HOME` before running.
Example:
```bat
set "JAVA_HOME=C:\Users\gustavo.santos3\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.2.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
mvnw.cmd spring-boot:run
```

## Temporary Database

The project currently use H2 in-memory database for development
H2 console:
```
http://localhost:8080/h2-console
```
Default JDBC URL
```
jdbc:h2:mem:ipcheckdb
```

## IP Intelligence Provider

The project currently uses a mock IP intelligence provider.

Available provider:

```properties
ip-intelligence.provider=mock
```

Future supported providers may include:

 - ProxyCheck
 - IPinfo
 - IPQualityScore

## ProxyCheck Provider

The project includes an initial ProxyCheck client implementation.

```properties
ip-intelligence.provider=proxycheck
ip-intelligence.base-url=https://proxycheck.io/v2
ip-intelligence.api-key=${IP_INTELLIGENCE_API_KEY:}
```

For local development, keep:
```properties
ip-intelligence.provider=mock
```
ProxyCheck free usage limits may vary by account and API version. Check the official documentation before production usage.

## Future Improvements

 - Integrate with a real external IP intelligence API
 - Add PostgreSQL profile
 - Add Swagger/OpenAPI documentation
 - Add pagination to list endpoints
 - Add unit and integration tests
 - Add Docker support
 - Add authentication for protected endpoints
 - Add CSV/Excel import for IP lists