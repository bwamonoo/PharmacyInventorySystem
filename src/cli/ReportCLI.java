package cli;

import services.*;
import util.Color;

import java.util.Scanner;

public class ReportCLI {
  private final Scanner sc;
  private final ReportService reportService;

  public ReportCLI(Scanner sc,
      ReportService reportService) {
    this.sc = sc;
    this.reportService = reportService;
  }

  public void menu() {
    boolean back = false;
    while (!back) {
      System.out.println(Color.CYAN + "╔═════════════════════╗");
      System.out.println(Color.CYAN + "║" + Color.BOLD + "  🧾 REPORTS  " + Color.RESET + Color.CYAN + "║");
      System.out.println(Color.CYAN + "╚═════════════════════╝" + Color.RESET);
      System.out.println("1. Drugs Sold Over a Period");
      System.out.println("2. Customer Purchase Report");
      System.out.println("3. Top Selling Drugs");
      System.out.println("4. Back to Main");
      System.out.println(Color.CYAN + "─".repeat(30) + Color.RESET);
      int opt = Input.readInt("Select: ", sc);
      switch (opt) {
        case 1 -> reportService.drugsSoldOverPeriod(sc);
        case 2 -> reportService.customerPurchaseReport();
        case 3 -> reportService.topSellingDrugsReport();
        case 4 -> back = true;
        default -> System.out.println(Color.RED + "✗ " + "Invalid option." + Color.RESET);
      }
    }
  }
}
