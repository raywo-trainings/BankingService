# Clean Code Principles

Writing clean, maintainable code is essential for the long-term success of the 
Banking Service project. This document outlines the clean code principles that 
should be followed when developing for the project.

## Method Length

### Keep Methods Short

A method should ideally not be longer than 20 lines. If a method is getting too 
long, consider breaking it down into smaller, more focused methods:

```java
// AVOID: Long method with multiple responsibilities
public void processTransaction(Transaction transaction) {
    // 30+ lines of code handling validation, processing, and logging
}

// BETTER: Break down into smaller methods
public void processTransaction(Transaction transaction) {
    validateTransaction(transaction);
    executeTransaction(transaction);
    logTransaction(transaction);
}

private void validateTransaction(Transaction transaction) {
    // 5-10 lines of validation logic
}

private void executeTransaction(Transaction transaction) {
    // 5-10 lines of execution logic
}

private void logTransaction(Transaction transaction) {
    // 5-10 lines of logging logic
}
```

### Benefits of Short Methods

- Easier to understand
- Easier to test
- Easier to reuse
- Easier to maintain
- Easier to debug

## Single Responsibility Principle

### One Reason to Change

Create classes whenever the Single Responsibility Principle calls for it. Each 
class should have only one reason to change:

```java
// AVOID: Class with multiple responsibilities
public class AccountManager {
    public void createAccount(Account account) { /* ... */ }
    public void updateAccount(Account account) { /* ... */ }
    public void deleteAccount(String iban) { /* ... */ }
    public void deposit(String iban, BigDecimal amount) { /* ... */ }
    public void withdraw(String iban, BigDecimal amount) { /* ... */ }
    public List<Entry> getEntries(String iban) { /* ... */ }
    public void generateAccountStatement(String iban) { /* ... */ }
    public void emailAccountStatement(String iban, String email) { /* ... */ }
}

// BETTER: Split into classes with single responsibilities
public class AccountRepository {
    public void createAccount(Account account) { /* ... */ }
    public void updateAccount(Account account) { /* ... */ }
    public void deleteAccount(String iban) { /* ... */ }
}

public class TransactionService {
    public void deposit(String iban, BigDecimal amount) { /* ... */ }
    public void withdraw(String iban, BigDecimal amount) { /* ... */ }
    public List<Entry> getEntries(String iban) { /* ... */ }
}

public class StatementService {
    public void generateAccountStatement(String iban) { /* ... */ }
    public void emailAccountStatement(String iban, String email) { /* ... */ }
}
```

### Signs That a Class Has Too Many Responsibilities

- The class name is vague or contains words like "Manager", "Processor", 
  or "Handler"
- The class has many unrelated methods
- The class has many unrelated fields
- Changes to one part of the class affect other parts
- The class is difficult to test

## Modern Java Code

### Use Java 21 Features

Prefer modern Java code over traditional code. Use Java 21 features 
where appropriate:

```java
// AVOID: Traditional Java code
List<Account> filteredAccounts = new ArrayList<>();
for (Account account : accounts) {
    if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
        filteredAccounts.add(account);
    }
}

// BETTER: Modern Java code with streams
List<Account> filteredAccounts = accounts.stream()
    .filter(account -> account.getBalance().compareTo(BigDecimal.ZERO) > 0)
    .toList();
```

### Prefer Lambdas to Anonymous Classes

```java
// AVOID: Anonymous class
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Button clicked");
    }
});

// BETTER: Lambda expression
button.addActionListener(e -> System.out.println("Button clicked"));
```

### Use Optional for Nullable Values

```java
// AVOID: Null checks
Account account = accountRepository.findByIban(iban);
if (account != null) {
    // Do something with account
} else {
    // Handle null case
}

// BETTER: Optional
Optional<Account> accountOpt = accountRepository.findByIban(iban);
accountOpt.ifPresentOrElse(
    account -> { /* Do something with account */ },
    () -> { /* Handle empty case */ }
);
```

## Naming Conventions

### Use Descriptive Names

- Class names should be nouns (e.g., `Account`, `Transaction`)
- Method names should be verbs (e.g., `deposit`, `withdraw`)
- Boolean variables/methods should start with "is", "has", or "can" 
  (e.g., `isActive`, `hasOverdraft`)

### Avoid Abbreviations

```java
// AVOID: Abbreviations
int n = 5;
String addr = "123 Main St";

// BETTER: Descriptive names
int numberOfAccounts = 5;
String address = "123 Main St";
```

## Comments

### Code Should Be Self-Documenting

Write code that is self-explanatory, reducing the need for comments:

```java
// AVOID: Comment explaining what the code does
// Check if the account has sufficient funds for withdrawal
if (account.getBalance().compareTo(amount) >= 0) {
    // ...
}

// BETTER: Method name explains the purpose
if (account.hasSufficientFundsForWithdrawal(amount)) {
    // ...
}
```

### Use Comments for Why, Not What

Comments should explain why something is done, not what is being done:

```java
// GOOD: Comment explaining why
// We need to check both the balance and the overdraft limit 
// for current accounts
if (account instanceof CurrentAccount) {
    // ...
}
```

## Code Formatting

### Consistent Indentation

Use 2 spaces for indentation (not tabs).

### Line Length

Keep lines at about 80 characters. 

### Whitespace

Use whitespace to improve readability:

```java
// AVOID: Cramped code
for(int i=0;i<10;i++){doSomething(i);}

// BETTER: Proper spacing
for (int i = 0; i < 10; i++) {
    doSomething(i);
}
```

## Summary

Following these clean code principles will help ensure that the Banking Service 
codebase remains maintainable, readable, and robust over time. Remember that 
code is read much more often than it is written, so prioritize readability and 
clarity in your code.
