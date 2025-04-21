package de.raywotrainings.banking.bankingservice.boundary.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Integer id;

  @NotBlank(message = "Firstname cannot be blank")
  @Size(min = 1, max = 100, message = "Firstname must be between 1 and 100 characters")
  private String firstname;

  @NotBlank(message = "Lastname cannot be blank")
  @Size(min = 1, max = 100, message = "Lastname must be between 1 and 100 characters")
  private String lastname;

}
