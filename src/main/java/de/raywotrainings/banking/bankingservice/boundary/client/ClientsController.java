package de.raywotrainings.banking.bankingservice.boundary.client;


import de.raywotrainings.banking.bankingservice.boundary.mapper.ClientDTOMapper;
import de.raywotrainings.banking.bankingservice.control.client.Client;
import de.raywotrainings.banking.bankingservice.control.client.ClientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/clients")
public class ClientsController {

  private final ClientsService clientsService;
  private final ClientDTOMapper mapper;


  @GetMapping
  public Collection<ClientDTO> getAllClients() {
    return clientsService.getAllClients()
        .stream()
        .map(mapper::map)
        .toList();
  }


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClientDTO addClient(@RequestBody ClientDTO client) {
    Client newClient = clientsService.addClient(mapper.map(client));

    return mapper.map(newClient);
  }


  @GetMapping("/{id}")
  public ClientDTO getClientById(@PathVariable int id) {
    return mapper.map(clientsService.getClientById(id));
  }


  @PutMapping("/{id}")
  public ClientDTO updateClientById(@PathVariable int id,
                                    @RequestBody ClientDTO client) {
    return mapper.map(clientsService.updateClientById(id, mapper.map(client)));
  }


  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteClientById(@PathVariable int id) {
    clientsService.deleteClientById(id);
  }

}
