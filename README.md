# IP Check API

[Português](README.pt-BR.md)

IP Check API is a REST API built with Java and Spring Boot for registering, importing, and analyzing IP addresses.

The project was developed as both a study/portfolio project and a practical support tool for access risk analysis in security-related systems used in Brazil. During my internship at MJSP, working in the context of SINESP, the API proved useful for analyzing large IP lists through CSV imports, processing more than 500 IP addresses and helping identify high-risk entries that could be reviewed and blocked when necessary.

The API allows users to store IP addresses, import IP lists from JSON or CSV, run automatic and manual IP analyses, and check indicators such as VPN, proxy, Tor, datacenter/hosting, anonymity, ASN, country, city, hostname, provider, network range, and risk level.

This project focuses on backend development, API design, external API integration, testing, documentation, and clean project organization.

## Technologies

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* H2 Database
* PostgreSQL
* Maven
* Lombok
* Swagger / OpenAPI
* WebClient
* JUnit
* Mockito
* MockMvc

## Features

* Register IP addresses
* List registered IP addresses
* List active IP addresses
* Activate and deactivate IP addresses
* Import IP addresses from JSON
* Import IP addresses from CSV text
* Import IP addresses from CSV files
* Automatically analyze IP addresses
* Manually analyze IP addresses
* Batch analyze all active IP addresses
* Cache recent IP analyses
* Check analysis history by IP address
* Generate risk level reports
* Generate anonymity indicator reports
* Filter analyses by risk level
* Filter analyses by country, provider, ASN, and indicators
* Swagger documentation
* Unit and controller tests

## Running the Project

### Requirements

* Java 21 or higher
* Maven Wrapper included in the project

To run the project on Windows:

```bat
mvnw.cmd spring-boot:run
```

Or, if you are using the local `run.bat` file:

```bat
run.bat
```

The API will be available at:

```text
http://localhost:8080
```

## Swagger Documentation

The interactive API documentation is available at:

```text
http://localhost:8080/docs
```

## Health Check

```http
GET /health
```

Expected response:

```text
IP Check API is running
```

## Development Database

By default, the project uses the `dev` profile with an in-memory H2 database.

H2 Console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:ipcheckdb
```

## Environment Variables

The project includes a `.env.example` file with the expected environment variables:

```env
SPRING_PROFILES_ACTIVE=dev
IP_INTELLIGENCE_API_KEY=your_api_key_here

POSTGRES_DB_URL=jdbc:postgresql://localhost:5432/ip_check_db
POSTGRES_DB_USERNAME=postgres
POSTGRES_DB_PASSWORD=postgres
```

## IP Intelligence Integration

The API supports configurable IP intelligence providers.

Main configuration:

```properties
ip-intelligence.provider=mock
ip-intelligence.base-url=https://proxycheck.io/v2
ip-intelligence.api-key=${IP_INTELLIGENCE_API_KEY:}
ip-intelligence.cache-duration-minutes=60
```

Currently supported providers in the project:

* `mock`
* `proxycheck`

To check the current integration configuration:

```http
GET /ip-intelligence/config
```

Example response:

```json
{
  "provider": "proxycheck",
  "baseUrlConfigured": true,
  "apiKeyConfigured": true,
  "cacheDurationMinutes": 60
}
```

## Main Endpoints

### IP Addresses

```http
POST /ips
GET /ips
GET /ips/active
GET /ips/page
GET /ips/{id}
PATCH /ips/{id}/activate
PATCH /ips/{id}/deactivate
```

### IP Import

```http
POST /ips/import
POST /ips/import/csv-text
POST /ips/import/csv-file
```

### IP Analysis

```http
POST /ips/{address}/analyze
POST /ips/{address}/analyze/manual
GET /ips/{address}/analyses
POST /ips/active/analyze
```

### Analysis Reports

```http
GET /analyses
GET /analyses/page
GET /analyses/risk-level/{riskLevel}
GET /analyses/filter
GET /analyses/country/{country}
GET /analyses/provider/{externalProvider}
GET /analyses/asn/{asn}
GET /analyses/report/risk-level
GET /analyses/report/anonymity
GET /analyses/report/summary
```

## Request and Response Examples

### Register an IP Address

Request:

```http
POST /ips
Content-Type: application/json
```

Body:

```json
{
  "address": "8.8.8.8",
  "description": "Google DNS"
}
```

Response:

```json
{
  "id": 1,
  "address": "8.8.8.8",
  "description": "Google DNS",
  "active": true,
  "createdAt": "2026-06-02T15:00:00"
}
```

### Automatically Analyze an IP Address

Request:

```http
POST /ips/8.8.8.8/analyze
```

Response:

```json
{
  "id": 1,
  "address": "8.8.8.8",
  "vpn": false,
  "proxy": false,
  "tor": false,
  "datacenter": false,
  "anonymous": false,
  "riskLevel": "LOW",
  "source": "EXTERNAL_API",
  "externalRiskScore": 0,
  "externalType": "Business",
  "externalProvider": "Google LLC",
  "asn": "AS15169",
  "country": "United States",
  "city": "Mountain View",
  "hostname": "dns.google",
  "networkRange": "8.8.8.0/24",
  "analyzedAt": "2026-06-02T15:00:00"
}
```

### Manually Analyze an IP Address

Request:

```http
POST /ips/45.90.28.1/analyze/manual
Content-Type: application/json
```

Body:

```json
{
  "vpn": true,
  "proxy": false,
  "tor": false,
  "datacenter": false,
  "anonymous": true,
  "riskLevel": "MEDIUM"
}
```

Response:

```json
{
  "id": 1,
  "address": "45.90.28.1",
  "vpn": true,
  "proxy": false,
  "tor": false,
  "datacenter": false,
  "anonymous": true,
  "riskLevel": "MEDIUM",
  "source": "MANUAL_SIMULATION",
  "externalRiskScore": null,
  "externalType": "Manual",
  "externalProvider": "Manual input",
  "asn": null,
  "country": null,
  "city": null,
  "hostname": null,
  "networkRange": null,
  "analyzedAt": "2026-06-02T15:00:00"
}
```

### Import IP Addresses from JSON

Request:

```http
POST /ips/import
Content-Type: application/json
```

Body:

```json
{
  "addresses": [
    "8.8.8.8",
    "1.1.1.1"
  ],
  "description": "Imported JSON",
  "analyzeAfterImport": false
}
```

### Import IP Addresses from CSV Text

Request:

```http
POST /ips/import/csv-text
Content-Type: application/json
```

Body:

```json
{
  "csvContent": "address,description\n8.8.8.8,Google DNS\n1.1.1.1,Cloudflare DNS",
  "analyzeAfterImport": false
}
```

### Batch Analyze Active IP Addresses

Request:

```http
POST /ips/active/analyze
```

Example response:

```json
{
  "totalProcessed": 2,
  "successCount": 2,
  "errorCount": 0,
  "analyses": [
    {
      "id": 1,
      "address": "8.8.8.8",
      "vpn": false,
      "proxy": false,
      "tor": false,
      "datacenter": false,
      "anonymous": false,
      "riskLevel": "LOW",
      "source": "EXTERNAL_API",
      "externalRiskScore": 0,
      "externalType": "Business",
      "externalProvider": "Google LLC",
      "asn": "AS15169",
      "country": "United States",
      "city": "Mountain View",
      "hostname": "dns.google",
      "networkRange": "8.8.8.0/24",
      "analyzedAt": "2026-06-02T15:00:00"
    }
  ],
  "errors": []
}
```

## Risk Levels

The API currently works with the following risk levels:

```text
LOW
ATTENTION
MEDIUM
HIGH
CRITICAL
```

## Analysis Sources

The API currently works with the following analysis sources:

```text
INTERNAL_RULES
MANUAL_SIMULATION
EXTERNAL_API
```

## Risk Levels

| Level       | Description                                                                                  |
| ----------- | -------------------------------------------------------------------------------------------- |
| `LOW`       | No relevant anonymity indicator detected                                                     |
| `ATTENTION` | Datacenter or hosting provider detected                                                      |
| `MEDIUM`    | VPN, proxy, or moderate external risk score detected                                         |
| `HIGH`      | Strong risk indicators detected, such as VPN combined with proxy or high external risk score |
| `CRITICAL`  | Tor detected or critical external risk score detected                                        |

## Risk Level Calculation

The risk level is calculated using anonymity indicators and the external risk score.

| Condition                                              | Risk Level  |
| ------------------------------------------------------ | ----------- |
| Tor detected or external risk score >= 90              | `CRITICAL`  |
| VPN + Proxy detected or external risk score >= 70      | `HIGH`      |
| VPN, Proxy, or external risk score >= 40               | `MEDIUM`    |
| Datacenter detected or external risk score >= 20       | `ATTENTION` |
| No relevant indicator and external risk score below 20 | `LOW`       |

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── br.com.gustavo.ip_check_api
│   │       ├── clients
│   │       ├── config
│   │       ├── controllers
│   │       ├── dtos
│   │       ├── enums
│   │       ├── exceptions
│   │       ├── models
│   │       ├── repositories
│   │       ├── services
│   │       └── utils
│   └── resources
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-postgres.properties
└── test
    └── java
        └── br.com.gustavo.ip_check_api
            ├── clients
            ├── controllers
            ├── services
            └── utils
```

### Main Packages

| Package        | Responsibility                                                             |
| -------------- | -------------------------------------------------------------------------- |
| `clients`      | External IP intelligence clients, such as mock and ProxyCheck integrations |
| `config`       | Application configuration, properties, WebClient and OpenAPI setup         |
| `controllers`  | REST controllers and API endpoints                                         |
| `dtos`         | Request and response objects used by the API                               |
| `enums`        | Risk levels, analysis sources and provider-related enums                   |
| `exceptions`   | Custom exceptions and global exception handling                            |
| `models`       | JPA entities                                                               |
| `repositories` | Spring Data JPA repositories                                               |
| `services`     | Business rules, IP registration, import and analysis logic                 |
| `utils`        | Utility classes such as IP validation, CSV parsing and risk calculation    |

## Tests

To run all tests:

```bat
mvnw.cmd test
```

To clean and run all tests:

```bat
mvnw.cmd clean test
```

The project includes tests for:

* Utility classes
* Risk level parsing
* Risk level calculation
* CSV parsing
* Mock IP intelligence client
* IP import service
* IP analysis service
* Controllers using MockMvc

## Project Status

This project is currently under development.

Current status:

* Functional REST API
* Configurable external IP intelligence integration
* H2 database for development
* PostgreSQL profile support
* Swagger documentation
* Unit tests
* Controller tests
* Import and analysis flows implemented

## Future Improvements

Possible future improvements:

* Add authentication and authorization
* Add Docker support
* Add database migrations with Flyway or Liquibase
* Improve PostgreSQL production configuration
* Add pagination to more endpoints
* Add more external IP intelligence providers
* Add frontend dashboard
* Add deployment configuration
