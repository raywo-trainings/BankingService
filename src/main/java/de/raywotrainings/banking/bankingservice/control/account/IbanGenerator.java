package de.raywotrainings.banking.bankingservice.control.account;

import de.raywotrainings.banking.bankingservice.configuration.BankConfigurationData;
import de.raywotrainings.banking.bankingservice.entity.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class IbanGenerator {

  private final AccountRepository repo;
  private final BankConfigurationData bankConfig;


  public String getNextIban() {
    final String bic = bankConfig.getBic();
    Set<String> existingIbans = repo.getAllIbans();

    return generateIban(bic, getNextAccountNumber(existingIbans));
  }


  private @NotNull String calculateChecksum(String bban) {
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


  private @NotNull String generateIban(String bic, long accountNumber) {
    String bban = bic + String.format("%010d", accountNumber);
    String checkDigits = calculateChecksum(bban);

    return bankConfig.getCountryCode() + checkDigits + bban;
  }


  private long getNextAccountNumber(@NotNull Set<String> existingIbans) {
    List<BigInteger> mappedIbans = existingIbans.stream()
        .map(a -> a.substring(12))
        .map(BigInteger::new)
        .toList();

    BigInteger randomAccountNo;

    do {
      randomAccountNo = getRandomAccountNumber();
    } while (mappedIbans.contains(randomAccountNo));

    return randomAccountNo.longValue();
  }


  private @NotNull BigInteger getRandomAccountNumber() {
    BigInteger minValue = BigInteger.ONE;
    BigInteger maxValue = new BigInteger("99999");
    BigInteger range = maxValue.subtract(minValue).add(BigInteger.ONE);
    Random random = new Random();

    return new BigInteger(range.bitLength(), random)
        .mod(range)
        .add(minValue);
  }

}
