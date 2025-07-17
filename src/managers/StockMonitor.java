package managers;

import models.Drug;

import structures.CustomList;
import structures.CustomArrayList;
import structures.CustomHashMap;

import java.util.Comparator;

public class StockMonitor {
  private CustomHashMap<String, Drug> lowStockDrugsMap = new CustomHashMap<>();
  private static final int STOCK_THRESHOLD = 20;

  private CustomList<Drug> drugList;

  public StockMonitor(CustomList<Drug> drugList) {
    this.drugList = drugList;
  }

  public void addToLowStockMap(Drug drug) {
    lowStockDrugsMap.put(drug.getCode(), drug);
  }

  public void removeFromLowStockMap(String code) {
    lowStockDrugsMap.remove(code);
  }

  public CustomList<Drug> getReorderPriorityList() {
    CustomList<Drug> sorted = new CustomArrayList<>(drugList);
    sorted.sort(Comparator.comparingInt(Drug::getStockLevel));
    return sorted;
  }

  public CustomList<Drug> getLowStockDrugs() {
    CustomList<Drug> list = new CustomArrayList<>();

    for (Drug drug : lowStockDrugsMap.values())
      list.add(drug);
    return list;
  }

  public int getThreshold() {
    return STOCK_THRESHOLD;
  }
}
