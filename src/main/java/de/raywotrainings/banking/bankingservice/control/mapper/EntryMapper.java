package de.raywotrainings.banking.bankingservice.control.mapper;

import de.raywotrainings.banking.bankingservice.control.account.Entry;
import de.raywotrainings.banking.bankingservice.entity.account.EntryEntity;
import org.springframework.stereotype.Component;

@Component
public class EntryMapper {

  public Entry map(EntryEntity entryEntity) {
    if (entryEntity == null) return null;

    return new Entry(
        entryEntity.getId(),
        entryEntity.getIban(),
        entryEntity.getDescription(),
        entryEntity.getEntryDate(),
        entryEntity.getAmount(),
        mapType(entryEntity.getType())
    );
  }


  public EntryEntity map(Entry entry) {
    if (entry == null) return null;

    return new EntryEntity(
        entry.getId(),
        entry.getIban(),
        entry.getDescription(),
        entry.getEntryDate(),
        entry.getAmount(),
        mapType(entry.getType())
    );
  }


  private Entry.Type mapType(EntryEntity.Type type) {
    switch (type) {
      case DEPOSIT -> {
        return Entry.Type.DEPOSIT;
      }
      case WITHDRAW -> {
        return Entry.Type.WITHDRAW;
      }
      default -> {
        return null;
      }
    }
  }


  private EntryEntity.Type mapType(Entry.Type type) {
    switch (type) {
      case DEPOSIT -> {
        return EntryEntity.Type.DEPOSIT;
      }
      case WITHDRAW -> {
        return EntryEntity.Type.WITHDRAW;
      }
      default -> {
        return null;
      }
    }
  }

}
