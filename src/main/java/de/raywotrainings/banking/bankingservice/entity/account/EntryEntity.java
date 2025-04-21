package de.raywotrainings.banking.bankingservice.entity.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class EntryEntity {

  @Id
  private String id;

  @NotNull
  @Size(min = 22, max = 22)
  private String iban;

  @Size(min = 1, max = 255)
  private String description;

  @NotNull
  @PastOrPresent
  private ZonedDateTime entryDate;

  @NotNull
  @Min(0)
  private BigDecimal amount;

  @NotNull
  private EntryEntity.Type type;


  public enum Type {
    DEPOSIT, WITHDRAW
  }

}
