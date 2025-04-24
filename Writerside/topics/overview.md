# Banking Service Overview

The Banking Service is a comprehensive RESTful application that provides APIs for managing clients, accounts (current and savings), and financial transactions.

## Introduction

Banking Service is a Spring Boot application that simulates a banking system. It allows for the creation and management of clients, different types of bank accounts (current accounts and savings accounts), and financial transactions (deposits and withdrawals).

## Key Features

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
  - Business rule validation (e.g., preventing account deletion if balance is not zero)

## Architecture

The Banking Service follows a hexagonal architecture pattern with clear separation of concerns:

- **boundary**: API layer with controllers, DTOs, and mappers
- **control**: Business logic layer
- **entity**: Data persistence layer
- **configuration**: Application configuration
- **init**: Initialization code

## Technologies

- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- Spring Validation
- Spring Web
- H2 Database
- Lombok
- Maven
