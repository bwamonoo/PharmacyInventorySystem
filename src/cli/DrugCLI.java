package cli;

import managers.*;
import models.*;
import services.*;
import structures.*;
import util.Color;

import java.util.*;

public class DrugCLI {
  private final Scanner sc;
  private final DrugManager drugMgr;
  private final SupplierManager supplierMgr;
  private final StockMonitor stockMon;
  private final DrugSearchService searchSvc;

  public DrugCLI(Scanner sc, DrugManager drugManager, SupplierManager supplierManager,
      StockMonitor stockMonitor, DrugSearchService drugSearchService) {
    this.sc = sc;
    this.drugMgr = drugManager;
    this.supplierMgr = supplierManager;
    this.stockMon = stockMonitor;
    this.searchSvc = drugSearchService;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out.println(Color.CYAN + "║" + Color.BOLD + "  💊 DRUGS MENU " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Add Drug");
      System.out.println("2. Remove Drug");
      System.out.println("3. Update Drug");
      System.out.println("4. List All Drugs");
      System.out.println("5. Link Supplier to Drug");
      System.out.println("6. View Suppliers of a Drug");
      System.out.println("7. Back to Menu");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);

      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> addDrug();
        case 2 -> removeDrug();
        case 3 -> updateDrug();
        case 4 -> drugMgr.listAllDrugs();
        case 5 -> linkSupplierToDrug();
        case 6 -> viewSuppliersOfDrug();
        case 7 -> back = true;
        default -> System.out.println(Color.RED + "✗ " + "Invalid option." + Color.RESET);
      }
    }
  }

  private void addDrug() {
    String code = Input.readNonEmpty("Enter drug code: ", sc).trim();

    String name = Input.readNonEmpty("Enter drug name: ", sc);
    String[] ids = Input.readNonEmpty("Enter supplier IDs (comma separated): ", sc).split(",");
    CustomList<String> supplierIds = new CustomArrayList<>();
    for (String id : ids) {
      supplierIds.add(id.trim());
    }
    String expDate = Input.readNonEmpty("Enter expiration date (YYYY-MM-DD): ", sc);
    double price = Input.readDouble("Enter price: ", sc);
    int stock = Input.readInt("Enter stock quantity: ", sc);
    if (price < 0 || stock < 0) {
      System.out.println(Color.RED + "✗ " + "Price and stock cannot be negative." + Color.RESET);
      return;
    }
    Drug drug = new Drug(code, name, supplierIds, expDate, price, stock);
    if (drugMgr.addDrug(drug)) {
      System.out.println(Color.GREEN + "✓ " + "Drug added." + Color.RESET);
    } else {
      System.out.println(Color.RED + "✗ " + "Drug code already exists." + Color.RESET);
    }
  }

  private void updateDrug() {
    String code = Input.readNonEmpty("Enter drug code to update: ", sc);
    Drug drug = drugMgr.getDrugByCode(code);
    if (drug == null) {
      System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
      return;
    }

    System.out.println("Leave input empty to skip updating that field.");

    String newName = Input.read("New name [" + drug.getName() + "]: ", sc);
    if (!newName.isEmpty())
      drug.setName(newName);

    String newExpDate = Input.read("New expiration date [" + drug.getExpirationDate() + "]: ", sc);
    if (!newExpDate.isEmpty())
      drug.setExpirationDate(newExpDate);

    String newPriceStr = Input.read("New price [" + drug.getPrice() + "]: ", sc);
    if (!newPriceStr.isEmpty()) {
      try {
        drug.setPrice(Double.parseDouble(newPriceStr));
      } catch (NumberFormatException e) {
        System.out.println(Color.RED + "✗ " + "Invalid price input. Skipped." + Color.RESET);
      }
    }

    String newStockStr = Input.read("New stock [" + drug.getStockLevel() + "]: ", sc);
    if (!newStockStr.isEmpty()) {
      try {
        drug.setStockLevel(Integer.parseInt(newStockStr));
      } catch (NumberFormatException e) {
        System.out.println(Color.RED + "✗ " + "Invalid stock input. Skipped." + Color.RESET);
      }
    }

    System.out.println(Color.GREEN + "✓ " + "Drug updated." + Color.RESET);
  }

  private void removeDrug() {
    String code = Input.readNonEmpty("Enter drug code to remove: ", sc);
    if (drugMgr.removeDrug(code)) {
      stockMon.removeFromLowStockMap(code);
      System.out.println(Color.GREEN + "✓ " + "Drug removed." + Color.RESET);
    } else {
      System.out.println(Color.RED + "✗ " + "No drug with that code." + Color.RESET);
    }
  }

  private void linkSupplierToDrug() {
    String drugCode = Input.readNonEmpty("Enter drug code: ", sc).trim();
    Drug drug = drugMgr.getDrugByCode(drugCode);

    if (drug == null) {
      System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
      return;
    }

    String supplierId = Input.readNonEmpty("Enter supplier ID to link: ", sc).trim();
    Supplier supplier = supplierMgr.getSupplierById(supplierId);

    if (supplier == null) {
      System.out.println(Color.RED + "✗ " + "Supplier not found." + Color.RESET);
      return;
    }

    // Link supplier to drug
    if (!drug.getSuppliersIds().contains(supplierId)) {
      drug.getSuppliersIds().add(supplierId);
    }

    // Link drug to supplier
    if (!supplier.getDrugCodesSupplied().contains(drugCode)) {
      supplier.getDrugCodesSupplied().add(drugCode);
    }

    System.out.println(Color.GREEN + "✓ " + "Supplier linked to drug." + Color.RESET);
  }

  private void viewSuppliersOfDrug() {
    String code = Input.readNonEmpty("Enter drug code: ", sc).trim();
    Drug drug = drugMgr.getDrugByCode(code);

    if (drug == null) {
      System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
      return;
    }

    CustomList<String> supplierIds = drug.getSuppliersIds();
    if (supplierIds == null || supplierIds.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No suppliers linked to this drug." + Color.RESET);
      return;
    }

    System.out.println(Color.CYAN + "🔗 Suppliers for " + drug.getName() + ":" + Color.RESET);
    for (String id : supplierIds) {
      Supplier s = supplierMgr.getSupplierById(id);
      String name = (s != null) ? s.getName() : "Unknown (" + id + ")";
      System.out.println(" - " + name);
    }
  }
}
