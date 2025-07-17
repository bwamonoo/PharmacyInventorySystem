package cli;

import managers.DrugManager;
import managers.SupplierManager;
import models.Drug;
import models.Supplier;
import structures.*;
import util.Color;

import java.util.*;

public class SupplierCLI {
  private final Scanner sc;
  private final SupplierManager supMgr;
  private final DrugManager drugManager;

  public SupplierCLI(Scanner sc, SupplierManager supplierManager, DrugManager drugManager) {
    this.sc = sc;
    this.supMgr = supplierManager;
    this.drugManager = drugManager;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out
          .println(Color.CYAN + "║" + Color.BOLD + "  🚛 SUPPLIERS MENU  " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Add Supplier");
      System.out.println("2. Remove Supplier");
      System.out.println("3. List All Suppliers");
      System.out.println("4. Filter by Location");
      System.out.println("5. Filter by Turnaround");
      System.out.println("6. Link Drug to Supplier");
      System.out.println("7. View Drugs Supplied by a Supplier");
      System.out.println("8. Back");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);

      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> addSupplier();
        case 2 -> removeSupplier();
        case 3 -> listSuppliers();
        case 4 -> filterByLocation();
        case 5 -> filterByTurnaround();
        case 6 -> linkDrugToSupplier();
        case 7 -> viewDrugsBySupplier();
        case 8 -> back = true;
        default -> System.out.println(Color.RED + "✗ " + "Invalid option." + Color.RESET);
      }
    }
  }

  private void addSupplier() {
    String id = Input.readNonEmpty("Enter supplier ID: ", sc).trim();
    String name = Input.readNonEmpty("Enter supplier name: ", sc).trim();
    String location = Input.readNonEmpty("Enter location: ", sc).trim();
    int days = Input.readInt("Enter delivery turnaround days: ", sc);

    String codesInput = Input.read("Enter drug codes supplied (comma-separated): ", sc).trim();
    CustomList<String> suppliedDrugs = new CustomArrayList<>();

    if (!codesInput.isEmpty()) {
      String[] codes = codesInput.split(",");
      for (String code : codes) {
        String trimmedCode = code.trim();
        if (drugManager.getDrugByCode(trimmedCode) != null) {
          suppliedDrugs.add(trimmedCode);
        } else {
          System.out
              .println(
                  Color.YELLOW + "⚠ " + "Warning: Drug code " + trimmedCode + " not found. Skipped." + Color.RESET);
        }
      }
    }

    Supplier s = new Supplier(id, name, location, days, suppliedDrugs);

    if (supMgr.addSupplier(s)) {
      System.out.println(Color.GREEN + "✓ " + "Supplier added successfully." + Color.RESET);
    } else {
      System.out.println(Color.RED + "✗ " + "Supplier ID already exists." + Color.RESET);
    }
  }

  private void removeSupplier() {
    String id = Input.readNonEmpty("Enter supplier ID to remove: ", sc);
    if (supMgr.removeSupplier(id)) {
      System.out.println(Color.GREEN + "✓ " + "Supplier removed." + Color.RESET);
    } else {
      System.out.println(Color.RED + "✗ " + "Supplier not found." + Color.RESET);
    }
  }

  private void listSuppliers() {
    var all = supMgr.getAllSuppliers();
    if (all.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No suppliers found." + Color.RESET);
    } else {
      all.forEach(System.out::println);
    }
  }

  private void filterByLocation() {
    String location = Input.readNonEmpty("Enter location to filter by: ", sc);
    var list = supMgr.filterByLocation(location);
    if (list.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No suppliers found in that location." + Color.RESET);
    } else {
      list.forEach(System.out::println);
    }
  }

  private void filterByTurnaround() {
    int maxDays = Input.readInt("Enter maximum turnaround time (days): ", sc);
    var list = supMgr.filterByTurnaroundTime(maxDays);
    if (list.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No suppliers found with that turnaround time." + Color.RESET);
    } else {
      list.forEach(System.out::println);
    }
  }

  private void linkDrugToSupplier() {
    String supplierId = Input.readNonEmpty("Enter supplier ID: ", sc).trim();

    Supplier supplier = supMgr.getSupplierById(supplierId);
    if (supplier == null) {
      System.out.println(Color.RED + "✗ " + "Supplier not found." + Color.RESET);
      return;
    }

    String drugCode = Input.readNonEmpty("Enter drug code to link: ", sc).trim();

    Drug drug = drugManager.getDrugByCode(drugCode);
    if (drug == null) {
      System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
      return;
    }

    if (!supplier.getDrugCodesSupplied().contains(drugCode)) {
      supplier.getDrugCodesSupplied().add(drugCode);
    }

    if (!drug.getSuppliersIds().contains(supplierId)) {
      drug.getSuppliersIds().add(supplierId);
    }

    System.out.println(Color.GREEN + "✓ " + "Drug linked to Supplier." + Color.RESET);
  }

  private void viewDrugsBySupplier() {
    String supplierId = Input.readNonEmpty("Enter supplier ID: ", sc).trim();
    Supplier supplier = supMgr.getSupplierById(supplierId);

    if (supplier == null) {
      System.out.println(Color.RED + "✗ " + "Supplier not found." + Color.RESET);
      return;
    }

    CustomList<String> drugCodes = supplier.getDrugCodesSupplied();
    if (drugCodes == null || drugCodes.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No drugs linked to this supplier." + Color.RESET);
      return;
    }

    System.out.println(Color.CYAN + "🔗 Drugs supplied by " + supplier.getName() + ":" + Color.RESET);
    for (String code : drugCodes) {
      Drug drug = drugManager.getDrugByCode(code);
      String name = (drug != null) ? drug.getName() : "Unknown (" + code + ")";
      System.out.println(" - " + name);
    }
  }

}
