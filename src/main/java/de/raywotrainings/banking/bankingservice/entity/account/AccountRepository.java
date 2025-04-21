package de.raywotrainings.banking.bankingservice.entity.account;

import de.raywotrainings.banking.bankingservice.entity.client.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {

  Collection<AccountEntity> findAllByOwner(ClientEntity client);

  Collection<AccountEntity> findAllByOwnerId(Integer clientId);

  @Query("SELECT iban FROM AccountEntity")
  Set<String> getAllIbans();

}
