package cli;

import java.util.*;

import managers.*;
import models.*;
import structures.*;
import services.ReceiptService;
import util.Color;

public class SalesManagementCLI {
  private final Scanner sc;
  private final DrugManager drugManager;
  private final CustomerManager customerManager;
  private final SalesLogManager salesLogManager;
  private final StockMonitor stockMonitor;

  public SalesManagementCLI(Scanner sc, DrugManager drugManager, SupplierManager supplierManager,
      CustomerManager customerManager,
      SalesLogManager salesLogManager, StockMonitor stockMonitor) {
    this.sc = sc;
    this.drugManager = drugManager;
    this.customerManager = customerManager;
    this.salesLogManager = salesLogManager;
    this.stockMonitor = stockMonitor;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out
          .println(Color.CYAN + "║" + Color.BOLD + "  🛒 SALES MANAGEMENT MENU " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Record Customer Sale");
      System.out.println("2. View Recent Sales");
      System.out.println("3. View All Sales");
      System.out.println("4. Undo Last Sale");
      System.out.println("5. Back");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);
      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> recordCustomerSale();
        case 2 -> viewRecentSales();
        case 3 -> viewAllSales();
        case 4 -> undoLastSale();
        case 5 -> back = true;
        default -> System.out.println(Color.RED + "✗ " + "Invalid option." + Color.RESET);
      }
    }
  }

  private void recordCustomerSale() {
    String customerId = Input.readNonEmpty("Enter customer ID (or type GUEST for anonymous): ", sc).trim();
    boolean isGuest = customerId.equalsIgnoreCase("GUEST");

    Customer customer = customerManager.getCustomerById(customerId);
    if (!isGuest && customer == null) {
      System.out.println(Color.RED + "✗ " + "Customer not found." + Color.RESET);
      return;
    }

    List<Sale> cart = new ArrayList<>();
    boolean adding = true;

    while (adding) {
      String drugCode = Input.readNonEmpty("Enter drug code: ", sc);
      Drug drug = drugManager.getDrugByCode(drugCode);
      if (drug == null) {
        System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
        continue;
      }

      int qty = Input.readInt("Enter quantity: ", sc);
      if (qty <= 0 || qty > drug.getStockLevel()) {
        System.out.println(Color.YELLOW + "⚠ " + "Invalid quantity. Available: " + drug.getStockLevel() + Color.RESET);
        continue;
      }

      cart.add(new Sale(drugCode, qty, new Date(), customerId));
      int updatedStock = drug.getStockLevel() - qty;
      drugManager.updateDrugStock(drugCode, updatedStock);

      if (updatedStock <= stockMonitor.getThreshold()) {
        System.out.println(
            Color.YELLOW + "⚠ " + "LOW STOCK ALERT: " + drug.getName() + " now has " + updatedStock + " units."
                + Color.RESET);
        stockMonitor.addToLowStockMap(drug);
      } else {
        stockMonitor.removeFromLowStockMap(drugCode);
      }

      System.out.print(Color.BLUE + "▸ " + "Add another drug? (y/n): " + Color.RESET);
      if (!sc.nextLine().trim().equalsIgnoreCase("y"))
        adding = false;
    }

    if (cart.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No items added. Sale cancelled." + Color.RESET);
      return;
    }

    System.out.println("\n🧾 Sale Summary:");
    double total = 0;

    for (Sale s : cart) {
      Drug d = drugManager.getDrugByCode(s.getDrugCode());
      if (d == null)
        continue;

      double subtotal = s.getQuantity() * d.getPrice();
      total += subtotal;
      System.out.printf("| %s | Qty: %d | Unit Price: GHS %.2f | Subtotal: GHS %.2f%n",
          d.getName(), s.getQuantity(), d.getPrice(), subtotal);
    }

    System.out.printf("TOTAL: GHS", total);
    System.out.print(Color.GREEN + "✓ " + "Confirm sale? (y/n): ");

    if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
      System.out.println(Color.RED + "✗ " + "Sale cancelled." + Color.RESET);

      for (Sale s : cart) {
        Drug d = drugManager.getDrugByCode(s.getDrugCode());
        if (d != null) {
          int restoredStock = d.getStockLevel() + s.getQuantity();
          drugManager.updateDrugStock(d.getCode(), restoredStock);

          if (restoredStock <= stockMonitor.getThreshold()) {
            stockMonitor.addToLowStockMap(d);
          } else {
            stockMonitor.removeFromLowStockMap(d.getCode());
          }
        }
      }

      return;
    }

    SaleTransaction txn = new SaleTransaction(customerId, cart);
    salesLogManager.logSaleTransaction(txn);

    if (!isGuest) {
      customerManager.addTransactionToCustomer(customerId, txn);
    }

    ReceiptService.generateReceipt(txn, drugManager, customerManager);

    System.out.println(Color.GREEN + "✓ " + "Sale recorded successfully." + Color.RESET);
  }

  private void viewRecentSales() {
    int count = Input.readInt("How many recent sales to view? ", sc);
    CustomList<SaleTransaction> recent = salesLogManager.viewRecentTransactions(count);

    if (recent.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No recent sales found." + Color.RESET);
      return;
    }

    System.out.println(Color.CYAN + "\n🧾 Recent Sales:" + Color.RESET);
    for (SaleTransaction txn : recent) {
      displayTransaction(txn);
    }
  }

  private void viewAllSales() {
    CustomList<SaleTransaction> all = salesLogManager.getAllTransactions();
    if (all.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No sales recorded." + Color.RESET);
      return;
    }

    System.out.println(Color.CYAN + "\n📋 All Sales Transactions:" + Color.RESET);
    for (SaleTransaction txn : all) {
      displayTransaction(txn);
    }
  }

  private void displayTransaction(SaleTransaction txn) {
    System.out.println(Color.BLUE + "┌" + "─".repeat(58) + "┐" + Color.RESET);
    System.out.printf("│ %-20s: %-35s │\n", "🧾 Transaction ID", txn.getTransactionId());
    System.out.printf("│ %-20s: %-35s │\n", "👤 Customer ID", txn.getCustomerId());
    System.out.printf("│ %-20s: %-35s │\n", "📅 Date", txn.getTimestamp());
    System.out.println(Color.BLUE + "├" + "─".repeat(58) + "┤" + Color.RESET);
    double total = 0;

    for (Sale s : txn.getItems()) {
      Drug d = drugManager.getDrugByCode(s.getDrugCode());
      if (d == null)
        continue;

      double subtotal = d.getPrice() * s.getQuantity();
      total += subtotal;

      System.out.printf("| %s (%s) | Qty: %d | GHS %.2f | Subtotal: GHS %.2f%n",
          d.getName(), d.getCode(), s.getQuantity(), d.getPrice(), subtotal);
    }

    System.out.println(Color.BLUE + "├" + "─".repeat(58) + "┤" + Color.RESET);
    System.out.printf("│ %-42s %12.2f │\n", "TOTAL: GHS", total);
    System.out.println(Color.BLUE + "└" + "─".repeat(58) + "┘" + Color.RESET);
  }

  private void undoLastSale() {
    SaleTransaction txn = salesLogManager.peekLastTransaction();
    if (txn == null) {
      System.out.println(Color.YELLOW + "⚠ " + "No sale transaction to undo." + Color.RESET);
      return;
    }

    System.out.println("🧾 Last Sale Transaction:");
    System.out.println("- Transaction ID: " + txn.getTransactionId());
    System.out.println("- Customer ID: " + txn.getCustomerId());
    System.out.printf("- Total Items: %d%n", txn.getItems().size());
    System.out.print(Color.YELLOW + "⚠ " + "Are you sure you want to undo this sale? (y/n): " + Color.RESET);
    String confirm = sc.nextLine().trim().toLowerCase();
    if (!confirm.equals("y")) {
      System.out.println(Color.GREEN + "✓ " + "Undo cancelled." + Color.RESET);
      return;
    }

    SaleTransaction trxn = salesLogManager.undoLastTransaction();

    for (Sale s : trxn.getItems()) {
      Drug drug = drugManager.getDrugByCode(s.getDrugCode());
      if (drug != null) {
        drugManager.updateDrugStock(s.getDrugCode(), drug.getStockLevel() + s.getQuantity());
      }
    }

    Customer customer = customerManager.getCustomerById(txn.getCustomerId());
    CustomList<SaleTransaction> history = customer.getSalesHistory();

    if (customer != null && history != null) {
      Iterator<SaleTransaction> it = history.iterator();
      while (it.hasNext()) {
        if (it.next().getTransactionId().equals(txn.getTransactionId())) {
          it.remove();
          break;
        }
      }
    }

    System.out.println(Color.GREEN + "✓ " + "Undone sale transaction: " + trxn.getTransactionId() + Color.RESET);
  }

}
