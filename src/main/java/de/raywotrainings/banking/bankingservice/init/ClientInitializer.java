package de.raywotrainings.banking.bankingservice.init;

import com.github.javafaker.Faker;
import de.raywotrainings.banking.bankingservice.control.client.Client;
import de.raywotrainings.banking.bankingservice.control.client.ClientsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Initializer responsible for creating client data.
 * Follows the Single Responsibility Principle by focusing only on client creation.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ClientInitializer {

  private static final int NUMBER_OF_CLIENTS = 10;

  private final ClientsService clientsService;

  private static final ThreadLocal<Faker> threadLocalFaker =
      ThreadLocal.withInitial(() ->
          new Faker(Locale.GERMANY, new Random(42)));


  /**
   * Gets the thread-local Faker instance for generating consistent test data.
   */
  private Faker getFaker() {
    return threadLocalFaker.get();
  }


  public List<Client> initialize() {
    log.info("Initializing clients");
    return createClients();
  }


  /**
   * Creates a specified number of clients with realistic German names.
   *
   * @return List of created clients
   */
  public List<Client> createClients() {
    List<Client> clients = new ArrayList<>();

    for (int i = 1; i <= NUMBER_OF_CLIENTS; i++) {
      String firstName = getFaker().name().firstName();
      String lastName = getFaker().name().lastName();
      Client client = new Client(null, firstName, lastName);

      clients.add(clientsService.addClient(client));
      log.info("Created client: {} {}", client.getFirstname(), client.getLastname());
    }

    return clients;
  }
}
