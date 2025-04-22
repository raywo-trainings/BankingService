package de.raywotrainings.banking.bankingservice.init;

import de.raywotrainings.banking.bankingservice.control.account.AccountsService;
import de.raywotrainings.banking.bankingservice.control.account.SavingsAccount;
import de.raywotrainings.banking.bankingservice.control.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Initializer responsible for creating savings account data.
 * Follows the Single Responsibility Principle by focusing only on savings account creation and operations.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SavingsAccountInitializer implements AccountInitializer<List<String>> {

  // Constants for savings accounts
  private static final double MIN_SAVINGS_INTEREST_RATE = 0.25;
  private static final double MAX_ADDITIONAL_SAVINGS_INTEREST = 3.0;
  private static final double INTEREST_RATE_ROUNDING_FACTOR = 20.0;

  private final AccountsService accountsService;
  private final Random random = new Random();


  @Override
  public List<String> initialize(int numberOfAccounts, List<Client> clients) {
    log.info("Savings account initializer ready");
    return createAccounts(numberOfAccounts, clients);
  }


  private List<String> createAccounts(int numberOfAccounts, List<Client> clients) {
    List<String> accountIbans = new ArrayList<>();

    for (int i = 0; i < numberOfAccounts; i++) {
      Client client = clients.get(random.nextInt(clients.size()));
      accountIbans.add(createSavingsAccount(client));
    }

    return accountIbans;
  }


  /**
   * Creates a savings account for the specified client without an initial deposit.
   *
   * @param client The client to create the account for
   * @return The IBAN of the created account
   */
  private String createSavingsAccount(Client client) {
    BigDecimal interestRate = generateInterestRate(
    );

    SavingsAccount account = new SavingsAccount(
        null,
        null,
        BigDecimal.ZERO,
        interestRate
    );

    SavingsAccount savedAccount = accountsService.addSavingsAccount(account, client.getId());

    log.info("Created savings account with IBAN: {} for client: {} {}, interest rate: {}",
        savedAccount.getIban(), client.getFirstname(), client.getLastname(), interestRate);

    return savedAccount.getIban();
  }


  /**
   * Generates a random interest rate within the specified range, rounded to
   * the nearest 0.05.
   *
   * @return The generated interest rate as BigDecimal
   */
  private BigDecimal generateInterestRate() {
    final double randomAmount = random.nextInt((int) (MAX_ADDITIONAL_SAVINGS_INTEREST * 100)) / 100.0;
    double rawInterestRate = MIN_SAVINGS_INTEREST_RATE + randomAmount;
    double roundedInterestRate = Math.round(rawInterestRate * INTEREST_RATE_ROUNDING_FACTOR)
        / INTEREST_RATE_ROUNDING_FACTOR;

    return BigDecimal.valueOf(roundedInterestRate);
  }

}
