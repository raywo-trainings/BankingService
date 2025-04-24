# Client Management

The Banking Service provides comprehensive client management capabilities through its API. This section explains how to manage clients in the system.

## Client Data Model

A client in the Banking Service has the following attributes:

- **ID**: A unique identifier for the client (automatically generated)
- **First Name**: The client's first name
- **Last Name**: The client's last name
- **Email**: The client's email address (must be unique)

## Client Operations

### Creating a Client

To create a new client, you need to provide the client's personal information:

```bash
curl -X POST http://localhost:8080/api/v2/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName": "John", "lastName": "Doe", "email": "john.doe@example.com"}'
```

The response will include the newly created client with its assigned ID.

### Retrieving Clients

#### Get All Clients

To retrieve a list of all clients:

```bash
curl -X GET http://localhost:8080/api/v2/clients
```

#### Get a Specific Client

To retrieve a specific client by ID:

```bash
curl -X GET http://localhost:8080/api/v2/clients/1
```

### Updating a Client

To update an existing client's information:

```bash
curl -X PUT http://localhost:8080/api/v2/clients/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName": "John", "lastName": "Smith", "email": "john.smith@example.com"}'
```

### Deleting a Client

To delete a client:

```bash
curl -X DELETE http://localhost:8080/api/v2/clients/1
```

Note: A client can only be deleted if they don't have any active accounts. All accounts must be closed before a client can be deleted.

## Validation Rules

The following validation rules apply to client operations:

- First name and last name cannot be empty
- Email must be a valid email address format
- Email must be unique in the system
- A client cannot be deleted if they have active accounts

## Error Handling

The API returns appropriate error messages when validation fails:

- 400 Bad Request: For invalid input data
- 404 Not Found: When a client with the specified ID doesn't exist
- 409 Conflict: When trying to create a client with an email that already exists
- 422 Unprocessable Entity: When trying to delete a client with active accounts

## Related Topics

- [Account Management](account-management.md): Learn how to manage accounts for clients
- [API Documentation](api-documentation.md): Complete API reference
