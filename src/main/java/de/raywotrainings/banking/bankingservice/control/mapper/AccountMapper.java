package de.raywotrainings.banking.bankingservice.control.mapper;

import de.raywotrainings.banking.bankingservice.control.account.Account;
import de.raywotrainings.banking.bankingservice.control.account.CurrentAccount;
import de.raywotrainings.banking.bankingservice.control.account.SavingsAccount;
import de.raywotrainings.banking.bankingservice.entity.account.AccountEntity;
import de.raywotrainings.banking.bankingservice.entity.account.CurrentAccountEntity;
import de.raywotrainings.banking.bankingservice.entity.account.SavingsAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class AccountMapper {

  private final CurrentAccountMapper currentAccountMapper;
  private final SavingsAccountMapper savingsAccountMapper;


  public Account map(AccountEntity accountEntity) {
    return switch (accountEntity) {
      case null -> null;
      case CurrentAccountEntity currentAccountEntity -> currentAccountMapper.map(currentAccountEntity);
      case SavingsAccountEntity savingsAccountEntity -> savingsAccountMapper.map(savingsAccountEntity);
      default -> throw new IllegalArgumentException("Unsupported account type: " + accountEntity.getClass());
    };

  }


  public AccountEntity map(Account account) {
    return switch (account) {
      case null -> null;
      case CurrentAccount currentAccount -> currentAccountMapper.map(currentAccount);
      case SavingsAccount savingsAccount -> savingsAccountMapper.map(savingsAccount);
      default -> throw new IllegalArgumentException("Unsupported account type: " + account.getClass());
    };

  }

}
