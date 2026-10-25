# DataPilot

[![DataPilot CI](https://github.com/ImamitK/datapilot/actions/workflows/ci.yml/badge.svg)](https://github.com/ImamitK/datapilot/actions/workflows/ci.yml)

### Enterprise Data Intelligence & RAG Platform

DataPilot is an evolving enterprise data platform designed to ingest, manage, validate, and investigate data using reliable backend engineering and, in later milestones, Generative AI and retrieval-augmented generation (RAG).

The goal is to build a practical, production-oriented system that connects data ingestion, data quality, metadata, search, and evidence-backed AI investigation.

Current milestone: Dataset management, versioned uploads, and PostgreSQL persistence.

> **Project status:** The backend foundation and file-upload capabilities are implemented. Automated content processing, data profiling, RAG, and AI investigation remain planned capabilities.

---

## Table of Contents

- [Why DataPilot?](#why-datapilot)
- [Current Status](#current-status)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Implemented APIs](#implemented-apis)
- [Error Handling](#error-handling)
- [Testing Strategy](#testing-strategy)
- [Running Locally](#running-locally)
- [Project Structure](#project-structure)
- [Development Roadmap](#development-roadmap)
- [Engineering Principles](#engineering-principles)
- [Architecture Decisions](#architecture-decisions)
- [Long-Term Vision](#long-term-vision)

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

The diagram represents the long-term vision, not the current implementation.

The objective is to build a platform where AI operates on reliable, structured data and supporting evidence rather than acting as a standalone chatbot.

---

# Current Status

### 

Implemented:

* ✅ Java 21 and Spring Boot REST API 
* ✅ PostgreSQL persistence using Spring Data JPA / Hibernate
* ✅ Dataset CRUD APIs and soft deletion
* ✅ Requestand response DTOs
* ✅ request validation
* ✅ Dataset name uniqueness
* ✅ Centralized exception handling
* ✅ Dataset version creation and retrieval APIs
* ✅ CSV and JSON file upload using multipart/form-data
* ✅ Local file storage organized by dataset ID
* ✅ Dataset version metadata persisted in PostgreSQL
* ✅ OpenAPI contract for the implemented APIs
* ✅ Service unit tests and controller/API tests
* ✅ PostgreSQL integration tests

### In Progress / Planned

* ⬜ Kafka event-driven processing
* ⬜ CSV and JSON content parsing
* ⬜ Schema and column detection
* ⬜ Automatic row-count calculation and data profiling
* ⬜ Data quality rules and validation reports
* ⬜ Asynchronous ingestion and processing
* ⬜ Kafka-based event-driven processing
* ⬜ Cloud object storage integration
* ⬜ RAG ingestion pipeline and vector search
* ⬜ AI-powered data investigation
* ⬜ Authentication and role-based access control
* ⬜ Observability and cloud deployment

Only capabilities implemented and verified by the code and tests should be treated as complete.

---

# Architecture

## Current Architecture

```text

                 Client / Postman
                       |
                      HTTP
                       |
                       v
           +---------------------------+
           | DataPilot API              |
           | Java 21 / Spring Boot      |
           |                            |
           | Controllers                |
           | Services                   |
           | Repositories               |
           +------------+---------------+
                        |
               +----+----------------+
               |                     |
               v                     v
       +----------------+   +-------------------+
       | PostgreSQL     |   | Local File Storage|
       | Datasets       |   | Uploaded CSV/JSON |
       | DatasetVersion |   | Files             |
       +----------------+   +-------------------+

```

## Target Architecture

```text
                      API / Gateway
                            |
                 +----------+----------+
                 |                     |
                 v                     v
           Dataset Services       AI Service
                 |                     |
                 v                     v
           Ingestion Jobs          RAG Pipeline
                 |                     |
                 v                     v
              Kafka                Vector Search
                 |                     |
                 v                     v
           Data Profiling              LLM
                 |
                 v
           Data Quality
                 |
                 v
           Evidence & Insights
```
This target architecture will evolve as the ingestion, processing, and AI capabilities are implemented.

### Architecture Documentation

Architecture documentation and API contracts are maintained in the repository's `docs/` directory where present.

---

## Technology Stack

### Currently Used

| Area | Technologies |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot, Spring MVC |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Build | Maven |
| Local environment | Docker, Docker Compose |
| API documentation | OpenAPI |
| Testing | JUnit 5, Mockito, Spring testing, Testcontainers where configured |
| Version control | Git, GitHub |

### Planned Technologies

| Area | Candidate technologies |
|---|---|
| Event-driven processing | Apache Kafka |
| Cloud storage | OCI Object Storage or AWS S3 |
| Vector search | pgvector |
| AI service | Python, FastAPI |
| Retrieval and generation | Embeddings, RAG, LLMs |
| AI tool integration | Tool calling and agent workflows |
| Observability | OpenTelemetry, metrics and dashboards |
| Cloud deployment | AWS or OCI, based on implementation needs |

Planned technologies are not part of the implemented stack unless explicitly stated otherwise.

Kafka deployment will be evaluated based on cost and operational complexity.

---

## Implemented APIs

Base URL for local development:

`http://localhost:8080`

### Dataset Management

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/v1/datasets` | Create a dataset |
| `GET` | `/api/v1/datasets` | List datasets |
| `GET` | `/api/v1/datasets/{id}` | Retrieve a dataset |
| `DELETE` | `/api/v1/datasets/{id}` | Soft-delete a dataset |

### Dataset Versions

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/v1/datasets/{datasetId}/versions` | Upload a file and create a version |
| `GET` | `/api/v1/datasets/{datasetId}/versions` | List versions for a dataset |

If an individual-version endpoint is implemented, document its exact path from the OpenAPI contract here.

### Create Dataset

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

Expected success status: `201 Created`.

Use the actual response DTO to determine the complete response schema.

The database record is retained rather than physically removed.

### Upload a Dataset Version

Request:

```http
POST /api/v1/datasets/{datasetId}/versions
Content-Type: multipart/form-data
```

Multipart form field:

| Field | Type | Description |
|---|---|---|
| `file` | File | CSV or JSON file to upload |

Expected success status: `201 Created`.

The upload operation stores the file locally and persists its version metadata in PostgreSQL.

Current upload constraints:

- Supported file extensions: `.csv` and `.json`
- Maximum configured upload size: 10 MB
- Storage: local filesystem
- Version metadata: PostgreSQL

**Important:** File upload does not yet mean that DataPilot has parsed, profiled, or validated the contents of the file.

### List Dataset Versions

Request:

```http
GET /api/v1/datasets/{datasetId}/versions
```

Returns the versions associated with the specified dataset.

Refer to the OpenAPI contract and response DTOs for the exact response structure.

### API Contract

The API contract is maintained in:

`docs/api/openapi.yaml`

The contract should remain synchronized with the implemented controllers, DTOs, validation rules, and HTTP responses.

---

## Error Handling

DataPilot uses centralized REST exception handling.

Current API semantics include:

| Situation                            |       HTTP Status               |       
| ------------------------------------ | -------------------------------:|
| Successful creation                  |            `201 Created`        |
| Successful read                      |                 `200 OK`        |
| Successful soft delete               |         `204 No Content`        |
| Invalid request                      |        `400 Bad Request`        |
| Dataset not found/doesn't exist      |          `404 Not Found`        |
| Duplicate dataset                    |           `409 Conflict`        |
| Invalid request or empty upload      |        `400 Bad Request`        |
| Dataset version doesn't exist        |          `404 Not Found`        |
| Upload exceeds configured limit      |  `413 Payload Too Large`        |

These mappings must be verified against the current exception handler and automated tests before being considered complete.

Error responses should provide a consistent structure with the status, message, and request path where supported by the response DTO.

## Testing Strategy

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
Test business logic in isolation, including:

- Dataset creation and retrieval
- Duplicate dataset handling
- Missing dataset behavior
- Dataset version creation
- Invalid upload validation
- File storage behavior
```

## Controller / API Tests

Spring MockMvc verifies the HTTP API contract.

Examples:

```text
Verify HTTP behavior, including:

- Successful dataset creation
- Dataset retrieval
- Missing-resource responses
- Invalid request validation
- Dataset deletion
- Version upload
- Version listing
- Unsupported file types
- Empty uploads
- Upload-size limits
```

### PostgreSQL Integration Tests

Integration tests verify persistence behavior against PostgreSQL where configured.

The intended persistence path is:

```text
       Spring Boot
            |
            v
       JPA / Hibernate
            |
            v
           JDBC
            |
            v
       PostgreSQL
```
### Run the Tests

From the `datapilot-api` directory:

```powershell
       .\mvnw.cmd clean test
```

If the Maven wrapper is not present, use the installed Maven executable:

```powershell
       mvn clean test
```

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

## Project Structure

```text
              datapilot/
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
                  ├── .gitignore
                  └── src/
                      ├── main/
                      │   ├── java/com/datapilot/api/
                      │   │   ├── controller/
                      │   │   ├── dto/
                      │   │   ├── entity/
                      │   │   ├── exception/
                      │   │   ├── repository/
                      │   │   └── service/
                      │   └── resources/
                      │       └── application.yml
                      └── test/
                          └── java/com/datapilot/api/
```

This is a representative structure. Keep it synchronized with the actual repository as new modules and files are added.

---

## Running Locally

### Prerequisites

- Java 21
- Docker Desktop
- Git
- Maven, or the repository's Maven wrapper

### 1. Clone the Repository

```powershell
git clone https://github.com/ImamitK/datapilot.git
cd datapilot
```

### 2. Start the Development Environment

Navigate to the API module:

```powershell
cd datapilot-api
```

Start the services defined in the Compose configuration:

```powershell
docker compose up -d
```

Check the running containers:

```powershell
docker compose ps
```

If you prefer running the API directly from your IDE, start only the database service defined in `docker-compose.yml`, then run the Spring Boot application from IntelliJ IDEA or Maven.

### 3. Run the Tests

```powershell
.\mvnw.cmd clean test
```

### 4. Access the API

```text
http://localhost:8080
```

Use the endpoints listed in the API section or the OpenAPI contract.

### Configuration Notes

- PostgreSQL connection settings are configured through the application's Spring configuration.
- File storage uses the `datapilot.storage.directory` property.
- The default development storage directory is `./datapilot-uploads` unless overridden.
- The upload-size limit is configured through Spring multipart settings.
- Uploaded files and local environment secrets should not be committed to Git.

Use the actual health-check endpoint implemented in the application if one is available.

---

## Development Roadmap

### Phase 1 — Backend Foundation

- Java and Spring Boot setup
- PostgreSQL integration
- Dataset management APIs
- API contracts and DTOs
- Validation and exception handling
- Initial automated testing

**Status:** Implemented foundation.

### Phase 2 — Versioned File Ingestion

- Dataset version creation and retrieval
- CSV and JSON uploads
- Local file storage
- File metadata persistence
- Upload validation and error handling

**Status:** Implemented; verify tests and API behavior as changes are made.

### Phase 3 — Data Understanding

- CSV and JSON parsing
- Schema and column detection
- Row-count calculation
- Data type inference
- Basic data profiling
- Malformed-file handling
- Processing status management

**Status:** Planned next milestone.

### Phase 4 — Data Quality and Processing

- Data quality rules
- Null and duplicate analysis
- Profiling reports
- Background ingestion jobs
- Retry and failure handling
- Event-driven processing where justified

### Phase 5 — Retrieval-Augmented Generation

- Dataset documentation and metadata retrieval
- Document and metadata indexing
- Embeddings and vector search
- Retrieval evaluation
- Evidence-backed responses
- RAG pipeline integration

### Phase 6 — AI-Assisted Investigation

Potential investigation capabilities:

- Retrieve dataset schema
- Inspect column profiles
- Find data quality issues
- Search dataset documentation
- Explain changes between dataset versions
- Recommend investigation steps

Any SQL execution or tool-based operations will require appropriate access controls, validation, resource limits, and auditing.

### Phase 7 — Production Readiness

- Authentication and authorization
- Role-based access control
- Observability and tracing
- Resilience and operational safeguards
- CI/CD
- Cloud deployment
- Security hardening

---

## Engineering Principles

### API-First Design

Keep the OpenAPI contract aligned with the implementation.

### Separation of Concerns

Controllers handle HTTP interactions, services contain business logic, repositories handle persistence, and DTOs define API contracts.

### Data Integrity

Use application validation together with appropriate database constraints.

### Testability

Test business logic, API behavior, and persistence at the appropriate levels.

### Explicit Failure Handling

Return meaningful HTTP status codes and consistent error responses.

### Incremental Architecture

Introduce Kafka, vector databases, AI services, and distributed processing when a demonstrated requirement justifies them, rather than adding infrastructure prematurely.

---

## Architecture Decisions

Important architectural decisions should be recorded in Architecture Decision Records (ADRs).

The repository's `docs/decisions/` and `docs/architecture/` directories should contain the decisions that actually exist.

Potential future decisions include:

- File storage abstraction
- Asynchronous ingestion
- Cloud object storage
- Search and vector indexing
- AI tool execution and security

Do not list proposed ADRs as completed documents until they have been created.

---

## Long-Term Vision

The long-term goal is to help engineers investigate enterprise data using evidence.

For example, a future version of DataPilot could answer:

> Why did the quality score of customer data decrease this week?

An investigation might identify:

- An increase in missing values in a critical column
- A schema change between two dataset versions
- The ingestion job associated with the change
- The downstream data-quality impact
- Suggested remediation steps

These are illustrative future capabilities, not features currently claimed as implemented.

The guiding principle is to combine reliable data engineering with retrieval and AI-assisted investigation so that answers are grounded in available data and evidence.

---

## Author

**Amit Kaushik**

Java / Data Platform Engineer

Areas of focus:

- Java and Spring Boot
- Backend and API engineering
- Data platforms
- Distributed systems
- Cloud engineering
- Generative AI and RAG

---

DataPilot is an actively developed proof-of-work project. Each milestone aims to add a working capability supported by appropriate tests, documentation, and engineering decisions.

