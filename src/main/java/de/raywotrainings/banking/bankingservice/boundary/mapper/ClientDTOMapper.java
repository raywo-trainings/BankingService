package de.raywotrainings.banking.bankingservice.boundary.mapper;

import de.raywotrainings.banking.bankingservice.boundary.client.ClientDTO;
import de.raywotrainings.banking.bankingservice.control.client.Client;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ClientDTOMapper {

  public Client map(ClientDTO clientDTO) {
    if (clientDTO == null) {
      return null;
    }

    return new Client(
        clientDTO.getId(),
        clientDTO.getFirstname(),
        clientDTO.getLastname()
    );
  }


  public ClientDTO map(Client client) {
    if (client == null) {
      return null;
    }

    return new ClientDTO(
        client.getId(),
        client.getFirstname(),
        client.getLastname()
    );
  }

}
