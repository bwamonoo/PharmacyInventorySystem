# 💊 Pharmacy Inventory System – Atinka Meds

A terminal-based pharmacy inventory management system built in Java using core data structures and algorithms. This offline-first application was developed as part of the DCIT308: Data Structures & Algorithms II course at the University of Ghana.

---

## 📌 Project Description

Atinka Meds, a community pharmacy in Adenta, needed a custom solution to manage increasing drug stock and customer activity — all without reliance on internet or cloud-based systems. This project simulates real pharmacy operations using only native Java features, file storage, and algorithmic techniques.

---

## 🧱 Features

### ✅ Core Functionalities

- Add, update, delete, and list drug items
- Search drugs by code, name, or supplier using linear/binary search
- Sort drugs by name (Merge Sort) and price (Insertion Sort)
- Manage supplier profiles and drug mappings
- Track drug purchases and generate history reports
- Maintain real-time sales logs using a stack (LIFO)
- Monitor low-stock drugs using a min-heap (priority queue)
- Manage customer records and associate them with purchases
- Persist all data using local file I/O (no databases or libraries)

### 📂 Data Persistence

- All operations are stored in `.txt` files inside a `data/` directory
- Supports loading and saving drugs, suppliers, customers, purchases, and sales

---

## 🛠 Technologies Used

| Tool               | Description                                          |
| ------------------ | ---------------------------------------------------- |
| Java               | Core language                                        |
| Java I/O           | File handling using `BufferedReader` & `Writer`      |
| Data Structures    | HashMap, ArrayList, LinkedList, Stack, PriorityQueue |
| Sorting Algorithms | Merge Sort, Insertion Sort                           |

---

## 📁 Project Structure

PharmacyInventorySystem/
│
├── src/
│ ├── cli/ # CLI and main launcher
│ ├── models/ # Data models (Drug, Customer, etc.)
│ ├── managers/ # Business logic managers
│ ├── services/ # File I/O storage layer
│ └── report/ # (Optional) performance analysis
│
├── data/ # Persistent data files
│ ├── drugs.txt
│ ├── suppliers.txt
│ ├── customers.txt
│ ├── purchases.txt
│ ├── sales.txt
│ └── PerformanceReport.txt
│
└── README.md

---

## 📊 Performance Overview

| Feature          | Structure Used | Time Complexity (Avg) |
| ---------------- | -------------- | --------------------- |
| Drug Lookup      | HashMap        | O(1)                  |
| Search (Linear)  | ArrayList      | O(n)                  |
| Search (Binary)  | Sorted List    | O(log n)              |
| Sort by Name     | Merge Sort     | O(n log n)            |
| Sort by Price    | Insertion Sort | O(n²)                 |
| Sales Log        | Stack          | O(1) push/pop         |
| Stock Alerts     | PriorityQueue  | O(log n) insert       |
| Purchase History | LinkedList     | O(n) traversal        |

---

## 🧪 How to Run

1. Clone this repository.
2. Compile the project using any Java IDE or VS Code.
3. Run the `Main` class inside `src/cli/Main.java`.
4. Follow the menu to interact with the system.
5. All changes are saved to the `data/` folder automatically on exit.

---

## ✍️ Authors

1. Benjamin Amonoo Wilberforce  
2. Derrick Boateng
3. Fiawoyipe Leslie Elikem
4. Zephanila Afia Opoku
5. Baakum Saviour
6. Sabadu Endurance Offeibea
7. Stephanie Awurabena Dunyo

---

## ✅ License

This project is developed solely for academic purposes. Not intended for production or commercial use.
