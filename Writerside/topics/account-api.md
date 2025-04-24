# Account API

The Account API provides endpoints for managing accounts in the Banking Service. This API includes general operations that apply to all account types.

## Endpoints

### Get All Accounts

Retrieves a list of all accounts in the system.

**Request:**
```
GET /api/v2/accounts
```

**Query Parameters:**
- `ownerId` (optional): Filter accounts by owner ID

**Response:**
```json
[
  {
    "iban": "DE123456789",
    "balance": 1000.00,
    "ownerId": 1,
    "type": "CURRENT"
  },
  {
    "iban": "DE987654321",
    "balance": 5000.00,
    "ownerId": 2,
    "type": "SAVINGS"
  }
]
```

**Status Codes:**
- 200 OK: Successfully retrieved the list of accounts

### Get Account by IBAN

Retrieves a specific account by its IBAN.

**Request:**
```
GET /api/v2/accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Response:**
```json
{
  "iban": "DE123456789",
  "balance": 1000.00,
  "ownerId": 1,
  "type": "CURRENT"
}
```

**Status Codes:**
- 200 OK: Successfully retrieved the account
- 404 Not Found: Account with the specified IBAN does not exist

### Delete Account

Deletes an account from the system.

**Request:**
```
DELETE /api/v2/accounts/{iban}
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Response:**
No content

**Status Codes:**
- 204 No Content: Successfully deleted the account
- 404 Not Found: Account with the specified IBAN does not exist
- 422 Unprocessable Entity: Account has a non-zero balance and cannot be deleted

### Get Account Entries

Retrieves the transaction history for an account.

**Request:**
```
GET /api/v2/accounts/{iban}/entries
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Query Parameters:**
- `from` (optional): Start date for filtering entries (format: YYYY-MM-DD)
- `to` (optional): End date for filtering entries (format: YYYY-MM-DD)

**Response:**
```json
[
  {
    "id": 1,
    "amount": 500.00,
    "description": "Salary",
    "timestamp": "2023-01-15T10:30:00",
    "type": "DEPOSIT"
  },
  {
    "id": 2,
    "amount": 200.00,
    "description": "ATM Withdrawal",
    "timestamp": "2023-01-20T15:45:00",
    "type": "WITHDRAW"
  }
]
```

**Status Codes:**
- 200 OK: Successfully retrieved the account entries
- 404 Not Found: Account with the specified IBAN does not exist

### Make a Deposit

Makes a deposit to an account.

**Request:**
```
POST /api/v2/accounts/{iban}/deposits
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Request Body:**
```json
{
  "amount": 500.00,
  "description": "Salary",
  "entryType": "deposit"
}
```

**Response:**
```json
{
  "id": 3,
  "amount": 500.00,
  "description": "Salary",
  "timestamp": "2023-02-15T10:30:00",
  "type": "DEPOSIT"
}
```

**Status Codes:**
- 201 Created: Successfully made the deposit
- 400 Bad Request: Invalid input data
- 404 Not Found: Account with the specified IBAN does not exist

### Make a Withdrawal

Makes a withdrawal from an account.

**Request:**
```
POST /api/v2/accounts/{iban}/withdrawals
```

**Path Parameters:**
- `iban`: The International Bank Account Number

**Request Body:**
```json
{
  "amount": 200.00,
  "description": "ATM Withdrawal",
  "entryType": "withdraw"
}
```

**Response:**
```json
{
  "id": 4,
  "amount": 200.00,
  "description": "ATM Withdrawal",
  "timestamp": "2023-02-20T15:45:00",
  "type": "WITHDRAW"
}
```

**Status Codes:**
- 201 Created: Successfully made the withdrawal
- 400 Bad Request: Invalid input data
- 404 Not Found: Account with the specified IBAN does not exist
- 422 Unprocessable Entity: Insufficient funds for the withdrawal

## Data Models

### Account DTO

```json
{
  "iban": "DE123456789",
  "balance": 1000.00,
  "ownerId": 1,
  "type": "CURRENT"
}
```

| Field | Type | Description |
|-------|------|-------------|
| iban | String | International Bank Account Number (unique identifier) |
| balance | BigDecimal | Current balance of the account |
| ownerId | Integer | ID of the client who owns the account |
| type | String | Type of account (CURRENT or SAVINGS) |

### Entry DTO

```json
{
  "id": 1,
  "amount": 500.00,
  "description": "Salary",
  "timestamp": "2023-01-15T10:30:00",
  "type": "DEPOSIT"
}
```

| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Unique identifier for the entry (auto-generated) |
| amount | BigDecimal | Amount of the transaction |
| description | String | Description of the transaction |
| timestamp | DateTime | Date and time when the transaction occurred |
| type | String | Type of transaction (DEPOSIT or WITHDRAW) |

## Related Topics

- [Client API](client-api.md): API for managing clients
- [Current Account API](current-account-api.md): API for current accounts
- [Savings Account API](savings-account-api.md): API for savings accounts
- [API Documentation](api-documentation.md): Overview of all APIs
