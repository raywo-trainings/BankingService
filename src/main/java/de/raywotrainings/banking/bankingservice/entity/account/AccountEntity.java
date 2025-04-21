package de.raywotrainings.banking.bankingservice.entity.account;

import de.raywotrainings.banking.bankingservice.entity.client.ClientEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AccountEntity {

  @Id
  private String iban;

  private BigDecimal balance;

  @ManyToOne
  private ClientEntity owner;

}
