# DataPilot

### Enterprise Data Intelligence & RAG Platform

DataPilot is a production-oriented data platform designed to help engineering and data teams **ingest, understand, validate, monitor, and investigate enterprise data** using modern data engineering and Generative AI techniques.

The project combines **Java/Spring backend engineering, event-driven data processing, data quality, RAG, vector search, and AI agents** into one end-to-end platform.

> **Goal:** Build an enterprise-style platform that can move from raw data → data quality insights → evidence-backed AI investigation.

---

## Why DataPilot?

Most AI portfolio projects demonstrate a simple:

```text
Prompt → LLM → Answer
```

DataPilot takes a different approach:

```text
Enterprise Data
       ↓
Ingestion
       ↓
Profiling & Data Quality
       ↓
Metadata + Documentation
       ↓
RAG / Vector Search
       ↓
AI Investigation Agent
       ↓
Evidence-backed Answer & Recommendation
```

The AI is built around the **data platform**, rather than being a standalone chatbot.

---

# Current Status

### Day 2 — Dataset Management

Implemented:

* ✅ Java 21 + Spring Boot foundation
* ✅ PostgreSQL persistence
* ✅ Dataset CRUD APIs
* ✅ Request/response DTOs
* ✅ Request validation
* ✅ Dataset name uniqueness
* ✅ Global exception handling
* ✅ `400 Bad Request` validation responses
* ✅ `404 Not Found` handling
* ✅ `409 Conflict` for duplicate datasets
* ✅ Soft deletion (`ACTIVE → INACTIVE`)
* ✅ OpenAPI API contract
* ✅ Service unit tests
* ✅ Controller/API tests
* ✅ PostgreSQL integration test using Testcontainers

### In Progress / Planned

* ⬜ Dataset ingestion
* ⬜ Dataset versions
* ⬜ Kafka event-driven processing
* ⬜ Data profiling engine
* ⬜ Data quality rules
* ⬜ Object storage integration
* ⬜ RAG ingestion pipeline
* ⬜ Vector search with pgvector
* ⬜ AI Data Assistant
* ⬜ Tool-using Data Investigation Agent
* ⬜ Authentication and RBAC
* ⬜ Observability
* ⬜ AWS deployment

---

# Architecture

## Current Architecture

```text
Client / Postman
       |
      HTTP
       |
       v
+-------------------------+
| DataPilot API           |
| Java 21 / Spring Boot   |
|                         |
| Controller              |
| Service                 |
| Repository              |
+------------+------------+
             |
          JPA / Hibernate
             |
             v
+-------------------------+
| PostgreSQL              |
| datasets                |
+-------------------------+
```

## Target Architecture

```text
                       +------------------+
                       |  React / Client  |
                       +--------+---------+
                                |
                                v
                    +----------------------+
                    | API / Gateway        |
                    | Java 21 / Spring     |
                    +----------+-----------+
                               |
                    +----------+----------+
                    |                     |
                    v                     v
             Dataset / Job         AI Service
             Services              Python / FastAPI
                    |                     |
                    v                     v
                 Kafka                 RAG / Agent
                    |                     |
          +---------+---------+      pgvector
          |                   |          |
          v                   v          v
      Profiling           Data Quality   LLM
          |                   |
          +---------+---------+
                    |
                    v
             PostgreSQL / S3
```

Architecture documentation is maintained under:

```text
docs/
├── architecture/
└── decisions/
```

---

# Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* Hibernate
* Maven

### Data

* PostgreSQL
* pgvector
* Object storage / S3
* Kafka

### AI

* Python
* FastAPI
* Embeddings
* RAG
* LLMs
* Tool calling
* AI agents

### Engineering

* Docker
* Docker Compose
* Testcontainers
* JUnit 5
* Mockito
* MockMvc
* GitHub Actions
* OpenTelemetry
* Grafana

### Cloud

AWS is the initial target cloud platform for deployment.

Planned services include:

```text
S3
RDS PostgreSQL
ECS / Fargate
```

Kafka deployment will be evaluated based on cost and operational complexity.

---

# Dataset Management API

## Create Dataset

```http
POST /api/v1/datasets
Content-Type: application/json
```

Request:

```json
{
  "name": "customer-data",
  "description": "Customer master dataset",
  "sourceType": "CSV"
}
```

Response:

```http
201 Created
```

```json
{
  "id": "02df195c-f0fb-41ad-9268-bc014049de96",
  "name": "customer-data",
  "description": "Customer master dataset",
  "sourceType": "CSV",
  "status": "ACTIVE",
  "createdAt": "2026-10-04T10:30:00Z",
  "updatedAt": "2026-10-04T10:30:00Z"
}
```

## Get All Datasets

```http
GET /api/v1/datasets
```

Returns active datasets.

## Get Dataset

```http
GET /api/v1/datasets/{id}
```

Example:

```http
GET /api/v1/datasets/02df195c-f0fb-41ad-9268-bc014049de96
```

## Delete Dataset

```http
DELETE /api/v1/datasets/{id}
```

Response:

```http
204 No Content
```

DataPilot currently uses **soft deletion**:

```text
ACTIVE
   ↓
DELETE
   ↓
INACTIVE
```

The database record is retained rather than physically removed.

---

# API Contract

The machine-readable API contract is maintained using OpenAPI:

```text
docs/api/openapi.yaml
```

The API contract defines:

* endpoints
* request schemas
* response schemas
* validation requirements
* HTTP status codes
* error responses
* examples

---

# Error Handling

DataPilot uses centralized REST exception handling.

Current API semantics include:

| Situation              |       HTTP Status |
| ---------------------- | ----------------: |
| Successful creation    |     `201 Created` |
| Successful read        |          `200 OK` |
| Successful soft delete |  `204 No Content` |
| Invalid request        | `400 Bad Request` |
| Dataset not found      |   `404 Not Found` |
| Duplicate dataset      |    `409 Conflict` |

Example duplicate response:

```json
{
  "timestamp": "2026-10-04T10:50:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Dataset already exists: customer-data",
  "path": "/api/v1/datasets"
}
```

---

# Testing Strategy

DataPilot uses multiple levels of automated testing.

```text
                  Integration Tests
                         ▲
                         |
                  Controller Tests
                         ▲
                         |
                     Unit Tests
```

## Unit Tests

JUnit 5 + Mockito are used to test service/business logic in isolation.

Examples:

```text
Create dataset
Get dataset
Get nonexistent dataset
Delete dataset
Duplicate dataset
```

## Controller / API Tests

Spring MockMvc verifies the HTTP API contract.

Examples:

```text
POST → 201
GET → 200
GET missing → 404
Invalid request → 400
DELETE → 204
Duplicate → 409
```

## Integration Tests

Testcontainers is used to run PostgreSQL for integration testing.

The integration test verifies:

```text
Spring Boot
    ↓
JPA / Hibernate
    ↓
JDBC
    ↓
PostgreSQL
```

This ensures that the persistence layer works against a real PostgreSQL database rather than a mock.

---

# Engineering Principles

DataPilot is intentionally being built using production-oriented engineering practices.

### API-first design

API contracts are defined before implementation.

```text
OpenAPI Contract
      ↓
DTO
      ↓
Controller
      ↓
Service
      ↓
Repository
```

### Separation of concerns

The application separates:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Persistence entities are not exposed directly as the public API contract.

### Database-enforced integrity

Application validation is combined with database constraints.

For example, dataset names are protected by a database-level unique constraint.

### Soft deletion

Datasets are not immediately physically removed, allowing future lineage, audit, and recovery capabilities.

### Testability

Business logic, HTTP behavior, and persistence are tested at different levels.

### Architecture decisions

Important technical decisions are documented using Architecture Decision Records:

```text
docs/architecture/
docs/decisions/
```

---

# Project Structure

```text
datapilot/
│
├── README.md
├── docs/
│   ├── api/
│   │   └── openapi.yaml
│   ├── architecture/
│   └── decisions/
│
└── datapilot-api/
    ├── pom.xml
    ├── Dockerfile
    ├── docker-compose.yml
    │
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── com/datapilot/api/
        │   │       ├── controller/
        │   │       ├── dto/
        │   │       ├── entity/
        │   │       ├── exception/
        │   │       ├── repository/
        │   │       └── service/
        │   │
        │   └── resources/
        │       └── application.yml
        │
        └── test/
            └── java/
                └── com/datapilot/api/
```

---

# Running Locally

### Prerequisites

```text
Java 21
Docker Desktop
Git
```

### Start the application

From `datapilot-api`:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### API

```text
http://localhost:8080
```

### Health Check

```http
GET /api/v1/health
```

Expected response:

```json
{
  "status": "UP",
  "service": "datapilot-api"
}
```

---

# Development Roadmap

## Phase 1 — Foundation

* Java 21
* Spring Boot
* PostgreSQL
* REST APIs
* API contracts
* Testing foundation

## Phase 2 — Data Ingestion

* CSV / JSON ingestion
* Dataset versions
* Object storage
* ingestion jobs

## Phase 3 — Event-Driven Processing

* Kafka
* asynchronous processing
* retries
* dead-letter queues
* idempotency
* job tracking

## Phase 4 — Data Intelligence

* schema detection
* profiling
* null analysis
* duplicate detection
* statistical summaries
* data quality rules

## Phase 5 — RAG

```text
Documents
   ↓
Parsing
   ↓
Chunking
   ↓
Embeddings
   ↓
pgvector
   ↓
Retrieval
   ↓
LLM
```

The assistant will provide citations and evidence rather than unsupported answers.

## Phase 6 — AI Data Investigation Agent

The agent will be able to use controlled tools such as:

```text
get_dataset_schema()
get_column_profile()
get_data_quality_issues()
search_documentation()
get_lineage()
execute_readonly_sql()
```

The SQL tool will be protected by:

* read-only database access
* SQL validation
* query timeout
* row limits
* audit logging

## Phase 7 — Production Engineering

* JWT authentication
* RBAC
* observability
* distributed tracing
* resilience
* security hardening
* CI/CD
* AWS deployment

---

# Architecture Decisions

Important architectural decisions are documented rather than hidden inside implementation details.

Examples:

```text
ADR-001 — Initial Architecture
ADR-002 — Java 21
ADR-003 — Kafka
ADR-004 — pgvector
ADR-005 — AI Agent Security
```

See:

```text
docs/architecture/
docs/decisions/
```

---

# Long-Term Vision

DataPilot is intended to evolve into an **enterprise data investigation platform** where engineers can ask questions such as:

> Why did the quality score of customer data decrease this week?

and receive an evidence-backed investigation:

```text
Quality score: 91% → 74%

Primary issue:
customer_email null rate increased
from 2.1% to 17.8%.

Detected change:
Ingestion job #1842

Likely cause:
customer_email column missing
from the source file.

Impact:
CRM export pipeline affected.

Recommended action:
Restore customer_email in the upstream
source pipeline and reprocess the affected
dataset version.
```

The long-term objective is to combine **data engineering + distributed systems + RAG + agentic AI** into a system that can investigate real enterprise data rather than simply generate text.

---

# Author

**Amit Kaushik**

Java / Data Platform Engineer

Focused on:

```text
Java
Spring Boot
Data Platforms
Distributed Systems
Kafka
Cloud
Generative AI
RAG
AI Agents
```

---

## Project Status

This project is being developed incrementally, with each milestone adding a production-oriented capability and corresponding tests, documentation, and architectural decisions.
