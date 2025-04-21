package de.raywotrainings.banking.bankingservice.control.account;

import de.raywotrainings.banking.bankingservice.configuration.BankConfigurationData;
import de.raywotrainings.banking.bankingservice.entity.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class IbanGenerator {

  private final AccountRepository repo;
  private final BankConfigurationData bankConfig;


  public String getNextIban() {
    final String bic = bankConfig.getBic();
    Set<String> ibans = repo.getAllIbans();

    if (ibans.isEmpty()) {
      return generateIban(bic, 1);
    }

    List<String> sortedIbans = ibans.stream()
        .sorted(Comparator.comparing(a -> a.substring(12)))
        .toList();

    String lastIban = sortedIbans.getLast();
    long lastNumber = Long.parseLong(lastIban.substring(12));

    return generateIban(bic, lastNumber + 1);
  }


  private String calculateChecksum(String bban) {
    String tmp = bban + bankConfig.getCountryCode() + "00";
    StringBuilder digits = new StringBuilder();

    for (char ch : tmp.toCharArray()) {
      if (Character.isLetter(ch)) {
        final int i = (ch - 'A') + 10;
        digits.append(i);
      } else {
        digits.append(ch);
      }
    }

    BigInteger bigInt = new BigInteger(digits.toString());
    BigInteger remainder = bigInt.mod(BigInteger.valueOf(97));
    int checksum = 98 - remainder.intValue();

    return String.format("%02d", checksum);
  }


  private String generateIban(String bic, long accountNumber) {
    String bban = bic + String.format("%010d", accountNumber);
    String checkDigits = calculateChecksum(bban);

    return bankConfig.getCountryCode() + checkDigits + bban;
  }


}
