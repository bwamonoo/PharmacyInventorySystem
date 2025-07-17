package managers;

import models.Drug;
import models.Supplier;
import util.Color;

import structures.CustomHashMap;
import structures.CustomList;
import structures.CustomArrayList;

public class DrugManager {
  private CustomHashMap<String, Drug> drugMap;
  private CustomList<Drug> drugList;
  private CustomHashMap<String, CustomList<Drug>> drugNameMap;

  private static final int INSERTION_THRESHOLD = 100;

  public DrugManager() {
    drugMap = new CustomHashMap<>();
    drugNameMap = new CustomHashMap<>();
    drugList = new CustomArrayList<>();
  }

  public boolean addDrug(Drug drug) {
    String code = drug.getCode();
    if (drugMap.containsKey(code)) {
      return false;
    }

    drugMap.put(code, drug);
    drugList.add(drug);

    String nameKey = drug.getName().toLowerCase();
    if (!drugNameMap.containsKey(nameKey)) {
      drugNameMap.put(nameKey, new CustomArrayList<>());
    }
    drugNameMap.get(nameKey).add(drug);

    return true;
  }

  public boolean removeDrug(String code) {
    if (!drugMap.containsKey(code)) {
      return false;
    }

    Drug drug = drugMap.remove(code);
    drugList.remove(drug);

    String nameKey = drug.getName().toLowerCase();
    CustomList<Drug> nameList = drugNameMap.get(nameKey);
    if (nameList != null) {
      nameList.remove(drug);
      if (nameList.isEmpty()) {
        drugNameMap.remove(nameKey);
      }
    }

    return true;
  }

  public Drug getDrugByCode(String code) {
    return drugMap.get(code.trim());
  }

  public CustomList<Drug> searchByName(String name) {
    CustomList<Drug> result = drugNameMap.get(name.toLowerCase());

    CustomList<Drug> list = new CustomArrayList<>();
    for (Drug drug : result) {
      list.add(drug);
    }
    return list;
  }

  public CustomList<Drug> searchByNameContains(String partialName) {
    CustomList<Drug> matches = new CustomArrayList<>();
    String lower = partialName.toLowerCase();

    for (String nameKey : drugNameMap.keySet()) {
      if (nameKey.contains(lower)) {
        matches.addAll(drugNameMap.get(nameKey));
      }
    }

    return matches;
  }

  public boolean updateDrugStock(String code, int newStock) {
    Drug drug = drugMap.get(code);
    if (drug == null)
      return false;

    drug.setStockLevel(newStock);
    return true;
  }

  public void printDrugWithSuppliers(Drug drug, SupplierManager supplierManager) {
    System.out.println(Color.CYAN + "🔹 Drug: " + drug.getName() + Color.RESET);
    System.out.println(Color.YELLOW + "   Code: " + drug.getCode() + Color.RESET);
    System.out.println("   Price: GHS " + drug.getPrice());
    System.out.println("   Stock: " + drug.getStockLevel());
    System.out.println("   Suppliers:");
    for (String id : drug.getSuppliersIds()) {
      Supplier s = supplierManager.getSupplierById(id);
      String supplierName = (s != null ? s.getName() : "Unknown (" + id + ")");
      System.out.println("     - " + supplierName);
    }
  }

  public CustomList<Drug> getAllDrugs() {
    return drugList;
  }

  public void listAllDrugs() {
    if (drugList.isEmpty()) {
      System.out.println(Color.YELLOW + "⚠ " + "No drugs found.");
    } else {
      for (Drug drug : drugList) {
        System.out.println(drug);
      }
    }
  }

  public Drug searchDrugByCodeLinear(String code) {
    for (Drug drug : drugList) {
      if (drug.getCode().equalsIgnoreCase(code)) {
        return drug;
      }
    }
    return null;
  }

  // (helper method for binary search)
  private void sortDrugListByCode() {
    drugList.sort((a, b) -> a.getCode().compareToIgnoreCase(b.getCode()));
  }

  public Drug searchDrugByCodeBinary(String code) {
    sortDrugListByCode();

    int low = 0;
    int high = drugList.size() - 1;

    while (low <= high) {
      int mid = (low + high) / 2;
      Drug midDrug = drugList.get(mid);
      int comparison = midDrug.getCode().compareToIgnoreCase(code);

      if (comparison == 0) {
        return midDrug;
      } else if (comparison < 0) {
        low = mid + 1;
      } else {
        high = mid - 1;
      }
    }

    return null;
  }

  public void sortByName() {
    if (drugList.size() <= INSERTION_THRESHOLD) {
      insertionSortByName(drugList);
      System.out.println("✅ Sorted drugs by name using Insertion Sort.");
    } else {
      drugList = mergeSortByName(drugList);
      System.out.println("✅ Sorted drugs by name using Merge Sort.");
    }
  }

  public void sortByPrice() {
    if (drugList.size() <= INSERTION_THRESHOLD) {
      insertionSortByPrice(drugList);
      System.out.println("✅ Sorted drugs by price using Insertion Sort.");
    } else {
      drugList = mergeSortByPrice(drugList);
      System.out.println("✅ Sorted drugs by price using Merge Sort.");
    }
  }

  private void insertionSortByName(CustomList<Drug> list) {
    for (int i = 1; i < list.size(); i++) {
      Drug key = list.get(i);
      int j = i - 1;
      while (j >= 0 && list.get(j).getName().compareToIgnoreCase(key.getName()) > 0) {
        list.set(j + 1, list.get(j));
        j--;
      }
      list.set(j + 1, key);
    }
  }

  private CustomList<Drug> mergeSortByName(CustomList<Drug> list) {
    if (list.size() <= 1)
      return list;

    int mid = list.size() / 2;
    CustomList<Drug> left = mergeSortByName(list.subList(0, mid));
    CustomList<Drug> right = mergeSortByName(list.subList(mid, list.size()));

    return mergeByName(left, right);
  }

  private CustomList<Drug> mergeByName(CustomList<Drug> left, CustomList<Drug> right) {
    CustomList<Drug> result = new CustomArrayList<>();
    int i = 0, j = 0;

    while (i < left.size() && j < right.size()) {
      if (left.get(i).getName().compareToIgnoreCase(right.get(j).getName()) <= 0) {
        result.add(left.get(i++));
      } else {
        result.add(right.get(j++));
      }
    }

    while (i < left.size())
      result.add(left.get(i++));
    while (j < right.size())
      result.add(right.get(j++));

    return result;
  }

  private void insertionSortByPrice(CustomList<Drug> list) {
    for (int i = 1; i < list.size(); i++) {
      Drug key = list.get(i);
      int j = i - 1;
      while (j >= 0 && list.get(j).getPrice() > key.getPrice()) {
        list.set(j + 1, list.get(j));
        j--;
      }
      list.set(j + 1, key);
    }
  }

  private CustomList<Drug> mergeSortByPrice(CustomList<Drug> list) {
    if (list.size() <= 1)
      return list;

    int mid = list.size() / 2;
    CustomList<Drug> left = mergeSortByPrice(list.subList(0, mid));
    CustomList<Drug> right = mergeSortByPrice(list.subList(mid, list.size()));

    return mergeByPrice(left, right);
  }

  private CustomList<Drug> mergeByPrice(CustomList<Drug> left, CustomList<Drug> right) {
    CustomList<Drug> result = new CustomArrayList<>();
    int i = 0, j = 0;

    while (i < left.size() && j < right.size()) {
      if (left.get(i).getPrice() <= right.get(j).getPrice()) {
        result.add(left.get(i++));
      } else {
        result.add(right.get(j++));
      }
    }

    while (i < left.size())
      result.add(left.get(i++));
    while (j < right.size())
      result.add(right.get(j++));

    return result;
  }
}
