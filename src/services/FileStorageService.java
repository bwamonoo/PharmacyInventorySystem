package services;

import models.*;
import structures.CustomList;
import structures.CustomArrayList;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileStorageService {
  private static final String DRUG_FILE = "../data/drugs.txt";
  private static final String SUPPLIER_FILE = "../data/suppliers.txt";
  private static final String CUSTOMER_FILE = "../data/customers.txt";
  private static final String PURCHASE_FILE = "../data/purchases.txt";
  private static final String SALES_FILE = "../data/sales.txt";
  private static final String SALE_TRANSACTIONS_FILE = "../data/sale_transactions.txt";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static void saveDrugs(CustomList<Drug> drugs) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(DRUG_FILE));
    for (Drug d : drugs) {
      StringBuilder suppliers = new StringBuilder();
      for (int i = 0; i < d.getSuppliersIds().size(); i++) {
        suppliers.append(d.getSuppliersIds().get(i));
        if (i < d.getSuppliersIds().size() - 1)
          suppliers.append(";");
      }

      writer.write(d.getCode() + "," + d.getName() + "," + suppliers + "," +
          d.getExpirationDate() + "," + d.getPrice() + "," + d.getStockLevel());
      writer.newLine();
    }
    writer.close();
  }

  public static CustomList<Drug> loadDrugs() throws IOException {
    CustomList<Drug> drugs = new CustomArrayList<>();
    File file = new File(DRUG_FILE);
    if (!file.exists())
      return drugs;

    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split(",", -1);
      String code = parts[0];
      String name = parts[1];

      CustomList<String> suppliersIds = new CustomArrayList<>();
      if (!parts[2].isEmpty()) {
        for (String id : parts[2].split(";")) {
          suppliersIds.add(id);
        }
      }

      String expiry = parts[3];
      double price = Double.parseDouble(parts[4]);
      int stock = Integer.parseInt(parts[5]);

      drugs.add(new Drug(code, name, suppliersIds, expiry, price, stock));
    }
    reader.close();
    return drugs;
  }

  public static void saveSuppliers(CustomList<Supplier> suppliers) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPLIER_FILE));
    for (Supplier s : suppliers) {
      String drugs = String.join(";", s.getDrugCodesSupplied());
      writer.write(s.getId() + "," + s.getName() + "," + s.getLocation() + "," +
          s.getDeliveryTurnaroundDays() + "," + drugs);
      writer.newLine();
    }
    writer.close();
  }

  public static CustomList<Supplier> loadSuppliers() throws IOException {
    CustomList<Supplier> suppliers = new CustomArrayList<>();
    File file = new File(SUPPLIER_FILE);
    if (!file.exists())
      return suppliers;

    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split(",", -1);
      String id = parts[0];
      String name = parts[1];
      String location = parts[2];
      int turnaround = Integer.parseInt(parts[3]);

      CustomList<String> drugCodes = new CustomArrayList<>();
      if (!parts[4].isEmpty()) {
        for (String code : parts[4].split(";")) {
          drugCodes.add(code);
        }
      }

      suppliers.add(new Supplier(id, name, location, turnaround, drugCodes));
    }
    reader.close();
    return suppliers;
  }

  public static void saveCustomers(CustomList<Customer> customers) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_FILE));
    for (Customer c : customers) {
      writer.write(c.getId() + "," + c.getName() + "," + c.getContact());
      writer.newLine();
    }
    writer.close();
  }

  public static CustomList<Customer> loadCustomers() throws IOException {
    CustomList<Customer> customers = new CustomArrayList<>();
    File file = new File(CUSTOMER_FILE);
    if (!file.exists())
      return customers;

    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split(",", -1);
      customers.add(new Customer(parts[0], parts[1], parts[2], new CustomArrayList<>()));
    }
    reader.close();
    return customers;
  }

  public static void savePurchaseHistory(CustomList<PurchaseTransaction> purchases) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_FILE));
    for (PurchaseTransaction p : purchases) {
      writer.write(p.getBuyerID() + "|" + p.getDrugCode() + "," + p.getQuantity() + "," +
          p.getDateTime().getTime() + "," + p.getTotalCost());
      writer.newLine();
    }
    writer.close();
  }

  public static CustomList<PurchaseTransaction> loadPurchaseHistory() throws IOException {
    CustomList<PurchaseTransaction> history = new CustomArrayList<>();
    File file = new File(PURCHASE_FILE);
    if (!file.exists())
      return history;

    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split("\\|");
      String buyerId = parts[0];
      String[] details = parts[1].split(",", -1);

      String drugCode = details[0];
      int qty = Integer.parseInt(details[1]);
      Date date = new Date(Long.parseLong(details[2]));
      double cost = Double.parseDouble(details[3]);

      history.add(new PurchaseTransaction(drugCode, qty, date, buyerId, cost));
    }
    reader.close();
    return history;
  }

  public static void saveSalesLog(CustomList<Sale> sales) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(SALES_FILE));
    for (Sale s : sales) {
      writer.write(s.getBuyerID() + "|" + s.getDrugCode() + "," + s.getQuantity() + "," + s.getTimeOfSale().getTime());
      writer.newLine();
    }
    writer.close();
  }

  public static void saveSaleTransactions(CustomList<SaleTransaction> transactions) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALE_TRANSACTIONS_FILE))) {
      for (SaleTransaction txn : transactions) {
        StringBuilder itemString = new StringBuilder();
        for (Sale sale : txn.getItems()) {
          itemString.append(sale.getDrugCode())
              .append(":")
              .append(sale.getQuantity())
              .append(";");
        }

        if (itemString.length() > 0)
          itemString.setLength(itemString.length() - 1); // remove trailing semicolon

        writer.write(txn.getTransactionId() + "|" +
            txn.getCustomerId() + "|" +
            DATE_FORMAT.format(txn.getTimestamp()) + "|" +
            itemString);
        writer.newLine();
      }
    }
  }

  public static CustomList<Sale> loadSalesLog() throws IOException {
    CustomList<Sale> sales = new CustomArrayList<>();
    File file = new File(SALES_FILE);
    if (!file.exists())
      return sales;

    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split("\\|");
      String buyerId = parts[0];
      String[] details = parts[1].split(",", -1);

      String code = details[0];
      int qty = Integer.parseInt(details[1]);
      Date date = new Date(Long.parseLong(details[2]));

      sales.add(new Sale(code, qty, date, buyerId));
    }
    reader.close();
    return sales;
  }

  public static CustomList<SaleTransaction> loadSaleTransactions() throws IOException {
    CustomList<SaleTransaction> transactions = new CustomArrayList<>();
    File file = new File(SALE_TRANSACTIONS_FILE);
    if (!file.exists())
      return transactions;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\\|");
        if (parts.length < 4)
          continue;

        String txnId = parts[0];
        String customerId = parts[1];
        Date timestamp;

        try {
          timestamp = DATE_FORMAT.parse(parts[2]);
        } catch (Exception e) {
          timestamp = new Date();
        }

        List<Sale> items = new ArrayList<>();
        String[] sales = parts[3].split(";");

        for (String entry : sales) {
          String[] pair = entry.split(":");
          if (pair.length == 2) {
            String drugCode = pair[0];
            int qty = Integer.parseInt(pair[1]);
            items.add(new Sale(drugCode, qty, timestamp, customerId));
          }
        }

        SaleTransaction txn = new SaleTransaction(txnId, customerId, items, timestamp);
        transactions.add(txn);
      }
    }

    return transactions;
  }
}