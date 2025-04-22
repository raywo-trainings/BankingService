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
   */
  @EventListener(ApplicationReadyEvent.class)
  public void initData() {
    if (clientRepository.count() > 0) {
      log.info("Database already contains data, skipping initialization");
      return;
    }

    log.info("Initializing database with sample data");

    List<Client> clients = createClients();
    List<String> accountIbans = createAccounts(clients);
    createTransactions(accountIbans);

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
   * @return List of IBANs for the created accounts
   */
  private List<String> createAccounts(List<Client> clients) {
    List<String> accountIbans = new ArrayList<>();
    int totalAccounts = MIN_ACCOUNTS + random.nextInt(MAX_ADDITIONAL_ACCOUNTS + 1);

    for (int i = 0; i < totalAccounts; i++) {
      int clientIndex = random.nextInt(clients.size());
      Client client = clients.get(clientIndex);

      if (random.nextBoolean()) {
        String iban = createCurrentAccount(client);
        accountIbans.add(iban);
      } else {
        String iban = createSavingsAccount(client);
        accountIbans.add(iban);
      }
    }

    return accountIbans;
  }


  /**
   * Creates a current account for the specified client with an initial deposit.
   *
   * @param client The client to create the account for
   * @return The IBAN of the created account
   */
  private String createCurrentAccount(Client client) {
    BigDecimal initialDeposit = generateInitialDeposit(
        MIN_CURRENT_ACCOUNT_DEPOSIT,
        MAX_ADDITIONAL_CURRENT_DEPOSIT
    );

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
    makeInitialDeposit(savedAccount.getIban(), initialDeposit);

    log.info("Created current account with IBAN: {} for client: {} {}, initial deposit: {}",
        savedAccount.getIban(), client.getFirstname(), client.getLastname(), initialDeposit);

    return savedAccount.getIban();
  }


  /**
   * Creates a savings account for the specified client with an initial deposit.
   *
   * @param client The client to create the account for
   * @return The IBAN of the created account
   */
  private String createSavingsAccount(Client client) {
    BigDecimal initialDeposit = generateInitialDeposit(
        MIN_SAVINGS_ACCOUNT_DEPOSIT,
        MAX_ADDITIONAL_SAVINGS_DEPOSIT
    );

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
    makeInitialDeposit(savedAccount.getIban(), initialDeposit);

    log.info("Created savings account with IBAN: {} for client: {} {}, initial deposit: {}",
        savedAccount.getIban(), client.getFirstname(), client.getLastname(), initialDeposit);

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
   * Makes an initial deposit to the specified account.
   * The deposit date is set to a random date in the past.
   *
   * @param iban   The IBAN of the account to deposit to
   * @param amount The amount to deposit
   */
  private void makeInitialDeposit(String iban, BigDecimal amount) {
    int monthsAgo = MIN_DEPOSIT_MONTHS_AGO + random.nextInt(MAX_ADDITIONAL_DEPOSIT_MONTHS + 1);
    ZonedDateTime depositDate = ZonedDateTime.now().minusMonths(monthsAgo);

    Entry depositEntry = new Entry(
        iban,
        INITIAL_DEPOSIT_DESCRIPTION,
        depositDate,
        amount,
        Entry.Type.DEPOSIT
    );

    entriesService.makeEntry(iban, depositEntry);
  }


  /**
   * Creates transactions for the given accounts.
   * One account will have many transactions, some will have a few, and some none.
   *
   * @param accountIbans List of account IBANs to create transactions for
   */
  private void createTransactions(List<String> accountIbans) {
    String accountWithManyTransactions = accountIbans.get(random.nextInt(accountIbans.size()));

    for (String iban : accountIbans) {
      if (iban.equals(accountWithManyTransactions)) {
        createManyTransactions(iban);
      } else if (random.nextDouble() < ACCOUNTS_WITH_TRANSACTIONS_PERCENTAGE) {
        int transactionCount = MIN_FEW_TRANSACTIONS + random.nextInt(MAX_ADDITIONAL_FEW_TRANSACTIONS + 1);
        createTransactionsForAccount(iban, transactionCount);
      } else {
        log.info("Account with IBAN: {} has no transactions", iban);
      }
    }
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
   * Only allows withdrawals if there's enough balance.
   *
   * @param account The savings account
   * @param amount  The transaction amount
   * @return The determined transaction type
   */
  private Entry.Type determineSavingsAccountTransactionType(SavingsAccount account, BigDecimal amount) {
    if (account.getBalance().compareTo(amount) >= 0 && random.nextBoolean()) {
      return Entry.Type.WITHDRAW;
    } else {
      return Entry.Type.DEPOSIT;
    }
  }


  /**
   * Determines the transaction type for a current account.
   * Allows withdrawals if within balance + overdraft limit.
   *
   * @param account The current account
   * @param amount  The transaction amount
   * @return The determined transaction type
   */
  private Entry.Type determineCurrentAccountTransactionType(CurrentAccount account, BigDecimal amount) {
    if (account.getBalance().add(account.getOverdraftLimit()).compareTo(amount) >= 0
        && random.nextBoolean()) {
      return Entry.Type.WITHDRAW;
    } else {
      return Entry.Type.DEPOSIT;
    }
  }
}
