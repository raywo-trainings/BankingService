# Best Practices

This document outlines the best practices to follow when developing for the Banking Service project. Following these guidelines will help ensure code quality, maintainability, and consistency across the codebase.

## General Coding Practices

### Follow the Hexagonal Architecture

- Keep controllers in the boundary layer
- Keep business logic in the control layer
- Keep data access in the entity layer
- Maintain clear separation of concerns

### Use DTOs for API Communication

- Don't expose domain objects directly to the API
- Use mappers to convert between DTOs and domain objects
- Keep DTOs focused on their specific use case

### Implement Proper Validation

- Use Bean Validation annotations on DTOs
- Implement business rule validation in the service layer
- Provide clear error messages for validation failures

## Financial Best Practices

### Use BigDecimal for Monetary Values

- Never use `float` or `double` for financial calculations
- Always use `BigDecimal` for monetary values to avoid rounding errors
- Configure Jackson to handle BigDecimal properly (already done in application.yml)

### Handle Rounding Appropriately

- Be explicit about rounding modes and scale when performing calculations
- Use `RoundingMode.HALF_UP` for most financial calculations
- Document any non-standard rounding behavior

## Account Management Practices

### Account Creation

- Always initialize accounts with a ZERO balance
- Never create an account with a non-zero balance
- Validate client existence before creating an account

### Balance Changes

- Only change account balances via `deposit()` and `withdraw()` methods
- Never modify the balance directly
- Let the account decide whether an entry is valid

### Account Operations

- Don't assume anything about the inner workings of an account
- Use the `availableAmount()` method to check if a withdrawal is possible
- Don't perform manual calculations like `getBalance().add(getOverdraftLimit).compareTo(...)`

## Code Quality Practices

### Method Design

- Keep methods short (ideally under 20 lines)
- Each method should have a single responsibility
- Use descriptive method names that indicate what the method does

### Class Design

- Follow the Single Responsibility Principle
- Create classes whenever the Single Responsibility Principle calls for it
- Each class should have only one reason to change

### Modern Java Practices

- Use Java 21 features where appropriate
- Prefer lambdas and streams over traditional loops
- Use optional for values that might be null

## Error Handling Practices

### Exception Types

- Use appropriate exception types for different error scenarios
- Create custom exception classes for business errors
- Make exceptions as specific as possible

### Error Messages

- Provide clear and helpful error messages
- Include relevant details in error messages (e.g., account IBAN, requested amount)
- Make error messages user-friendly when they might be exposed to end users

### Exception Handling

- Use the GlobalExceptionHandler for consistent API responses
- Don't catch exceptions unless you can handle them properly
- Log exceptions appropriately

## API Design Practices

### RESTful Conventions

- Use appropriate HTTP methods (GET, POST, PUT, DELETE)
- Use meaningful resource paths
- Return appropriate HTTP status codes

### API Versioning

- All endpoints are under `/api/v2/`
- Maintain backward compatibility when possible
- Document breaking changes clearly

## Testing Practices

### Unit Testing

- Write unit tests for all business logic
- Mock external dependencies
- Test edge cases and error scenarios

### Integration Testing

- Test API endpoints with realistic data
- Verify database interactions
- Test the complete request-response cycle

### Test Data

- Use meaningful test data
- Avoid magic numbers and strings
- Set up and tear down test data properly
