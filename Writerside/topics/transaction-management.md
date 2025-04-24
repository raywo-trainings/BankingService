# Transaction Management

The Banking Service provides comprehensive transaction management capabilities for all account types. This section explains how to manage transactions in the system.

## Transaction Types

The Banking Service supports the following transaction types:

### Deposits

Deposits add money to an account, increasing its balance. They can be used for:

- Salary payments
- Cash deposits
- Transfers from other accounts
- Other income

### Withdrawals

Withdrawals remove money from an account, decreasing its balance. They can be used for:

- Cash withdrawals
- Bill payments
- Transfers to other accounts
- Other expenses

## Transaction Operations

### Making a Deposit

To make a deposit to an account:

```bash
curl -X POST http://localhost:8080/api/v2/accounts/DE123456789/deposits \
  -H "Content-Type: application/json" \
  -d '{"amount": 500, "description": "Salary", "entryType": "deposit"}'
```

The response will include the updated account balance and the transaction details.

### Making a Withdrawal

To make a withdrawal from an account:

```bash
curl -X POST http://localhost:8080/api/v2/accounts/DE123456789/withdrawals \
  -H "Content-Type: application/json" \
  -d '{"amount": 200, "description": "ATM Withdrawal", "entryType": "withdraw"}'
```

The response will include the updated account balance and the transaction details.

### Viewing Transaction History

To view the transaction history for an account:

```bash
curl -X GET http://localhost:8080/api/v2/accounts/DE123456789/entries
```

### Filtering Transactions by Date

To view transactions within a specific date range:

```bash
curl -X GET "http://localhost:8080/api/v2/accounts/DE123456789/entries?from=2023-01-01&to=2023-12-31"
```

## Transaction Data Model

Each transaction (entry) in the Banking Service has the following attributes:

- **ID**: A unique identifier for the transaction (automatically generated)
- **Account IBAN**: The IBAN of the account associated with the transaction
- **Amount**: The monetary amount of the transaction
- **Description**: A text description of the transaction
- **Type**: The type of transaction (deposit or withdrawal)
- **Timestamp**: The date and time when the transaction occurred

## Validation Rules

The following validation rules apply to transaction operations:

- The amount must be a positive number
- The account must exist in the system
- For withdrawals, the account must have sufficient funds (or available overdraft for current accounts)
- The description should not be empty

## Special Considerations

### Current Accounts

Current accounts can have withdrawals that exceed the account balance, up to the overdraft limit. The available amount for withdrawal is calculated as:

```
availableAmount = balance + overdraftLimit
```

### Savings Accounts

Savings accounts do not have an overdraft facility. Withdrawals are limited to the available balance:

```
availableAmount = balance
```

## Error Handling

The API returns appropriate error messages when validation fails:

- 400 Bad Request: For invalid input data
- 404 Not Found: When an account with the specified IBAN doesn't exist
- 422 Unprocessable Entity: When trying to withdraw more than the available amount

## Related Topics

- [Account Management](account-management.md): Learn how to manage accounts
- [API Documentation](api-documentation.md): Complete API reference
