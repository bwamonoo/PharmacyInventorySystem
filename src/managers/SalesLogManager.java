package managers;

import models.SaleTransaction;

import structures.CustomArrayList;
import structures.CustomList;
import structures.CustomStack;

import java.util.Date;

public class SalesLogManager {

  private final CustomStack<SaleTransaction> transactionStack;

  public SalesLogManager() {
    this.transactionStack = new CustomStack<>();
  }

  public void logSaleTransaction(SaleTransaction txn) {
    transactionStack.push(txn);
  }

  public CustomList<SaleTransaction> viewRecentTransactions(int n) {
    CustomList<SaleTransaction> recent = new CustomArrayList<>();
    int size = transactionStack.size();
    for (int i = size - 1; i >= Math.max(0, size - n); i--) {
      recent.add(transactionStack.get(i));
    }
    return recent;
  }

  public SaleTransaction peekLastTransaction() {
    return transactionStack.isEmpty() ? null : transactionStack.peek();
  }

  public SaleTransaction undoLastTransaction() {
    if (transactionStack.isEmpty())
      return null;
    return transactionStack.pop();
  }

  public CustomList<SaleTransaction> getAllTransactions() {
    CustomList<SaleTransaction> list = new CustomArrayList<>();
    for (SaleTransaction txn : transactionStack)
      list.add(txn);
    return list;
  }

  public CustomList<SaleTransaction> filterByBuyer(String buyerId) {
    CustomList<SaleTransaction> result = new CustomArrayList<>();
    for (SaleTransaction t : transactionStack) {
      if (t.getCustomerId().equalsIgnoreCase(buyerId)) {
        result.add(t);
      }
    }
    return result;
  }

  public CustomList<SaleTransaction> getSalesWithinPeriod(Date start, Date end) {
    CustomList<SaleTransaction> result = new CustomArrayList<>();
    for (SaleTransaction txn : transactionStack) {
      if (!txn.getTimestamp().before(start) && !txn.getTimestamp().after(end)) {
        result.add(txn);
      }
    }
    return result;
  }
}
