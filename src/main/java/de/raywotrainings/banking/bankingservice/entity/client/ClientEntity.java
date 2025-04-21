package de.raywotrainings.banking.bankingservice.entity.client;

import de.raywotrainings.banking.bankingservice.entity.account.AccountEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ClientEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @NotBlank
  @Size(min = 1, max = 100)
  private String firstname;

  @NotBlank
  @Size(min = 1, max = 100)
  private String lastname;

  @OneToMany(mappedBy = "owner")
  private Set<AccountEntity> accounts;

}
