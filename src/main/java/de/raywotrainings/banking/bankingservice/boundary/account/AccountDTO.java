package de.raywotrainings.banking.bankingservice.boundary.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.raywotrainings.banking.bankingservice.boundary.client.ClientDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
abstract public class AccountDTO {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Size(min = 22, max = 22, message = "IBAN must be 22 characters long")
  private String iban;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BigDecimal balance;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private ClientDTO owner;


  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotNull(message = "Owner cannot be null")
  private Integer ownerId;

  @Pattern(regexp = "savings|current")
  private String type;


  public AccountDTO(String iban, ClientDTO owner, String type) {
    this(iban, owner, BigDecimal.ZERO, type);
  }


  public AccountDTO(String iban,
                    ClientDTO owner,
                    BigDecimal balance,
                    String type) {
    this.iban = iban;
    this.owner = owner;
    this.balance = balance;
    this.type = type;
  }


  @Override
  public String toString() {
    return "AccountDTO{" +
        "iban='" + iban + '\'' +
        ", balance=" + balance +
        ", owner=" + owner +
        ", ownerId=" + ownerId +
        '}';
  }
}
