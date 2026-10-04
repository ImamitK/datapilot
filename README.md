# DataPilot

Enterprise Data Intelligence & RAG Platform.

## Vision

DataPilot helps engineering and data teams ingest, understand,
validate and investigate enterprise data using traditional
data engineering capabilities combined with GenAI.

## Current Status

Day 1 — Project foundation.

## Architecture

Coming soon.

## Technology

- Java 21
- Spring Boot
- PostgreSQL
- Docker
- Maven

## Roadmap

- Dataset ingestion
- Data profiling
- Data quality engine
- Event-driven processing
- RAG
- AI Data Assistant
- Tool-using AI Agent
- AWS deployment


## Day 2: Dataset Management

Dataset Management

	DataPilot currently provides basic dataset management APIs.

API: 

Method 		Endpoint				Description					Success

POST		/api/v1/datasets		Create a dataset			201 Created

GET			/api/v1/datasets		Get all active datasets		200 OK

GET			/api/v1/datasets/{id}	Get a dataset by ID			200 OK

DELETE		/api/v1/datasets/{id}	Soft-delete a dataset		204 No Content


Create Dataset:

Request : 

	POST /api/v1/datasets
	Content-Type: application/json
	{
	  "name": "customer-data",
	  "description": "Customer master dataset",
	  "sourceType": "CSV"
	}

Response — 201 Created

	{
	  "id": "02df195c-f0fb-41ad-9268-bc014049de96",
	  "name": "customer-data",
	  "description": "Customer master dataset",
	  "sourceType": "CSV",
	  "status": "ACTIVE",
	  "createdAt": "2026-10-04T10:30:00Z",
	  "updatedAt": "2026-10-04T10:30:00Z"
	}
	
Get Dataset

	GET /api/v1/datasets/02df195c-f0fb-41ad-9268-bc014049de96

Response — 200 OK

	{
	  "id": "02df195c-f0fb-41ad-9268-bc014049de96",
	  "name": "customer-data",
	  "description": "Customer master dataset",
	  "sourceType": "CSV",
	  "status": "ACTIVE",
	  "createdAt": "2026-10-04T10:30:00Z",
	  "updatedAt": "2026-10-04T10:30:00Z"
	}
Delete Dataset

	DELETE /api/v1/datasets/02df195c-f0fb-41ad-9268-bc014049de96

Response — 204 No Content

	The dataset is soft-deleted. Its database record is retained, but its status changes from:

ACTIVE → INACTIVE

Common Errors:

400 Bad Request

	Invalid request or validation failure.
404 Not Found

	Dataset does not exist.
409 Conflict

	A dataset with the same name already exists.
