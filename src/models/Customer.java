package models;

import structures.CustomList;

public class Customer {
  private String id;
  private String name;
  private String contact;
  private CustomList<SaleTransaction> salesHistory;

  public Customer(String id, String name, String contact, CustomList<SaleTransaction> salesHistory) {
    this.id = id;
    this.name = name;
    this.contact = contact;
    this.salesHistory = salesHistory;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getContact() {
    return contact;
  }

  public CustomList<SaleTransaction> getSalesHistory() {
    return salesHistory;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public void setSalesHistory(CustomList<SaleTransaction> salesHistory) {
    this.salesHistory = salesHistory;
  }

  @Override
  public String toString() {
    return "Customer ID: " + id + "\n"
        + "Name: " + name + "\n"
        + "Contact: " + contact + "\n"
        + "Number of Purchases: " + (salesHistory != null ? salesHistory.size() : 0) + "\n";
  }
}
