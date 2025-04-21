package de.raywotrainings.banking.bankingservice.boundary.mapper;

import de.raywotrainings.banking.bankingservice.boundary.account.SavingsAccountDTO;
import de.raywotrainings.banking.bankingservice.control.account.SavingsAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class SavingsAccountDTOMapper {

  private final ClientDTOMapper clientDTOMapper;


  public SavingsAccountDTO map(SavingsAccount account) {
    if (account == null) {
      return null;
    }

    return new SavingsAccountDTO(
        account.getIban(),
        clientDTOMapper.map(account.getOwner()),
        account.getBalance(),
        account.getInterestRate()
    );
  }


  public SavingsAccount map(SavingsAccountDTO accountDTO) {
    if (accountDTO == null) {
      return null;
    }

    return new SavingsAccount(
        accountDTO.getIban(),
        clientDTOMapper.map(accountDTO.getOwner()),
        BigDecimal.ZERO,
        accountDTO.getInterestRate()
    );
  }

}
