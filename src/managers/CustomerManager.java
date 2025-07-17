package managers;

import models.Customer;
import models.SaleTransaction;

import structures.CustomHashMap;
import structures.CustomList;
import structures.CustomArrayList;

public class CustomerManager {

  private final CustomHashMap<String, Customer> customerMap;

  public CustomerManager() {
    this.customerMap = new CustomHashMap<>();
  }

  public boolean addCustomer(Customer customer) {
    if (customer == null || customerMap.containsKey(customer.getId()))
      return false;

    if (customer.getSalesHistory() == null) {
      customer.setSalesHistory(new CustomArrayList<>());
    }

    customerMap.put(customer.getId(), customer);
    return true;
  }

  public boolean removeCustomer(String customerId) {
    return customerMap.remove(customerId) != null;
  }

  public boolean updateCustomerName(String customerId, String newName) {
    Customer customer = customerMap.get(customerId);
    if (customer == null)
      return false;
    customer.setName(newName);
    return true;
  }

  public boolean updateCustomerContact(String customerId, String newContact) {
    Customer customer = customerMap.get(customerId);
    if (customer == null)
      return false;
    customer.setContact(newContact);
    return true;
  }

  public Customer getCustomerById(String customerId) {
    return customerMap.get(customerId);
  }

  public boolean addTransactionToCustomer(String customerId, SaleTransaction txn) {
    Customer customer = customerMap.get(customerId);
    if (customer == null || txn == null)
      return false;

    CustomList<SaleTransaction> history = customer.getSalesHistory();
    if (history == null) {
      history = new CustomArrayList<>();
      customer.setSalesHistory(history);
    }

    history.add(txn);
    return true;
  }

  // get customer's sales history (defensive copy)
  public CustomList<SaleTransaction> getCustomerSalesHistory(String customerId) {
    Customer customer = customerMap.get(customerId);
    if (customer == null || customer.getSalesHistory() == null) {
      return new CustomArrayList<>();
    }

    CustomList<SaleTransaction> list = new CustomArrayList<>();
    for (SaleTransaction txn : customer.getSalesHistory()) {
      list.add(txn);
    }
    return list;
  }

  public CustomList<Customer> getAllCustomers() {
    CustomList<Customer> list = new CustomArrayList<>();
    for (Customer customer : customerMap.values()) {
      list.add(customer);
    }
    return list;
  }

  public void listAllCustomers() {
    if (customerMap.isEmpty()) {
      System.out.println("📭 No customers found.");
      return;
    }
    for (Customer c : customerMap.values()) {
      System.out.println(c);
    }
  }
}
