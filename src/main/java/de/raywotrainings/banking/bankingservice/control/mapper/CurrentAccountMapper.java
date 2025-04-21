package de.raywotrainings.banking.bankingservice.control.mapper;

import de.raywotrainings.banking.bankingservice.control.account.CurrentAccount;
import de.raywotrainings.banking.bankingservice.entity.account.CurrentAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CurrentAccountMapper {

  private final ClientMapper clientMapper;


  public CurrentAccount map(CurrentAccountEntity currentAccountEntity) {
    if (currentAccountEntity == null) return null;

    return new CurrentAccount(
        currentAccountEntity.getIban(),
        clientMapper.map(currentAccountEntity.getOwner()),
        currentAccountEntity.getBalance(),
        currentAccountEntity.getOverdraftLimit(),
        currentAccountEntity.getOverdraftInterestRate()
    );
  }


  public CurrentAccountEntity map(CurrentAccount currentAccount) {
    if (currentAccount == null) return null;

    return new CurrentAccountEntity(
        currentAccount.getIban(),
        currentAccount.getBalance(),
        clientMapper.map(currentAccount.getOwner()),
        currentAccount.getOverdraftLimit(),
        currentAccount.getOverdraftInterestRate()
    );
  }

}
