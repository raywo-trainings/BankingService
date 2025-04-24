# Savings Account API

The Savings Account API provides endpoints for managing savings accounts in the Banking Service. Savings accounts are designed for saving money and earning interest.

## Endpoints

### Get All Savings Accounts

Retrieves a list of all savings accounts in the system.

**Request:**
```
GET /api/v2/savings-accounts
```

**Response:**
```json
[
  {
    "iban": "DE987654321",
    "balance": 5000.00,
    "ownerId": 1,
    "interestRate": 0.02
  },
  {
    "iban": "DE876543210",
    "balance": 10000.00,
    "ownerId": 2,
    "interestRate": 0.03
  }
]
```

**Status Codes:**
- 200 OK: Successfully retrieved the list of savings accounts

### Get Savings Account by IBAN

Retrieves a specific savings account by its IBAN.

**Request:**
```
GET /api/v2/savings-accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Response:**
```json
{
  "iban": "DE987654321",
  "balance": 5000.00,
  "ownerId": 1,
  "interestRate": 0.02
}
```

**Status Codes:**
- 200 OK: Successfully retrieved the savings account
- 404 Not Found: Savings account with the specified IBAN does not exist

### Create a Savings Account

Creates a new savings account for a client.

**Request:**
```
POST /api/v2/savings-accounts
```

**Request Body:**
```json
{
  "ownerId": 1,
  "interestRate": 0.02
}
```

**Response:**
```json
{
  "iban": "DE987654321",
  "balance": 0.00,
  "ownerId": 1,
  "interestRate": 0.02
}
```

**Status Codes:**
- 201 Created: Successfully created the savings account
- 400 Bad Request: Invalid input data
- 404 Not Found: Client with the specified ID does not exist

### Update a Savings Account

Updates an existing savings account's properties.

**Request:**
```
PUT /api/v2/savings-accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Request Body:**
```json
{
  "interestRate": 0.03
}
```

**Response:**
```json
{
  "iban": "DE987654321",
  "balance": 5000.00,
  "ownerId": 1,
  "interestRate": 0.03
}
```

**Status Codes:**
- 200 OK: Successfully updated the savings account
- 400 Bad Request: Invalid input data
- 404 Not Found: Savings account with the specified IBAN does not exist

### Delete a Savings Account

Deletes a savings account from the system.

**Request:**
```
DELETE /api/v2/savings-accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Response:**
No content

**Status Codes:**
- 204 No Content: Successfully deleted the savings account
- 404 Not Found: Savings account with the specified IBAN does not exist
- 422 Unprocessable Entity: Savings account has a non-zero balance and cannot be deleted

## Data Model

### Savings Account DTO

```json
{
  "iban": "DE987654321",
  "balance": 5000.00,
  "ownerId": 1,
  "interestRate": 0.02
}
```

| Field | Type | Description |
|-------|------|-------------|
| iban | String | International Bank Account Number (unique identifier) |
| balance | BigDecimal | Current balance of the account |
| ownerId | Integer | ID of the client who owns the account |
| interestRate | BigDecimal | Interest rate applied to the account balance (between 0 and 1) |

## Special Considerations

### Interest Calculation

Savings accounts earn interest on the positive balance at the rate specified by `interestRate`. This interest is typically calculated daily and applied monthly or quarterly, but the exact calculation may vary.

### Withdrawal Limitations

Unlike current accounts, savings accounts do not have an overdraft facility. Withdrawals are limited to the available balance:

```
availableAmount = balance
```

Some savings accounts may also have additional withdrawal restrictions, such as:
- Minimum withdrawal amounts
- Maximum number of withdrawals per month
- Notice periods for large withdrawals

These restrictions are not currently implemented in the API but could be added in future versions.

## Related Topics

- [Account API](account-api.md): General account operations
- [Current Account API](current-account-api.md): API for current accounts
- [API Documentation](api-documentation.md): Overview of all APIs
