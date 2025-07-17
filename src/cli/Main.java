package cli;

import managers.*;
import services.*;
import util.Color;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // chcp 65001
    private static final DrugManager drugManager = new DrugManager();
    private static final SupplierManager supplierManager = new SupplierManager();
    private static final CustomerManager customerManager = new CustomerManager();
    private static final PurchaseHistoryManager purchaseHistoryManager = new PurchaseHistoryManager();
    private static final SalesLogManager salesLogManager = new SalesLogManager();
    private static final StockMonitor stockMonitor = new StockMonitor(drugManager.getAllDrugs());

    private static final DrugSearchService drugSearchService = new DrugSearchService(drugManager, supplierManager,
            scanner);
    private static final ReportService reportService = new ReportService(drugManager, customerManager,
            purchaseHistoryManager, salesLogManager);

    public static void main(String[] args) {
        loadData();
        mainMenu();
        saveData();
        System.out.println("👋 Goodbye!");
    }

    private static void mainMenu() {
        DrugCLI drugCLI = new DrugCLI(scanner, drugManager, supplierManager, stockMonitor, drugSearchService);
        SearchAndSortDrugsCLI searchAndSortDrugsCLI = new SearchAndSortDrugsCLI(scanner, drugManager, drugSearchService,
                supplierManager);
        SupplierCLI supplierCLI = new SupplierCLI(scanner, supplierManager, drugManager);
        CustomerCLI customerCLI = new CustomerCLI(scanner, drugManager, customerManager);
        SalesManagementCLI salesManagementCLI = new SalesManagementCLI(scanner, drugManager, supplierManager,
                customerManager, salesLogManager, stockMonitor);
        PurchaseManagementCLI purchaseManagementCLI = new PurchaseManagementCLI(scanner, drugManager, supplierManager,
                purchaseHistoryManager, stockMonitor);
        ReportCLI reportCLI = new ReportCLI(scanner, reportService);

        boolean running = true;
        while (running) {
            System.out.println(Color.BLUE + "╔════════════════════════════╗");
            System.out.println(
                    Color.BLUE + "║" + Color.BOLD + " 📦 PHARMACY MANAGEMENT  " + Color.RESET + Color.BLUE + "║");
            System.out.println(Color.BLUE + "╚════════════════════════════╝" + Color.RESET);
            System.out.println(Color.PURPLE + "1. Drugs Management");
            System.out.println("2. Search & Sort Drugs");
            System.out.println("3. Suppliers Management");
            System.out.println("4. Customers Management");
            System.out.println("5. Pharmacy Purchases");
            System.out.println("6. Customer Sales");
            System.out.println("7. Reports");
            System.out.println("8. Exit" + Color.RESET);
            System.out.println(Color.BLUE + "─".repeat(30) + Color.RESET);

            int choice = Input.readInt("Select option: ", scanner);

            switch (choice) {
                case 1 -> drugCLI.menu();
                case 2 -> searchAndSortDrugsCLI.menu();
                case 3 -> supplierCLI.menu();
                case 4 -> customerCLI.menu();
                case 5 -> purchaseManagementCLI.menu();
                case 6 -> salesManagementCLI.menu();
                case 7 -> reportCLI.menu();
                case 8 -> running = false;
                default -> System.out.println(Color.RED + "Invalid option." + Color.RESET);
            }
        }

    }

    private static void loadData() {
        try {
            var drugs = FileStorageService.loadDrugs();
            drugs.forEach(d -> {
                drugManager.addDrug(d);
                // stockMonitor.updateCache(d);
            });
            FileStorageService.loadSuppliers().forEach(supplierManager::addSupplier);
            FileStorageService.loadCustomers().forEach(customerManager::addCustomer);
            FileStorageService.loadPurchaseHistory().forEach(purchaseHistoryManager::logPurchase);
            FileStorageService.loadSaleTransactions().forEach(txn -> {
                salesLogManager.logSaleTransaction(txn);
                customerManager.addTransactionToCustomer(txn.getCustomerId(), txn);
            });
            System.out.println(Color.GREEN + "Data loaded." + Color.RESET);
        } catch (IOException e) {
            System.out.println(Color.RED + "Error loading data: " + e.getMessage() + Color.RESET);
        }
    }

    private static void saveData() {
        try {
            FileStorageService.saveDrugs(drugManager.getAllDrugs());
            FileStorageService.saveSuppliers(supplierManager.getAllSuppliers());
            FileStorageService.saveCustomers(customerManager.getAllCustomers());
            FileStorageService.savePurchaseHistory(purchaseHistoryManager.getAllPurchases());
            FileStorageService.saveSaleTransactions(salesLogManager.getAllTransactions());
            System.out.println(Color.GREEN + "Data saved." + Color.RESET);
        } catch (IOException e) {
            System.out.println(Color.RED + "Error saving data: " + e.getMessage() + Color.RESET);
        }
    }

}
