package de.raywotrainings.banking.bankingservice.control.account;

import de.raywotrainings.banking.bankingservice.control.mapper.EntryMapper;
import de.raywotrainings.banking.bankingservice.entity.account.EntryEntity;
import de.raywotrainings.banking.bankingservice.entity.account.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EntriesService {

  private final EntryRepository repo;
  private final EntryMapper mapper;
  private final AccountsService accountsService;

  private static final String NONE = "NONE";
  private static final String TO_ONLY = "TO_ONLY";
  private static final String FROM_ONLY = "FROM_ONLY";
  private static final String BOTH = "BOTH";


  public List<Entry> getEntries(String iban,
                                ZonedDateTime from,
                                ZonedDateTime to) {
    accountsService.validateAccountExists(iban);

    List<EntryEntity> entries = switch (getCase(from, to)) {
      case NONE -> repo.findByIban(iban);
      case TO_ONLY -> repo.findByIbanAndEntryDateBefore(iban, to);
      case FROM_ONLY -> repo.findByIbanAndEntryDateAfter(iban, from);
      case BOTH -> repo.findByIbanAndEntryDateBetween(iban, from, to);
      default -> throw new IllegalStateException("Unexpected value: " + getCase(from, to));
    };

    return entries
        .stream()
        .map(mapper::map)
        .toList();
  }


  public Entry makeEntry(String iban, Entry entry) {
    accountsService.validateAccountExists(iban);

    entry.setIban(iban);
    accountsService.makeEntry(iban, entry);

    return mapper.map(repo.save(mapper.map(entry)));
  }


  private String getCase(ZonedDateTime from, ZonedDateTime to) {
    if (from == null && to == null) return NONE;
    if (from == null) return TO_ONLY;
    if (to == null) return FROM_ONLY;

    return BOTH;
  }


}
