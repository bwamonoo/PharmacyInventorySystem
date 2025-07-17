package models;

import java.util.Date;
import java.text.SimpleDateFormat;

public class PurchaseTransaction {
  private String drugCode;
  private int quantity;
  private Date dateTime;
  private String buyerID;
  private double totalCost;

  public PurchaseTransaction(String drugCode, int quantity, Date dateTime, String buyerID, double totalCost) {
    this.drugCode = drugCode;
    this.quantity = quantity;
    this.dateTime = dateTime;
    this.buyerID = buyerID;
    this.totalCost = totalCost;
  }

  public String getDrugCode() {
    return drugCode;
  }

  public int getQuantity() {
    return quantity;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public String getBuyerID() {
    return buyerID;
  }

  public double getTotalCost() {
    return totalCost;
  }

  public void setDrugCode(String drugCode) {
    this.drugCode = drugCode;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public void setBuyerID(String buyerID) {
    this.buyerID = buyerID;
  }

  public void setTotalCost(double totalCost) {
    this.totalCost = totalCost;
  }

  // For readable CLI output
  @Override
  public String toString() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return "Drug Code: " + drugCode + "\n"
        + "Quantity: " + quantity + "\n"
        + "Date & Time: " + formatter.format(dateTime) + "\n"
        + "Buyer ID: " + buyerID + "\n"
        + "Total Cost: GHS " + totalCost + "\n";
  }
}

// things to do: decide whether drugs should be stored by the code or references
// to their objects
