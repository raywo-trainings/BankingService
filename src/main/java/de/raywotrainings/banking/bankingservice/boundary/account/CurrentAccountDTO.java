package de.raywotrainings.banking.bankingservice.boundary.account;

import de.raywotrainings.banking.bankingservice.boundary.client.ClientDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CurrentAccountDTO extends AccountDTO {

  @NotNull
  @Min(0)
  private BigDecimal overdraftLimit;

  @NotNull
  @Min(0)
  private BigDecimal overdraftInterestRate;


  public CurrentAccountDTO(String iban,
                           ClientDTO owner,
                           BigDecimal balance,
                           BigDecimal overdraftLimit,
                           BigDecimal overdraftInterestRate) {
    super(iban, owner, balance, "current");
    this.overdraftLimit = overdraftLimit;
    this.overdraftInterestRate = overdraftInterestRate;
  }


  @Override
  public String toString() {
    return "CurrentAccountDTO{" +
        super.toString() +
        " overdraftLimit=" + overdraftLimit +
        ", overdraftInterestRate=" + overdraftInterestRate +
        '}';
  }
}
