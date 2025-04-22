package de.raywotrainings.banking.bankingservice.init;

import com.github.javafaker.Faker;
import de.raywotrainings.banking.bankingservice.control.account.*;
import de.raywotrainings.banking.bankingservice.control.client.Client;
import de.raywotrainings.banking.bankingservice.control.client.ClientsService;
import de.raywotrainings.banking.bankingservice.entity.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Initializes sample data for the banking application.
 * Creates clients, accounts, and transactions for testing purposes.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer {
  // Constants for data generation
  private static final int NUMBER_OF_CLIENTS = 10;
  private static final int MIN_ACCOUNTS = 20;
  private static final int MAX_ADDITIONAL_ACCOUNTS = 10;
  private static final double ACCOUNTS_WITH_TRANSACTIONS_PERCENTAGE = 0.7;
  private static final double CURRENT_ACCOUNTS_WITH_NEGATIVE_BALANCE_PERCENTAGE = 0.3;

  // Constants for current accounts
  private static final int MIN_CURRENT_ACCOUNT_DEPOSIT = 1000;
  private static final int MAX_ADDITIONAL_CURRENT_DEPOSIT = 9000;
  private static final int MIN_OVERDRAFT_LIMIT = 500;
  private static final int MAX_ADDITIONAL_OVERDRAFT = 1500;
  private static final int OVERDRAFT_ROUNDING_FACTOR = 100;
  private static final double MIN_OVERDRAFT_INTEREST_RATE = 7.0;
  private static final double MAX_ADDITIONAL_OVERDRAFT_INTEREST = 6.75;

  // Constants for savings accounts
  private static final int MIN_SAVINGS_ACCOUNT_DEPOSIT = 2000;
  private static final int MAX_ADDITIONAL_SAVINGS_DEPOSIT = 8000;
  private static final double MIN_SAVINGS_INTEREST_RATE = 0.25;
  private static final double MAX_ADDITIONAL_SAVINGS_INTEREST = 3.0;

  // Constants for transactions
  private static final int MIN_FEW_TRANSACTIONS = 1;
  private static final int MAX_ADDITIONAL_FEW_TRANSACTIONS = 9;
  private static final int MIN_MANY_TRANSACTIONS = 50;
  private static final int MAX_ADDITIONAL_MANY_TRANSACTIONS = 50;
  private static final int MANY_TRANSACTIONS_WEEKS = 8;
  private static final int FEW_TRANSACTIONS_WEEKS = 4;
  private static final int MIN_TRANSACTION_AMOUNT = 1;
  private static final int MAX_ADDITIONAL_TRANSACTION_AMOUNT = 999;

  // Constants for time periods
  private static final int MIN_DEPOSIT_MONTHS_AGO = 1;
  private static final int MAX_ADDITIONAL_DEPOSIT_MONTHS = 11;

  // Interest rate rounding factor (to nearest 0.05)
  private static final double INTEREST_RATE_ROUNDING_FACTOR = 20.0;

  // Transaction descriptions
  private static final String INITIAL_DEPOSIT_DESCRIPTION = "Ersteinzahlung";
  private static final String[] TRANSACTION_DESCRIPTIONS = {
      "Gehalt", "Miete", "Einkauf", "Versicherung", "Strom", "Internet",
      "Telefon", "Restaurant", "Kino", "Tanken", "Kleidung", "Geschenk"
  };

  private final ClientRepository clientRepository;
  private final ClientsService clientsService;
  private final AccountsService accountsService;
  private final EntriesService entriesService;

  private final Random random = new Random();

  private static final ThreadLocal<Faker> threadLocalFaker =
      ThreadLocal.withInitial(() ->
          new Faker(Locale.GERMANY, new Random(42)));


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
    List<Client> clients = createClients();
    Map<String, List<String>> accountIbans = createAccounts(clients);
    List<String> allAccountIbans = new ArrayList<>();
    allAccountIbans.addAll(accountIbans.get("current"));
    allAccountIbans.addAll(accountIbans.get("savings"));

    // Then generate transactions for the accounts
    createTransactions(allAccountIbans, accountIbans.get("current"));

    log.info("Database initialization completed");
  }


  /**
   * Gets the thread-local Faker instance for generating consistent test data.
   */
  private Faker getFaker() {
    return threadLocalFaker.get();
  }


  /**
   * Creates a specified number of clients with realistic German names.
   *
   * @return List of created clients
   */
  private List<Client> createClients() {
    List<Client> clients = new ArrayList<>();

    for (int i = 1; i <= NUMBER_OF_CLIENTS; i++) {
      String firstName = getFaker().name().firstName();
      String lastName = getFaker().name().lastName();
      Client client = new Client(null, firstName, lastName);

      clients.add(clientsService.addClient(client));
      log.info("Created client: {} {}", client.getFirstname(), client.getLastname());
    }

    return clients;
  }


  /**
   * Creates accounts for the given clients.
   * Randomly creates current or savings accounts with initial deposits.
   *
   * @param clients List of clients to create accounts for
   * @return Map of IBANs for the created accounts
   */
  private Map<String, List<String>> createAccounts(List<Client> clients) {
    Map<String, List<String>> accountIbans = new HashMap<>();
    List<String> currentAccountIbans = new ArrayList<>();
    List<String> savingsAccountIbans = new ArrayList<>();
    accountIbans.put("current", currentAccountIbans);
    accountIbans.put("savings", savingsAccountIbans);

    int totalAccounts = MIN_ACCOUNTS + random.nextInt(MAX_ADDITIONAL_ACCOUNTS + 1);

    for (int i = 0; i < totalAccounts; i++) {
      int clientIndex = random.nextInt(clients.size());
      Client client = clients.get(clientIndex);

      if (random.nextBoolean()) {
        String iban = createCurrentAccount(client);
        currentAccountIbans.add(iban);
      } else {
        String iban = createSavingsAccount(client);
        savingsAccountIbans.add(iban);
      }
    }

    return accountIbans;
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
        MIN_OVERDRAFT_INTEREST_RATE,
        MAX_ADDITIONAL_OVERDRAFT_INTEREST
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


  /**
   * Creates a savings account for the specified client without an initial deposit.
   *
   * @param client The client to create the account for
   * @return The IBAN of the created account
   */
  private String createSavingsAccount(Client client) {
    BigDecimal interestRate = generateInterestRate(
        MIN_SAVINGS_INTEREST_RATE,
        MAX_ADDITIONAL_SAVINGS_INTEREST
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
   * Generates a random initial deposit amount within the specified range.
   *
   * @param minAmount     The minimum deposit amount
   * @param maxAdditional The maximum additional amount above the minimum
   * @return The generated deposit amount as BigDecimal
   */
  private BigDecimal generateInitialDeposit(int minAmount, int maxAdditional) {
    return new BigDecimal(minAmount + random.nextInt(maxAdditional + 1));
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
   * @param minRate       The minimum interest rate
   * @param maxAdditional The maximum additional rate above the minimum
   * @return The generated interest rate as BigDecimal
   */
  private BigDecimal generateInterestRate(double minRate, double maxAdditional) {
    double rawInterestRate = minRate + (random.nextInt((int) (maxAdditional * 100)) / 100.0);
    double roundedInterestRate = Math.round(rawInterestRate * INTEREST_RATE_ROUNDING_FACTOR)
        / INTEREST_RATE_ROUNDING_FACTOR;
    return BigDecimal.valueOf(roundedInterestRate);
  }


  /**
   * Creates transactions for the given accounts.
   * First creates an initial transaction for each account according to its type,
   * then creates additional transactions for some accounts.
   *
   * @param accountIbans List of account IBANs to create transactions for
   */
  private void createTransactions(List<String> accountIbans, List<String> currentAccountIbans) {
    // First create initial transactions for all accounts
    for (String iban : accountIbans) {
      createInitialTransaction(iban);
    }

    // Ensure some current accounts have negative balances
    ensureCurrentAccountsWithNegativeBalances(currentAccountIbans);

    // Then create additional transactions for some accounts
    String accountWithManyTransactions = accountIbans.get(random.nextInt(accountIbans.size()));

    for (String iban : accountIbans) {
      if (iban.equals(accountWithManyTransactions)) {
        createManyTransactions(iban);
      } else if (random.nextDouble() < ACCOUNTS_WITH_TRANSACTIONS_PERCENTAGE) {
        int transactionCount = MIN_FEW_TRANSACTIONS + random.nextInt(MAX_ADDITIONAL_FEW_TRANSACTIONS + 1);
        createTransactionsForAccount(iban, transactionCount);
      } else {
        log.info("Account with IBAN: {} has only the initial transaction", iban);
      }
    }
  }


  /**
   * Creates the initial transaction for an account based on its type.
   * For savings accounts, the first transaction must be a deposit.
   * For current accounts, the first transaction can be a withdrawal if within overdraft limit.
   *
   * @param iban The IBAN of the account to create the initial transaction for
   */
  private void createInitialTransaction(String iban) {
    Account account = accountsService.getAccountByIban(iban);

    // Generate a random date in the past for the initial transaction
    int monthsAgo = MIN_DEPOSIT_MONTHS_AGO + random.nextInt(MAX_ADDITIONAL_DEPOSIT_MONTHS + 1);
    ZonedDateTime transactionDate = ZonedDateTime.now().minusMonths(monthsAgo);

    // Create the entry with a random amount
    Entry entry = new Entry(
        iban,
        INITIAL_DEPOSIT_DESCRIPTION,
        transactionDate,
        BigDecimal.ZERO,
        Entry.Type.DEPOSIT
    );

    if (account instanceof SavingsAccount) {
      // For savings accounts, first transaction must be a deposit
      BigDecimal amount = generateInitialDeposit(
          MIN_SAVINGS_ACCOUNT_DEPOSIT,
          MAX_ADDITIONAL_SAVINGS_DEPOSIT
      );
      entry.setAmount(amount);
      entry.setType(Entry.Type.DEPOSIT);

      log.info("Created initial deposit of {} for savings account with IBAN: {}", amount, iban);
    } else if (account instanceof CurrentAccount) {
      // For current accounts, first transaction can be a withdrawal if within overdraft limit
      BigDecimal amount = generateInitialDeposit(
          MIN_CURRENT_ACCOUNT_DEPOSIT,
          MAX_ADDITIONAL_CURRENT_DEPOSIT
      );
      entry.setAmount(amount);

      // Randomly decide if this should be a deposit or withdrawal
      if (random.nextBoolean() && account.availableAmount().compareTo(amount) >= 0) {
        // Make it a withdrawal (will result in a negative balance within overdraft limit)
        entry.setType(Entry.Type.WITHDRAW);
        log.info("Created initial withdrawal of {} for current account with IBAN: {}", amount, iban);
      } else {
        // Make it a deposit
        entry.setType(Entry.Type.DEPOSIT);
        log.info("Created initial deposit of {} for current account with IBAN: {}", amount, iban);
      }
    }

    // Process the transaction
    entriesService.makeEntry(iban, entry);
  }


  /**
   * Creates many transactions for an account over a longer period.
   *
   * @param iban The IBAN of the account to create transactions for
   */
  private void createManyTransactions(String iban) {
    int transactionCount = MIN_MANY_TRANSACTIONS + random.nextInt(MAX_ADDITIONAL_MANY_TRANSACTIONS + 1);
    log.info("Creating {} transactions for account with IBAN: {}", transactionCount, iban);

    ZonedDateTime startDate = ZonedDateTime.now().minusDays(MANY_TRANSACTIONS_WEEKS * 7);
    createRandomTransactions(iban, transactionCount, startDate, MANY_TRANSACTIONS_WEEKS);
  }


  /**
   * Creates a few transactions for an account over a shorter period.
   *
   * @param iban  The IBAN of the account to create transactions for
   * @param count The number of transactions to create
   */
  private void createTransactionsForAccount(String iban, int count) {
    log.info("Creating {} transactions for account with IBAN: {}", count, iban);

    ZonedDateTime startDate = ZonedDateTime.now().minusDays(FEW_TRANSACTIONS_WEEKS * 7);
    createRandomTransactions(iban, count, startDate, FEW_TRANSACTIONS_WEEKS);
  }


  /**
   * Creates random transactions for an account over the specified period.
   *
   * @param iban      The IBAN of the account to create transactions for
   * @param count     The number of transactions to create
   * @param startDate The start date for the transactions
   * @param weeks     The number of weeks to distribute transactions over
   */
  private void createRandomTransactions(String iban, int count, ZonedDateTime startDate, int weeks) {
    for (int i = 0; i < count; i++) {
      long minutesToAdd = random.nextInt(weeks * 7 * 24 * 60);
      ZonedDateTime transactionDate = startDate.plusMinutes(minutesToAdd);
      createTransaction(iban, transactionDate);
    }
  }


  /**
   * Creates a single transaction for an account on the specified date.
   *
   * @param iban The IBAN of the account to create the transaction for
   * @param date The date of the transaction
   */
  private void createTransaction(String iban, ZonedDateTime date) {
    Entry entry = new Entry();
    entry.setId(UUID.randomUUID().toString());
    entry.setIban(iban);
    entry.setEntryDate(date);

    BigDecimal amount = new BigDecimal(
        MIN_TRANSACTION_AMOUNT + random.nextInt(MAX_ADDITIONAL_TRANSACTION_AMOUNT + 1)
    );
    entry.setAmount(amount);

    Account account = accountsService.getAccountByIban(iban);
    Entry.Type type = determineTransactionType(account, amount);
    entry.setType(type);

    entry.setDescription(TRANSACTION_DESCRIPTIONS[random.nextInt(TRANSACTION_DESCRIPTIONS.length)]);

    try {
      entriesService.makeEntry(iban, entry);
    } catch (InsufficientFundsException e) {
      // If withdrawal fails due to insufficient funds, make it a deposit instead
      entry.setType(Entry.Type.DEPOSIT);
      entriesService.makeEntry(iban, entry);
    }
  }


  /**
   * Determines whether a transaction should be a deposit or withdrawal
   * based on the account type and available balance.
   *
   * @param account The account to determine the transaction type for
   * @param amount  The amount of the transaction
   * @return The determined transaction type
   */
  private Entry.Type determineTransactionType(Account account, BigDecimal amount) {
    if (account instanceof SavingsAccount) {
      return determineSavingsAccountTransactionType((SavingsAccount) account, amount);
    } else if (account instanceof CurrentAccount) {
      return determineCurrentAccountTransactionType((CurrentAccount) account, amount);
    } else {
      return Entry.Type.DEPOSIT;
    }
  }


  /**
   * Determines the transaction type for a savings account.
   * Only allows withdrawals if there's enough available amount.
   * Uses the availableAmount() method to determine the withdrawal limit.
   *
   * @param account The savings account
   * @param amount  The transaction amount
   * @return The determined transaction type
   */
  private Entry.Type determineSavingsAccountTransactionType(SavingsAccount account, BigDecimal amount) {
    // For savings accounts, availableAmount() returns just the balance
    if (account.availableAmount().compareTo(amount) >= 0 && random.nextBoolean()) {
      return Entry.Type.WITHDRAW;
    } else {
      return Entry.Type.DEPOSIT;
    }
  }


  /**
   * Determines the transaction type for a current account.
   * Allows withdrawals if within the available amount (balance + overdraft limit).
   * Uses the availableAmount() method to determine the withdrawal limit.
   * This ensures current accounts can have negative balances within their overdraft limits.
   *
   * @param account The current account
   * @param amount  The transaction amount
   * @return The determined transaction type
   */
  private Entry.Type determineCurrentAccountTransactionType(CurrentAccount account, BigDecimal amount) {
    // For current accounts, availableAmount() returns balance + overdraft limit
    if (account.availableAmount().compareTo(amount) >= 0 && random.nextBoolean()) {
      return Entry.Type.WITHDRAW;
    } else {
      return Entry.Type.DEPOSIT;
    }
  }


  /**
   * Ensures that a certain percentage of current accounts have negative balances.
   * Uses the CURRENT_ACCOUNTS_WITH_NEGATIVE_BALANCE_PERCENTAGE constant to determine
   * how many accounts should have negative balances.
   * 
   * @param currentAccountIbans List of current account IBANs
   */
  private void ensureCurrentAccountsWithNegativeBalances(List<String> currentAccountIbans) {
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
        BigDecimal randomFactor = new BigDecimal(random.nextDouble());
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
}
