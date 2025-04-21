package de.raywotrainings.banking.bankingservice.boundary.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EntryDTO {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String iban;

  @Size(min = 1, max = 255)
  private String description;

  @NotNull
  @PastOrPresent
  private ZonedDateTime entryDate;

  @NotNull
  @Min(0)
  private BigDecimal amount;

  @NotNull
  @Pattern(regexp = "deposit|withdraw")
  private String entryType;

}
