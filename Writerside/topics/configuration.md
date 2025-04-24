# Configuration

This guide explains how to configure the Banking Service application to suit your needs.

## Configuration File

The main configuration file for the Banking Service is `application.yml`, located in the `src/main/resources` directory. This file contains various settings that control the behavior of the application.

## Key Configuration Options

### Bank Information

You can configure the bank's basic information:

```yaml
bank:
  name: "Your Bank Name"
  bic: "YOURBICCODE"
  country-code: "DE"  # ISO country code
```

### Database Configuration

The application uses an H2 database by default. You can configure the database connection:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./.database/data
    username: user
    password: ""
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
```

### CORS Configuration

Cross-Origin Resource Sharing (CORS) settings can be configured to control which domains can access your API:

```yaml
cors:
  allowed-origins: "*"
  allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
  allowed-headers: "*"
  allow-credentials: true
  max-age: 3600
```

### Logging Configuration

You can configure the logging level for different components:

```yaml
logging:
  level:
    root: INFO
    de.raywotrainings.banking: DEBUG
    org.springframework: INFO
```

## Environment-Specific Configuration

Spring Boot allows you to have different configuration files for different environments. You can create environment-specific configuration files:

- `application-dev.yml`: Development environment configuration
- `application-test.yml`: Testing environment configuration
- `application-prod.yml`: Production environment configuration

To activate a specific profile, you can:

1. Set the `spring.profiles.active` property in the main `application.yml`:
   ```yaml
   spring:
     profiles:
       active: dev
   ```

2. Or use a command-line argument when starting the application:
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=dev
   ```

## Advanced Configuration

### JVM Options

You can configure JVM options when running the application:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m -Xms256m"
```

### Server Port

To change the default server port (8080):

```yaml
server:
  port: 9090
```

## Next Steps

After configuring the Banking Service, you can:

- Learn about the available features in the [Features](features.md) section
- Explore the API in the [API Documentation](api-documentation.md) section
- See practical examples in the [Usage Examples](usage-examples.md) section
