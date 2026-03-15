# Product Information Aggregator

## Overview

This project implements a **Product Information Aggregator** service for a B2B e-commerce platform serving agricultural and machinery parts across multiple European markets.

The service aggregates product data from multiple upstream services and exposes a **single REST endpoint** optimized for frontend consumption.

The main goals are:

- Reduce the number of client API calls
- Improve page load performance
- Provide consistent behavior when upstream services fail
- Centralize business logic related to product display

The service simulates a distributed environment with **mock upstream services** that include artificial latency and failure probabilities.

---

# Architecture

The application follows a **Hexagonal Architecture (Ports & Adapters)** pattern.

```
Controller (API Layer)
        │
        ▼
Application Service (ProductAggregationService)
        │
        ▼
Ports (CatalogPort, PricingPort, AvailabilityPort, CustomerPort)
        │
        ▼
Adapters (Mock services)
```

## Key architectural goals

- Separation of domain logic from infrastructure
- Easy replacement of upstream services
- High testability
- Resilience against partial failures

---

# API Endpoint

## Aggregated Product Endpoint

```
GET /products/{id}?market={market}&customerId={optional}
```

Example request:

```
GET /products/123?market=pl-PL
```

Example response:

```json
{
  "id": "123",
  "name": "Hydraulic Pump",
  "description": "High performance pump",
  "price": {
    "amount": 100,
    "currency": "EUR",
    "available": true
  },
  "stock": {
    "inStock": true,
    "quantity": 10
  },
  "customerSegment": "PRO"
}
```

---

# Upstream Services (Mocked)

| Service | Data | Latency | Reliability |
|-------|------|------|------|
| Catalog Service | Product details | 50ms | 99.9% |
| Pricing Service | Pricing + discounts | 80ms | 99.5% |
| Availability Service | Stock levels | 100ms | 98% |
| Customer Service | Customer segmentation | 60ms | 99% |

Mocks simulate:

- Network latency
- Random failures

---

# Aggregation Strategy

The aggregator calls upstream services **concurrently** using:

```java
StructuredTaskScope (Java 21)
```

## Benefits

- Reduced total latency
- Improved scalability
- Structured error handling

Expected response time is approximately **max service latency**, not the sum of all service latencies.

---

# Failure Handling

Different services have different failure criticality.

| Service | Behaviour |
|------|------|
| Catalog | Request fails completely |
| Pricing | Product returned, price unavailable |
| Availability | Product returned, stock unknown |
| Customer | Product returned without personalization |

Example degraded response:

```json
{
  "timestamp": "2026-03-15T07:49:23.430530Z",
  "status": 503,
  "error": "Service unavailable, caused by timeout.",
  "message": null
}
```

---

# Resilience Patterns

The system uses **Resilience4j** for fault tolerance.

Implemented patterns:

- Circuit Breaker – prevents cascading failures
- Retry – automatically retries transient failures

Example usage:

```java
@CircuitBreaker(name = "pricing")
@Retry(name = "pricing")
```

---

# Testing Strategy

## Unit Tests

Unit tests focus on validating the aggregation logic:

- correct aggregation of product data
- proper fallback behaviour when services fail

## Controller Tests

Controller layer is tested using **MockMvc**:

- REST endpoint validation
- HTTP status validation
- JSON response structure verification

## Concurrency Tests

Concurrency tests verify that:

- upstream services are executed in parallel
- total execution time depends on the slowest dependency rather than the sum of latencies

---

# How to Run

## Requirements

- Java 21
- Maven 3.9+

## Start the application

```
mvn spring-boot:run
```

Application will start on:

```
http://localhost:8080
```

Example request:

```
http://localhost:8080/products/1?market=pl-PL
```

---

# Design Decisions

## Hexagonal Architecture

The project uses Ports & Adapters architecture to:

- separate business logic from infrastructure
- improve testability
- allow easy replacement of upstream integrations

## Structured Concurrency

`StructuredTaskScope` from Java 21 is used to:

- execute upstream calls in parallel
- simplify concurrent error handling
- enforce request time limits

## Resilience4j

Resilience4j provides:

- circuit breaker support
- retry mechanisms
- protection against cascading failures

## Mock Services

Mock implementations simulate:

- realistic network latency
- random service failures

This helps demonstrate real distributed-system behaviour.

---

# Trade-offs

The current implementation intentionally omits some production features to keep the solution focused:

- No caching layer
- No distributed tracing
- No metrics or monitoring
- No authentication or authorization

---

# Future Improvements

Possible next steps for production readiness:

- Add caching layer for catalog and pricing data
- Integrate observability (Micrometer + Prometheus + Grafana)
- Implement distributed tracing (OpenTelemetry)
- Add rate limiting
- Replace mocks with real upstream services
- Add gRPC support for internal communication

---

# Design Question – Option A

## Adding a Related Products Service

Assume a new upstream service:

- Latency: 200ms
- Reliability: 90%

### Decision

This service should be treated as **optional**.

### Integration approach

- Call the service asynchronously using `StructuredTaskScope`
- Apply timeout protection
- Return an empty list when the service fails

### Reasoning

Related products are useful but **not critical for product display**, so they should not block the main product response.

---

# Summary

This implementation demonstrates:

- Concurrent service aggregation
- Graceful degradation on partial failures
- Clear architecture separation using Hexagonal Architecture
- High testability through ports and mocks
- Extensibility for additional upstream services