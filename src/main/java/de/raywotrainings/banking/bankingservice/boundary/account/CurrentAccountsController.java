package de.raywotrainings.banking.bankingservice.boundary.account;

import de.raywotrainings.banking.bankingservice.boundary.mapper.AccountDTOMapper;
import de.raywotrainings.banking.bankingservice.boundary.mapper.CurrentAccountDTOMapper;
import de.raywotrainings.banking.bankingservice.control.account.AccountsService;
import de.raywotrainings.banking.bankingservice.control.account.CurrentAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/current-accounts")
public class CurrentAccountsController {

  private final AccountDTOMapper mapper;
  private final CurrentAccountDTOMapper currentAccountMapper;
  private final AccountsService accountsService;


  @GetMapping
  public Collection<CurrentAccountDTO> getAllAccounts() {
    return accountsService.getCurrentAccounts()
        .stream()
        .map(currentAccountMapper::map)
        .toList();
  }


  @GetMapping("/{iban}")
  public CurrentAccountDTO getAccountByIban(@PathVariable String iban) {
    return currentAccountMapper.map(accountsService.getCurrentAccountByIban(iban));
  }


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CurrentAccountDTO addAccount(@Valid @RequestBody CurrentAccountDTO account) {
    final CurrentAccount currentAccount = currentAccountMapper.map(account);
    return (CurrentAccountDTO) mapper.map(
        accountsService.addCurrentAccount(currentAccount, account.getOwnerId())
    );

  }


  @PutMapping("/{iban}")
  public CurrentAccountDTO updateAccountByIban(@PathVariable String iban,
                                               @Valid @RequestBody CurrentAccountDTO account) {
    return currentAccountMapper.map((CurrentAccount) accountsService.updateAccount(
            iban,
            currentAccountMapper.map(account),
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
