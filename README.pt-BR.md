# IP Check API

[English](README.md)

IP Check API é uma API REST desenvolvida com Java e Spring Boot para cadastro, importação e análise de endereços IP.

O projeto foi desenvolvido tanto como estudo/projeto de portfólio quanto como uma ferramenta prática de apoio à análise de risco de acessos em sistemas relacionados à segurança no Brasil. Durante meu estágio no MJSP, no contexto do SINESP, a API se mostrou útil para analisar grandes listas de IPs por meio de importação CSV, processando mais de 500 endereços IP e ajudando a identificar entradas de alto risco que poderiam ser revisadas e bloqueadas quando necessário.

A API permite cadastrar endereços IP, importar listas em JSON ou CSV, realizar análises automáticas e manuais, além de verificar indicadores como VPN, proxy, Tor, datacenter/hosting, anonimidade, ASN, país, cidade, hostname, provedor, faixa de rede e nível de risco.

Este projeto tem foco em desenvolvimento backend, design de API, integração com API externa, testes, documentação e organização limpa de projeto.

## Tecnologias

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

## Funcionalidades

* Cadastro de endereços IP
* Listagem de endereços IP cadastrados
* Listagem de IPs ativos
* Ativação e desativação de endereços IP
* Importação de IPs via JSON
* Importação de IPs via CSV em texto
* Importação de IPs via arquivo CSV
* Análise automática de endereços IP
* Análise manual de endereços IP
* Análise em lote de todos os IPs ativos
* Cache de análises recentes
* Consulta de histórico de análises por endereço IP
* Relatórios por nível de risco
* Relatórios por indicadores de anonimidade
* Filtros por nível de risco
* Filtros por país, provedor, ASN e indicadores
* Documentação com Swagger
* Testes unitários e testes de controller

## Como rodar o projeto

### Requisitos

* Java 21 ou superior
* Maven Wrapper incluído no projeto

Para rodar o projeto no Windows:

```bat
mvnw.cmd spring-boot:run
```

Ou, se estiver usando o arquivo local `run.bat`:

```bat
run.bat
```

A API ficará disponível em:

```text
http://localhost:8080
```

## Documentação Swagger

A documentação interativa da API está disponível em:

```text
http://localhost:8080/docs
```

## Health Check

```http
GET /health
```

Resposta esperada:

```text
IP Check API is running
```

## Banco de dados de desenvolvimento

Por padrão, o projeto utiliza o profile `dev`, com banco H2 em memória.

Console do H2:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:ipcheckdb
```

## Variáveis de ambiente

O projeto possui um arquivo `.env.example` com as variáveis de ambiente esperadas:

```env
SPRING_PROFILES_ACTIVE=dev
IP_INTELLIGENCE_API_KEY=your_api_key_here

POSTGRES_DB_URL=jdbc:postgresql://localhost:5432/ip_check_db
POSTGRES_DB_USERNAME=postgres
POSTGRES_DB_PASSWORD=postgres
```

## Integração de inteligência de IP

A API suporta provedores configuráveis de inteligência de IP.

Configuração principal:

```properties
ip-intelligence.provider=mock
ip-intelligence.base-url=https://proxycheck.io/v2
ip-intelligence.api-key=${IP_INTELLIGENCE_API_KEY:}
ip-intelligence.cache-duration-minutes=60
```

Provedores atualmente suportados no projeto:

* `mock`
* `proxycheck`

Para verificar a configuração atual da integração:

```http
GET /ip-intelligence/config
```

Exemplo de resposta:

```json
{
  "provider": "proxycheck",
  "baseUrlConfigured": true,
  "apiKeyConfigured": true,
  "cacheDurationMinutes": 60
}
```

> **Observação:** O ProxyCheck pode ser usado sem API key, mas requisições não autenticadas podem ter limites menores e podem falhar durante análises em lote com muitos IPs. Para um uso mais estável, configure `IP_INTELLIGENCE_API_KEY` usando uma variável de ambiente ou um arquivo local `run.bat`. Não faça commit de chaves reais no repositório.

## Principais endpoints

### Endereços IP

```http
POST /ips
GET /ips
GET /ips/active
GET /ips/page
GET /ips/{id}
PATCH /ips/{id}/activate
PATCH /ips/{id}/deactivate
```

### Importação de IPs

```http
POST /ips/import
POST /ips/import/csv-text
POST /ips/import/csv-file
```

### Análise de IPs

```http
POST /ips/{address}/analyze
POST /ips/{address}/analyze/manual
GET /ips/{address}/analyses
POST /ips/active/analyze
```

### Relatórios de análise

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

## Exemplos de requisição e resposta

### Cadastrar um endereço IP

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

### Analisar automaticamente um endereço IP

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

### Analisar manualmente um endereço IP

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

### Importar endereços IP via JSON

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

### Importar endereços IP via CSV em texto

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

### Analisar IPs ativos em lote

Request:

```http
POST /ips/active/analyze
```

Exemplo de resposta:

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

## Níveis de risco

A API trabalha atualmente com os seguintes níveis de risco:

| Nível       | Descrição                                                                                      |
| ----------- | ---------------------------------------------------------------------------------------------- |
| `LOW`       | Nenhum indicador relevante de anonimidade detectado                                            |
| `ATTENTION` | Datacenter ou provedor de hosting detectado                                                    |
| `MEDIUM`    | VPN, proxy ou pontuação externa moderada de risco detectada                                    |
| `HIGH`      | Indicadores fortes de risco detectados, como VPN combinada com proxy ou alta pontuação externa |
| `CRITICAL`  | Tor detectado ou pontuação externa crítica detectada                                           |

## Cálculo do nível de risco

O nível de risco é calculado usando indicadores de anonimidade e a pontuação externa de risco.

| Condição                                                    | Nível de risco |
| ----------------------------------------------------------- | -------------- |
| Tor detectado ou pontuação externa >= 90                    | `CRITICAL`     |
| VPN + Proxy detectados ou pontuação externa >= 70           | `HIGH`         |
| VPN, Proxy ou pontuação externa >= 40                       | `MEDIUM`       |
| Datacenter detectado ou pontuação externa >= 20             | `ATTENTION`    |
| Nenhum indicador relevante e pontuação externa abaixo de 20 | `LOW`          |

## Fontes de análise

A API trabalha atualmente com as seguintes fontes de análise:

```text
INTERNAL_RULES
MANUAL_SIMULATION
EXTERNAL_API
```

## Estrutura do projeto

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

### Principais pacotes

| Pacote         | Responsabilidade                                                             |
| -------------- | ---------------------------------------------------------------------------- |
| `clients`      | Clients externos de inteligência de IP, como integrações mock e ProxyCheck   |
| `config`       | Configurações da aplicação, properties, WebClient e OpenAPI                  |
| `controllers`  | Controllers REST e endpoints da API                                          |
| `dtos`         | Objetos de request e response usados pela API                                |
| `enums`        | Níveis de risco, fontes de análise e enums relacionados a provedores         |
| `exceptions`   | Exceções customizadas e tratamento global de exceções                        |
| `models`       | Entidades JPA                                                                |
| `repositories` | Repositórios Spring Data JPA                                                 |
| `services`     | Regras de negócio, cadastro, importação e análise de IPs                     |
| `utils`        | Classes utilitárias, como validação de IP, parsing de CSV e cálculo de risco |

## Testes

Para rodar todos os testes:

```bat
mvnw.cmd test
```

Para limpar e rodar todos os testes:

```bat
mvnw.cmd clean test
```

O projeto possui testes para:

* Classes utilitárias
* Parsing de nível de risco
* Cálculo de nível de risco
* Parsing de CSV
* Client mock de inteligência de IP
* Serviço de importação de IPs
* Serviço de análise de IPs
* Controllers usando MockMvc

## Status do projeto

Este projeto está atualmente em desenvolvimento.

Status atual:

* API REST funcional
* Integração externa configurável de inteligência de IP
* Banco H2 para desenvolvimento
* Suporte a profile PostgreSQL
* Documentação Swagger
* Testes unitários
* Testes de controller
* Fluxos de importação e análise implementados

## Melhorias futuras

Possíveis melhorias futuras:

* Adicionar autenticação e autorização
* Adicionar suporte a Docker
* Adicionar migrations de banco com Flyway ou Liquibase
* Melhorar configuração de produção com PostgreSQL
* Adicionar paginação em mais endpoints
* Adicionar mais provedores externos de inteligência de IP
* Adicionar dashboard frontend
* Adicionar configuração de deploy
