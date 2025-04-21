package de.raywotrainings.banking.bankingservice.control.account;

import de.raywotrainings.banking.bankingservice.control.client.Client;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SavingsAccount extends Account {
  private BigDecimal interestRate;


  public SavingsAccount(String iban, Client owner) {
    this(iban, owner, BigDecimal.ZERO, BigDecimal.ZERO);
  }


  public SavingsAccount(String iban,
                        Client owner,
                        BigDecimal balance,
                        BigDecimal interestRate) {
    super(iban, owner, balance);
    this.interestRate = interestRate;
  }


  public SavingsAccount(SavingsAccount other) {
    super(other);
    this.interestRate = other.interestRate;
  }


  @Override
  public String toString() {
    return "(Sparkonto) " + super.toString() + " Zins: " + getInterestRate() + "%";
  }
}
