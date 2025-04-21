package de.raywotrainings.banking.bankingservice.entity.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccountEntity, String> {
}
