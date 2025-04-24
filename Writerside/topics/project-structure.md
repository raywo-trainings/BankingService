# Project Structure

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

## Package Organization

The project is organized into the following package structure:

```
de.raywotrainings.banking.bankingservice
├── boundary
│   ├── account
│   ├── client
│   ├── mapper
│   └── shared
├── control
│   ├── account
│   ├── client
│   ├── mapper
│   └── shared
├── entity
│   ├── account
│   └── client
├── configuration
└── init
```

This structure reflects the hexagonal architecture and helps maintain a clear separation of concerns throughout the codebase.

## Benefits of Hexagonal Architecture

The hexagonal architecture (also known as ports and adapters) provides several benefits:

1. **Separation of Concerns**: Clear boundaries between different layers of the application
2. **Testability**: Business logic can be tested independently of external dependencies
3. **Flexibility**: Easy to replace or modify components without affecting the core business logic
4. **Maintainability**: Code is organized in a way that makes it easier to understand and maintain
