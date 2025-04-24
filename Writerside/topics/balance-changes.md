# Balance Changes in Accounts

Proper handling of account balance changes is critical for maintaining the integrity and accuracy of financial data in the Banking Service. This document outlines the guidelines and best practices for implementing balance changes in accounts.

## Account Creation

### Zero Initial Balance

When creating a new account, always initialize the balance to zero:

```java
// Correct way to initialize an account
Account account = new Account(client, AccountType.CURRENT);
// Balance is automatically set to ZERO
```

Never create an account with a non-zero balance:

```java
// INCORRECT - Do not initialize with non-zero balance
Account account = new Account(client, AccountType.CURRENT);
account.setBalance(new BigDecimal("1000.00")); // WRONG!
```

## Modifying Account Balances

### Use Deposit and Withdraw Methods

Always use the `deposit()` and `withdraw()` methods to change the balance of an account:

```java
// Correct way to add money to an account
account.deposit(new BigDecimal("1000.00"), "Initial deposit");

// Correct way to remove money from an account
account.withdraw(new BigDecimal("500.00"), "ATM withdrawal");
```

Never modify the balance directly:

```java
// INCORRECT - Do not modify balance directly
BigDecimal currentBalance = account.getBalance();
BigDecimal newBalance = currentBalance.add(new BigDecimal("1000.00"));
account.setBalance(newBalance); // WRONG!
```

### Let the Account Validate Entries

Always let the account decide whether an entry is valid or not. The account implementation contains the business rules for validating entries:

```java
try {
    // Let the account handle the validation
    account.withdraw(amount, description);
} catch (InsufficientFundsException e) {
    // Handle the exception appropriately
}
```

## Checking Available Funds

### Use the availableAmount() Method

If you need to check if a withdrawal is possible, use the `availableAmount()` method:

```java
BigDecimal withdrawalAmount = new BigDecimal("1000.00");
if (account.availableAmount().compareTo(withdrawalAmount) >= 0) {
    // Withdrawal is possible
    account.withdraw(withdrawalAmount, "Withdrawal");
} else {
    // Insufficient funds
}
```

### Don't Make Assumptions About Account Implementation

Don't perform manual calculations based on assumptions about how an account works:

```java
// INCORRECT - Don't do this
BigDecimal balance = account.getBalance();
BigDecimal overdraftLimit = ((CurrentAccount) account).getOverdraftLimit();
if (balance.add(overdraftLimit).compareTo(withdrawalAmount) >= 0) {
    // WRONG! This assumes knowledge of how CurrentAccount works
}
```

## Account Types and Balance Behavior

Different account types have different rules for balance changes. While current 
accounts allow overdrafting, savings accounts enforce a strictly positive 
balance. The behaviour of an object is controlled by its `isAmountAvailable()` 
method.

```java
// Account implementation (simplified)
@Override
public BigDecimal availableAmount() {
    return balance.add(overdraftLimit);
}

@Override
public void withdraw(BigDecimal amount, String description) {
  if (!isAmountAvailable(amount)) {
    throw new InsufficientFundsException("...");
  }

  balance = balance.subtract(amount);
}

protected boolean isAmountAvailable(BigDecimal amount) {
  return balance.compareTo(amount) >= 0;
}
```

### Current Accounts

Current accounts allow overdrafts up to a specified limit:

```Java
@Override
public BigDecimal availableAmount() {
  return this.getBalance().add(overdraftLimit);
}

@Override
protected boolean isAmountAvailable(BigDecimal amount) {
  return availableAmount().compareTo(amount) >= 0;
}
```

### Savings Accounts

Savings accounts typically don't allow overdrafts:

```java
// SavingsAccount uses the base class implementation
```
