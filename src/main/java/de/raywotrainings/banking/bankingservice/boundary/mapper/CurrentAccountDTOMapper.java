package de.raywotrainings.banking.bankingservice.boundary.mapper;

import de.raywotrainings.banking.bankingservice.boundary.account.CurrentAccountDTO;
import de.raywotrainings.banking.bankingservice.control.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class CurrentAccountDTOMapper {

  private final ClientDTOMapper clientDTOMapper;


  public CurrentAccountDTO map(CurrentAccount account) {
    if (account == null) {
      return null;
    }

    return new CurrentAccountDTO(
        account.getIban(),
        clientDTOMapper.map(account.getOwner()),
        account.getBalance(),
        account.getOverdraftLimit(),
        account.getOverdraftInterestRate()
    );
  }


  public CurrentAccount map(CurrentAccountDTO accountDTO) {
    if (accountDTO == null) {
      return null;
    }

    return new CurrentAccount(
        accountDTO.getIban(),
        clientDTOMapper.map(accountDTO.getOwner()),
        BigDecimal.ZERO,
        accountDTO.getOverdraftLimit(),
        accountDTO.getOverdraftInterestRate()
    );
  }

}
