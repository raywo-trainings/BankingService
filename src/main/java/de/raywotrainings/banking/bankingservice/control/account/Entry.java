package de.raywotrainings.banking.bankingservice.control.account;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Entry {

  private String id;
  private String iban;
  private String description;
  private ZonedDateTime entryDate;
  private BigDecimal amount;
  private Type type;


  public Entry(String iban,
               String description,
               ZonedDateTime entryDate,
               BigDecimal amount,
               Type type) {
    this.id = UUID.randomUUID().toString();
    this.iban = iban;
    this.description = description;
    this.entryDate = entryDate;
    this.amount = amount;
    this.type = type;
  }


  public Entry(Entry other) {
    this.id = other.id;
    this.iban = other.iban;
    this.description = other.description;
    this.entryDate = other.entryDate;
    this.amount = other.amount;
    this.type = other.type;
  }


  public enum Type {
    DEPOSIT, WITHDRAW
  }
}
