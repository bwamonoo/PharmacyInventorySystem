package services;

import managers.DrugManager;
import managers.SupplierManager;
import models.Drug;
import models.Supplier;
import util.Color;

import java.io.PrintWriter;
import java.util.Scanner;

import structures.CustomArrayList;
import structures.CustomList;

public class DrugSearchService {

  private final DrugManager drugManager;
  private final SupplierManager supplierManager;
  private final Scanner scanner;

  public DrugSearchService(DrugManager drugManager, SupplierManager supplierManager, Scanner scanner) {
    this.drugManager = drugManager;
    this.supplierManager = supplierManager;
    this.scanner = scanner;
  }

  public void searchByExactName() {
    System.out.print(Color.BLUE + "▸ " + "🔍 Enter drug name (exact match): " + Color.RESET);
    String name = scanner.nextLine().trim();

    CustomList<Drug> matches = drugManager.searchByName(name);
    handleSearchResults(matches, name);
  }

  public void searchByPartialName() {
    System.out.print(Color.BLUE + "▸ " + "🔍 Enter part of drug name: " + Color.RESET);
    String name = scanner.nextLine().trim();

    CustomList<Drug> matches = drugManager.searchByNameContains(name);
    handleSearchResults(matches, name);
  }

  private void handleSearchResults(CustomList<Drug> matches, String searchQuery) {
    if (matches.isEmpty()) {
      System.out.println(Color.RED + "⚠ " + "No drugs match '" + searchQuery + "'." + Color.RESET);
      return;
    }

    for (Drug d : matches) {
      drugManager.printDrugWithSuppliers(d, supplierManager);
      System.out.println();
    }

    System.out.print(Color.BLUE + "▸ " + "💾 Save results to file? (y/n): " + Color.RESET);
    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
      System.out.print(Color.BLUE + "▸ " + "Enter filename (without extension): " + Color.RESET);
      String base = scanner.nextLine().trim();
      exportDrugsToText(matches, base + ".txt");
      exportDrugsToCSV(matches, base + ".csv");
    }
  }

  public void exportDrugsToText(CustomList<Drug> drugs, String filename) {
    try (PrintWriter writer = new PrintWriter(filename)) {
      for (Drug drug : drugs) {
        writer.println("Drug: " + drug.getName());
        writer.println("Code: " + drug.getCode());
        writer.println("Price: GHS " + drug.getPrice());
        writer.println("Stock: " + drug.getStockLevel());
        writer.println("Suppliers:");
        for (String id : drug.getSuppliersIds()) {
          Supplier s = supplierManager.getSupplierById(id);
          writer.println(" - " + (s != null ? s.getName() : "Unknown (" + id + ")"));
        }
        writer.println();
      }
      System.out.println(Color.GREEN + "✓ " + "📄 Text report saved to: " + filename + Color.RESET);
    } catch (Exception e) {
      System.out.println(Color.RED + "✗ " + "Failed to save TXT: " + e.getMessage() + Color.RESET);
    }
  }

  public void exportDrugsToCSV(CustomList<Drug> drugs, String filename) {
    try (PrintWriter writer = new PrintWriter(filename)) {
      writer.println("Drug Name,Code,Price,Stock Level,Suppliers");

      for (Drug drug : drugs) {
        CustomList<String> supplierNames = new CustomArrayList<>();
        for (String id : drug.getSuppliersIds()) {
          Supplier s = supplierManager.getSupplierById(id);
          supplierNames.add(s != null ? s.getName() : "Unknown(" + id + ")");
        }

        StringBuilder suppliersBuilder = new StringBuilder();
        for (int i = 0; i < supplierNames.size(); i++) {
          suppliersBuilder.append(supplierNames.get(i));
          if (i < supplierNames.size() - 1) {
            suppliersBuilder.append(" | ");
          }
        }
        String suppliers = suppliersBuilder.toString();

        writer.printf("%s,%s,%.2f,%d,%s%n",
            drug.getName(), drug.getCode(), drug.getPrice(),
            drug.getStockLevel(), suppliers);
      }

      System.out.println(Color.GREEN + "✓ " + "📊 CSV report saved to: " + filename + Color.RESET);
    } catch (Exception e) {
      System.out.println(Color.RED + "✗ " + "Failed to save CSV: " + e.getMessage() + Color.RESET);
    }
  }
}
