package cli;

import java.util.*;

import managers.*;
import models.Drug;
import structures.*;
import models.PurchaseTransaction;
import util.Color;

public class PurchaseManagementCLI {
  private final Scanner sc;
  private final DrugManager drugManager;
  private final SupplierManager supplierManager;
  private final PurchaseHistoryManager purchaseHistoryManager;
  private final StockMonitor stockMonitor;

  public PurchaseManagementCLI(Scanner sc, DrugManager drugManager, SupplierManager supplierManager,
      PurchaseHistoryManager purchaseHistoryManager, StockMonitor stockMonitor) {
    this.sc = sc;
    this.drugManager = drugManager;
    this.supplierManager = supplierManager;
    this.purchaseHistoryManager = purchaseHistoryManager;
    this.stockMonitor = stockMonitor;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out
          .println(Color.CYAN + "║" + Color.BOLD + "  🛒 PURCHASE MANAGEMENT MENU  " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Record Pharmacy Purchase");
      System.out.println("2. View Last 5 Purchases for Drug");
      System.out.println("3. Get All Purchases");
      System.out.println("4. Undo Last Purchase");
      System.out.println("5. View Low Stock Drugs");
      System.out.println("6. Back");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);
      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> recordPharmacyPurchase();
        case 2 -> viewRecentDrugPurchases();
        case 3 -> viewAllPurchases();
        case 4 -> undoLastPurchase();
        case 5 -> viewLowStock();
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

  private void viewRecentDrugPurchases() {
    String drugCode = Input.readNonEmpty("Enter drug code: ", sc);
    Drug drug = drugManager.getDrugByCode(drugCode);

    if (drug == null) {
      System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
      return;
    }

    CustomList<PurchaseTransaction> recent = purchaseHistoryManager.getRecentPurchasesByDrug(drugCode);

    if (recent.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No purchase history for this drug." + Color.RESET);
      return;
    }

    System.out.printf(Color.GREEN + "🧾 Last %d purchases for %s (%s):\n" + Color.RESET,
        recent.size(), drug.getName(), drug.getCode());

    for (PurchaseTransaction tx : recent) {
      System.out.printf("- Qty: %d | Total: GHS %.2f | Date: %s\n",
          tx.getQuantity(), tx.getTotalCost(), tx.getDateTime());
    }
  }

  private void viewAllPurchases() {
    CustomList<PurchaseTransaction> all = purchaseHistoryManager.getAllPurchases();

    if (all.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No purchase records found." + Color.RESET);
      return;
    }

    System.out.println(Color.GREEN + "\n📋 ALL PURCHASE TRANSACTIONS:" + Color.RESET);
    for (PurchaseTransaction tx : all) {
      System.out.printf("- Drug: %s | Qty: %d | Cost: GHS %.2f | Date: %s\n",
          tx.getDrugCode(), tx.getQuantity(), tx.getTotalCost(), tx.getDateTime());
    }
  }

  private void undoLastPurchase() {
    PurchaseTransaction lastTx = purchaseHistoryManager.peekLastPurchase();
    if (lastTx == null) {
      System.out.println(Color.YELLOW + "⚠ " + "No purchase to undo." + Color.RESET);
      return;
    }

    System.out.println("🧾 Last Purchase:");
    System.out.println("- Drug: " + lastTx.getDrugCode());
    System.out.println("- Quantity: " + lastTx.getQuantity());
    System.out.println("- Total Cost: GHS " + lastTx.getTotalCost());
    System.out.print(Color.YELLOW + "⚠ " + "Are you sure you want to undo this purchase? (y/n): " + Color.RESET);
    String confirm = sc.nextLine().trim().toLowerCase();
    if (!confirm.equals("y")) {
      System.out.println(Color.GREEN + "✓ " + "Undo cancelled." + Color.RESET);
      return;
    }

    PurchaseTransaction undone = purchaseHistoryManager.undoLastPurchase();
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
}
