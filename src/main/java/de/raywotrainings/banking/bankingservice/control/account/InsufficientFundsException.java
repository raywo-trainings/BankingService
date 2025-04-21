package de.raywotrainings.banking.bankingservice.control.account;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
    super(message);
  }
}
