# Usage Examples

This section provides practical examples of how to use the Banking Service API for common operations. These examples use `curl` commands that you can run from your terminal.

## Client Management Examples

### Creating a New Client

```bash
curl -X POST http://localhost:8080/api/v2/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName": "John", "lastName": "Doe", "email": "john.doe@example.com"}'
```

### Retrieving All Clients

```bash
curl -X GET http://localhost:8080/api/v2/clients
```

### Updating a Client

```bash
curl -X PUT http://localhost:8080/api/v2/clients/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName": "John", "lastName": "Smith", "email": "john.smith@example.com"}'
```

## Account Management Examples

### Creating a Current Account

```bash
curl -X POST http://localhost:8080/api/v2/current-accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerId": 1, "overdraftLimit": 1000, "overdraftInterestRate": 0.05}'
```

### Creating a Savings Account

```bash
curl -X POST http://localhost:8080/api/v2/savings-accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerId": 1, "interestRate": 0.02}'
```

### Retrieving All Accounts for a Client

```bash
curl -X GET http://localhost:8080/api/v2/accounts?ownerId=1
```

### Updating a Current Account

```bash
curl -X PUT http://localhost:8080/api/v2/current-accounts/DE123456789 \
  -H "Content-Type: application/json" \
  -d '{"overdraftLimit": 2000, "overdraftInterestRate": 0.06}'
```

## Transaction Examples

### Making a Deposit

```bash
curl -X POST http://localhost:8080/api/v2/accounts/DE123456789/deposits \
  -H "Content-Type: application/json" \
  -d '{"amount": 500, "description": "Salary", "entryType": "deposit"}'
```

### Making a Withdrawal

```bash
curl -X POST http://localhost:8080/api/v2/accounts/DE123456789/withdrawals \
  -H "Content-Type: application/json" \
  -d '{"amount": 200, "description": "ATM Withdrawal", "entryType": "withdraw"}'
```

### Viewing Transaction History

```bash
curl -X GET http://localhost:8080/api/v2/accounts/DE123456789/entries
```

### Filtering Transactions by Date

```bash
curl -X GET "http://localhost:8080/api/v2/accounts/DE123456789/entries?from=2023-01-01&to=2023-12-31"
```

## Complete Workflow Example

This example demonstrates a complete workflow from creating a client to making transactions:

### 1. Create a Client

```bash
curl -X POST http://localhost:8080/api/v2/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName": "Jane", "lastName": "Doe", "email": "jane.doe@example.com"}'
```

Response:
```json
{
  "id": 3,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com"
}
```

### 2. Create a Current Account for the Client

```bash
curl -X POST http://localhost:8080/api/v2/current-accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerId": 3, "overdraftLimit": 1000, "overdraftInterestRate": 0.05}'
```

Response:
```json
{
  "iban": "DE345678901",
  "balance": 0.00,
  "ownerId": 3,
  "overdraftLimit": 1000.00,
  "overdraftInterestRate": 0.05
}
```

### 3. Make a Deposit to the Account

```bash
curl -X POST http://localhost:8080/api/v2/accounts/DE345678901/deposits \
  -H "Content-Type: application/json" \
  -d '{"amount": 1500, "description": "Initial deposit", "entryType": "deposit"}'
```

Response:
```json
{
  "id": 5,
  "amount": 1500.00,
  "description": "Initial deposit",
  "timestamp": "2023-05-15T14:30:00",
  "type": "DEPOSIT"
}
```

### 4. Make a Withdrawal from the Account

```bash
curl -X POST http://localhost:8080/api/v2/accounts/DE345678901/withdrawals \
  -H "Content-Type: application/json" \
  -d '{"amount": 500, "description": "Online purchase", "entryType": "withdraw"}'
```

Response:
```json
{
  "id": 6,
  "amount": 500.00,
  "description": "Online purchase",
  "timestamp": "2023-05-16T09:45:00",
  "type": "WITHDRAW"
}
```

### 5. Check the Account Balance and Transaction History

```bash
curl -X GET http://localhost:8080/api/v2/accounts/DE345678901
```

Response:
```json
{
  "iban": "DE345678901",
  "balance": 1000.00,
  "ownerId": 3,
  "overdraftLimit": 1000.00,
  "overdraftInterestRate": 0.05
}
```

```bash
curl -X GET http://localhost:8080/api/v2/accounts/DE345678901/entries
```

Response:
```json
[
  {
    "id": 5,
    "amount": 1500.00,
    "description": "Initial deposit",
    "timestamp": "2023-05-15T14:30:00",
    "type": "DEPOSIT"
  },
  {
    "id": 6,
    "amount": 500.00,
    "description": "Online purchase",
    "timestamp": "2023-05-16T09:45:00",
    "type": "WITHDRAW"
  }
]
```

## Related Topics

- [Client API](client-api.md): Detailed API documentation for client operations
- [Account API](account-api.md): Detailed API documentation for account operations
- [Current Account API](current-account-api.md): Detailed API documentation for current account operations
- [Savings Account API](savings-account-api.md): Detailed API documentation for savings account operations
