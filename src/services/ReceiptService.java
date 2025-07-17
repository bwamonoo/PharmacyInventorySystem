package services;

import models.*;
import util.Color;
import managers.*;

import java.io.*;
import java.text.SimpleDateFormat;

public class ReceiptService {

    private static final String RECEIPT_DIR = "receipts/";

    public static void generateReceipt(SaleTransaction txn, DrugManager drugManager, CustomerManager customerManager) {
        try {
            File dir = new File(RECEIPT_DIR);
            if (!dir.exists())
                dir.mkdirs();

            Customer customer = customerManager.getCustomerById(txn.getCustomerId());
            String fileName = RECEIPT_DIR + "receipt_" + txn.getTransactionId() + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("🧾 ATINKA MEDS - SALE RECEIPT");
                writer.println("=".repeat(40));
                writer.println("Transaction ID: " + txn.getTransactionId());
                writer.println("Customer ID: " + customer.getId());
                writer.println("Customer Name: " + customer.getName());
                writer.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(txn.getTimestamp()));
                writer.println("\nItem\tQty\tUnit Price\tSubtotal");

                double total = 0;
                for (Sale s : txn.getItems()) {
                    Drug drug = drugManager.getDrugByCode(s.getDrugCode());
                    if (drug == null)
                        continue;
                    double subtotal = s.getQuantity() * drug.getPrice();
                    total += subtotal;

                    writer.printf("%s\t%d\t%.2f\t\t%.2f%n",
                            drug.getName(), s.getQuantity(), drug.getPrice(), subtotal);
                }

                writer.println("\n" + "-".repeat(40));
                writer.printf("TOTAL: GHS %.2f%n", total);
                writer.println("Thank you for shopping with us!");
            }

            System.out.println(
                    "Customer: " + (customer.getId().equals("GUEST") ? "Walk-in Customer" : customer.getName()));
            System.out.println(Color.GREEN + "✓ " + "📄 Receipt saved: " + fileName + Color.RESET);

        } catch (Exception e) {
            System.out.println(Color.RED + "✗ " + "Error generating receipt: " + e.getMessage() + Color.RESET);
        }
    }
}
