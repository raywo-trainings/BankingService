# Error Handling

Proper error handling is essential for creating a robust and user-friendly 
banking application. This document outlines the guidelines and best practices 
for handling errors in the Banking Service.

## Exception Types

### Use Appropriate Exception Types

Business exceptions should be meaningful and specific. Create custom exception 
classes for different types of business errors:

```java
// Generic exception - AVOID for specific business errors
throw new RuntimeException("Error processing withdrawal");

// Specific business exception - BETTER
throw new InsufficientFundsException(accountIban, 
                                     requestedAmount, 
                                     availableAmount);
```

### Create Custom Exception Classes

Create custom exception classes for different types of business errors:

```java
public class InsufficientFundsException extends BusinessException {
    private final String accountIban;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableAmount;

    public InsufficientFundsException(String accountIban, 
                                      BigDecimal requestedAmount, 
                                      BigDecimal availableAmount) {
        super(String.format("Insufficient funds in account %s: requested %s, available %s", 
              accountIban, requestedAmount, availableAmount));
        this.accountIban = accountIban;
        this.requestedAmount = requestedAmount;
        this.availableAmount = availableAmount;
    }

    // Getters for the fields
}
```

### Exception Hierarchy

Organize exceptions in a hierarchy that reflects the domain:

```java
// Base exception for all business exceptions
public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// Account-related exceptions
public abstract class AccountException extends BusinessException {
    private final String accountIban;

    public AccountException(String message, String accountIban) {
        super(message);
        this.accountIban = accountIban;
    }

    public String getAccountIban() {
        return accountIban;
    }
}

// Specific account exceptions
public class AccountNotFoundException extends AccountException {
    public AccountNotFoundException(String accountIban) {
        super(String.format("Account with IBAN %s not found", accountIban), 
            accountIban);
    }
}

public class InsufficientFundsException extends AccountException {
    // Implementation as shown above
}
```

## GlobalExceptionHandler

### Use the GlobalExceptionHandler for Consistent API Responses

The application includes a `GlobalExceptionHandler` that converts exceptions to appropriate API responses. Make sure your custom exceptions are handled properly in this class:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            "ACCOUNT_NOT_FOUND",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            "INSUFFICIENT_FUNDS",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // Other exception handlers...
}
```

### Error Response Format

The API definition prescribes error responses as described in RFC9457. Use the 
`ProblemDetail` class included in SpringBoot to create such responses.

```java
// Example implementation in GlobalExceptionHandler
@ExceptionHandler({
    ClientDoesntExistException.class,
    WrongBookingTypeException.class,
    InsufficientFundsException.class,
    IllegalStateException.class
})
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public ProblemDetail handleClientDoesntException(Exception exception) {
  var result = ProblemDetail.forStatus(UNPROCESSABLE_ENTITY);

  result.setTitle("Unprocessable Entity");
  result.setDetail(exception.getMessage());

  return result;
}
```

## Error Messages

### Provide Clear Error Messages

Error messages should be helpful for API consumers. Include relevant details in 
the error message, such as the account IBAN, the requested amount, etc.:

```java
// AVOID: Vague error message
throw new BusinessException("Operation failed");

// BETTER: Clear and detailed error message
throw new InsufficientFundsException(
    "DE123456789",
    new BigDecimal("1000.00"),
    new BigDecimal("500.00")
);
```

### Include Relevant Details

Include relevant details in error messages, but be careful not to expose sensitive information:

```java
// AVOID: Exposing sensitive information
throw new AuthenticationException(
    String.format("Invalid credentials for user %s with password %s", 
    username, password)
);

// BETTER: Providing useful information without exposing sensitive details
throw new AuthenticationException(
    String.format("Invalid credentials for user %s", username)
);
```

## Exception Handling Strategies

### Catch Exceptions at the Right Level

Catch exceptions at the level where you can handle them properly:

```java
// Service layer - handle business exceptions
public void transferFunds(String fromIban, String toIban, BigDecimal amount) {
  try {
    Account fromAccount = accountRepository.findByIban(fromIban)
        .orElseThrow(() -> new AccountNotFoundException(fromIban));
    Account toAccount = accountRepository.findByIban(toIban)
        .orElseThrow(() -> new AccountNotFoundException(toIban));
    
    fromAccount.withdraw(amount, "Transfer to " + toIban);
    toAccount.deposit(amount, "Transfer from " + fromIban);
    
    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
  } catch (InsufficientFundsException ex) {
    // Handle insufficient funds - maybe add to a retry queue or notify the user
    throw ex; // Rethrow to be handled by the GlobalExceptionHandler
  }
}
```

### Don’t Swallow Exceptions

Do not catch exceptions unless you can handle them properly:

```java
// AVOID: Swallowing exceptions
try {
  account.withdraw(amount, description);
} catch (Exception e) {
  // Do nothing - WRONG!
}

// BETTER: Handle the exception or let it propagate
try {
  account.withdraw(amount, description);
} catch (InsufficientFundsException e) {
  // Handle the specific exception
  logger.warn("Insufficient funds: {}", e.getMessage());
  throw e; // Rethrow to be handled at a higher level
}
```

## Logging

### Log Exceptions Appropriately

Log exceptions with appropriate severity levels:

```java
try {
  // Some operation that might throw an exception
} catch (BusinessException e) {
  // Business exceptions are expected and should be logged as warnings
  logger.warn("Business exception occurred: {}", e.getMessage());
  throw e;
} catch (Exception e) {
  // Unexpected exceptions should be logged as errors with stack traces
  logger.error("Unexpected error occurred", e);
  throw new SystemException("An unexpected error occurred", e);
}
```

### Include Context in Logs

Include relevant context in log messages:

```java
logger.warn("Failed to withdraw {} from account {}: {}", 
    amount, accountIban, e.getMessage());
```

## Transaction Management

### Use @Transactional for Database Operations

Use Spring's `@Transactional` annotation to ensure that database operations are rolled back in case of exceptions:

```java
@Service
public class TransferService {
    
  @Transactional
  public void transferFunds(String fromIban, 
                            String toIban, 
                            BigDecimal amount) {
    // If any exception occurs, the entire transaction will 
    // be rolled back
    Account fromAccount = accountRepository.findByIban(fromIban)
        .orElseThrow(() -> new AccountNotFoundException(fromIban));
    Account toAccount = accountRepository.findByIban(toIban)
        .orElseThrow(() -> new AccountNotFoundException(toIban));
    
    fromAccount.withdraw(amount, "Transfer to " + toIban);
    toAccount.deposit(amount, "Transfer from " + fromIban);
    
    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
  }
}
```

## Summary

Proper error handling is essential for creating a robust and user-friendly 
banking application. By following these guidelines, you can ensure that errors 
are handled consistently and that users receive clear and helpful 
error messages.
