package pos;

import pos.config.DB;
import pos.dao.UserDAO;
import pos.dao.ProductDAO;
import pos.dao.SaleDAO;
import pos.model.Models;
import pos.model.Models.User;
import pos.model.Models.Product;
import pos.model.Models.Sale;
import pos.model.Models.Sale.SaleItem;

import java.time.LocalDateTime;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        DB.init();
        showWelcome();
        try {
            mainLoop();
        } finally {
            scanner.close();
        }
    }

    private static void showWelcome() {
        System.out.println("====================================");
        System.out.println("       Java POS - Coffee Shop       ");
        System.out.println("====================================");
    }

    private static void mainLoop() {
        while (true) {
            if (currentUser == null) {
                System.out.println("\n1) Login\n2) Register (Cashier)\n3) Exit");
                System.out.print("Choose: ");
                switch (scanner.nextLine().trim()) {
                    case "1": login(); break;
                    case "2": registerCashier(); break;
                    case "3": System.exit(0);
                    default: System.out.println("Invalid choice");
                }
            } else {
                if (currentUser.role == User.Role.ADMIN) adminMenu();
                else cashierMenu();
            }
        }
    }

    private static void login() {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();

        if (UserDAO.verify(u, p)) {
            currentUser = UserDAO.getByUsername(u);
            System.out.println("Logged in as " + currentUser.username + " (" + currentUser.role + ")");
        } else {
            User maybe = UserDAO.getByUsername(u);
            if (maybe != null && maybe.role == User.Role.PENDING) {
                System.out.println("Registration pending admin approval. Please wait for admin to approve your account.");
            } else {
                System.out.println("Invalid credentials");
            }
        }
    }

    private static void registerCashier() {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        if (u.isEmpty()) { System.out.println("Username cannot be empty"); return; }
        if (UserDAO.getByUsername(u) != null) { System.out.println("Username exists"); return; }

        System.out.print("Password: ");
        String p = scanner.nextLine().trim();
        if (p.isEmpty()) { System.out.println("Password cannot be empty"); return; }

        User uobj = new User(u, p, User.Role.PENDING);
        UserDAO.insert(uobj);
        System.out.println("Registration submitted. Wait for admin approval.");
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    // ==================== Admin ====================
    private static void adminMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1) Manage Products\n2) View Inventory\n3) Manage Sales\n4) Manage Users\n5) Approve Registrations\n6) Logout");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": productManagement(); break;
            case "2": listProducts(); break;
            case "3": salesManagement(); break;
            case "4": manageUsers(); break;
            case "5": approveRegistrations(); break;
            case "6": logout(); break;
            default: System.out.println("Invalid");
        }
    }

    private static void cashierMenu() {
        System.out.println("\n--- Cashier Menu ---");
        System.out.println("1) Create Sale\n2) List My Sales\n3) List Products\n4) Logout");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": createSale(); break;
            case "2": listSalesForCurrentUser(); break;
            case "3": listProducts(); break;
            case "4": logout(); break;
            default: System.out.println("Invalid");
        }
    }

    // ==================== Products ====================
    private static void productManagement() {
        System.out.println("\n1) Add Product\n2) Update Product\n3) Delete Product\n4) List\n5) Back");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": addProduct(); break;
            case "2": updateProduct(); break;
            case "3": deleteProduct(); break;
            case "4": listProducts(); break;
            case "5": return;
            default: System.out.println("Invalid");
        }
    }

    private static void addProduct() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
        if (ProductDAO.getByName(name) != null) { System.out.println("Product exists."); return; }

        System.out.print("Price: ");
        double price;
        try { price = Double.parseDouble(scanner.nextLine().trim()); if (price < 0) { System.out.println("Price cannot be negative."); return; } }
        catch (NumberFormatException e) { System.out.println("Invalid price."); return; }

        System.out.print("Stock: ");
        int stock;
        try { stock = Integer.parseInt(scanner.nextLine().trim()); if (stock < 0) { System.out.println("Stock cannot be negative."); return; } }
        catch (NumberFormatException e) { System.out.println("Invalid stock."); return; }

        ProductDAO.insert(new Product(0, name, price, stock));
        System.out.println("Added.");
    }

    private static void updateProduct() {
        listProducts();
        System.out.print("ID to update: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid ID."); return; }

        Product p = ProductDAO.getById(id);
        if (p == null) { System.out.println("Not found."); return; }

        System.out.print("New name (" + p.name + "): ");
        String n = scanner.nextLine().trim();
        if (!n.isEmpty()) p.name = n;

        System.out.print("New price (" + p.price + "): ");
        String priceStr = scanner.nextLine().trim();
        if (!priceStr.isEmpty()) { try { p.price = Double.parseDouble(priceStr); } catch (NumberFormatException e) { System.out.println("Invalid price, keeping old."); } }

        System.out.print("New stock (" + p.stock + "): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) { try { p.stock = Integer.parseInt(stockStr); } catch (NumberFormatException e) { System.out.println("Invalid stock, keeping old."); } }

        ProductDAO.update(p);
        System.out.println("Updated.");
    }

    private static void deleteProduct() {
        listProducts();
        System.out.print("ID to delete: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid ID."); return; }

        ProductDAO.delete(id);
        System.out.println("Deleted.");
    }

    private static void listProducts() {
        System.out.println("\n--- Products ---");
        List<Product> products = ProductDAO.getAll();
        if (products.isEmpty()) { System.out.println("No products found."); return; }
        for (Product p : products)
            System.out.printf("%3d %-15s %8.2f %5d\n", p.id, p.name, p.price, p.stock);
    }

    // ==================== Sales ====================
    private static void createSale() {
        List<SaleItem> items = new ArrayList<>();
        while (true) {
            listProducts();
            System.out.print("Enter Product ID (0 to finish): ");
            int id;
            try { id = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid ID."); continue; }
            if (id == 0) break;

            Product p = ProductDAO.getById(id);
            if (p == null) { System.out.println("Product not found."); continue; }

            System.out.print("Qty: ");
            int qty;
            try { qty = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid quantity."); continue; }
            if (qty <= 0) { System.out.println("Invalid qty."); continue; }
            if (qty > p.stock) { System.out.println("Not enough stock."); continue; }

            items.add(new SaleItem(p.id, qty, p.price));
        }

        if (items.isEmpty()) { System.out.println("No items selected."); return; }

        double subtotal = items.stream().mapToDouble(i -> i.qty * i.price).sum();
        System.out.printf("Subtotal: %.2f\n", subtotal);

        double discountPercent = 0.0;
        System.out.print("Apply 20% discount? (y/n): ");
        if ("y".equalsIgnoreCase(scanner.nextLine().trim())) { discountPercent = 20.0; System.out.println("20% discount applied."); }
        else System.out.println("No discount applied.");

        double total = subtotal * (1.0 - discountPercent / 100.0);
        if (total < 0) total = 0.0;
        System.out.printf("Total after discount: %.2f\n", total);

        System.out.print("Proceed to payment? (y to pay / c to cancel / n to go back): ");
        String proceed = scanner.nextLine().trim();
        if ("c".equalsIgnoreCase(proceed)) { System.out.println("Order cancelled."); return; }
        if (!"y".equalsIgnoreCase(proceed)) { System.out.println("Aborted."); return; }

        System.out.println("Select payment mode:\n1) CASH\n2) CARD\n3) GCASH\n4) OTHER");
        System.out.print("Choose: ");
        String pmChoice = scanner.nextLine().trim();
        String paymentMode;
        boolean paymentSuccessful = false;

        switch (pmChoice) {
            case "1": paymentMode = "CASH"; paymentSuccessful = processCashPayment(total); break;
            case "2": paymentMode = "CARD"; paymentSuccessful = processCardPayment(); break;
            case "3": paymentMode = "GCASH"; paymentSuccessful = processGcashPayment(); break;
            default: paymentMode = "OTHER"; paymentSuccessful = true; System.out.println("Payment: OTHER"); break;
        }

        if (!paymentSuccessful) { System.out.println("Payment failed."); return; }

        Sale s = new Sale(0, currentUser.id, LocalDateTime.now().toString(), total, discountPercent, paymentMode, true, "COMPLETED");
        s.items.addAll(items);
        SaleDAO.insert(s);

        System.out.printf("Sale completed. Total: %.2f Payment: %s\n", total, paymentMode);
        printReceipt(s);
    }

    private static boolean processCashPayment(double total) {
        System.out.printf("Total due: %.2f\n", total);
        System.out.print("Enter amount tendered: ");
        try {
            double tendered = Double.parseDouble(scanner.nextLine().trim());
            if (tendered < total) { System.out.println("Insufficient."); return false; }
            System.out.printf("Change: %.2f\n", tendered - total);
            return true;
        } catch (NumberFormatException e) { System.out.println("Invalid."); return false; }
    }

    private static boolean processCardPayment() {
        System.out.print("Card number (16 digits): ");
        String cardNumber = scanner.nextLine().trim();
        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) { System.out.println("Invalid card."); return false; }
        if (Math.random() < 0.1) { System.out.println("Card declined."); return false; }
        System.out.println("Card accepted.");
        return true;
    }

    private static boolean processGcashPayment() {
        System.out.print("Mobile number (11 digits): ");
        String mobile = scanner.nextLine().trim();
        if (mobile.length() != 11 || !mobile.matches("\\d+")) { System.out.println("Invalid number."); return false; }
        System.out.print("PIN (4 digits): ");
        String pin = scanner.nextLine().trim();
        if (pin.length() != 4 || !pin.matches("\\d+")) { System.out.println("Invalid PIN."); return false; }
        if (Math.random() < 0.05) { System.out.println("Transaction failed."); return false; }
        System.out.println("GCash payment successful.");
        return true;
    }

   private static void listSalesForCurrentUser() {
    System.out.println("\n--- My Sales ---");
    List<Sale> sales = SaleDAO.getByUserId(currentUser.id);
    if (sales.isEmpty()) { System.out.println("No sales found."); return; }

    System.out.printf("%-5s %-20s %-12s %-10s %-8s %-10s\n", "ID", "Date", "Cashier", "Total", "Disc%", "Status");
    System.out.println("---------------------------------------------------------------");
    for (Sale s : sales) {
        User cashier = UserDAO.getById(s.userId);
        String cashierName = (cashier != null) ? cashier.username : "Unknown";
        System.out.printf("%-5d %-20s %-12s %-10.2f %-8.1f %-10s\n",
                s.id, s.datetime, cashierName, s.total, s.discount, s.status);
    }
}

private static void salesManagement() {
    System.out.println("\n--- All Sales ---");
    List<Sale> sales = SaleDAO.getAll();
    if (sales.isEmpty()) { System.out.println("No sales found."); return; }

    System.out.printf("%-5s %-20s %-12s %-10s %-8s %-10s\n", "ID", "Date", "Cashier", "Total", "Disc%", "Status");
    System.out.println("---------------------------------------------------------------");
    for (Sale s : sales) {
        User cashier = UserDAO.getById(s.userId);
        String cashierName = (cashier != null) ? cashier.username : "Unknown";
        System.out.printf("%-5d %-20s %-12s %-10.2f %-8.1f %-10s\n",
                s.id, s.datetime, cashierName, s.total, s.discount, s.status);
    }
}


    // ==================== Users ====================
    private static void manageUsers() {
        System.out.println("\n1) Add User (direct)\n2) List Users\n3) Back");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": createUser(); break;
            case "2": listUsers(); break;
            default: return;
        }
    }

    private static void createUser() {
        System.out.print("Username: "); String u = scanner.nextLine().trim(); if (u.isEmpty()) { System.out.println("Empty"); return; }
        System.out.print("Password: "); String p = scanner.nextLine().trim(); if (p.isEmpty()) { System.out.println("Empty"); return; }
        System.out.print("Role (ADMIN/CASHIER): "); String r = scanner.nextLine().toUpperCase().trim();
        User.Role role = "ADMIN".equals(r) ? User.Role.ADMIN : User.Role.CASHIER;
        UserDAO.insertDirect(new User(u, p, role));
        System.out.println("User created.");
    }

    private static void listUsers() {
        System.out.println("\n--- Users ---");
        List<User> users = UserDAO.getAll();
        if (users.isEmpty()) { System.out.println("No users."); return; }
        for (User u : users) System.out.printf("%3d %-10s %s\n", u.id, u.username, u.role);
    }

    private static void approveRegistrations() {
        List<User> pending = new ArrayList<>();
        for (User u : UserDAO.getAll()) if (u.role == User.Role.PENDING) pending.add(u);

        if (pending.isEmpty()) { System.out.println("No pending."); return; }
        System.out.println("\n--- Pending ---");
        for (User u : pending) System.out.printf("%3d %-12s\n", u.id, u.username);

        System.out.print("Enter ID to approve (0 to cancel): ");
        try { int id = Integer.parseInt(scanner.nextLine().trim()); if (id == 0) return; UserDAO.approveUser(id); System.out.println("Approved."); }
        catch (NumberFormatException ex) { System.out.println("Invalid."); }
    }

    // ==================== Receipt ====================
    private static void printReceipt(Sale sale) {
        System.out.println("\n======= RECEIPT =======");
        System.out.println("Cashier: " + currentUser.username);
        System.out.println("Date: " + sale.datetime);
        System.out.println("-----------------------");
        System.out.printf("%-15s %3s %8s %8s\n", "Item", "Qty", "Price", "Subtotal");
        System.out.println("-----------------------");
        for (SaleItem i : sale.items) {
            Product p = ProductDAO.getById(i.productId);
            if (p != null) {
                double subtotal = i.qty * i.price;
                System.out.printf("%-15s %3d %8.2f %8.2f\n", p.name, i.qty, i.price, subtotal);
            }
        }
        System.out.println("-----------------------");
        System.out.printf("Discount: %.1f%%\n", sale.discount);
        System.out.printf("Total: %.2f\n", sale.total);
        System.out.println("Payment: " + sale.paymentMode);
        System.out.println("=======================\n");
    }
}
