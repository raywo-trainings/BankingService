package de.raywotrainings.banking.bankingservice.control.shared;

public class ClientDoesntExistException extends RuntimeException {
  public ClientDoesntExistException(String message) {
    super(message);
  }
}
