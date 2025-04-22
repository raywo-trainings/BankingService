package de.raywotrainings.banking.bankingservice.init;

import de.raywotrainings.banking.bankingservice.control.account.*;
import de.raywotrainings.banking.bankingservice.control.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Initializer responsible for creating current account data.
 * Follows the Single Responsibility Principle by focusing only on current account creation and operations.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CurrentAccountInitializer implements AccountInitializer<List<String>> {

  // Constants for current accounts
  private static final int MIN_OVERDRAFT_LIMIT = 500;
  private static final int MAX_ADDITIONAL_OVERDRAFT = 1500;
  private static final int OVERDRAFT_ROUNDING_FACTOR = 100;
  private static final double MIN_OVERDRAFT_INTEREST_RATE = 7.0;
  private static final double MAX_ADDITIONAL_OVERDRAFT_INTEREST = 6.75;
  private static final double CURRENT_ACCOUNTS_WITH_NEGATIVE_BALANCE_PERCENTAGE = 0.3;
  private static final double INTEREST_RATE_ROUNDING_FACTOR = 20.0;

  private final AccountsService accountsService;
  private final EntriesService entriesService;
  private final Random random = new Random();


  @Override
  public List<String> initialize(int numberOfAccounts, List<Client> clients) {
    log.info("Current account initializer ready");
    return createAccounts(numberOfAccounts, clients);
  }


  /**
   * Ensures that a certain percentage of current accounts have negative balances.
   * Uses the CURRENT_ACCOUNTS_WITH_NEGATIVE_BALANCE_PERCENTAGE constant to determine
   * how many accounts should have negative balances.
   *
   * @param currentAccountIbans List of current account IBANs
   */
  public void ensureCurrentAccountsWithNegativeBalances(List<String> currentAccountIbans) {
    if (currentAccountIbans.isEmpty()) {
      return;
    }

    // Shuffle the list to randomize which accounts will have negative balances
    List<String> shuffledIbans = new ArrayList<>(currentAccountIbans);
    Collections.shuffle(shuffledIbans, random);

    // Calculate how many accounts should have negative balances
    int accountsToMakeNegative = (int) Math.ceil(
        shuffledIbans.size() * CURRENT_ACCOUNTS_WITH_NEGATIVE_BALANCE_PERCENTAGE);

    log.info("Ensuring {} out of {} current accounts have negative balances",
        accountsToMakeNegative, currentAccountIbans.size());

    // Process the first N accounts to ensure they have negative balances
    for (int i = 0; i < accountsToMakeNegative && i < shuffledIbans.size(); i++) {
      String iban = shuffledIbans.get(i);
      CurrentAccount account = (CurrentAccount) accountsService.getAccountByIban(iban);

      // Skip accounts that already have negative balances
      if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
        continue;
      }

      // Calculate a withdrawal amount that will result in a negative balance
      // but still within the overdraft limit
      BigDecimal balance = account.getBalance();
      BigDecimal overdraftLimit = account.getOverdraftLimit();

      // If balance is zero or account has no overdraft limit, skip it
      if (balance.compareTo(BigDecimal.ZERO) <= 0 || overdraftLimit.compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }

      // Calculate a withdrawal amount between balance and balance + 90% of overdraft limit
      BigDecimal maxWithdrawal = balance.add(overdraftLimit.multiply(new BigDecimal("0.9")));
      BigDecimal minWithdrawal = balance.add(new BigDecimal("0.1"));
      BigDecimal withdrawalAmount;

      if (maxWithdrawal.compareTo(minWithdrawal) <= 0) {
        withdrawalAmount = minWithdrawal;
      } else {
        // Generate a random amount between minWithdrawal and maxWithdrawal
        BigDecimal range = maxWithdrawal.subtract(minWithdrawal);
        BigDecimal randomFactor = BigDecimal.valueOf(random.nextDouble());
        withdrawalAmount = minWithdrawal.add(range.multiply(randomFactor));
      }

      // Round to whole numbers for simplicity
      withdrawalAmount = withdrawalAmount.setScale(0, java.math.RoundingMode.DOWN);

      // Skip if withdrawal amount is zero or negative
      if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }

      // Create and process the withdrawal entry
      Entry entry = new Entry(
          iban,
          "Negative balance transaction",
          ZonedDateTime.now().minusDays(random.nextInt(30)),
          withdrawalAmount,
          Entry.Type.WITHDRAW
      );

      try {
        entriesService.makeEntry(iban, entry);
        log.info("Created negative balance of {} for current account with IBAN: {}",
            account.getBalance(), iban);
      } catch (InsufficientFundsException e) {
        log.warn("Failed to create negative balance for account with IBAN: {}: {}",
            iban, e.getMessage());
      }
    }
  }


  /**
   * Creates a current account for the specified client without an initial deposit.
   *
   * @param client The client to create the account for
   * @return The IBAN of the created account
   */
  private String createCurrentAccount(Client client) {
    BigDecimal overdraftLimit = generateOverdraftLimit();
    BigDecimal overdraftInterestRate = generateInterestRate(
    );

    CurrentAccount account = new CurrentAccount(
        null,
        null,
        BigDecimal.ZERO,
        overdraftLimit,
        overdraftInterestRate
    );

    CurrentAccount savedAccount = accountsService.addCurrentAccount(account, client.getId());

    log.info("Created current account with IBAN: {} for client: {} {}, overdraft limit: {}",
        savedAccount.getIban(), client.getFirstname(), client.getLastname(), overdraftLimit);

    return savedAccount.getIban();
  }


  private List<String> createAccounts(int numberOfAccounts, List<Client> clients) {
    List<String> accountIbans = new ArrayList<>();

    for (int i = 0; i < numberOfAccounts; i++) {
      Client client = clients.get(random.nextInt(clients.size()));
      accountIbans.add(createCurrentAccount(client));
    }

    return accountIbans;
  }


  /**
   * Generates a random overdraft limit rounded to the nearest 100.
   *
   * @return The generated overdraft limit as BigDecimal
   */
  private BigDecimal generateOverdraftLimit() {
    int rawOverdraftLimit = MIN_OVERDRAFT_LIMIT + random.nextInt(MAX_ADDITIONAL_OVERDRAFT + 1);
    int roundedOverdraftLimit = Math.round(rawOverdraftLimit / (float) OVERDRAFT_ROUNDING_FACTOR)
        * OVERDRAFT_ROUNDING_FACTOR;
    return new BigDecimal(roundedOverdraftLimit);
  }


  /**
   * Generates a random interest rate within the specified range, rounded to
   * the nearest 0.05.
   *
   * @return The generated interest rate as BigDecimal
   */
  private BigDecimal generateInterestRate() {
    final double randomAmount = random.nextInt((int) (MAX_ADDITIONAL_OVERDRAFT_INTEREST * 100)) / 100.0;
    double rawInterestRate = MIN_OVERDRAFT_INTEREST_RATE + randomAmount;
    double roundedInterestRate = Math.round(rawInterestRate * INTEREST_RATE_ROUNDING_FACTOR)
        / INTEREST_RATE_ROUNDING_FACTOR;

    return BigDecimal.valueOf(roundedInterestRate);
  }

}
