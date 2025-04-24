# Contributing to Banking Service

We welcome contributions to the Banking Service project! This document provides guidelines and instructions for contributing.

## Ways to Contribute

There are several ways you can contribute to the Banking Service:

- **Reporting Bugs**: If you find a bug, please report it by creating an issue in the repository.
- **Suggesting Enhancements**: Have an idea for a new feature or improvement? Create an issue to suggest it.
- **Code Contributions**: You can contribute code by fixing bugs, adding features, or improving documentation.
- **Documentation**: Help improve the documentation by fixing errors, adding examples, or clarifying explanations.
- **Testing**: Help test the application and report any issues you find.

## Getting Started

### Prerequisites

Before you start contributing, make sure you have:

- A GitHub account
- Git installed on your local machine
- JDK 21 or higher
- Maven 3.6 or higher

### Setting Up the Development Environment

1. Fork the repository on GitHub
2. Clone your fork to your local machine:
   ```bash
   git clone https://github.com/yourusername/BankingService.git
   cd BankingService
   ```
3. Add the original repository as an upstream remote:
   ```bash
   git remote add upstream https://github.com/originalowner/BankingService.git
   ```
4. Build the project:
   ```bash
   ./mvnw clean install
   ```

## Development Workflow

1. Create a new branch for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```
   or
   ```bash
   git checkout -b fix/your-bug-fix
   ```

2. Make your changes, following the [coding standards](#coding-standards)

3. Add tests for your changes

4. Run the tests to ensure they pass:
   ```bash
   ./mvnw test
   ```

5. Commit your changes with a descriptive commit message:
   ```bash
   git commit -m "Add a concise commit message describing your changes"
   ```

6. Push your branch to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

7. Create a pull request from your fork to the original repository

## Coding Standards

Please follow these coding standards when contributing to the Banking Service:

### Java Code Style

- Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use 4 spaces for indentation (not tabs)
- Use meaningful variable and method names
- Keep methods short and focused (ideally under 20 lines)
- Add comments for complex logic
- Use Java 21 features where appropriate

### Clean Code Principles

- Follow the Single Responsibility Principle
- Write self-documenting code
- Avoid code duplication
- Prefer lambdas and streams over traditional loops
- Use appropriate exception handling

### Financial Calculations

- Always use `BigDecimal` for monetary values
- Be explicit about rounding modes and scale
- Follow the guidelines in the [Developer Guidelines](developer-guidelines.md) section

### Testing

- Write unit tests for all new code
- Ensure all tests pass before submitting a pull request
- Aim for high test coverage

## Pull Request Process

1. Update the README.md or documentation with details of changes, if applicable
2. Make sure all tests pass
3. Ensure your code follows the coding standards
4. The pull request will be reviewed by maintainers
5. Address any feedback from the code review
6. Once approved, your pull request will be merged

## Code of Conduct

Please be respectful and considerate of others when contributing to this project. We expect all contributors to:

- Be friendly and patient
- Be welcoming and inclusive
- Be respectful of differing viewpoints and experiences
- Gracefully accept constructive criticism

## License

By contributing to the Banking Service, you agree that your contributions will be licensed under the project's [GNU General Public License v2.0](license.md).

## Questions?

If you have any questions about contributing, please create an issue in the repository or contact the project maintainers.

Thank you for contributing to the Banking Service!
