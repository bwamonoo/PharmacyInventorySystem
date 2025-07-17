package models;

import structures.CustomList;

public class Drug {
  private String code;
  private String name;
  private CustomList<String> suppliersIds;
  private String expirationDate;
  private double price;
  private int stockLevel;

  public Drug(String code, String name, CustomList<String> suppliersIds, String expirationDate, double price,
      int stockLevel) {
    this.code = code;
    this.name = name;
    this.suppliersIds = suppliersIds;
    this.expirationDate = expirationDate;
    this.price = price;
    this.stockLevel = stockLevel;
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public CustomList<String> getSuppliersIds() {
    return suppliersIds;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public double getPrice() {
    return price;
  }

  public int getStockLevel() {
    return stockLevel;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setSuppliersIds(CustomList<String> suppliersIds) {
    this.suppliersIds = suppliersIds;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setStockLevel(int stockLevel) {
    this.stockLevel = stockLevel;
  }

  @Override
  public String toString() {
    return "Drug Code: " + code + "\n"
        + "Name: " + name + "\n"
        + "Suppliers: " + suppliersIds + "\n"
        + "Expiration Date: " + expirationDate + "\n"
        + "Price: GHS " + price + "\n"
        + "Stock Level: " + stockLevel + "\n";
  }
}
