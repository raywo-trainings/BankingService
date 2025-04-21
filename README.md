# Banking Service

A comprehensive RESTful banking service application that provides APIs for 
managing clients, accounts (current and savings), and financial transactions.

## Overview

Banking Service is a Spring Boot application that simulates a banking system. 
It allows for the creation and management of clients, different types of bank 
accounts (current accounts and savings accounts), and financial transactions 
(deposits and withdrawals).

## Technologies Used

- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- Spring Validation
- Spring Web
- H2 Database
- Lombok
- Maven

## Features

- **Client Management**
  - Create, read, update, and delete clients
  - Retrieve client information by ID

- **Account Management**
  - Create, read, update, and delete accounts
  - Support for different account types:
    - Current accounts with overdraft limits and interest rates
    - Savings accounts with interest rates
  - Automatic IBAN generation
  - Account balance tracking

- **Transaction Management**
  - Make deposits and withdrawals
  - View transaction history
  - Filter transactions by date range

- **Validation**
  - Input validation for all API endpoints
  - Business rule validation (e.g., preventing account deletion if balance is 
    not zero)

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Maven 3.6 or higher

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/BankingService.git
   cd BankingService
   ```

2. Build the application:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080` by default.

## Database

The application uses an H2 in-file database stored in `./.database/data`. The 
H2 console is enabled and can be accessed at `http://localhost:8080/h2-console` 
with the following credentials:
- JDBC URL: `jdbc:h2:file:./.database/data`
- Username: `user`
- Password: (empty)

## API Documentation

### Client API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v2/clients` | Get all clients |
| GET | `/api/v2/clients/{id}` | Get client by ID |
| POST | `/api/v2/clients` | Create a new client |
| PUT | `/api/v2/clients/{id}` | Update a client |
| DELETE | `/api/v2/clients/{id}` | Delete a client |

### Account API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v2/accounts` | Get all accounts (optional query param: ownerId) |
| GET | `/api/v2/accounts/{iban}` | Get account by IBAN |
| DELETE | `/api/v2/accounts/{iban}` | Delete account by IBAN |
| GET | `/api/v2/accounts/{iban}/entries` | Get account entries (optional query params: from, to) |
| POST | `/api/v2/accounts/{iban}/deposits` | Make a deposit |
| POST | `/api/v2/accounts/{iban}/withdrawals` | Make a withdrawal |

### Current Account API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v2/current-accounts` | Get all current accounts |
| GET | `/api/v2/current-accounts/{iban}` | Get current account by IBAN |
| POST | `/api/v2/current-accounts` | Create a new current account |
| PUT | `/api/v2/current-accounts/{iban}` | Update a current account |
| DELETE | `/api/v2/current-accounts/{iban}` | Delete a current account |

### Savings Account API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v2/savings-accounts` | Get all savings accounts |
| GET | `/api/v2/savings-accounts/{iban}` | Get savings account by IBAN |
| POST | `/api/v2/savings-accounts` | Create a new savings account |
| PUT | `/api/v2/savings-accounts/{iban}` | Update a savings account |
| DELETE | `/api/v2/savings-accounts/{iban}` | Delete a savings account |

## Usage Examples

### Creating a Client

```bash
curl -X POST http://localhost:8080/api/v2/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName": "John", "lastName": "Doe", "email": "john.doe@example.com"}'
```

### Creating a Current Account

```bash
curl -X POST http://localhost:8080/api/v2/current-accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerId": 1, "overdraftLimit": 1000, "overdraftInterestRate": 0.05}'
```

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

## Configuration

The application can be configured through the `application.yml` file. Key 
configuration options include:

- Bank name, BIC, and country code
- Database connection details
- CORS settings

## License

This project is licensed under the GNU General Public License v2.0 - see the 
[LICENSE](LICENSE) file for details.


## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
