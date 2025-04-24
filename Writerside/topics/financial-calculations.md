# Financial Calculations

Proper handling of financial calculations is critical in a banking application. This document outlines the guidelines and best practices for implementing financial calculations in the Banking Service.

## Using BigDecimal for Monetary Values

### Why BigDecimal?

In Java, floating-point types (`float` and `double`) are not suitable for financial calculations due to their inherent precision limitations. For example:

```
// Java example showing floating-point precision issues
double a = 0.1;
double b = 0.2;
System.out.println(a + b); // Outputs 0.30000000000000004, not 0.3
```

This imprecision is unacceptable for financial applications where exact calculations are required.

### Always Use BigDecimal

Always use `BigDecimal` for monetary values in the Banking Service:

```java
// Incorrect
double balance = 100.50;
double newBalance = balance + 50.25;

// Correct
BigDecimal balance = new BigDecimal("100.50");
BigDecimal deposit = new BigDecimal("50.25");
BigDecimal newBalance = balance.add(deposit);
```

### Creating BigDecimal Instances

When creating `BigDecimal` instances, prefer the string constructor to avoid floating-point precision issues:

```java
// Avoid this - may introduce precision errors
BigDecimal amount = new BigDecimal(100.50);

// Prefer this - exact precision
BigDecimal amount = new BigDecimal("100.50");

// Or use BigDecimal.valueOf for simple values
BigDecimal amount = BigDecimal.valueOf(100.50);
```

## Handling Rounding

### Be Explicit About Rounding

Always be explicit about rounding modes and scale when performing calculations with `BigDecimal`:

```java
BigDecimal amount = new BigDecimal("100.567");
BigDecimal roundedAmount = amount.setScale(2, RoundingMode.HALF_UP); // 100.57
```

### Common Rounding Modes

- `RoundingMode.HALF_UP`: Round towards the nearest neighbor, or towards the even neighbor if equidistant (standard financial rounding)
- `RoundingMode.DOWN`: Round towards zero (truncation)
- `RoundingMode.UP`: Round away from zero
- `RoundingMode.FLOOR`: Round towards negative infinity
- `RoundingMode.CEILING`: Round towards positive infinity

### Recommended Rounding for Banking Operations

For most banking operations, use `RoundingMode.HALF_UP` with a scale of 2 decimal places:

```java
public BigDecimal calculateInterest(BigDecimal principal, BigDecimal rate) {
    return principal.multiply(rate)
            .setScale(2, RoundingMode.HALF_UP);
}
```

## Comparing BigDecimal Values

Use the `compareTo` method instead of `equals` when comparing `BigDecimal` values:

```java
BigDecimal a = new BigDecimal("100.00");
BigDecimal b = new BigDecimal("100.0");

// Incorrect - returns false because scales are different
boolean isEqual = a.equals(b);

// Correct - returns 0 (equal) because values are numerically equal
int comparison = a.compareTo(b);
boolean isEqual = comparison == 0;
```

## BigDecimal in JSON Responses

### Jackson Configuration

The Banking Service is already configured to handle `BigDecimal` properly in JSON responses. This configuration is in `application.yml`:

```yaml
spring:
  jackson:
    serialization:
      WRITE_BIGDECIMAL_AS_PLAIN: true
    deserialization:
      USE_BIG_DECIMAL_FOR_FLOATS: true
```

This ensures that:
- `BigDecimal` values are serialized as plain numbers without scientific notation
- Floating-point numbers in JSON are deserialized as `BigDecimal`

## Performance Considerations

While `BigDecimal` provides exact precision, it is less performant than primitive types. However, in a banking application, precision is more important than performance for financial calculations.

If performance becomes a concern:
- Consider using `int` or `long` to represent monetary values in cents/pennies for internal calculations
- Convert to/from `BigDecimal` only at the boundaries of the system
- Use appropriate caching strategies for frequently accessed values

## Example: Interest Calculation

Here's an example of calculating interest on an account balance:

```java
public BigDecimal calculateMonthlyInterest(BigDecimal balance, BigDecimal annualInterestRate) {
    // Convert annual rate to monthly rate
    BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

    // Calculate interest
    BigDecimal interest = balance.multiply(monthlyRate);

    // Round to 2 decimal places
    return interest.setScale(2, RoundingMode.HALF_UP);
}
```
