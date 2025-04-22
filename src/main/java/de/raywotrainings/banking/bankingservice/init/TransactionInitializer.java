package de.raywotrainings.banking.bankingservice.init;

import de.raywotrainings.banking.bankingservice.control.account.Account;
import de.raywotrainings.banking.bankingservice.control.account.AccountsService;
import de.raywotrainings.banking.bankingservice.control.account.EntriesService;
import de.raywotrainings.banking.bankingservice.control.account.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.*;

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

    // Combine all account IBANs into a single list
    List<String> allAccountIbans = new ArrayList<>();
    allAccountIbans.addAll(accountIbans.get("current"));
    allAccountIbans.addAll(accountIbans.get("savings"));

    // 1. Pick randomly 85% of the accounts for transactions
    int accountsWithTransactionsCount = (int) Math.ceil(allAccountIbans.size() * ACCOUNTS_WITH_TRANSACTIONS_PERCENTAGE);
    Collections.shuffle(allAccountIbans);
    List<String> accountsWithTransactions = allAccountIbans.subList(0, accountsWithTransactionsCount);

    log.info("Creating transactions for {} out of {} accounts", accountsWithTransactionsCount, allAccountIbans.size());

    // 2. Create initial transactions for selected accounts
    createInitialTransactionsForSavingsAccounts(accountIbans.get("savings"));
    createInitialTransactionsForCurrentAccounts(accountIbans.get("current"));

    // 3. Create 10-15 transactions for 35% of accounts
    int manyTransactionsCount = (int) Math.ceil(allAccountIbans.size() * 0.35);
    Collections.shuffle(allAccountIbans);
    List<String> accountsWithManyTransactions = allAccountIbans.subList(0, manyTransactionsCount);
    createMultipleTransactions(accountsWithManyTransactions, accountIbans.get("savings"),
        MIN_MANY_TRANSACTIONS, MAX_ADDITIONAL_MANY_TRANSACTIONS, MANY_TRANSACTIONS_WEEKS);

    // 4. Create 2-3 transactions for 45% of accounts
    int fewTransactionsCount = (int) Math.ceil(allAccountIbans.size() * 0.45);
    Collections.shuffle(allAccountIbans);
    List<String> accountsWithFewTransactions = allAccountIbans.subList(0, fewTransactionsCount);
    createMultipleTransactions(accountsWithFewTransactions, accountIbans.get("savings"),
        MIN_FEW_TRANSACTIONS, MAX_ADDITIONAL_FEW_TRANSACTIONS, FEW_TRANSACTIONS_WEEKS);

    log.info("Transaction initialization completed");
  }


  private void createInitialTransactionsForSavingsAccounts(List<String> accounts) {
    BigDecimal amount = generateRandomAmount(BigDecimal.valueOf(MAX_ADDITIONAL_TRANSACTION_AMOUNT));
    ZonedDateTime transactionDate = generateRandomPastDate(
    );

    accounts.forEach(iban ->
        createEntry(
            iban,
            INITIAL_DEPOSIT_DESCRIPTION,
            amount,
            Entry.Type.DEPOSIT,
            transactionDate
        )
    );
  }


  private void createInitialTransactionsForCurrentAccounts(List<String> ibans) {
    ZonedDateTime transactionDate = generateRandomPastDate(
    );

    Collections.shuffle(ibans);
    int sixtyPercentAccounts = (int) Math.ceil(ibans.size() * 0.6);

    // Create an initial deposit for 60% of accounts
    for (int i = 0; i < sixtyPercentAccounts; i++) {
      createEntry(
          ibans.get(i),
          INITIAL_DEPOSIT_DESCRIPTION,
          generateRandomAmount(BigDecimal.valueOf(MAX_ADDITIONAL_TRANSACTION_AMOUNT)),
          Entry.Type.DEPOSIT,
          transactionDate
      );
    }

    // Create initial withdrawal for remaining 40% of accounts
    for (int i = sixtyPercentAccounts; i < ibans.size(); i++) {
      String iban = ibans.get(i);
      Account account = accountsService.getAccountByIban(iban);
      BigDecimal availableAmount = account.availableAmount();
      BigDecimal amount = generateRandomAmount(availableAmount);

      // Ensure withdrawal amount is within available amount
//      BigDecimal withdrawalAmount = availableAmount.min(amount.multiply(BigDecimal.valueOf(0.95)));
      createEntry(
          iban,
          getRandomTransactionDescription(),
          amount,
          Entry.Type.WITHDRAW,
          transactionDate
      );
    }
  }


  private void createMultipleTransactions(List<String> accounts,
                                          List<String> savingsAccounts,
                                          int minTransactions,
                                          int maxAdditionalTransactions,
                                          int weeksSpan) {
    for (String iban : accounts) {
      int transactionCount = minTransactions + random.nextInt(maxAdditionalTransactions + 1);

      for (int i = 0; i < transactionCount; i++) {
        BigDecimal amount = generateRandomAmount(BigDecimal.valueOf(MAX_ADDITIONAL_TRANSACTION_AMOUNT));
        ZonedDateTime transactionDate = generateRandomRecentDate(weeksSpan);
        String description = getRandomTransactionDescription();

        // For current accounts: random deposits and withdrawals
        Account account = accountsService.getAccountByIban(iban);

        if (random.nextBoolean()) {
          // Create deposit
          createEntry(
              iban,
              description,
              amount,
              Entry.Type.DEPOSIT,
              transactionDate
          );
        } else {
          // Create withdrawal if possible
          BigDecimal availableAmount = account.availableAmount();

          // Ensure withdrawal amount is within available amount
          BigDecimal withdrawalAmount = amount.min(availableAmount);
          if (withdrawalAmount.compareTo(BigDecimal.ZERO) >= 0) {
            createEntry(
                iban,
                description,
                withdrawalAmount,
                Entry.Type.WITHDRAW,
                transactionDate
            );
          } else {
            // If no funds available, create a deposit instead
            createEntry(
                iban,
                description,
                amount,
                Entry.Type.DEPOSIT,
                transactionDate
            );
          }
        }
      }
    }
  }


  private void createEntry(String iban,
                           String description,
                           BigDecimal amount,
                           Entry.Type type,
                           ZonedDateTime date) {
    Entry entry = new Entry(iban, description, date, amount, type);
    entriesService.makeEntry(iban, entry);
    log.debug("Created {} of {} for account {}", type, amount, iban);
  }


  private BigDecimal generateRandomAmount(BigDecimal maxAmount) {
    int amount = MIN_TRANSACTION_AMOUNT + random.nextInt(maxAmount.intValue() + 1);
    return BigDecimal.valueOf(amount);
  }


  private ZonedDateTime generateRandomPastDate() {
    int additionalMonths = random.nextInt(MAX_ADDITIONAL_DEPOSIT_MONTHS + 1);
    int monthsAgo = MIN_DEPOSIT_MONTHS_AGO + additionalMonths;
    return ZonedDateTime.now().minusMonths(monthsAgo)
        .withDayOfMonth(1 + random.nextInt(28))
        .withHour(9 + random.nextInt(9))
        .withMinute(random.nextInt(60))
        .withSecond(random.nextInt(60));
  }


  private ZonedDateTime generateRandomRecentDate(int weeksSpan) {
    ZonedDateTime baseDate = ZonedDateTime.now().minusWeeks(random.nextInt(weeksSpan) + 1);

    // Adjust to a weekday (Monday to Friday)
    DayOfWeek dayOfWeek = DayOfWeek.of(1 + random.nextInt(5)); // Monday to Friday
    int daysToAdd = (dayOfWeek.getValue() - baseDate.getDayOfWeek().getValue() + 7) % 7;

    return baseDate.plusDays(daysToAdd)
        .withHour(9 + random.nextInt(9))
        .withMinute(random.nextInt(60))
        .withSecond(random.nextInt(60));
  }


  private String getRandomTransactionDescription() {
    return TRANSACTION_DESCRIPTIONS[random.nextInt(TRANSACTION_DESCRIPTIONS.length)];
  }
}
