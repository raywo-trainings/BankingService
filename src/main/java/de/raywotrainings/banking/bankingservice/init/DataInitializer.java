package de.raywotrainings.banking.bankingservice.init;

import com.github.javafaker.Faker;
import de.raywotrainings.banking.bankingservice.control.account.*;
import de.raywotrainings.banking.bankingservice.control.client.Client;
import de.raywotrainings.banking.bankingservice.control.client.ClientsService;
import de.raywotrainings.banking.bankingservice.entity.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

@RequiredArgsConstructor
@Component
public class DataInitializer {

  private final Logger log = LoggerFactory.getLogger(DataInitializer.class);

  private final ClientRepository clientRepository;
  private final ClientsService clientsService;
  private final AccountsService accountsService;

  private final Random random = new Random();

  private static final ThreadLocal<Faker> threadLocalFaker =
      ThreadLocal.withInitial(() ->
          new Faker(Locale.GERMANY, new Random(42)));
  private final EntriesService entriesService;


  @EventListener(ApplicationReadyEvent.class)
  public void initData() {
    if (clientRepository.count() > 0) {
      log.info("Database already contains data, skipping initialization");
      return;
    }

    log.info("Initializing database with sample data");

    // Create clients
    List<Client> clients = createClients();

    // Create accounts
    List<String> accountIbans = createAccounts(clients);

    // Create transactions
    createTransactions(accountIbans);

    log.info("Database initialization completed");
  }


  private Faker getFaker() {
    return threadLocalFaker.get();
  }


  private List<Client> createClients() {
    List<Client> clients = new ArrayList<>();

    // Create 10 clients with realistic German names
    for (int i = 1; i <= 10; i++) {
      String firstName = getFaker().name().firstName();
      String lastName = getFaker().name().lastName();
      Client client = new Client(null, firstName, lastName);

      clients.add(clientsService.addClient(client));
      log.info("Created client: {} {}", client.getFirstname(), client.getLastname());
    }

    return clients;
  }


  private List<String> createAccounts(List<Client> clients) {
    List<String> accountIbans = new ArrayList<>();

    // Create 20-30 accounts of different types
    int totalAccounts = 20 + random.nextInt(11); // Between 20 and 30

    for (int i = 0; i < totalAccounts; i++) {
      // Some clients will have multiple accounts, others none
      int clientIndex = random.nextInt(clients.size());
      Client client = clients.get(clientIndex);

      // Randomly create current or savings account
      if (random.nextBoolean()) {
        // Create current account with zero balance
        BigDecimal initialDeposit = new BigDecimal(1000 + random.nextInt(9000));
        // Round overdraft limit to the nearest 100€
        int rawOverdraftLimit = 500 + random.nextInt(1500);
        int roundedOverdraftLimit = Math.round(rawOverdraftLimit / 100f) * 100;
        BigDecimal overdraftLimit = new BigDecimal(roundedOverdraftLimit);
        // Overdraft interest rate between 7.0% and 13.75%, rounded to nearest 0.05
        double rawInterestRate = 7.0 + (random.nextInt(675) / 100.0);
        double roundedInterestRate = Math.round(rawInterestRate * 20) / 20.0;
        BigDecimal overdraftInterestRate = BigDecimal.valueOf(roundedInterestRate);

        // null IBAN and null owner will be set by the service, balance is 0
        CurrentAccount account = new CurrentAccount(
            null,
            null,
            BigDecimal.ZERO,
            overdraftLimit,
            overdraftInterestRate
        );

        CurrentAccount savedAccount = accountsService.addCurrentAccount(account, client.getId());

        // Make initial deposit
        // Initial deposit should be in the past (between 1 and 12 months ago)
        int monthsAgo = 1 + random.nextInt(12);
        ZonedDateTime depositDate = ZonedDateTime.now().minusMonths(monthsAgo);

        Entry depositEntry = new Entry(
            savedAccount.getIban(),
            "Ersteinzahlung",
            depositDate,
            initialDeposit,
            Entry.Type.DEPOSIT
        );
        entriesService.makeEntry(savedAccount.getIban(), depositEntry);

        accountIbans.add(savedAccount.getIban());
        log.info("Created current account with IBAN: {} for client: {} {}, initial deposit: {}",
            savedAccount.getIban(), client.getFirstname(), client.getLastname(), initialDeposit);
      } else {
        // Create savings account with zero balance
        BigDecimal initialDeposit = new BigDecimal(2000 + random.nextInt(8000));
        // Interest rate between 0.25% and 3.25%, rounded to nearest 0.05
        double rawInterestRate = 0.25 + (random.nextInt(300) / 100.0);
        double roundedInterestRate = Math.round(rawInterestRate * 20) / 20.0;
        BigDecimal interestRate = BigDecimal.valueOf(roundedInterestRate);

        // null IBAN and null owner will be set by the service, balance is 0
        SavingsAccount account = new SavingsAccount(
            null,
            null,
            BigDecimal.ZERO,
            interestRate
        );

        SavingsAccount savedAccount = accountsService.addSavingsAccount(account, client.getId());

        // Make initial deposit
        // Initial deposit should be in the past (between 1 and 12 months ago)
        int monthsAgo = 1 + random.nextInt(12);
        ZonedDateTime depositDate = ZonedDateTime.now().minusMonths(monthsAgo);

        Entry depositEntry = new Entry(
            savedAccount.getIban(),
            "Ersteinzahlung",
            depositDate,
            initialDeposit,
            Entry.Type.DEPOSIT);
        entriesService.makeEntry(savedAccount.getIban(), depositEntry);

        accountIbans.add(savedAccount.getIban());
        log.info("Created savings account with IBAN: {} for client: {} {}, initial deposit: {}",
            savedAccount.getIban(), client.getFirstname(), client.getLastname(), initialDeposit);
      }
    }

    return accountIbans;
  }


  private void createTransactions(List<String> accountIbans) {
    // Select one account to have many transactions
    String accountWithManyTransactions = accountIbans.get(random.nextInt(accountIbans.size()));

    // For each account, decide if it should have transactions
    for (String iban : accountIbans) {
      if (iban.equals(accountWithManyTransactions)) {
        // Create many transactions over several weeks
        createManyTransactions(iban);
      } else if (random.nextDouble() < 0.7) { // 70% of accounts have transactions
        // Create a few transactions
        int transactionCount = 1 + random.nextInt(10);
        createTransactionsForAccount(iban, transactionCount);
      } else {
        log.info("Account with IBAN: {} has no transactions", iban);
      }
    }
  }


  private void createManyTransactions(String iban) {
    // Create 50-100 transactions over several weeks
    int transactionCount = 50 + random.nextInt(51);

    log.info("Creating {} transactions for account with IBAN: {}", transactionCount, iban);

    // Start date is 8 weeks ago
    ZonedDateTime startDate = ZonedDateTime.now().minusDays(8 * 7);

    for (int i = 0; i < transactionCount; i++) {
      // Distribute transactions over 8 weeks
      long minutesToAdd = random.nextInt(8 * 7 * 24 * 60);
      ZonedDateTime transactionDate = startDate.plusMinutes(minutesToAdd);

      createTransaction(iban, transactionDate);
    }
  }


  private void createTransactionsForAccount(String iban, int count) {
    log.info("Creating {} transactions for account with IBAN: {}", count, iban);

    // Start date is 4 weeks ago
    ZonedDateTime startDate = ZonedDateTime.now().minusDays(4 * 7);

    for (int i = 0; i < count; i++) {
      // Distribute transactions over 4 weeks
      long minutesToAdd = random.nextInt(4 * 7 * 24 * 60);
      ZonedDateTime transactionDate = startDate.plusMinutes(minutesToAdd);

      createTransaction(iban, transactionDate);
    }
  }


  private void createTransaction(String iban, ZonedDateTime date) {
    Entry entry = new Entry();
    entry.setId(UUID.randomUUID().toString());
    entry.setIban(iban);
    entry.setEntryDate(date);

    // Random amount between 1 and 1000
    BigDecimal amount = new BigDecimal(1 + random.nextInt(1000));
    entry.setAmount(amount);

    // Get the account to check if withdrawal is possible
    Account account = accountsService.getAccountByIban(iban);

    // Determine if this should be a deposit or withdrawal
    Entry.Type type;
    if (account instanceof SavingsAccount) {
      // For savings accounts, only allow withdrawals if there's enough balance
      if (account.getBalance().compareTo(amount) >= 0 && random.nextBoolean()) {
        type = Entry.Type.WITHDRAW;
      } else {
        type = Entry.Type.DEPOSIT;
      }
    } else if (account instanceof CurrentAccount) {
      // For current accounts, allow withdrawals if within balance + overdraft limit
      CurrentAccount currentAccount = (CurrentAccount) account;
      if (currentAccount.getBalance().add(currentAccount.getOverdraftLimit()).compareTo(amount) >= 0 
          && random.nextBoolean()) {
        type = Entry.Type.WITHDRAW;
      } else {
        type = Entry.Type.DEPOSIT;
      }
    } else {
      // Default to deposit for unknown account types
      type = Entry.Type.DEPOSIT;
    }

    entry.setType(type);

    // Random description
    String[] descriptions = {
        "Gehalt", "Miete", "Einkauf", "Versicherung", "Strom", "Internet",
        "Telefon", "Restaurant", "Kino", "Tanken", "Kleidung", "Geschenk"
    };
    entry.setDescription(descriptions[random.nextInt(descriptions.length)]);

    try {
      entriesService.makeEntry(iban, entry);
    } catch (InsufficientFundsException e) {
      // If withdrawal fails due to insufficient funds, make it a deposit instead
      entry.setType(Entry.Type.DEPOSIT);
      entriesService.makeEntry(iban, entry);
    }
  }
}
