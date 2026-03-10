# exp-distributor

Backend-for-Frontend (BFF) experience-layer service that aggregates multiple domain SDKs into a unified REST API for the distributor portal. It provides a single entry point for all distributor management journeys -- profile registration, branding, catalog, network (territories and agencies), agents, shipments, terms and conditions, operations, configurations, and simulations -- by composing calls to underlying domain microservices.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Functional Verticals](#functional-verticals)
- [API Endpoints](#api-endpoints)
- [Domain SDK Dependencies](#domain-sdk-dependencies)
- [Setup](#setup)
- [Testing](#testing)

## Overview

`exp-distributor` is an `exp-*` experience-layer service that follows a simple composition pattern. It does not contain domain logic, saga workflows, or persistent state. Instead, it acts as a thin orchestration layer that:

1. Receives requests from the distributor portal frontend.
2. Maps frontend DTOs to domain SDK models using MapStruct.
3. Delegates to one or more domain service SDKs via generated API clients.
4. Aggregates and transforms domain responses back into frontend-friendly DTOs.

This keeps the frontend decoupled from the internal domain service topology and allows the portal to interact with a single, purpose-built API surface.

## Architecture

### Module Structure

| Module | Description |
|--------|-------------|
| `exp-distributor-interfaces` | DTOs, request/response objects, and shared contracts for the experience layer API |
| `exp-distributor-core` | Service interfaces and implementations; MapStruct mappers; composition logic delegating to domain SDKs |
| `exp-distributor-infra` | ClientFactory beans and ConfigurationProperties for each downstream domain SDK |
| `exp-distributor-web` | Spring Boot WebFlux application: REST controllers, application entry point, OpenAPI configuration |
| `exp-distributor-sdk` | Auto-generated client SDK from the OpenAPI spec (WebClient-based, reactive) for upstream consumers |

### Architecture Pattern

The service uses a **simple composition** pattern with no workflows, sagas, or CQRS. Each controller delegates to a core service, which calls one or more domain SDK APIs and maps the results.

```
                         +---------------------+
                         |   Distributor Portal |
                         |     (Frontend)       |
                         +----------+----------+
                                    |
                                    v
                         +---------------------+
                         |   exp-distributor    |
                         |       (BFF)         |
                         +----------+----------+
                                    |
              +---------------------+---------------------+
              |           |           |           |        |
              v           v           v           v        v
  +-----------+--+ +------+------+ +-+--------+ ++-------+-+ +--------+--------+
  | domain-      | | domain-     | | domain-  | | domain-  | | domain-         |
  | distributor- | | distributor-| | product- | | product- | | common-         |
  | branding     | | catalog     | | catalog  | | pricing  | | contracts /     |
  |              | |             | |          | |          | | notifications   |
  +--------------+ +-------------+ +----------+ +----------+ +-----------------+
```

### Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 25 | Language runtime |
| Spring Boot + WebFlux | Reactive, non-blocking HTTP server |
| Project Reactor (Mono/Flux) | Reactive streams throughout the stack |
| FireflyFramework (`fireflyframework-web`, `fireflyframework-utils`, `fireflyframework-starter-application`, `fireflyframework-validators`) | Shared web configuration, utilities, and validation |
| MapStruct | Object mapping between experience DTOs and domain SDK models |
| Lombok | Boilerplate reduction |
| SpringDoc OpenAPI | API documentation and Swagger UI |
| Micrometer + Prometheus | Metrics export |
| Spring Boot Actuator | Health checks and operational endpoints |
| OpenAPI Generator | SDK generation from the OpenAPI spec (WebClient-based reactive client) |

## Functional Verticals

| Vertical | Controller | Base Path | Endpoints |
|----------|------------|-----------|-----------|
| Distributor Profile | `DistributorProfileController` | `/api/v1/experience/distributors` | 1 |
| Branding | `BrandingController` | `/api/v1/experience/distributors/{distributorId}/brandings` | 2 |
| Catalog | `CatalogController` | `/api/v1/experience/distributors/{distributorId}/catalog` | 4 |
| Products | `ProductController` | `/api/v1/experience/products` | 2 |
| Territories | `TerritoryController` | `/api/v1/experience/distributors/{distributorId}/territories` | 5 |
| Agencies | `AgencyController` | `/api/v1/experience/distributors/{distributorId}/agencies` | 5 |
| Agents | `AgentController` | `/api/v1/experience/distributors/{distributorId}/agents` | 5 |
| Agent-Agency Assignments | `AgentAgencyController` | `/api/v1/experience/distributors/{distributorId}/agent-agencies` | 3 |
| Shipments | `ShipmentController` | `/api/v1/experience/distributors/{distributorId}/products/{productId}/shipments` | 2 |
| Terms and Conditions | `TermsAndConditionsController` | `/api/v1/experience/distributors/{distributorId}/terms-and-conditions` | 10 |
| Operations | `OperationsController` | `/api/v1/experience/distributors/{distributorId}/operations` | 7 |
| Configurations | `ConfigurationController` | `/api/v1/experience/distributors/{distributorId}/configurations` | 4 |
| Simulations | `SimulationController` | `/api/v1/experience/distributors/{distributorId}/simulations` | 2 |

**Total: 52 endpoints across 13 controllers.**

## API Endpoints

### Distributor Profile

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `POST` | `/api/v1/experience/distributors` | Register distributor | `201 Created` |

### Branding

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `PUT` | `…/brandings/{brandingId}` | Revise branding | `200 OK` |
| `PUT` | `…/brandings/{brandingId}/set-default` | Set default branding | `200 OK` |

### Catalog

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/catalog` | List catalog items | `200 OK` |
| `POST` | `…/catalog` | Add to catalog | `201 Created` |
| `PUT` | `…/catalog/{productId}` | Update catalog item | `200 OK` |
| `DELETE` | `…/catalog/{productId}` | Remove from catalog | `200 OK` |

### Products

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `/api/v1/experience/products/{productId}` | Get product | `200 OK` |
| `POST` | `/api/v1/experience/products` | Register product | `201 Created` |

### Territories

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/territories` | List territories | `200 OK` |
| `POST` | `…/territories` | Create territory | `201 Created` |
| `GET` | `…/territories/{territoryId}` | Get territory | `200 OK` |
| `PUT` | `…/territories/{territoryId}` | Update territory | `200 OK` |
| `DELETE` | `…/territories/{territoryId}` | Delete territory | `204 No Content` |

### Agencies

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/agencies` | List agencies | `200 OK` |
| `POST` | `…/agencies` | Create agency | `201 Created` |
| `GET` | `…/agencies/{agencyId}` | Get agency | `200 OK` |
| `PUT` | `…/agencies/{agencyId}` | Update agency | `200 OK` |
| `DELETE` | `…/agencies/{agencyId}` | Delete agency | `204 No Content` |

### Agents

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/agents` | List agents | `200 OK` |
| `POST` | `…/agents` | Create agent | `201 Created` |
| `GET` | `…/agents/{agentId}` | Get agent | `200 OK` |
| `PUT` | `…/agents/{agentId}` | Update agent | `200 OK` |
| `DELETE` | `…/agents/{agentId}` | Delete agent | `204 No Content` |

### Agent-Agency Assignments

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/agent-agencies` | List assignments | `200 OK` |
| `POST` | `…/agent-agencies` | Assign agent to agency | `201 Created` |
| `DELETE` | `…/agent-agencies/{relationshipId}` | Unassign agent | `204 No Content` |

### Shipments

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `POST` | `…/products/{productId}/shipments` | Ship item | `201 Created` |
| `GET` | `…/products/{productId}/shipments` | Track shipments | `200 OK` |

### Terms and Conditions

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/terms-and-conditions` | List terms and conditions | `200 OK` |
| `GET` | `…/terms-and-conditions/active` | Get active terms | `200 OK` |
| `GET` | `…/terms-and-conditions/latest` | Get latest terms | `200 OK` |
| `POST` | `…/terms-and-conditions` | Create terms and conditions | `201 Created` |
| `GET` | `…/terms-and-conditions/{tcId}` | Get terms detail | `200 OK` |
| `PUT` | `…/terms-and-conditions/{tcId}` | Update terms and conditions | `200 OK` |
| `DELETE` | `…/terms-and-conditions/{tcId}` | Delete terms and conditions | `204 No Content` |
| `PATCH` | `…/terms-and-conditions/{tcId}/sign` | Sign terms and conditions | `200 OK` |
| `PATCH` | `…/terms-and-conditions/{tcId}/activate` | Activate terms and conditions | `200 OK` |
| `PATCH` | `…/terms-and-conditions/{tcId}/deactivate` | Deactivate terms and conditions | `200 OK` |

### Operations

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/operations` | List operations | `200 OK` |
| `POST` | `…/operations` | Create operation | `201 Created` |
| `PUT` | `…/operations/{operationId}` | Update operation | `200 OK` |
| `DELETE` | `…/operations/{operationId}` | Delete operation | `204 No Content` |
| `PATCH` | `…/operations/{operationId}/activate` | Activate operation | `200 OK` |
| `PATCH` | `…/operations/{operationId}/deactivate` | Deactivate operation | `200 OK` |
| `GET` | `…/operations/can-operate` | Check can operate | `200 OK` |

### Configurations

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `GET` | `…/configurations` | List configurations | `200 OK` |
| `POST` | `…/configurations` | Create configuration | `201 Created` |
| `PUT` | `…/configurations/{configId}` | Update configuration | `200 OK` |
| `DELETE` | `…/configurations/{configId}` | Delete configuration | `204 No Content` |

### Simulations

| Method | Path | Summary | Response |
|--------|------|---------|----------|
| `POST` | `…/simulations` | Create simulation | `201 Created` |
| `GET` | `…/simulations/{simulationId}` | Get simulation | `200 OK` |

> All distributor-scoped paths above use the base `…` = `/api/v1/experience/distributors/{distributorId}`.

## Domain SDK Dependencies

| SDK | ClientFactory | APIs Used | Purpose |
|-----|---------------|-----------|---------|
| `domain-distributor-branding-sdk` | `DistributorBrandingClientFactory` | `DistributorApi`, `AgencyApi`, `AgentApi`, `AgentAgencyApi`, `TerritoryApi`, `TermsAndConditionsApi`, `OperationApi`, `ConfigurationApi` | Distributor profile, branding, network management, agents, T&C, operations, and configurations |
| `domain-distributor-catalog-sdk` | `DistributorCatalogClientFactory` | `DistributorApi`, `SimulationsApi` | Catalog management and simulations |
| `domain-product-catalog-sdk` | `ProductCatalogClientFactory` | `ProductsApi` | Product registration and retrieval |
| `domain-product-pricing-sdk` | `ProductPricingClientFactory` | `PricingApi`, `EligibilityApi`, `FeesApi` | Product pricing, eligibility checks, and fee calculations |
| `domain-common-contracts-sdk` | `CommonContractsClientFactory` | `ContractsApi`, `ContractPartiesApi`, `ContractTermsApi`, `ContractDocumentsApi`, `ContractSignaturesApi` | Contract lifecycle management |
| `domain-common-notifications-sdk` | `CommonNotificationsClientFactory` | `NotificationsApi` | Notification delivery |

## Setup

### Prerequisites

- **Java 25**
- **Maven 3.9+**
- Access to the FireflyFramework Maven repository for parent POM and BOM dependencies
- Running instances of the downstream domain services (or their APIs accessible at the configured base paths)

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_ADDRESS` | `localhost` | Address the server binds to |
| `SERVER_PORT` | `8097` | HTTP port |
| `ENDPOINT_DOMAIN_DISTRIBUTOR_BRANDING` | `http://localhost:8080` | Base URL for domain-distributor-branding |
| `ENDPOINT_DOMAIN_DISTRIBUTOR_CATALOG` | `http://localhost:8080` | Base URL for domain-distributor-catalog |
| `ENDPOINT_DOMAIN_PRODUCT_CATALOG` | `http://localhost:8080` | Base URL for domain-product-catalog |
| `ENDPOINT_DOMAIN_PRODUCT_PRICING` | `http://localhost:8080` | Base URL for domain-product-pricing |
| `ENDPOINT_DOMAIN_COMMON_CONTRACTS` | `http://localhost:8080` | Base URL for domain-common-contracts |
| `ENDPOINT_DOMAIN_COMMON_NOTIFICATIONS` | `http://localhost:8095` | Base URL for domain-common-notifications |

### Application Configuration

```yaml
spring:
  application:
    name: exp-distributor
    version: 1.0.0
    description: Experience layer API for distributor management journeys
  threads:
    virtual:
      enabled: true

server:
  address: ${SERVER_ADDRESS:localhost}
  port: ${SERVER_PORT:8097}
  shutdown: graceful

api-configuration:
  domain-platform:
    distributor-branding:
      base-path: ${ENDPOINT_DOMAIN_DISTRIBUTOR_BRANDING:http://localhost:8080}
    distributor-catalog:
      base-path: ${ENDPOINT_DOMAIN_DISTRIBUTOR_CATALOG:http://localhost:8080}
    product-catalog:
      base-path: ${ENDPOINT_DOMAIN_PRODUCT_CATALOG:http://localhost:8080}
    product-pricing:
      base-path: ${ENDPOINT_DOMAIN_PRODUCT_PRICING:http://localhost:8080}
    common-contracts:
      base-path: ${ENDPOINT_DOMAIN_COMMON_CONTRACTS:http://localhost:8080}
    common-notifications:
      base-path: ${ENDPOINT_DOMAIN_COMMON_NOTIFICATIONS:http://localhost:8095}
```

Swagger UI is available at `/swagger-ui.html` and the OpenAPI spec at `/v3/api-docs`.

Actuator endpoints exposed: `health`, `info`, `prometheus`.

### Build

```bash
mvn clean install
```

### Run

```bash
mvn -pl exp-distributor-web spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar exp-distributor-web/target/exp-distributor.jar
```

## Testing

The project includes test dependencies for unit and integration testing:

- **Spring Boot Test** (`spring-boot-starter-test`) -- JUnit 5, Mockito, AssertJ, and Spring test context support
- **Reactor Test** (`reactor-test`) -- `StepVerifier` for testing reactive streams

Run all tests:

```bash
mvn test
```

Run tests for a specific module:

```bash
mvn -pl exp-distributor-core test
mvn -pl exp-distributor-web test
```
