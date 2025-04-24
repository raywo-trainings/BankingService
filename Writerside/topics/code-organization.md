# Code Organization

Proper code organization is essential for maintaining a clean, understandable, and maintainable codebase. The Banking Service follows specific guidelines for organizing code according to the hexagonal architecture pattern.

## Hexagonal Architecture

The Banking Service uses a hexagonal architecture (also known as ports and adapters) to organize code. This architecture separates the application into layers with clear boundaries:

- **Boundary Layer**: The outer layer that interacts with the outside world
- **Control Layer**: The middle layer that contains business logic
- **Entity Layer**: The inner layer that handles data persistence

## Layer Responsibilities

### Boundary Layer

The boundary layer is responsible for handling external interactions:

- **Controllers**: Handle HTTP requests and responses
- **DTOs**: Define data structures for API communication
- **Mappers**: Convert between DTOs and domain objects

```java
@RestController
@RequestMapping("/api/v2/clients")
public class ClientController {
    private final ClientService clientService;
    private final ClientMapper clientMapper;
    
    // Controller methods
}
```

### Control Layer

The control layer contains the core business logic:

- **Services**: Implement business operations
- **Domain Models**: Represent business entities and their behavior
- **Business Rules**: Enforce constraints and validations

```java
@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    // Service methods with business logic
}
```

### Entity Layer

The entity layer handles data persistence:

- **Entities**: JPA entities that map to database tables
- **Repositories**: Data access interfaces
- **Database Operations**: CRUD operations and queries

```java
@Entity
@Table(name = "clients")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Entity fields and methods
}

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    // Repository methods
}
```

## Package Structure

The package structure reflects the hexagonal architecture:

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

## Best Practices for Code Organization

### Use DTOs for API Communication

Never expose domain objects directly to the API. Always use DTOs (Data Transfer Objects) for communication with external systems:

```java
// DTO for client creation
public class CreateClientDto {
    private String firstName;
    private String lastName;
    private String email;
    
    // Getters and setters
}

// Mapper to convert between DTO and domain object
@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toClient(CreateClientDto dto);
    ClientDto toClientDto(Client client);
}
```

### Implement Validation at the Right Level

- **DTO Validation**: Use Bean Validation annotations on DTOs
- **Business Rule Validation**: Implement in the service layer

```java
// DTO validation
public class CreateClientDto {
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    // Getters and setters
}

// Business rule validation in service
@Service
public class ClientServiceImpl implements ClientService {
    public Client createClient(Client client) {
        // Business rule validation
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new ClientEmailAlreadyExistsException(client.getEmail());
        }
        
        // Save client
        return clientRepository.save(client);
    }
}
```

### Keep Layers Separate

Maintain clear separation between layers:

- Boundary layer should only depend on the control layer
- Control layer should only depend on the entity layer
- Entity layer should not depend on any other layer

This separation ensures that changes in one layer don't affect other layers unnecessarily.
