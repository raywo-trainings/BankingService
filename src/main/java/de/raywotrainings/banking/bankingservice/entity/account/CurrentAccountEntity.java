package de.raywotrainings.banking.bankingservice.entity.account;

import de.raywotrainings.banking.bankingservice.entity.client.ClientEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class CurrentAccountEntity extends AccountEntity {

  public CurrentAccountEntity(String iban,
                              BigDecimal balance,
                              ClientEntity owner,
                              BigDecimal overdraftLimit,
                              BigDecimal overdraftInterestRate) {
    super(iban, balance, owner);

    this.overdraftInterestRate = overdraftInterestRate;
    this.overdraftLimit = overdraftLimit;
  }


  @NotNull
  @Min(0)
  private BigDecimal overdraftInterestRate;

  @NotNull
  @Min(0)
  private BigDecimal overdraftLimit;

}
