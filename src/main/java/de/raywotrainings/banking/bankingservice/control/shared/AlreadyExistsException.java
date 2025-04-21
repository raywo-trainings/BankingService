package de.raywotrainings.banking.bankingservice.control.shared;

public class AlreadyExistsException extends RuntimeException {
  public AlreadyExistsException(String message) {
    super(message);
  }
}
