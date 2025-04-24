# API Documentation

The Banking Service provides a comprehensive RESTful API for managing clients, accounts, and transactions. This section provides detailed documentation for all available API endpoints.

## API Overview

All API endpoints are under the base path `/api/v2/`. The API follows RESTful conventions and uses standard HTTP methods:

- `GET`: Retrieve resources
- `POST`: Create new resources
- `PUT`: Update existing resources
- `DELETE`: Remove resources

## Authentication

The current version of the Banking Service does not include authentication. All endpoints are publicly accessible. In a production environment, you would want to implement proper authentication and authorization.

## Response Format

All API responses are in JSON format. Successful responses typically include:

- The requested data
- HTTP status code 200 (OK) for successful GET requests
- HTTP status code 201 (Created) for successful POST requests
- HTTP status code 204 (No Content) for successful DELETE requests

Error responses include:

- An error message
- An appropriate HTTP status code (4xx for client errors, 5xx for server errors)

## API Sections

The Banking Service API is divided into the following sections:

- [Client API](client-api.md): Endpoints for managing clients
- [Account API](account-api.md): Endpoints for managing accounts (general operations)
- [Current Account API](current-account-api.md): Endpoints specific to current accounts
- [Savings Account API](savings-account-api.md): Endpoints specific to savings accounts

## Common Request Headers

For all API requests that send data (POST, PUT), include the following header:

```
Content-Type: application/json
```

## Error Codes

The API uses standard HTTP status codes to indicate the success or failure of a request:

- 200 OK: The request was successful
- 201 Created: A new resource was successfully created
- 204 No Content: The request was successful, but there is no content to return
- 400 Bad Request: The request was invalid or cannot be served
- 404 Not Found: The requested resource does not exist
- 409 Conflict: The request conflicts with the current state of the server
- 422 Unprocessable Entity: The request was well-formed but contains semantic errors
- 500 Internal Server Error: An error occurred on the server

## API Versioning

The Banking Service uses URL-based versioning. The current version is v2, which is reflected in the base path `/api/v2/`. This approach ensures backward compatibility when new versions are introduced.

## Rate Limiting

The current version of the Banking Service does not implement rate limiting. In a production environment, you would want to implement rate limiting to prevent abuse.

## Next Steps

Explore the detailed documentation for each API section:

- [Client API](client-api.md)
- [Account API](account-api.md)
- [Current Account API](current-account-api.md)
- [Savings Account API](savings-account-api.md)

Or see practical examples in the [Usage Examples](usage-examples.md) section.
