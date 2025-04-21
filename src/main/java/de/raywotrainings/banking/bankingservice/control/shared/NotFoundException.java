package de.raywotrainings.banking.bankingservice.control.shared;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
