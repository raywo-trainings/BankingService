package de.raywotrainings.banking.bankingservice.boundary.account;

import de.raywotrainings.banking.bankingservice.boundary.client.ClientDTO;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class SavingsAccountDTO extends AccountDTO {

  @Min(0)
  private BigDecimal interestRate;


  public SavingsAccountDTO(String iban,
                           ClientDTO owner,
                           BigDecimal balance,
                           BigDecimal interestRate) {
    super(iban, owner, balance, "savings");
    this.interestRate = interestRate;
  }


  @Override
  public String toString() {
    return "SavingsAccountDTO{" +
        super.toString() +
        " interestRate=" + interestRate +
        '}';
  }

}
