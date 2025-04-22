package de.raywotrainings.banking.bankingservice.control.account;

import de.raywotrainings.banking.bankingservice.control.client.Client;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public abstract class Account {
  @Setter
  private String iban;
  @Setter
  private Client owner;
  private BigDecimal balance;


  public Account(String iban, Client owner, BigDecimal balance) {
    this.iban = iban;
    this.owner = owner;
    this.balance = balance;
  }


  protected Account(Account other) {
    this.iban = other.iban;
    this.owner = other.owner;
    this.balance = other.balance;
  }


  public void deposit(BigDecimal amount) {
    validateAmount(amount);
    balance = balance.add(amount);
  }


  public void withdraw(BigDecimal amount) throws InsufficientFundsException {
    validateAmount(amount);

    if (!isAmountAvailable(amount)) {
      throw new InsufficientFundsException("Der Betrag kann nicht abgebucht werden.");
    }

    balance = balance.subtract(amount);
  }


  public BigDecimal availableAmount() {
    return balance;
  }


  private void validateAmount(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
  }


  protected boolean isAmountAvailable(BigDecimal amount) {
    return balance.compareTo(amount) >= 0;
  }


  public String toString() {
    return "IBAN: " + iban + ", Inhaber: " + owner + ", Saldo: " + balance + "€";
  }


  public int hashCode() {
    return iban.hashCode();
  }


  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;
    return iban.equals(account.iban);
  }
}
