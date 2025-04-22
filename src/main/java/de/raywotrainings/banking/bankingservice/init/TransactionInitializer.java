package de.raywotrainings.banking.bankingservice.init;

import de.raywotrainings.banking.bankingservice.control.account.AccountsService;
import de.raywotrainings.banking.bankingservice.control.account.EntriesService;
import de.raywotrainings.banking.bankingservice.control.account.Entry;
import de.raywotrainings.banking.bankingservice.control.account.InsufficientFundsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Initializer responsible for creating and processing transactions.
 * Follows the Single Responsibility Principle by focusing only on transaction operations.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionInitializer {

  // Constants for transactions
  private static final int MIN_FEW_TRANSACTIONS = 1;
  private static final int MAX_ADDITIONAL_FEW_TRANSACTIONS = 9;
  private static final int MIN_MANY_TRANSACTIONS = 50;
  private static final int MAX_ADDITIONAL_MANY_TRANSACTIONS = 50;
  private static final int MANY_TRANSACTIONS_WEEKS = 8;
  private static final int FEW_TRANSACTIONS_WEEKS = 4;
  private static final int MIN_TRANSACTION_AMOUNT = 1;
  private static final int MAX_ADDITIONAL_TRANSACTION_AMOUNT = 999;
  private static final int MIN_DEPOSIT_MONTHS_AGO = 1;
  private static final int MAX_ADDITIONAL_DEPOSIT_MONTHS = 11;
  private static final double ACCOUNTS_WITH_TRANSACTIONS_PERCENTAGE = 0.7;

  // Transaction descriptions
  private static final String INITIAL_DEPOSIT_DESCRIPTION = "Ersteinzahlung";
  private static final String[] TRANSACTION_DESCRIPTIONS = {
      "Gehalt", "Miete", "Einkauf", "Versicherung", "Strom", "Internet",
      "Telefon", "Restaurant", "Kino", "Tanken", "Kleidung", "Geschenk"
  };

  private final AccountsService accountsService;
  private final EntriesService entriesService;
  private final Random random = new Random();


  public void initialize(Map<String, List<String>> accountIbans) {
    log.info("Transaction initializer ready");

    /*
    1. Pick randomly 85% of the accounts and create initial transactions for
       them. All others will have no transactions.
    2. For every account in that list, create a first transaction with a random
       amount. Make sure that savings accounts only have deposits. Create
       deposits for 60% of the current accounts. For the remaining 40% of
       the current accounts create a withdrawal within the available amount.
       Make sure that some of the current accounts have a negative balance.
    3. Pick randomly 35% of all accounts and create 10-15 transactions for them
    4. Pick randomly 45% of all accounts and create 2-3 transactions for them
     */

    createTransactions(accountIbans);
  }


  /**
   * Creates transactions for the given accounts.
   * First creates an initial transaction for each account according to its type,
   * then creates additional transactions for some accounts.
   *
   * @param accountIbans List of account IBANs to create transactions for
   */
  private void createTransactions(Map<String, List<String>> accountIbans) {
    List<String> allIbans = accountIbans.values()
        .stream()
        .flatMap(List::stream)
        .toList();

    for (String iban : allIbans) {
      createInitialTransaction(iban);
    }

    // Then create additional transactions for some accounts
    String accountWithManyTransactions = allIbans.get(random.nextInt(allIbans.size()));

    for (String iban : allIbans) {
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
   * This method determines the appropriate amount and type based on the account type.
   *
   * @param iban The IBAN of the account to create the initial transaction for
   */
  private void createInitialTransaction(String iban) {
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

    // The specific account initializers will handle setting the amount and type
    processEntry(entry);
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
  private void createRandomTransactions(String iban,
                                        int count,
                                        ZonedDateTime startDate,
                                        int weeks) {
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
    BigDecimal amount = generateTransactionAmount();

    String description = TRANSACTION_DESCRIPTIONS[random.nextInt(TRANSACTION_DESCRIPTIONS.length)];

    // Default to deposit, will be determined by account-specific initializers later
    Entry.Type type = Entry.Type.DEPOSIT;

    Entry entry = new Entry(
        iban,
        description,
        date,
        amount,
        type
    );

    try {
      processEntry(entry);
    } catch (InsufficientFundsException e) {
      // If withdrawal fails due to insufficient funds, make it a deposit instead
      entry.setType(Entry.Type.DEPOSIT);
      processEntry(entry);
    }
  }


  /**
   * Processes an entry by making the entry through the entries service.
   *
   * @param entry The entry to process
   */
  public void processEntry(Entry entry) {
    entriesService.makeEntry(entry.getIban(), entry);
  }


  /**
   * Generates a random transaction amount.
   *
   * @return A random transaction amount
   */
  public BigDecimal generateTransactionAmount() {
    return new BigDecimal(
        MIN_TRANSACTION_AMOUNT + random.nextInt(MAX_ADDITIONAL_TRANSACTION_AMOUNT + 1)
    );
  }
}
