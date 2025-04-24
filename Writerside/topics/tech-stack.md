# Tech Stack

The Banking Service is built using a modern technology stack that provides a robust foundation for developing a reliable and maintainable banking application.

## Core Technologies

### Java 21

The application is written in Java 21, taking advantage of the latest language features and improvements:

- Record classes for immutable data transfer objects
- Pattern matching for instanceof
- Text blocks for more readable multiline strings
- Enhanced switch expressions
- Sealed classes for better domain modeling

### Spring Boot 3.4.4

Spring Boot provides a comprehensive framework for building production-ready applications:

- Auto-configuration of application components
- Embedded web server (Tomcat)
- Production-ready features like health checks and metrics
- Easy deployment options

## Data Access

### Spring Data JPA

Spring Data JPA simplifies data access with JPA (Java Persistence API):

- Repository interfaces for common CRUD operations
- Query methods by convention
- Custom query methods with JPQL
- Pagination and sorting support

### H2 Database

H2 is used as the database for the application:

- In-file storage mode for persistence between application restarts
- In-memory mode available for testing
- Web console for database inspection and management
- SQL compatibility with other database systems

## Web Layer

### Spring Web

Spring Web provides the foundation for building RESTful APIs:

- Annotation-based controller definitions
- Content negotiation
- Exception handling
- Request mapping and parameter binding

### Spring Validation

Spring Validation is used for input validation:

- Bean Validation (JSR-380) support
- Annotation-based validation rules
- Custom validation constraints
- Integration with controller method parameters

## Development Tools

### Lombok

Lombok reduces boilerplate code through annotations:

- `@Data` for generating getters, setters, equals, hashCode, and toString
- `@Builder` for builder pattern implementation
- `@NoArgsConstructor`, `@AllArgsConstructor` for constructors
- `@Slf4j` for logging

### Maven

Maven is used for build automation and dependency management:

- Declarative dependency management
- Build lifecycle management
- Plugin-based architecture
- Multi-module project support

## Version Compatibility

The application is designed to work with the following minimum versions:

- JDK 21 or higher
- Maven 3.6 or higher

## Future Considerations

The tech stack may evolve in the future to include:

- Spring Security for authentication and authorization
- Redis for caching
- PostgreSQL or MySQL for production database
- Docker for containerization
- Kubernetes for orchestration
