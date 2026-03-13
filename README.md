# exp-distributor

> Backend-for-Frontend service for the distributor portal — aggregates distributor profile, branding, catalog, network, agents, shipments, terms and conditions, operations, configurations, and simulations into a unified API

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Module Structure](#module-structure)
- [Functional Verticals](#functional-verticals)
- [API Endpoints](#api-endpoints)
- [Domain SDK Dependencies](#domain-sdk-dependencies)
- [Configuration](#configuration)
- [Running Locally](#running-locally)
- [Testing](#testing)

## Overview

`exp-distributor` is the experience-layer service that powers the distributor portal frontend. A distributor is a commercial entity that distributes financial products through its own network of agencies, agents, and territories. This service provides a single, purpose-built REST API surface covering every operation the portal requires — from registering a distributor and managing its visual branding to configuring its product catalog, managing its geographic territories and agent network, handling shipments, and running simulations.

The service uses **simple stateless composition** exclusively. Each endpoint delegates to one or more downstream domain SDK calls, optionally maps the result through a MapStruct mapper, and returns a frontend-optimised DTO. There is no `@Workflow`, no Redis, and no persistent journey state within this service. All business rules and state transitions are owned by the domain services that `exp-distributor` calls.

MapStruct is used in the `-core` module to translate between experience-layer DTOs and domain SDK models, keeping the service decoupled from downstream type changes without manual mapping boilerplate.

## Architecture

```
Distributor Portal (Frontend)
         |
         v
exp-distributor  (port 8097)
         |
         +---> domain-distributor-branding-sdk   (DistributorApi, AgencyApi, AgentApi,
         |                                        AgentAgencyApi, TerritoryApi,
         |                                        TermsAndConditionsApi, OperationApi,
         |                                        ConfigurationApi)
         |
         +---> domain-distributor-catalog-sdk     (DistributorApi, SimulationsApi)
         |
         +---> domain-product-catalog-sdk         (ProductsApi)
         |
         +---> domain-product-pricing-sdk         (PricingApi, EligibilityApi, FeesApi)
         |
         +---> domain-common-contracts-sdk        (ContractsApi, ContractPartiesApi,
         |                                        ContractTermsApi, ContractDocumentsApi,
         |                                        ContractSignaturesApi)
         |
         +---> domain-common-notifications-sdk    (NotificationsApi)
         |
         +---> core-common-distributor-mgmt-sdk   (DistributorMgmtApi)
```

## Module Structure

| Module | Purpose |
|--------|---------|
| `exp-distributor-interfaces` | Reserved for future shared contracts |
| `exp-distributor-core` | Service interfaces and implementations per vertical, MapStruct mappers, command and query DTOs |
| `exp-distributor-infra` | `ClientFactory` beans and `@ConfigurationProperties` for each downstream SDK |
| `exp-distributor-web` | Controllers (one per vertical), Spring Boot application class, `application.yaml` |
| `exp-distributor-sdk` | Auto-generated reactive SDK from the OpenAPI spec |

## Functional Verticals

| Vertical | Controller | Base Path | Endpoints |
|----------|-----------|-----------|-----------|
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

All distributor-scoped paths use the base prefix `/api/v1/experience/distributors/{distributorId}`, abbreviated as `…` in the tables below.

### Distributor Profile

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `POST` | `/api/v1/experience/distributors` | Register a new distributor | `201 Created` |

### Branding

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `PUT` | `…/brandings/{brandingId}` | Update the visual branding configuration (logo, colours, theme) | `200 OK` |
| `PUT` | `…/brandings/{brandingId}/set-default` | Set the specified branding as the active default for the distributor | `200 OK` |

### Catalog

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/catalog` | List all products in the distributor's catalog | `200 OK` |
| `POST` | `…/catalog` | Add a product to the distributor's catalog | `201 Created` |
| `PUT` | `…/catalog/{productId}` | Update a catalog item's terms or configuration | `200 OK` |
| `DELETE` | `…/catalog/{productId}` | Remove a product from the distributor's catalog | `200 OK` |

### Products

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `/api/v1/experience/products/{productId}` | Retrieve a product definition from the product catalog | `200 OK` |
| `POST` | `/api/v1/experience/products` | Register a new product in the product catalog | `201 Created` |

### Territories

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/territories` | List all authorised geographic territories for the distributor | `200 OK` |
| `POST` | `…/territories` | Create a new territory | `201 Created` |
| `GET` | `…/territories/{territoryId}` | Retrieve a single territory by its identifier | `200 OK` |
| `PUT` | `…/territories/{territoryId}` | Update a territory's name or boundaries | `200 OK` |
| `DELETE` | `…/territories/{territoryId}` | Delete a territory | `204 No Content` |

### Agencies

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/agencies` | List all agencies belonging to the distributor | `200 OK` |
| `POST` | `…/agencies` | Create a new agency | `201 Created` |
| `GET` | `…/agencies/{agencyId}` | Retrieve a single agency by its identifier | `200 OK` |
| `PUT` | `…/agencies/{agencyId}` | Update agency details | `200 OK` |
| `DELETE` | `…/agencies/{agencyId}` | Delete an agency | `204 No Content` |

### Agents

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/agents` | List all agents belonging to the distributor | `200 OK` |
| `POST` | `…/agents` | Create a new agent | `201 Created` |
| `GET` | `…/agents/{agentId}` | Retrieve a single agent by their identifier | `200 OK` |
| `PUT` | `…/agents/{agentId}` | Update an agent's details | `200 OK` |
| `DELETE` | `…/agents/{agentId}` | Delete an agent | `204 No Content` |

### Agent-Agency Assignments

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/agent-agencies` | List all agent-to-agency assignment relationships | `200 OK` |
| `POST` | `…/agent-agencies` | Assign an agent to an agency | `201 Created` |
| `DELETE` | `…/agent-agencies/{relationshipId}` | Remove an agent from an agency | `204 No Content` |

### Shipments

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `POST` | `…/products/{productId}/shipments` | Register a new shipment for a distributor product | `201 Created` |
| `GET` | `…/products/{productId}/shipments` | List all shipments for a distributor product | `200 OK` |

### Terms and Conditions

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/terms-and-conditions` | List all terms and conditions for the distributor | `200 OK` |
| `GET` | `…/terms-and-conditions/active` | Retrieve the currently active terms and conditions | `200 OK` |
| `GET` | `…/terms-and-conditions/latest` | Retrieve the latest version regardless of status | `200 OK` |
| `POST` | `…/terms-and-conditions` | Create a new terms and conditions document | `201 Created` |
| `GET` | `…/terms-and-conditions/{tcId}` | Retrieve a specific terms and conditions document | `200 OK` |
| `PUT` | `…/terms-and-conditions/{tcId}` | Update an existing terms and conditions document | `200 OK` |
| `DELETE` | `…/terms-and-conditions/{tcId}` | Delete a terms and conditions document | `204 No Content` |
| `PATCH` | `…/terms-and-conditions/{tcId}/sign` | Sign a terms and conditions document | `200 OK` |
| `PATCH` | `…/terms-and-conditions/{tcId}/activate` | Activate a terms and conditions document, making it the operative version | `200 OK` |
| `PATCH` | `…/terms-and-conditions/{tcId}/deactivate` | Deactivate a currently active terms and conditions document | `200 OK` |

### Operations

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/operations` | List all operations registered for the distributor | `200 OK` |
| `POST` | `…/operations` | Create a new operation | `201 Created` |
| `PUT` | `…/operations/{operationId}` | Update an operation's configuration | `200 OK` |
| `DELETE` | `…/operations/{operationId}` | Delete an operation | `204 No Content` |
| `PATCH` | `…/operations/{operationId}/activate` | Activate an operation | `200 OK` |
| `PATCH` | `…/operations/{operationId}/deactivate` | Deactivate an operation | `200 OK` |
| `GET` | `…/operations/can-operate` | Check whether the distributor is currently authorised to operate | `200 OK` |

### Configurations

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `…/configurations` | List all configuration entries for the distributor | `200 OK` |
| `POST` | `…/configurations` | Create a new configuration entry | `201 Created` |
| `PUT` | `…/configurations/{configId}` | Update a configuration entry | `200 OK` |
| `DELETE` | `…/configurations/{configId}` | Delete a configuration entry | `204 No Content` |

### Simulations

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `POST` | `…/simulations` | Create a product simulation for the distributor | `201 Created` |
| `GET` | `…/simulations/{simulationId}` | Retrieve a previously created simulation result | `200 OK` |

## Domain SDK Dependencies

| SDK | ClientFactory | APIs Used | Purpose |
|-----|--------------|-----------|---------|
| `domain-distributor-branding-sdk` | `DistributorBrandingClientFactory` | `DistributorApi`, `AgencyApi`, `AgentApi`, `AgentAgencyApi`, `TerritoryApi`, `TermsAndConditionsApi`, `OperationApi`, `ConfigurationApi` | Distributor profile, branding, network management (agencies, agents, territories), T&C lifecycle, operations, and configurations |
| `domain-distributor-catalog-sdk` | `DistributorCatalogClientFactory` | `DistributorApi`, `SimulationsApi` | Distributor catalog management and product simulations |
| `domain-product-catalog-sdk` | `ProductCatalogClientFactory` | `ProductsApi` | Product registration and retrieval from the global product catalog |
| `domain-product-pricing-sdk` | `ProductPricingClientFactory` | `PricingApi`, `EligibilityApi`, `FeesApi` | Product pricing, eligibility checks, and fee calculations |
| `domain-common-contracts-sdk` | `CommonContractsClientFactory` | `ContractsApi`, `ContractPartiesApi`, `ContractTermsApi`, `ContractDocumentsApi`, `ContractSignaturesApi` | Contract lifecycle management for distributor agreements |
| `domain-common-notifications-sdk` | `CommonNotificationsClientFactory` | `NotificationsApi` | Notification delivery to distributors and agents |
| `core-common-distributor-mgmt-sdk` | `CoreDistributorClientFactory` | `DistributorMgmtApi` | Core-level distributor entity management |

## Configuration

```yaml
server:
  port: ${SERVER_PORT:8097}

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
    core-distributor:
      base-path: ${ENDPOINT_CORE_DISTRIBUTOR:http://localhost:8080}
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8097` | HTTP server port |
| `SERVER_ADDRESS` | `localhost` | Bind address |
| `ENDPOINT_DOMAIN_DISTRIBUTOR_BRANDING` | `http://localhost:8080` | Base URL for `domain-distributor-branding` |
| `ENDPOINT_DOMAIN_DISTRIBUTOR_CATALOG` | `http://localhost:8080` | Base URL for `domain-distributor-catalog` |
| `ENDPOINT_DOMAIN_PRODUCT_CATALOG` | `http://localhost:8080` | Base URL for `domain-product-catalog` |
| `ENDPOINT_DOMAIN_PRODUCT_PRICING` | `http://localhost:8080` | Base URL for `domain-product-pricing` |
| `ENDPOINT_DOMAIN_COMMON_CONTRACTS` | `http://localhost:8080` | Base URL for `domain-common-contracts` |
| `ENDPOINT_DOMAIN_COMMON_NOTIFICATIONS` | `http://localhost:8095` | Base URL for `domain-common-notifications` |
| `ENDPOINT_CORE_DISTRIBUTOR` | `http://localhost:8080` | Base URL for `core-common-distributor-mgmt` |

## Running Locally

```bash
# Prerequisites — ensure downstream domain services are running or locally installed
cd exp-distributor
mvn spring-boot:run -pl exp-distributor-web
```

Server starts on port `8097`. Swagger UI: [http://localhost:8097/swagger-ui.html](http://localhost:8097/swagger-ui.html)

## Testing

```bash
mvn clean verify
```

Tests cover service implementations in `exp-distributor-core` (unit tests with mocked SDK clients using Mockito and `StepVerifier`) and all 13 controllers in `exp-distributor-web` (WebTestClient-based tests verifying HTTP status codes and response shapes for all 52 endpoints).
