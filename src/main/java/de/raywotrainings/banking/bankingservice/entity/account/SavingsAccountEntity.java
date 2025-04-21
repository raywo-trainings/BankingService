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
public class SavingsAccountEntity extends AccountEntity {

  public SavingsAccountEntity(String iban,
                              BigDecimal balance,
                              ClientEntity owner,
                              BigDecimal interestRate) {
    super(iban, balance, owner);
    this.interestRate = interestRate;
  }


  @NotNull
  @Min(0)
  private BigDecimal interestRate;

}
