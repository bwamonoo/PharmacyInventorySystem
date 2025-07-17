package models;

import java.util.Date;
import java.util.List;

public class SaleTransaction {
  private final String transactionId;
  private final String customerId;
  private final List<Sale> items;
  private final Date timestamp;
  public static final String GUEST_CUSTOMER_ID = "GUEST";

  public SaleTransaction(String customerId, List<Sale> items) {
    this.customerId = customerId;
    this.items = items;
    this.timestamp = new Date();
    this.transactionId = "TXN-" + this.timestamp.getTime();
  }

  public SaleTransaction(String transactionId, String customerId, List<Sale> items, Date timestamp) {
    this.transactionId = transactionId;
    this.customerId = customerId;
    this.items = items;
    this.timestamp = timestamp;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public List<Sale> getItems() {
    return items;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "SaleTransaction{" +
        "transactionId='" + transactionId + '\'' +
        ", customerId='" + customerId + '\'' +
        ", items=" + items +
        ", timestamp=" + timestamp +
        '}';
  }
}
