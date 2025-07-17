package models;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Sale {
  private String drugCode;
  private int quantity;
  private Date timeOfSale;
  private String buyerID;

  public Sale(String drugCode, int quantity, Date timeOfSale, String buyerID) {
    this.drugCode = drugCode;
    this.quantity = quantity;
    this.timeOfSale = timeOfSale;
    this.buyerID = buyerID;
  }

  public String getDrugCode() {
    return drugCode;
  }

  public int getQuantity() {
    return quantity;
  }

  public Date getTimeOfSale() {
    return timeOfSale;
  }

  public String getBuyerID() {
    return buyerID;
  }

  public void setDrugCode(String drugCode) {
    this.drugCode = drugCode;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setTimeOfSale(Date timeOfSale) {
    this.timeOfSale = timeOfSale;
  }

  public void setBuyerID(String buyerID) {
    this.buyerID = buyerID;
  }

  @Override
  public String toString() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return "Drug Code: " + drugCode + "\n"
        + "Quantity Sold: " + quantity + "\n"
        + "Time of Sale: " + formatter.format(timeOfSale) + "\n"
        + "Buyer ID: " + buyerID + "\n";
  }
}

// things to do: decide whether drugs should be stored by the code or references
// to their objects
