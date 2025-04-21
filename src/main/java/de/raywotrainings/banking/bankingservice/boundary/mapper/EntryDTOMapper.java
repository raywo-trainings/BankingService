package de.raywotrainings.banking.bankingservice.boundary.mapper;

import de.raywotrainings.banking.bankingservice.boundary.account.EntryDTO;
import de.raywotrainings.banking.bankingservice.control.account.Entry;
import org.springframework.stereotype.Component;

@Component
public class EntryDTOMapper {

  public EntryDTO map(Entry entry) {
    if (entry == null) {
      return null;
    }

    return new EntryDTO(
        entry.getId(),
        entry.getIban(),
        entry.getDescription(),
        entry.getEntryDate(),
        entry.getAmount(),
        mapType(entry.getType())
    );
  }


  public Entry map(EntryDTO entryDTO) {
    if (entryDTO == null) {
      return null;
    }

    return new Entry(
        entryDTO.getIban(),
        entryDTO.getDescription(),
        entryDTO.getEntryDate(),
        entryDTO.getAmount(),
        mapType(entryDTO.getEntryType())
    );
  }


  private String mapType(Entry.Type type) {
    return type.toString().toLowerCase();
  }


  private Entry.Type mapType(String type) {
    return Entry.Type.valueOf(type.toUpperCase());
  }

}
