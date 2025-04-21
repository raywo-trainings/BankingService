package de.raywotrainings.banking.bankingservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bank")
@Data
public class BankConfigurationData {

  private String countryCode = "";
  private String name = "";
  private String bic = "";

}
