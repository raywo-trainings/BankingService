# Developer Guidelines

This section provides guidelines and best practices for developers working on the Banking Service project.

## Project Structure

The Banking Service follows a hexagonal architecture pattern with clear separation of concerns:

- **boundary**: API layer with controllers, DTOs, and mappers
  - `controller`: REST controllers that handle HTTP requests
  - `dto`: Data Transfer Objects for API requests/responses
  - `mapper`: Converts between DTOs and domain objects

- **control**: Business logic layer
  - `account`: Account-related business logic
  - `client`: Client-related business logic
  - `shared`: Shared business logic components

- **entity**: Data persistence layer
  - `account`: Account-related entities and repositories
  - `client`: Client-related entities and repositories

- **configuration**: Application configuration
  - Contains configuration classes for the application

- **init**: Initialization code
  - Contains data initializers for development/testing

## Code Organization

### Follow the Hexagonal Architecture Pattern

- Keep controllers in the boundary layer
- Keep business logic in the control layer
- Keep data access in the entity layer

### Use DTOs for API Communication

- Don't expose domain objects directly
- Use mappers to convert between DTOs and domain objects

### Validation

- Use Bean Validation annotations on DTOs
- Implement business rule validation in the service layer

## Financial Calculations

### Always Use BigDecimal for Monetary Values

Never use `float` or `double` for financial calculations as they can lead to rounding errors. Always use `BigDecimal` for monetary values.

### Handle Rounding Appropriately

Be explicit about rounding modes and scale when performing calculations with `BigDecimal`.

### Configure Jackson to Handle BigDecimal Properly

The application is already configured to handle `BigDecimal` properly in JSON responses. This configuration is in `application.yml`.

## Balance Changes in Accounts

### Accounts are Always Created with a ZERO Balance

When creating a new account, always initialize the balance to zero. Never create an account with a non-zero balance.

### Balance is Only Changed via Deposits or Withdrawals

Always use the `deposit()` and `withdraw()` methods to change the balance of an account. Never modify the balance directly.

### Don't Assume Anything About the Inner Workings of an Account

Always let the account decide whether an entry is valid or not. For instance, if you want to withdraw a certain amount from a current account, just do the withdrawal. If it fails, the account will throw an appropriate exception.

If you want to be sure that the transaction will succeed, ask the account for the available amount using the `availableAmount()` method.

## Clean Code Principles

### Method Length

A method should ideally not be longer than 20 lines. If a method is getting too long, consider breaking it down into smaller, more focused methods.

### Single Responsibility Principle

Create classes whenever the Single Responsibility Principle calls for it. Each class should have only one reason to change.

### Modern Java Code

Prefer modern Java code over traditional code. Use lambdas, streams, and other Java 21 features where appropriate.

## Error Handling

### Use Appropriate Exception Types

Business exceptions should be meaningful and specific. Create custom exception classes for different types of business errors.

### Use the GlobalExceptionHandler for Consistent API Responses

The application includes a `GlobalExceptionHandler` that converts exceptions to appropriate API responses. Make sure your custom exceptions are handled properly in this class.

### Provide Clear Error Messages

Error messages should be helpful for API consumers. Include relevant details in the error message, such as the account IBAN, the requested amount, etc.

## API Guidelines

### Follow RESTful Conventions

- Use appropriate HTTP methods (GET, POST, PUT, DELETE)
- Use meaningful resource paths
- Return appropriate HTTP status codes

### API Versioning

All endpoints are under `/api/v2/`. Maintain backward compatibility when possible.
