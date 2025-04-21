package de.raywotrainings.banking.bankingservice.boundary.mapper;

import de.raywotrainings.banking.bankingservice.boundary.account.AccountDTO;
import de.raywotrainings.banking.bankingservice.boundary.account.CurrentAccountDTO;
import de.raywotrainings.banking.bankingservice.boundary.account.SavingsAccountDTO;
import de.raywotrainings.banking.bankingservice.control.account.Account;
import de.raywotrainings.banking.bankingservice.control.account.CurrentAccount;
import de.raywotrainings.banking.bankingservice.control.account.SavingsAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountDTOMapper {

  private final CurrentAccountDTOMapper currentAccountDTOMapper;
  private final SavingsAccountDTOMapper savingsAccountDTOMapper;


  public AccountDTO map(Account account) {
    return switch (account) {
      case null -> null;
      case CurrentAccount currentAccount -> currentAccountDTOMapper.map(currentAccount);
      case SavingsAccount savingsAccount -> savingsAccountDTOMapper.map(savingsAccount);
      default -> throw new IllegalArgumentException("Unsupported account type: " + account.getClass());
    };

  }


  public Account map(AccountDTO accountDTO) {
    return switch (accountDTO) {
      case null -> null;
      case CurrentAccountDTO currentAccountDTO -> currentAccountDTOMapper.map(currentAccountDTO);
      case SavingsAccountDTO savingsAccountDTO -> savingsAccountDTOMapper.map(savingsAccountDTO);
      default -> throw new IllegalArgumentException("Unsupported account type: " + accountDTO.getClass());
    };

  }

}
