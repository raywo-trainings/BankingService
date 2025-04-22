package de.raywotrainings.banking.bankingservice.init;

import de.raywotrainings.banking.bankingservice.control.client.Client;
import de.raywotrainings.banking.bankingservice.entity.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Coordinates the initialization of sample data for the banking application.
 * Delegates specific initialization tasks to specialized initializer classes
 * following the Single Responsibility Principle.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer {
  // Constants for data generation
  private static final int MIN_ACCOUNTS = 20;
  private static final int MAX_ADDITIONAL_ACCOUNTS = 10;

  private final ClientRepository clientRepository;
  private final ClientInitializer clientInitializer;
  private final CurrentAccountInitializer currentAccountInitializer;
  private final SavingsAccountInitializer savingsAccountInitializer;
  private final TransactionInitializer transactionInitializer;

  private final Random random = new Random();


  /**
   * Initializes the database with sample data when the application starts.
   * Only runs if the database is empty.
   * First creates clients and accounts (without transactions),
   * then generates transactions for the accounts.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void initData() {
    if (clientRepository.count() > 0) {
      log.info("Database already contains data, skipping initialization");
      return;
    }

    log.info("Initializing database with sample data");

    // First create clients and accounts (without transactions)
    List<Client> clients = clientInitializer.initialize();
    Map<String, List<String>> accounts = createAccounts(clients);
//    List<String> allAccountIbans = new ArrayList<>();
//    allAccountIbans.addAll(accountIbans.get("current"));
//    allAccountIbans.addAll(accountIbans.get("savings"));

    // Then generate transactions for the accounts
    transactionInitializer.initialize(accounts);

    // Ensure some current accounts have negative balances
    currentAccountInitializer.ensureCurrentAccountsWithNegativeBalances(accounts.get("current"));

    log.info("Database initialization completed");
  }


  /**
   * Creates accounts for the given clients.
   * Randomly creates current or savings accounts.
   *
   * @param clients List of clients to create accounts for
   * @return Map of IBANs for the created accounts
   */
  private Map<String, List<String>> createAccounts(List<Client> clients) {
    int totalAccounts = MIN_ACCOUNTS + random.nextInt(MAX_ADDITIONAL_ACCOUNTS + 1);
    int totalCurrentAccounts = (int) Math.ceil(totalAccounts * 0.5);
    int totalSavingsAccounts = totalAccounts - totalCurrentAccounts;

    Map<String, List<String>> accountIbans = new HashMap<>();

    List<String> currentAccountIbans = currentAccountInitializer.initialize(totalCurrentAccounts, clients);
    List<String> savingsAccountIbans = savingsAccountInitializer.initialize(totalSavingsAccounts, clients);

    accountIbans.put("current", currentAccountIbans);
    accountIbans.put("savings", savingsAccountIbans);

    return accountIbans;
  }

}
