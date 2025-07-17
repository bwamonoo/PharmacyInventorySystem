package cli;

import managers.*;
import models.*;
import structures.*;
import services.ReceiptService;
import util.Color;

import java.util.*;

public class InventoryCLI {
  private final Scanner sc;
  private final DrugManager drugManager;
  private final SupplierManager supplierManager;
  private final CustomerManager customerManager;
  private final PurchaseHistoryManager purchaseHistoryManager;
  private final SalesLogManager salesLogManager;
  private final StockMonitor stockMonitor;

  public InventoryCLI(Scanner sc, DrugManager drugManager, SupplierManager supplierManager,
      CustomerManager customerManager, PurchaseHistoryManager purchaseHistoryManager,
      SalesLogManager salesLogManager, StockMonitor stockMonitor) {
    this.sc = sc;
    this.drugManager = drugManager;
    this.supplierManager = supplierManager;
    this.customerManager = customerManager;
    this.purchaseHistoryManager = purchaseHistoryManager;
    this.salesLogManager = salesLogManager;
    this.stockMonitor = stockMonitor;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out.println(Color.CYAN + "║" + Color.BOLD + "  INVENTORY MENU  " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Record Pharmacy Purchase");
      System.out.println("2. Record Customer Sale");
      System.out.println("3. View Low Stock Drugs");
      System.out.println("4. Undo Last Purchase");
      System.out.println("5. Undo Last Sale");
      System.out.println("6. Back");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);
      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> recordPharmacyPurchase();
        case 2 -> recordCustomerSale();
        case 3 -> viewLowStock();
        case 4 -> undoLastPurchase();
        case 5 -> undoLastSale();
        case 6 -> back = true;
        default -> System.out.println(Color.RED + "✗ " + "Invalid option." + Color.RESET);
      }
    }
  }

  private void recordPharmacyPurchase() {
    String drugCode = Input.readNonEmpty("Enter drug code: ", sc);
    Drug drug = drugManager.getDrugByCode(drugCode);
    if (drug == null) {
      System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
      return;
    }

    String supplierId = Input.readNonEmpty("Enter supplier ID: ", sc);
    if (supplierManager.getSupplierById(supplierId) == null) {
      System.out.println(Color.RED + "✗ " + "Supplier not found." + Color.RESET);
      return;
    }

    int quantity = Input.readInt("Enter quantity: ", sc);
    double unitCost = Input.readDouble("Enter unit cost (GHS): ", sc);

    if (quantity <= 0 || unitCost < 0) {
      System.out.println(Color.RED + "✗ " + "Invalid quantity or cost." + Color.RESET);
      return;
    }

    double totalCost = quantity * unitCost;
    drugManager.updateDrugStock(drugCode, drug.getStockLevel() + quantity);

    PurchaseTransaction tx = new PurchaseTransaction(drugCode, quantity, new Date(), "Pharmacy001", totalCost);
    purchaseHistoryManager.logPurchase(tx);

    System.out.println(Color.GREEN + "✓ " + "Purchase recorded: " + tx + Color.RESET);
  }

  private void undoLastPurchase() {
    PurchaseTransaction undone = purchaseHistoryManager.undoLastPurchase();
    if (undone == null) {
      System.out.println(Color.YELLOW + "⚠ " + "No purchase to undo." + Color.RESET);
      return;
    }

    Drug drug = drugManager.getDrugByCode(undone.getDrugCode());
    if (drug != null) {
      drugManager.updateDrugStock(drug.getCode(), drug.getStockLevel() - undone.getQuantity());
    }

    System.out.println(Color.GREEN + "✓ " + "Undone purchase: " + undone + Color.RESET);
  }

  private void viewLowStock() {
    CustomList<Drug> lowStock = stockMonitor.getLowStockDrugs();
    if (lowStock.isEmpty()) {
      System.out.println(Color.GREEN + "✓ " + "All drugs are sufficiently stocked." + Color.RESET);
      return;
    }

    System.out.println(Color.YELLOW + "⚠ " + "LOW STOCK DRUGS:" + Color.RESET);
    for (Drug d : lowStock) {
      System.out.printf("- %s (%s): %d units%n", d.getName(), d.getCode(), d.getStockLevel());
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
            Color.YELLOW + "⚠ " + "LOW STOCK ALERT: " + drug.getName() + " now has " + updatedStock + " units.");
        stockMonitor.addToLowStockMap(drug);
      } else {
        stockMonitor.removeFromLowStockMap(drugCode);
      }

      System.out.print(Color.BLUE + "▸ " + "Add another drug? (y/n): " + Color.RESET);
      if (!sc.nextLine().trim().equalsIgnoreCase("y"))
        adding = false;
    }

    if (cart.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No items added. Sale cancelled.");
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
      System.out.printf("- %s | Qty: %d | Unit Price: GHS %.2f | Subtotal: GHS %.2f%n",
          d.getName(), s.getQuantity(), d.getPrice(), subtotal);
    }

    System.out.printf("TOTAL: GHS %.2f%n", total);
    System.out.print(Color.BLUE + "▸ " + "Confirm sale? (y/n): " + Color.RESET);

    if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
      System.out.println(Color.RED + "✗ " + "Sale cancelled.");

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

  private void undoLastSale() {
    SaleTransaction txn = salesLogManager.undoLastTransaction();
    if (txn == null) {
      System.out.println(Color.YELLOW + "⚠ " + "No sale transaction to undo." + Color.RESET);
      return;
    }

    for (Sale s : txn.getItems()) {
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

    System.out.println(Color.GREEN + "✓ " +
        "Undone sale transaction: " + txn.getTransactionId() + Color.RESET);
  }
}
