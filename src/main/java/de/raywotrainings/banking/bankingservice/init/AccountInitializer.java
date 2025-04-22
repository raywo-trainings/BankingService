package de.raywotrainings.banking.bankingservice.init;

import de.raywotrainings.banking.bankingservice.control.client.Client;

import java.util.List;

/**
 * Interface for data initializers that follow the Single Responsibility Principle.
 * Each implementation should handle a specific type of data initialization.
 */
public interface AccountInitializer<T> {

  /**
   * Initializes data according to the implementation's responsibility.
   */
  T initialize(int numberOfAccounts, List<Client> clients);

}
