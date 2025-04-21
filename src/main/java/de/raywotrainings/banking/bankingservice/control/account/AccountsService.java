package de.raywotrainings.banking.bankingservice.control.account;

import de.raywotrainings.banking.bankingservice.control.client.ClientsService;
import de.raywotrainings.banking.bankingservice.control.mapper.AccountMapper;
import de.raywotrainings.banking.bankingservice.control.mapper.CurrentAccountMapper;
import de.raywotrainings.banking.bankingservice.control.mapper.SavingsAccountMapper;
import de.raywotrainings.banking.bankingservice.control.shared.ClientDoesntExistException;
import de.raywotrainings.banking.bankingservice.control.shared.NotFoundException;
import de.raywotrainings.banking.bankingservice.entity.account.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class AccountsService {

  private final ClientsService clientsService;
  private final AccountRepository accountRepo;
  private final SavingsAccountRepository savingsAccountRepo;
  private final CurrentAccountRepository currentAccountRepo;
  private final EntryRepository entryRepo;
  private final AccountMapper accountMapper;
  private final SavingsAccountMapper savingsAccountMapper;
  private final CurrentAccountMapper currentAccountMapper;
  private final IbanGenerator ibanGenerator;


  public Collection<Account> getAllAccounts(Integer ownerId) {
    return (ownerId != null ? accountRepo.findAllByOwnerId(ownerId) : accountRepo.findAll())
        .stream()
        .map(accountMapper::map)
        .toList();
  }


  public Collection<CurrentAccount> getCurrentAccounts() {
    return currentAccountRepo.findAll()
        .stream()
        .map(currentAccountMapper::map)
        .toList();
  }


  public Collection<SavingsAccount> getSavingsAccounts() {
    return savingsAccountRepo.findAll()
        .stream()
        .map(savingsAccountMapper::map)
        .toList();
  }


  public Account getAccountByIban(String iban) {
    validateAccountExists(iban);

    return accountRepo.findById(iban)
        .map(accountMapper::map)
        .get();
  }


  public CurrentAccount getCurrentAccountByIban(String iban) {
    validateAccountExists(iban);

    return currentAccountRepo.findById(iban)
        .map(currentAccountMapper::map)
        .get();
  }


  public SavingsAccount getSavingsAccountByIban(String iban) {
    validateAccountExists(iban);

    return savingsAccountRepo.findById(iban)
        .map(savingsAccountMapper::map)
        .get();
  }


  public CurrentAccount addCurrentAccount(CurrentAccount account, Integer ownerId) {
    final String iban = ibanGenerator.getNextIban();

    try {
      account.setIban(iban);
      account.setOwner(clientsService.getClientById(ownerId));
      CurrentAccountEntity newAccount = currentAccountRepo.save(currentAccountMapper.map(account));

      return currentAccountMapper.map(newAccount);
    } catch (NotFoundException e) {
      throw new ClientDoesntExistException("Der Kunde mit der Kundennummer "
          + ownerId + " existiert nicht.");
    }
  }


  public SavingsAccount addSavingsAccount(SavingsAccount account, Integer ownerId) {
    final String iban = ibanGenerator.getNextIban();

    try {
      account.setIban(iban);
      account.setOwner(clientsService.getClientById(ownerId));
      SavingsAccountEntity newAccount = savingsAccountRepo.save(savingsAccountMapper.map(account));

      return savingsAccountMapper.map(newAccount);
    } catch (NotFoundException e) {
      throw new ClientDoesntExistException("Der Kunde mit der Kundennummer "
          + ownerId + " existiert nicht.");
    }
  }


  public Account updateAccount(String iban, Account account, Integer ownerId) {
    validateAccountExists(iban);

    if (account instanceof CurrentAccount) {
      return updateCurrentAccountByIban(iban, (CurrentAccount) account, ownerId);
    }

    if (account instanceof SavingsAccount) {
      return updateSavingsAccountByIban(iban, (SavingsAccount) account, ownerId);
    }

    throw new IllegalArgumentException("Unbekannter Account-Typ.");
  }


  @Transactional
  public void deleteAccountByIban(String iban) {
    validateAccountExists(iban);
    validateAccountCanBeDeleted(getAccountByIban(iban));
    entryRepo.deleteByIban(iban);
    accountRepo.deleteById(iban);
  }


  public Account makeEntry(String iban, Entry entry) {
    validateAccountExists(iban);

    Account account = getAccountByIban(iban);

    if (entry.getType() == Entry.Type.DEPOSIT) {
      account.deposit(entry.getAmount());
    }

    if (entry.getType() == Entry.Type.WITHDRAW) {
      account.withdraw(entry.getAmount());
    }

    updateAccount(iban, account, account.getOwner().getId());

    final AccountEntity accountEntity = accountMapper.map(account);
    final AccountEntity updatedAccount = accountRepo.save(accountEntity);

    return accountMapper.map(updatedAccount);
  }


  public void validateAccountExists(String iban) {
    if (!accountRepo.existsById(iban)) {
      throw new NotFoundException("Das Konto " + iban
          + " wurde nicht gefunden.");
    }
  }


  private CurrentAccount updateCurrentAccountByIban(String iban,
                                                    CurrentAccount account,
                                                    Integer ownerId) {
    validateAccountExists(iban);

    try {
      CurrentAccount existingAccount = currentAccountRepo.findById(iban)
          .map(currentAccountMapper::map)
          .get();
      existingAccount.setOwner(clientsService.getClientById(ownerId));
      existingAccount.setOverdraftLimit(account.getOverdraftLimit());
      existingAccount.setOverdraftInterestRate(account.getOverdraftInterestRate());

      CurrentAccountEntity newAccount = currentAccountRepo.save(currentAccountMapper.map(existingAccount));

      return currentAccountMapper.map(newAccount);
    } catch (NotFoundException e) {
      throw new ClientDoesntExistException("Der Kunde mit der Kundennummer "
          + ownerId + " existiert nicht.");
    }
  }


  private SavingsAccount updateSavingsAccountByIban(String iban,
                                                    SavingsAccount account,
                                                    Integer ownerId) {
    validateAccountExists(iban);

    try {
      SavingsAccount existingAccount = savingsAccountRepo.findById(iban)
          .map(savingsAccountMapper::map)
          .get();
      existingAccount.setOwner(clientsService.getClientById(ownerId));
      existingAccount.setInterestRate(account.getInterestRate());

      SavingsAccountEntity newAccount = savingsAccountRepo.save(savingsAccountMapper.map(existingAccount));

      return savingsAccountMapper.map(newAccount);
    } catch (NotFoundException e) {
      throw new ClientDoesntExistException("Der Kunde mit der Kundennummer "
          + ownerId + " existiert nicht.");
    }
  }


  private void validateAccountCanBeDeleted(Account account) {
    if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
      throw new IllegalStateException("Das Konto ist noch nicht auf Null " +
          "ausgeglichen und kann deshalb nicht gelöscht werden.");
    }
  }

}
