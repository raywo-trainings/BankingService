# Installation

This guide provides step-by-step instructions for installing and setting up the Banking Service application.

## Prerequisites

Before proceeding with the installation, make sure you have met all the [prerequisites](prerequisites.md).

## Clone the Repository

1. Open a terminal or command prompt
2. Clone the repository using Git:
   ```bash
   git clone https://github.com/yourusername/BankingService.git
   cd BankingService
   ```

## Build the Application

1. Build the application using Maven:
   ```bash
   ./mvnw clean install
   ```
   This command will:
   - Download all required dependencies
   - Compile the source code
   - Run tests
   - Package the application into a JAR file

2. If you encounter any build errors, check the error messages and ensure that:
   - You have the correct JDK version installed
   - Your Maven installation is working properly
   - You have internet access to download dependencies

## Run the Application

1. Start the application using Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

2. The application will start and be available at `http://localhost:8080`

3. You can access the H2 database console at `http://localhost:8080/h2-console` with the following credentials:
   - JDBC URL: `jdbc:h2:file:./.database/data`
   - Username: `user`
   - Password: (empty)

## Verify the Installation

To verify that the application is running correctly:

1. Open a web browser and navigate to `http://localhost:8080/api/v2/clients`
2. You should see a JSON response with a list of clients (if any exist)

## Next Steps

After successfully installing the Banking Service, you can:

- Configure the application by following the [Configuration](configuration.md) guide
- Start using the API as described in the [API Documentation](api-documentation.md)
- Explore the [Usage Examples](usage-examples.md) to learn how to interact with the application
