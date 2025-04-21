package de.raywotrainings.banking.bankingservice.control.account;

import de.raywotrainings.banking.bankingservice.control.client.Client;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CurrentAccount extends Account {

  private BigDecimal overdraftInterestRate;
  private BigDecimal overdraftLimit;


  public CurrentAccount(String iban, Client owner) {
    this(iban, owner, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
  }


  public CurrentAccount(String iban,
                        Client owner,
                        BigDecimal balance,
                        BigDecimal overdraftLimit,
                        BigDecimal overdraftInterestRate) {
    super(iban, owner, balance);
    this.overdraftLimit = overdraftLimit;
    this.overdraftInterestRate = overdraftInterestRate;
  }


  public CurrentAccount(CurrentAccount other) {
    super(other);
    this.overdraftLimit = other.overdraftLimit;
    this.overdraftInterestRate = other.overdraftInterestRate;
  }


  @Override
  public String toString() {
    return "(Girokonto) " + super.toString() + " Dispo: "
        + getOverdraftLimit() + "€, Dispozins: " + getOverdraftInterestRate() + "%";
  }


  @Override
  protected boolean isAmountAvailable(BigDecimal amount) {
    return getBalance().add(overdraftLimit).compareTo(amount) >= 0;
  }


}
