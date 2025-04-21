package de.raywotrainings.banking.bankingservice.boundary.account;

import de.raywotrainings.banking.bankingservice.boundary.mapper.AccountDTOMapper;
import de.raywotrainings.banking.bankingservice.boundary.mapper.EntryDTOMapper;
import de.raywotrainings.banking.bankingservice.boundary.shared.WrongBookingTypeException;
import de.raywotrainings.banking.bankingservice.control.account.AccountsService;
import de.raywotrainings.banking.bankingservice.control.account.EntriesService;
import de.raywotrainings.banking.bankingservice.control.account.Entry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/accounts")
public class AccountsController {

  private final AccountsService accountsService;
  private final EntriesService entriesService;
  private final AccountDTOMapper accountMapper;
  private final EntryDTOMapper mapper;


  @GetMapping
  public Collection<AccountDTO> getAllAccounts(@RequestParam(required = false) Integer ownerId) {
    return accountsService.getAllAccounts(ownerId)
        .stream()
        .map(accountMapper::map)
        .toList();
  }


  @GetMapping("/{iban}")
  public AccountDTO getAccountByIban(@PathVariable String iban) {
    return accountMapper.map(accountsService.getAccountByIban(iban));
  }


  @DeleteMapping("/{iban}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccountByIban(@PathVariable String iban) {
    accountsService.deleteAccountByIban(iban);
  }


  @GetMapping("/{iban}/entries")
  public Collection<EntryDTO> getEntriesService(@PathVariable String iban,
                                                @RequestParam(required = false) ZonedDateTime from,
                                                @RequestParam(required = false) ZonedDateTime to) {
    return entriesService.getEntries(iban, from, to)
        .stream()
        .sorted(Comparator.comparing(Entry::getEntryDate))
        .map(mapper::map)
        .toList();
  }


  @PostMapping("/{iban}/deposits")
  public EntryDTO deposit(@PathVariable String iban,
                          @Valid @RequestBody EntryDTO entryDTO) {
    if (!entryDTO.getEntryType().equalsIgnoreCase("deposit")) {
      throw new WrongBookingTypeException("Der Typ der Buchung muss \"deposit\" sein");
    }

    return mapper.map(
        entriesService.makeEntry(iban, mapper.map(entryDTO))
    );
  }


  @PostMapping("/{iban}/withdrawals")
  public EntryDTO withdraw(@PathVariable String iban,
                           @Valid @RequestBody EntryDTO entryDTO) {
    if (!entryDTO.getEntryType().equalsIgnoreCase("withdraw")) {
      throw new WrongBookingTypeException("Der Typ der Buchung muss \"withdraw\" sein");
    }

    return mapper.map(
        entriesService.makeEntry(iban, mapper.map(entryDTO))
    );
  }

}
