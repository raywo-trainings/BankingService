package de.raywotrainings.banking.bankingservice.control.client;

import de.raywotrainings.banking.bankingservice.control.mapper.AccountMapper;
import de.raywotrainings.banking.bankingservice.control.mapper.ClientMapper;
import de.raywotrainings.banking.bankingservice.control.shared.NotFoundException;
import de.raywotrainings.banking.bankingservice.entity.account.AccountEntity;
import de.raywotrainings.banking.bankingservice.entity.account.AccountRepository;
import de.raywotrainings.banking.bankingservice.entity.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ClientsService {

  private final ClientRepository repo;
  private final AccountRepository accountRepo;
  private final ClientMapper mapper;
  private final AccountMapper accountMapper;


  public Collection<Client> getAllClients() {
    return repo.findAll()
        .stream()
        .map(mapper::map)
        .toList();
  }


  public Client getClientById(int id) {
    validateClientExists(id);

    return repo.findById(id)
        .map(mapper::map)
        .get();
  }


  public Client addClient(Client client) {
    if (client.getId() != null) {
      throw new IllegalArgumentException("Beim Anlegen eines Kunden darf keine ID übergeben werden.");
    }

    return mapper.map(repo.save(mapper.map(client)));
  }


  public void deleteClientById(Integer id) {
    validateClientExists(id);
    validateNoAccountsExists(id);
    repo.deleteById(id);
  }


  public Client updateClientById(int id, Client client) {
    validateClientExists(id);

    client.setId(id);

    return mapper.map(repo.save(mapper.map(client)));
  }


  private void validateClientExists(Integer id) {
    if (!repo.existsById(id)) {
      throw new NotFoundException("Der Kunde mit der ID " + id
          + " wurde nicht gefunden.");
    }
  }


  private void validateNoAccountsExists(Integer id) {
    Client client = getClientById(id);
    Collection<AccountEntity> accounts = accountRepo.findAllByOwner(mapper.map(client));

    if (!accounts.isEmpty()) {
      throw new IllegalStateException("Der Kunde mit der ID " + id
          + " hat bereits Konten und kann deshalb nicht gelöscht werden.");
    }
  }

}
