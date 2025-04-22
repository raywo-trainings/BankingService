# Banking Service Developer Guidelines

## Project Structure

The Banking Service follows a hexagonal architecture pattern with clear 
separation of concerns:

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

## Tech Stack

- **Java 21**: Programming language
- **Spring Boot 3.4.4**: Application framework
- **Spring Data JPA**: Data access layer
- **Spring Validation**: Input validation
- **H2 Database**: In-memory/file database
- **Lombok**: Reduces boilerplate code
- **Maven**: Build tool

## Building and Running

### Prerequisites
- JDK 21 or higher
- Maven 3.6 or higher

### Build Commands
```bash
# Clean and build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

### Database
- H2 in-file database stored in `./.database/data`
- H2 console available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./.database/data`
- Username: `user`
- Password: (empty)

## Testing

### Running Tests
```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ApplicationTests
```

### API Testing
- HTTP request examples are available in the `requests/` directory
- These can be executed using tools like IntelliJ HTTP Client or VS Code REST Client

## Best Practices

### Code Organization
1. **Follow the hexagonal architecture pattern**:
   - Keep controllers in the boundary layer
   - Keep business logic in the control layer
   - Keep data access in the entity layer

2. **Use DTOs for API communication**:
   - Don't expose domain objects directly
   - Use mappers to convert between DTOs and domain objects

3. **Validation**:
   - Use Bean Validation annotations on DTOs
   - Implement business rule validation in the service layer

### Financial Calculations
1. **Always use BigDecimal for monetary values**:
   - Never use float or double for financial calculations
   - Configure Jackson to handle BigDecimal properly (already done in application.yml)

2. **Handle rounding appropriately**:
   - Be explicit about rounding modes and scale

### Balance changes in accounts
1. **Accounts are always created with a ZERO balance**
2. **Balance is only changed via deposits or withdrawals**
   Always use the `deposit()` and `withdraw()` methods to change the balance of 
   an account. 
3. **Don’t assume anything about the inner workings of an account**
   Always let the account decide whether an entry is valid or not. For instance 
   if you want to withdraw a certain amount from a current account, just do the 
   withdrawal. If it fails the account will throw an appropriate exception. If 
   you want to be sure, that the transaction will succeed ask the account for 
   the available amount using the `availableAmount()` method. Don’t do any 
   manual calculations like `getBalance().add(getOverdraftLimit).compareTo(...)`!

### Clean Code Principle
Adhere strongly to the Clean Code principles. A method should ideally not longer 
than 20 lines. Create classes whenever the Single Responsibility Principle calls 
for it. Prefer modern Java code over traditional code. Prefer lambdas to loops 
or similar.

### Error Handling
1. **Use appropriate exception types**:
   - Business exceptions should be meaningful and specific
   - Use the GlobalExceptionHandler for consistent API responses

2. **Provide clear error messages**:
   - Error messages should be helpful for API consumers

## API Guidelines
1. **Follow RESTful conventions**:
   - Use appropriate HTTP methods (GET, POST, PUT, DELETE)
   - Use meaningful resource paths
   - Return appropriate HTTP status codes

2. **API Versioning**:
   - All endpoints are under `/api/v2/`
   - Maintain backward compatibility when possible

## Configuration
- Application configuration is in `application.yml`
- Environment-specific configurations can be added using Spring profiles
