package de.raywotrainings.banking.bankingservice.boundary.shared;

public class WrongBookingTypeException extends RuntimeException {
  public WrongBookingTypeException(String message) {
    super(message);
  }
}
