package de.raywotrainings.banking.bankingservice.control.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {

  private Integer id;
  private String firstname;
  private String lastname;


  public Client(Integer id, String firstname, String lastname) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
  }


  public String getFullName() {
    return lastname + ", " + firstname;
  }


  public int hashCode() {
    return id;
  }


  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    Client client = (Client) o;
    return id.equals(client.id);
  }


  public String toString() {
    return "Kundennummer: " + id + ", Vorname: " + firstname + ", Nachname: " + lastname;
  }

}
