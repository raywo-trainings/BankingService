# Account Management

The Banking Service supports different types of bank accounts, each with its own features and capabilities. This section explains how to manage accounts in the system.

## Account Types

The Banking Service supports the following account types:

### Current Accounts

Current accounts are everyday banking accounts that allow for regular transactions. They have the following features:

- Overdraft facility with configurable limit
- Interest rate on overdraft amounts
- Regular deposits and withdrawals

### Savings Accounts

Savings accounts are designed for saving money and earning interest. They have the following features:

- Interest rate on positive balances
- Deposits and withdrawals with potential restrictions
- No overdraft facility

## Common Account Features

All accounts in the Banking Service share the following features:

- Unique IBAN (International Bank Account Number)
- Balance tracking
- Transaction history
- Owner association (link to a client)

## Account Operations

### Creating Accounts

#### Creating a Current Account

To create a new current account for a client:

```bash
curl -X POST http://localhost:8080/api/v2/current-accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerId": 1, "overdraftLimit": 1000, "overdraftInterestRate": 0.05}'
```

#### Creating a Savings Account

To create a new savings account for a client:

```bash
curl -X POST http://localhost:8080/api/v2/savings-accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerId": 1, "interestRate": 0.02}'
```

### Retrieving Accounts

#### Get All Accounts

To retrieve all accounts:

```bash
curl -X GET http://localhost:8080/api/v2/accounts
```

To retrieve all accounts for a specific client:

```bash
curl -X GET http://localhost:8080/api/v2/accounts?ownerId=1
```

#### Get a Specific Account

To retrieve a specific account by IBAN:

```bash
curl -X GET http://localhost:8080/api/v2/accounts/DE123456789
```

#### Get Account by Type

To retrieve all current accounts:

```bash
curl -X GET http://localhost:8080/api/v2/current-accounts
```

To retrieve all savings accounts:

```bash
curl -X GET http://localhost:8080/api/v2/savings-accounts
```

### Updating Accounts

#### Updating a Current Account

To update a current account's properties:

```bash
curl -X PUT http://localhost:8080/api/v2/current-accounts/DE123456789 \
  -H "Content-Type: application/json" \
  -d '{"overdraftLimit": 2000, "overdraftInterestRate": 0.06}'
```

#### Updating a Savings Account

To update a savings account's properties:

```bash
curl -X PUT http://localhost:8080/api/v2/savings-accounts/DE123456789 \
  -H "Content-Type: application/json" \
  -d '{"interestRate": 0.03}'
```

### Deleting Accounts

To delete an account:

```bash
curl -X DELETE http://localhost:8080/api/v2/accounts/DE123456789
```

Note: An account can only be deleted if its balance is zero.

## Validation Rules

The following validation rules apply to account operations:

- A valid client ID must be provided when creating an account
- Overdraft limit must be a positive number
- Interest rates must be between 0 and 1
- An account can only be deleted if its balance is zero

## Related Topics

- [Client Management](client-management.md): Learn how to manage clients
- [Transaction Management](transaction-management.md): Learn how to make deposits and withdrawals
- [API Documentation](api-documentation.md): Complete API reference
