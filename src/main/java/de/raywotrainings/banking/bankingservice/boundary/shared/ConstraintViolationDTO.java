package de.raywotrainings.banking.bankingservice.boundary.shared;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConstraintViolationDTO {

  private final String field;

  private final String constraint;

  private final String message;

}
