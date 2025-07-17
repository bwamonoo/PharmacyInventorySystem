package services;

import managers.*;
import models.*;
import util.Color;
import structures.CustomList;
import structures.CustomMap;
import structures.CustomHashMap;
import structures.CustomArrayList;

import java.util.*;

import cli.Input;

public class ReportService {

  private final DrugManager drugManager;
  private final CustomerManager customerManager;
  private final PurchaseHistoryManager purchaseHistoryManager;
  private final SalesLogManager salesLogManager;

  public ReportService(DrugManager drugManager,
      CustomerManager customerManager,
      PurchaseHistoryManager purchaseHistoryManager,
      SalesLogManager salesLogManager) {
    this.drugManager = drugManager;
    this.customerManager = customerManager;
    this.purchaseHistoryManager = purchaseHistoryManager;
    this.salesLogManager = salesLogManager;
  }

  public void customerPurchaseReport() {
    Scanner sc = new Scanner(System.in);
    System.out.print("Enter customer ID: ");
    String id = sc.nextLine().trim();

    CustomList<SaleTransaction> history = customerManager.getCustomerSalesHistory(id);
    if (history == null || history.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No purchases found for this customer." + Color.RESET);
      return;
    }

    double total = 0;
    for (SaleTransaction txn : history) {
      System.out.println("=".repeat(40));
      System.out.println("Transaction ID: " + txn.getTransactionId());
      System.out.println("Date: " + txn.getTimestamp());
      for (Sale s : txn.getItems()) {
        Drug drug = drugManager.getDrugByCode(s.getDrugCode());
        if (drug != null) {
          double subtotal = s.getQuantity() * drug.getPrice();
          total += subtotal;
          System.out.printf("- %s | Qty: %d | Price: GHS %.2f | Subtotal: GHS %.2f%n",
              drug.getName(), s.getQuantity(), drug.getPrice(), subtotal);
        }
      }
    }
    System.out.println("=".repeat(40));
    System.out.printf("TOTAL PURCHASED: GHS %.2f%n", total);
  }

  public void drugsSoldOverPeriod(Scanner sc) {
    System.out.println(Color.CYAN + "\n📊 Drugs Sold Over a Period" + Color.RESET);

    Date start = Input.readDate("Enter start date (yyyy-MM-dd): ", sc);
    Date end = Input.readDate("Enter end date (yyyy-MM-dd): ", sc);

    CustomList<SaleTransaction> periodSales = salesLogManager.getSalesWithinPeriod(start, end);
    if (periodSales.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No sales found in this period." + Color.RESET);
      return;
    }

    CustomMap<String, Integer> drugSalesMap = new CustomHashMap<>();

    for (SaleTransaction txn : periodSales) {
      for (Sale s : txn.getItems()) {
        int currentCount = drugSalesMap.getOrDefault(s.getDrugCode(), 0);
        drugSalesMap.put(s.getDrugCode(), currentCount + s.getQuantity());
      }
    }

    if (drugSalesMap.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No drug sales in this period." + Color.RESET);
      return;
    }

    CustomList<Map.Entry<String, Integer>> sortedEntries = new CustomArrayList<>();
    for (String drugCode : drugSalesMap.keySet()) {
      sortedEntries.add(new AbstractMap.SimpleEntry<>(drugCode, drugSalesMap.get(drugCode)));
    }

    sortedEntries.sort((a, b) -> b.getValue() - a.getValue());

    System.out.println("🧾 Drugs sold from " + start + " to " + end + ":");
    for (Map.Entry<String, Integer> entry : sortedEntries) {
      Drug d = drugManager.getDrugByCode(entry.getKey());
      String name = (d != null ? d.getName() : entry.getKey());
      System.out.println("• " + name + ": " + entry.getValue() + " units");
    }
  }

  public void totalSalesValueReport() {
    CustomList<PurchaseTransaction> purchases = purchaseHistoryManager.getAllPurchases();
    double total = 0;
    for (PurchaseTransaction p : purchases) {
      total += p.getTotalCost();
    }
    System.out.printf("💰 Total Pharmacy Purchase Cost: GHS %.2f%n", total);
  }

  public void topSellingDrugsReport() {
    CustomMap<String, Integer> salesMap = new CustomHashMap<>();

    CustomList<SaleTransaction> allSales = salesLogManager.getAllTransactions();
    for (SaleTransaction txn : allSales) {
      for (Sale s : txn.getItems()) {
        int current = salesMap.getOrDefault(s.getDrugCode(), 0);
        salesMap.put(s.getDrugCode(), current + s.getQuantity());
      }
    }

    if (salesMap.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No sales data available." + Color.RESET);
      return;
    }

    // Manual sorting and top 5
    CustomList<Map.Entry<String, Integer>> entries = new CustomArrayList<>();
    for (String drugCode : salesMap.keySet()) {
      entries.add(new AbstractMap.SimpleEntry<>(drugCode, salesMap.get(drugCode)));
    }

    // Sort descending
    entries.sort((a, b) -> b.getValue() - a.getValue());

    System.out.println("🏆 Top Selling Drugs:");
    int count = Math.min(5, entries.size());
    for (int i = 0; i < count; i++) {
      Map.Entry<String, Integer> entry = entries.get(i);
      Drug d = drugManager.getDrugByCode(entry.getKey());
      String name = d != null ? d.getName() : "Unknown Drug";
      System.out.printf("- %s (%s): %d units sold%n", name, entry.getKey(), entry.getValue());
    }
  }
}
