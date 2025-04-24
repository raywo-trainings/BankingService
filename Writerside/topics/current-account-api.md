# Current Account API

The Current Account API provides endpoints for managing current accounts in the Banking Service. Current accounts are everyday banking accounts that allow for regular transactions and include an overdraft facility.

## Endpoints

### Get All Current Accounts

Retrieves a list of all current accounts in the system.

**Request:**
```
GET /api/v2/current-accounts
```

**Response:**
```json
[
  {
    "iban": "DE123456789",
    "balance": 1000.00,
    "ownerId": 1,
    "overdraftLimit": 500.00,
    "overdraftInterestRate": 0.05
  },
  {
    "iban": "DE234567890",
    "balance": 2500.00,
    "ownerId": 2,
    "overdraftLimit": 1000.00,
    "overdraftInterestRate": 0.06
  }
]
```

**Status Codes:**
- 200 OK: Successfully retrieved the list of current accounts

### Get Current Account by IBAN

Retrieves a specific current account by its IBAN.

**Request:**
```
GET /api/v2/current-accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Response:**
```json
{
  "iban": "DE123456789",
  "balance": 1000.00,
  "ownerId": 1,
  "overdraftLimit": 500.00,
  "overdraftInterestRate": 0.05
}
```

**Status Codes:**
- 200 OK: Successfully retrieved the current account
- 404 Not Found: Current account with the specified IBAN does not exist

### Create a Current Account

Creates a new current account for a client.

**Request:**
```
POST /api/v2/current-accounts
```

**Request Body:**
```json
{
  "ownerId": 1,
  "overdraftLimit": 500.00,
  "overdraftInterestRate": 0.05
}
```

**Response:**
```json
{
  "iban": "DE123456789",
  "balance": 0.00,
  "ownerId": 1,
  "overdraftLimit": 500.00,
  "overdraftInterestRate": 0.05
}
```

**Status Codes:**
- 201 Created: Successfully created the current account
- 400 Bad Request: Invalid input data
- 404 Not Found: Client with the specified ID does not exist

### Update a Current Account

Updates an existing current account's properties.

**Request:**
```
PUT /api/v2/current-accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Request Body:**
```json
{
  "overdraftLimit": 1000.00,
  "overdraftInterestRate": 0.06
}
```

**Response:**
```json
{
  "iban": "DE123456789",
  "balance": 1000.00,
  "ownerId": 1,
  "overdraftLimit": 1000.00,
  "overdraftInterestRate": 0.06
}
```

**Status Codes:**
- 200 OK: Successfully updated the current account
- 400 Bad Request: Invalid input data
- 404 Not Found: Current account with the specified IBAN does not exist

### Delete a Current Account

Deletes a current account from the system.

**Request:**
```
DELETE /api/v2/current-accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Response:**
No content

**Status Codes:**
- 204 No Content: Successfully deleted the current account
- 404 Not Found: Current account with the specified IBAN does not exist
- 422 Unprocessable Entity: Current account has a non-zero balance and cannot be deleted

## Data Model

### Current Account DTO

```json
{
  "iban": "DE123456789",
  "balance": 1000.00,
  "ownerId": 1,
  "overdraftLimit": 500.00,
  "overdraftInterestRate": 0.05
}
```

| Field | Type | Description |
|-------|------|-------------|
| iban | String | International Bank Account Number (unique identifier) |
| balance | BigDecimal | Current balance of the account |
| ownerId | Integer | ID of the client who owns the account |
| overdraftLimit | BigDecimal | Maximum amount that can be overdrawn |
| overdraftInterestRate | BigDecimal | Interest rate applied to overdrawn amounts (between 0 and 1) |

## Special Considerations

### Overdraft Facility

Current accounts include an overdraft facility that allows the account holder to withdraw more money than is available in the account, up to the overdraft limit. The available amount for withdrawal is calculated as:

```
availableAmount = balance + overdraftLimit
```

### Interest on Overdraft

When the account balance is negative (overdrawn), interest is charged on the overdrawn amount at the rate specified by `overdraftInterestRate`. This interest is typically calculated daily and applied monthly, but the exact calculation may vary.

## Related Topics

- [Account API](account-api.md): General account operations
- [Savings Account API](savings-account-api.md): API for savings accounts
- [API Documentation](api-documentation.md): Overview of all APIs
