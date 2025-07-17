package cli;

import managers.CustomerManager;
import managers.DrugManager;
import models.Customer;
import models.Drug;
import models.Sale;
import models.SaleTransaction;
import structures.*;
import util.Color;

import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerCLI {
  private final Scanner sc;
  private final DrugManager drugManager;
  private final CustomerManager customerManager;

  public CustomerCLI(Scanner sc, DrugManager drugManager, CustomerManager customerManager) {
    this.sc = sc;
    this.drugManager = drugManager;
    this.customerManager = customerManager;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out.println(Color.CYAN + "║" + Color.BOLD + "  👥 CUSTOMERS MENU  " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Add Customer");
      System.out.println("2. Remove Customer");
      System.out.println("3. List All Customers");
      System.out.println("4. View Customer Sales History");
      System.out.println("5. Back");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);

      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> addCustomer();
        case 2 -> removeCustomer();
        case 3 -> listCustomers();
        case 4 -> viewCustomerSalesHistory();
        case 5 -> back = true;
        default -> System.out.println(Color.RED + "✗ " + "Invalid option." + Color.RESET);
      }
    }
  }

  private void addCustomer() {
    String id = Input.readNonEmpty("Enter customer ID: ", sc);
    String name = Input.readNonEmpty("Enter customer name: ", sc);
    String contact = Input.readNonEmpty("Enter contact: ", sc);

    Customer customer = new Customer(id, name, contact, new CustomArrayList<>());
    if (customerManager.addCustomer(customer)) {
      System.out.println(Color.GREEN + "✓ " + "Customer added." + Color.RESET);
    } else {
      System.out.println(Color.RED + "✗ " + "Customer ID already exists." + Color.RESET);
    }
  }

  private void removeCustomer() {
    String id = Input.readNonEmpty("Enter customer ID to remove: ", sc);
    if (customerManager.removeCustomer(id)) {
      System.out.println(Color.GREEN + "✓ " + "Customer removed." + Color.RESET);
    } else {
      System.out.println(Color.RED + "✓ " + "Customer not found." + Color.RESET);
    }
  }

  private void listCustomers() {
    var list = customerManager.getAllCustomers();
    if (list.isEmpty()) {
      System.out.println("📭 No customers available.");
    } else {
      list.forEach(System.out::println);
    }
  }

  private void viewCustomerSalesHistory() {
    String customerId = Input.readNonEmpty("Enter customer ID: ", sc);
    CustomList<SaleTransaction> history = customerManager.getCustomerSalesHistory(customerId);

    if (history == null || history.isEmpty()) {
      System.out.println("📭 No sales found for this customer.");
      return;
    }

    for (SaleTransaction txn : history) {
      System.out.println(Color.BLUE + "┌" + "─".repeat(58) + "┐" + Color.RESET);
      System.out.printf("│ %-20s: %-35s │\n", "🧾 Transaction ID", txn.getTransactionId());
      System.out.printf("│ %-20s: %-35s │\n", "👤 Customer ID", txn.getCustomerId());
      System.out.printf("│ %-20s: %-35s │\n", "📅 Date",
          new SimpleDateFormat("yyyy-MM-dd HH:mm").format(txn.getTimestamp()));
      System.out.println(Color.BLUE + "├" + "─".repeat(58) + "┤" + Color.RESET);
      double total = 0;

      for (Sale s : txn.getItems()) {
        Drug drug = drugManager.getDrugByCode(s.getDrugCode());
        if (drug == null)
          continue;

        double subtotal = drug.getPrice() * s.getQuantity();
        total += subtotal;

        System.out.printf("│%s │ Qty: %d │ Price: GHS %.2f │ Subtotal: GHS %.2f%n│\n",
            drug.getName(), s.getQuantity(), drug.getPrice(), subtotal);
      }

      System.out.println(Color.BLUE + "├" + "─".repeat(58) + "┤" + Color.RESET);
      System.out.printf("│ %-42s %12.2f │\n", "TOTAL: GHS ", total);
      System.out.println(Color.BLUE + "└" + "─".repeat(58) + "┘" + Color.RESET);
    }
  }
}
