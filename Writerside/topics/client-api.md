# Client API

The Client API provides endpoints for managing clients in the Banking Service.

## Endpoints

### Get All Clients

Retrieves a list of all clients in the system.

**Request:**
```
GET /api/v2/clients
```

**Response:**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  },
  {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com"
  }
]
```

**Status Codes:**
- 200 OK: Successfully retrieved the list of clients

### Get Client by ID

Retrieves a specific client by their ID.

**Request:**
```
GET /api/v2/clients/{id}
```

**Path Parameters:**
- `id`: The unique identifier of the client

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

**Status Codes:**
- 200 OK: Successfully retrieved the client
- 404 Not Found: Client with the specified ID does not exist

### Create a Client

Creates a new client in the system.

**Request:**
```
POST /api/v2/clients
```

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

**Status Codes:**
- 201 Created: Successfully created the client
- 400 Bad Request: Invalid input data
- 409 Conflict: A client with the same email already exists

### Update a Client

Updates an existing client's information.

**Request:**
```
PUT /api/v2/clients/{id}
```

**Path Parameters:**
- `id`: The unique identifier of the client

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

**Status Codes:**
- 200 OK: Successfully updated the client
- 400 Bad Request: Invalid input data
- 404 Not Found: Client with the specified ID does not exist
- 409 Conflict: Another client with the same email already exists

### Delete a Client

Deletes a client from the system.

**Request:**
```
DELETE /api/v2/clients/{id}
```

**Path Parameters:**
- `id`: The unique identifier of the client

**Response:**
No content

**Status Codes:**
- 204 No Content: Successfully deleted the client
- 404 Not Found: Client with the specified ID does not exist
- 422 Unprocessable Entity: Client has active accounts and cannot be deleted

## Data Model

### Client DTO

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Unique identifier for the client (auto-generated) |
| firstName | String | Client's first name |
| lastName | String | Client's last name |
| email | String | Client's email address (must be unique) |

## Related Topics

- [Account API](account-api.md): API for managing accounts
- [API Documentation](api-documentation.md): Overview of all APIs
