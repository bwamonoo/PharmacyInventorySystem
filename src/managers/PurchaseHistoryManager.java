package managers;

import models.PurchaseTransaction;

import structures.CustomLinkedList;
import structures.CustomList;
import structures.CustomStack;
import structures.CustomArrayList;

public class PurchaseHistoryManager {
  private CustomLinkedList<PurchaseTransaction> purchases;
  private CustomStack<PurchaseTransaction> undoStack = new CustomStack<>();

  public PurchaseHistoryManager() {
    purchases = new CustomLinkedList<>();
  }

  public void logPurchase(PurchaseTransaction tx) {
    purchases.add(tx);
    undoStack.push(tx);
  }

  public PurchaseTransaction peekLastPurchase() {
    return undoStack.isEmpty() ? null : undoStack.peek();
  }

  public PurchaseTransaction undoLastPurchase() {
    if (!undoStack.isEmpty()) {
      PurchaseTransaction last = undoStack.pop();
      purchases.remove(last);
      return last;
    }
    return null;
  }

  public CustomList<PurchaseTransaction> getAllPurchases() {
    CustomList<PurchaseTransaction> list = new CustomArrayList<>();

    for (PurchaseTransaction txn : purchases)
      list.add(txn);
    return list;
  }

  // get a drug's most recent 5 purchases
  public CustomList<PurchaseTransaction> getRecentPurchasesByDrug(String drugCode) {
    CustomList<PurchaseTransaction> recent = new CustomArrayList<>();
    int count = 0;

    for (int i = purchases.size() - 1; i >= 0 && count < 5; i--) {
      PurchaseTransaction t = purchases.get(i);
      if (t.getDrugCode().equalsIgnoreCase(drugCode)) {
        recent.add(t);
        count++;
      }
    }

    return recent;
  }

  public CustomList<PurchaseTransaction> getMostRecentPurchases(int n) {
    CustomList<PurchaseTransaction> all = new CustomArrayList<>();
    for (PurchaseTransaction txn : purchases) {
      all.add(txn);
    }
    int size = all.size();
    CustomList<PurchaseTransaction> recent = new CustomArrayList<>();
    for (int i = Math.max(0, size - n); i < size; i++) {
      recent.add(all.get(i));
    }
    return recent;
  }

  public void sortTransactionsByDate() {
    for (int i = 1; i < purchases.size(); i++) {
      PurchaseTransaction key = purchases.get(i);
      int j = i - 1;

      while (j >= 0 && purchases.get(j).getDateTime().after(key.getDateTime())) {
        purchases.set(j + 1, purchases.get(j));
        j--;
      }

      purchases.set(j + 1, key);
    }
  }
}
