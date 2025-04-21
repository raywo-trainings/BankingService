package de.raywotrainings.banking.bankingservice.control.mapper;

import de.raywotrainings.banking.bankingservice.control.account.SavingsAccount;
import de.raywotrainings.banking.bankingservice.entity.account.SavingsAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SavingsAccountMapper {

  private final ClientMapper clientMapper;


  public SavingsAccount map(SavingsAccountEntity entity) {
    if (entity == null) return null;

    return new SavingsAccount(
        entity.getIban(),
        clientMapper.map(entity.getOwner()),
        entity.getBalance(),
        entity.getInterestRate()
    );
  }


  public SavingsAccountEntity map(SavingsAccount account) {
    if (account == null) return null;

    return new SavingsAccountEntity(
        account.getIban(),
        account.getBalance(),
        clientMapper.map(account.getOwner()),
        account.getInterestRate()
    );
  }

}
