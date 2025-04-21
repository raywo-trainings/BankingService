package de.raywotrainings.banking.bankingservice.control.mapper;

import de.raywotrainings.banking.bankingservice.control.client.Client;
import de.raywotrainings.banking.bankingservice.entity.client.ClientEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

  public Client map(ClientEntity clientEntity) {
    if (clientEntity == null) return null;

    return new Client(
        clientEntity.getId(),
        clientEntity.getFirstname(),
        clientEntity.getLastname()
    );
  }


  public ClientEntity map(Client client) {
    if (client == null) return null;

    ClientEntity entity = new ClientEntity();
    entity.setId(client.getId());
    entity.setFirstname(client.getFirstname());
    entity.setLastname(client.getLastname());

    return entity;
  }

}
