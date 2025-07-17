package managers;

import models.Supplier;

import structures.CustomList;
import structures.CustomArrayList;
import structures.CustomHashMap;

public class SupplierManager {
  private CustomHashMap<String, Supplier> supplierMap;

  public SupplierManager() {
    supplierMap = new CustomHashMap<>();
  }

  public CustomList<Supplier> getAllSuppliers() {
    CustomList<Supplier> list = new CustomArrayList<>();
    for (Supplier supplier : supplierMap.values())
      list.add(supplier);
    return list;
  }

  public Supplier getSupplierById(String supplierId) {
    return supplierMap.get(supplierId);
  }

  public boolean addSupplier(Supplier supplier) {
    if (supplierMap.containsKey(supplier.getId()))
      return false;
    supplierMap.put(supplier.getId(), supplier);
    return true;
  }

  public boolean removeSupplier(String supplierId) {
    if (!supplierMap.containsKey(supplierId))
      return false;
    supplierMap.remove(supplierId);
    return true;
  }

  public boolean updateLocation(String supplierId, String newLocation) {
    Supplier s = supplierMap.get(supplierId);
    if (s == null)
      return false;
    s.setLocation(newLocation);
    return true;
  }

  public boolean updateTurnaround(String supplierId, int newDays) {
    Supplier s = supplierMap.get(supplierId);
    if (s == null)
      return false;
    s.setDeliveryTurnaroundDays(newDays);
    return true;
  }

  public boolean linkDrugToSupplier(String supplierId, String drugCode) {
    Supplier s = supplierMap.get(supplierId);
    if (s == null)
      return false;

    CustomList<String> currentDrugs = s.getDrugCodesSupplied();
    if (!currentDrugs.contains(drugCode)) {
      currentDrugs.add(drugCode);
    }
    return true;
  }

  public CustomList<Supplier> filterByLocation(String location) {
    CustomList<Supplier> filtered = new CustomArrayList<>();
    for (Supplier s : supplierMap.values()) {
      if (s.getLocation().equalsIgnoreCase(location)) {
        filtered.add(s);
      }
    }
    return filtered;
  }

  public CustomList<Supplier> filterByTurnaroundTime(int maxDays) {
    CustomList<Supplier> filtered = new CustomArrayList<>();
    for (Supplier s : supplierMap.values()) {
      if (s.getDeliveryTurnaroundDays() <= maxDays) {
        filtered.add(s);
      }
    }
    return filtered;
  }
}
