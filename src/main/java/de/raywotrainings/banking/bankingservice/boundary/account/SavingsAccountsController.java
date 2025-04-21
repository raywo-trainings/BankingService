package de.raywotrainings.banking.bankingservice.boundary.account;

import de.raywotrainings.banking.bankingservice.boundary.mapper.AccountDTOMapper;
import de.raywotrainings.banking.bankingservice.boundary.mapper.SavingsAccountDTOMapper;
import de.raywotrainings.banking.bankingservice.control.account.Account;
import de.raywotrainings.banking.bankingservice.control.account.AccountsService;
import de.raywotrainings.banking.bankingservice.control.account.SavingsAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/savings-accounts")
public class SavingsAccountsController {

  private final AccountsService accountsService;
  private final SavingsAccountDTOMapper savingsAccountDTOMapper;
  private final AccountDTOMapper mapper;


  @GetMapping
  public Collection<SavingsAccountDTO> getAllAccounts() {
    return accountsService.getSavingsAccounts()
        .stream()
        .map(savingsAccountDTOMapper::map)
        .toList();
  }


  @GetMapping("/{iban}")
  public SavingsAccountDTO getAccountByIban(@PathVariable String iban) {
    return savingsAccountDTOMapper.map(
        accountsService.getSavingsAccountByIban(iban)
    );
  }


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SavingsAccountDTO addAccount(@Valid @RequestBody SavingsAccountDTO account) {
    final Account newAccount = accountsService.addSavingsAccount(
        savingsAccountDTOMapper.map(account),
        account.getOwnerId()
    );
    return (SavingsAccountDTO) mapper.map(newAccount);
  }


  @PutMapping("/{iban}")
  public SavingsAccountDTO updateAccountByIban(@PathVariable String iban,
                                               @Valid @RequestBody SavingsAccountDTO account) {
    return savingsAccountDTOMapper.map(
        (SavingsAccount) accountsService.updateAccount(
            iban,
            savingsAccountDTOMapper.map(account),
            account.getOwnerId()
        )
    );
  }


  @DeleteMapping("/{iban}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccountByIban(@PathVariable String iban) {
    accountsService.deleteAccountByIban(iban);
  }

}
