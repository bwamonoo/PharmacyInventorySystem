package cli;

import java.util.Scanner;

import managers.DrugManager;
import managers.SupplierManager;
import models.Drug;
import services.DrugSearchService;
import util.Color;

public class SearchAndSortDrugsCLI {
  private final Scanner sc;
  private final DrugManager drugMgr;
  private final DrugSearchService drugSearchService;
  private final SupplierManager supplierMgr;

  public SearchAndSortDrugsCLI(Scanner sc, DrugManager drugManager, DrugSearchService drugSearchService,
      SupplierManager supplierManager) {
    this.sc = sc;
    this.drugMgr = drugManager;
    this.drugSearchService = drugSearchService;
    this.supplierMgr = supplierManager;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out
          .println(Color.CYAN + "║" + Color.BOLD + "  🔍 SEARCH & SORT DRUGS MENU  " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Search by Code");
      System.out.println("2. Search Drugs by Name (Exact Match)");
      System.out.println("3. Search Drugs by Name (Partial Match)");
      System.out.println("4. Sort by Name");
      System.out.println("5. Sort by Price");
      System.out.println("6. Back to Menu");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);

      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> searchByCode();
        case 2 -> drugSearchService.searchByExactName();
        case 3 -> drugSearchService.searchByPartialName();
        case 4 -> {
          drugMgr.sortByName();
          System.out.println(Color.GREEN + "✓ " + "Sorted by name." + Color.RESET);
        }
        case 5 -> {
          drugMgr.sortByPrice();
          System.out.println(Color.GREEN + "✓ " + "Sorted by price." + Color.RESET);
        }
        case 6 -> back = true;
        default -> System.out.println(Color.RED + "✗ " + "Invalid option." + Color.RESET);
      }
    }
  }

  private void searchByCode() {
    System.out.println("1. Linear Search");
    System.out.println("2. Binary Search");
    System.out.println("3. HashMap Lookup (Fastest)");
    int c = Input.readInt("Option: ", sc);
    String code = Input.readNonEmpty("Enter drug code: ", sc);
    Drug d;

    switch (c) {
      case 1 -> d = drugMgr.searchDrugByCodeLinear(code);
      case 2 -> d = drugMgr.searchDrugByCodeBinary(code);
      case 3 -> d = drugMgr.getDrugByCode(code);
      default -> {
        System.out.println(Color.RED + "✗ " + "Invalid search option." + Color.RESET);
        return;
      }
    }

    if (d != null)
      drugMgr.printDrugWithSuppliers(d, supplierMgr);
    else
      System.out.println(Color.RED + "✗ " + "Drug not found." + Color.RESET);
  }

}
