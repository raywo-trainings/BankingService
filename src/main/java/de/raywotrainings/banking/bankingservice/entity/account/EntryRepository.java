package de.raywotrainings.banking.bankingservice.entity.account;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<EntryEntity, String> {

  List<EntryEntity> findByIban(@NotNull @Size(min = 22, max = 22) String iban);

  List<EntryEntity> findByIbanAndEntryDateBetween(@NotNull @Size(min = 22, max = 22) String iban,
                                                  @NotNull ZonedDateTime from,
                                                  @NotNull ZonedDateTime to);

  List<EntryEntity> findByIbanAndEntryDateAfter(@NotNull @Size(min = 22, max = 22) String iban,
                                                @NotNull ZonedDateTime from);

  List<EntryEntity> findByIbanAndEntryDateBefore(@NotNull @Size(min = 22, max = 22) String iban,
                                                 @NotNull ZonedDateTime to);

  void deleteByIban(@NotNull @Size(min = 22, max = 22) String iban);
}
