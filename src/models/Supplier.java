package models;

import structures.CustomList;

public class Supplier {
  private String id;
  private String name;
  private String location;
  private int deliveryTurnaroundDays;
  private CustomList<String> drugCodesSupplied;

  public Supplier(String id, String name, String location, int deliveryTurnaroundDays,
      CustomList<String> drugCodesSupplied) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.deliveryTurnaroundDays = deliveryTurnaroundDays;
    this.drugCodesSupplied = drugCodesSupplied;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLocation() {
    return location;
  }

  public int getDeliveryTurnaroundDays() {
    return deliveryTurnaroundDays;
  }

  public CustomList<String> getDrugCodesSupplied() {
    return drugCodesSupplied;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setDeliveryTurnaroundDays(int deliveryTurnaroundDays) {
    this.deliveryTurnaroundDays = deliveryTurnaroundDays;
  }

  public void setDrugCodesSupplied(CustomList<String> drugCodesSupplied) {
    this.drugCodesSupplied = drugCodesSupplied;
  }

  @Override
  public String toString() {
    return "Supplier ID: " + id + "\n"
        + "Name: " + name + "\n"
        + "Location: " + location + "\n"
        + "Turnaround Time: " + deliveryTurnaroundDays + " days\n"
        + "Drugs Supplied: " + drugCodesSupplied + "\n";
  }
}