        package pos;

<<<<<<< HEAD
        import pos.config.DB;
        import pos.model.*;
        import pos.dao.*;
        import pos.util.PasswordUtil;
        import java.util.*;
        import java.time.*;

        public class Main {
            private static final Scanner scanner = new Scanner(System.in);
            private static User currentUser = null;
=======
import pos.config.DB;
import pos.model.*;
import pos.dao.*;
import java.util.*;
import java.time.*;
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d

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
                System.out.println("       Java POS - Coffee Shop ");
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
                        if (currentUser.isAdmin()) adminMenu();
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
                    if (maybe != null && "PENDING".equalsIgnoreCase(maybe.role)) {
                        System.out.println("Registration pending admin approval. Please wait for admin to approve your account.");
                    } else {
                        System.out.println("Invalid credentials");
                    }
                }
            }

            private static void registerCashier() {
                System.out.print("Username: ");
                String u = scanner.nextLine().trim();
                if (u.isEmpty()) {
                    System.out.println("Username cannot be empty");
                    return;
                }
                if (UserDAO.getByUsername(u) != null) {
                    System.out.println("Username exists");
                    return;
                }
                System.out.print("Password: ");
                String p = scanner.nextLine().trim();
                if (p.isEmpty()) {
                    System.out.println("Password cannot be empty");
                    return;
                }
                User uobj = new User(u, p, "PENDING");
                UserDAO.insert(uobj);
                System.out.println("Registration submitted. Wait for admin approval.");
            }

            private static void logout() {
                currentUser = null;
                System.out.println("Logged out.");
            }

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
                if (name.isEmpty()) {
                    System.out.println("Name cannot be empty. Operation cancelled.");
                    return;
                }
                if (ProductDAO.getByName(name) != null) {
                    System.out.println("Product with this name already exists. Operation cancelled.");
                    return;
                }
                System.out.print("Price: ");
                double price;
                try {
                    price = Double.parseDouble(scanner.nextLine().trim());
                    if (price < 0) {
                        System.out.println("Price cannot be negative. Operation cancelled.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price. Operation cancelled.");
                    return;
                }
                System.out.print("Stock: ");
                int stock;
                try {
                    stock = Integer.parseInt(scanner.nextLine().trim());
                    if (stock < 0) {
                        System.out.println("Stock cannot be negative. Operation cancelled.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid stock. Operation cancelled.");
                    return;
                }
                ProductDAO.insert(new Product(0, name, price, stock));
                System.out.println("Added.");
            }

            private static void updateProduct() {
                listProducts();
                System.out.print("ID to update: ");
                int id;
                try {
                    id = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Operation cancelled.");
                    return;
                }
                Product p = ProductDAO.getById(id);
                if (p == null) {
                    System.out.println("Not found.");
                    return;
                }
                System.out.print("New name (" + p.name + "): ");
                String n = scanner.nextLine().trim();
                if (!n.isEmpty()) p.name = n;
                System.out.print("New price (" + p.price + "): ");
                String priceStr = scanner.nextLine().trim();
                if (!priceStr.isEmpty()) {
                    try {
                        p.price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price. Keeping old value.");
                    }
                }
                System.out.print("New stock (" + p.stock + "): ");
                String stockStr = scanner.nextLine().trim();
                if (!stockStr.isEmpty()) {
                    try {
                        p.stock = Integer.parseInt(stockStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid stock. Keeping old value.");
                    }
                }
                ProductDAO.update(p);
                System.out.println("Updated.");
            }

            private static void deleteProduct() {
                listProducts();
                System.out.print("ID to delete: ");
                int id;
                try {
                    id = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Operation cancelled.");
                    return;
                }
                ProductDAO.delete(id);
                System.out.println("Deleted.");
            }

            private static void listProducts() {
                System.out.println("\n--- Products ---");
                List<Product> products = ProductDAO.getAll();
                if (products.isEmpty()) {
                    System.out.println("No products found.");
                    return;
                }
                for (Product p : products)
                    System.out.printf("%3d %-15s %8.2f %5d\n", p.id, p.name, p.price, p.stock);
            }

            private static void createSale() {
                List<SaleItem> items = new ArrayList<>();
                while (true) {
                    listProducts();
                    System.out.print("Enter Product ID (0 to finish): ");
                    int id;
                    try {
                        id = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID. Try again.");
                        continue;
                    }
                    if (id == 0) break;
                    Product p = ProductDAO.getById(id);
                    if (p == null) {
                        System.out.println("Product not found.");
                        continue;
                    }
                    System.out.print("Qty: ");
                    int qty;
                    try {
                        qty = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity. Try again.");
                        continue;
                    }
                    if (qty <= 0) {
                        System.out.println("Invalid qty.");
                        continue;
                    }
                    if (qty > p.stock) {
                        System.out.println("Not enough stock. Available: " + p.stock);
                        continue;
                    }
                    items.add(new SaleItem(0, id, qty, p.price));
                }

                if (items.isEmpty()) {
                    System.out.println("No items selected.");
                    return;
                }

                double subtotal = items.stream().mapToDouble(i -> i.qty * i.price).sum();
                System.out.printf("Subtotal: %.2f\n", subtotal);

                double discountPercent = 0.0;
                System.out.print("Apply 20% discount? (y/n): ");
                String choice = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(choice)) {
                    discountPercent = 20.0;
                    System.out.println("20% discount applied.");
                } else {
                    System.out.println("No discount applied.");
                }

                double total = subtotal * (1.0 - discountPercent / 100.0);
                if (total < 0) total = 0.0;
                System.out.printf("Total after discount: %.2f\n", total);

                System.out.print("Proceed to payment? (y to pay / c to cancel order / n to go back): ");
                String proceed = scanner.nextLine().trim();
                if ("c".equalsIgnoreCase(proceed)) {
                    System.out.println("Order cancelled (not recorded).");
                    return;
                }
                if (!"y".equalsIgnoreCase(proceed)) {
                    System.out.println("Aborted. Order not recorded.");
                    return;
                }

                System.out.println("Select payment mode:");
                System.out.println("1) CASH\n2) CARD\n3) GCASH\n4) OTHER");
                System.out.print("Choose: ");
                String pmChoice = scanner.nextLine().trim();
                String paymentMode;
                boolean paymentSuccessful = false;
                switch (pmChoice) {
                    case "1":
                        paymentMode = "CASH";
                        paymentSuccessful = processCashPayment(total);
                        break;
                    case "2":
                        paymentMode = "CARD";
                        paymentSuccessful = processCardPayment();
                        break;
                    case "3":
                        paymentMode = "GCASH";
                        paymentSuccessful = processGcashPayment();
                        break;
                    default:
                        paymentMode = "OTHER";
                        paymentSuccessful = true;
                        System.out.println("Payment method selected: OTHER. Proceeding...");
                }

                if (!paymentSuccessful) {
                    System.out.println("Payment failed. Sale cancelled (not recorded).");
                    return;
                }

                boolean paid = true;
                Sale s = new Sale(0, currentUser.id, LocalDateTime.now().toString(), total, discountPercent, paymentMode, paid, "COMPLETED");
                SaleDAO.insert(s, items);

                System.out.printf("Sale completed. Total: %.2f Payment: %s\n", total, paymentMode);
            }

            private static boolean processCashPayment(double total) {
                System.out.printf("Total due: %.2f\n", total);
                System.out.print("Enter amount tendered: ");
                try {
                    double tendered = Double.parseDouble(scanner.nextLine().trim());
                    if (tendered < total) {
                        System.out.println("Insufficient amount. Payment failed.");
                        return false;
                    }
                    double change = tendered - total;
                    System.out.printf("Payment successful. Change: %.2f\n", change);
                    return true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount. Payment failed.");
                    return false;
                }
            }

            private static boolean processCardPayment() {
                System.out.print("Enter card number (16 digits): ");
                String cardNumber = scanner.nextLine().trim();
                if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
                    System.out.println("Invalid card number. Payment failed.");
                    return false;
                }
                if (Math.random() < 0.1) {
                    System.out.println("Card declined. Payment failed.");
                    return false;
                }
                System.out.println("Card accepted. Payment successful.");
                return true;
            }

            private static boolean processGcashPayment() {
                System.out.print("Enter mobile number (11 digits, e.g., 09123456789): ");
                String mobile = scanner.nextLine().trim();
                if (mobile.length() != 11 || !mobile.matches("\\d+")) {
                    System.out.println("Invalid mobile number. Payment failed.");
                    return false;
                }
                System.out.print("Enter PIN (4 digits): ");
                String pin = scanner.nextLine().trim();
                if (pin.length() != 4 || !pin.matches("\\d+")) {
                    System.out.println("Invalid PIN. Payment failed.");
                    return false;
                }
                if (Math.random() < 0.05) {
                    System.out.println("GCash transaction failed. Please try again.");
                    return false;
                }
                System.out.println("GCash payment successful.");
                return true;
            }

            private static void listSalesForCurrentUser() {
                System.out.println("\n--- My Sales ---");
                List<Sale> sales = SaleDAO.getByUserId(currentUser.id);
                if (sales.isEmpty()) {
                    System.out.println("No sales found.");
                    return;
                }
                for (Sale s : sales)
                    System.out.printf("%3d %s %.2f Disc:%.1f%% %s %s\n", s.id, s.datetime, s.total, s.discount, s.paymentMode, s.status);
            }

            private static void salesManagement() {
                System.out.println("\n--- All Sales ---");
                List<Sale> sales = SaleDAO.getAll();
                if (sales.isEmpty()) {
                    System.out.println("No sales found.");
                    return;
                }
                for (Sale s : sales)
                    System.out.printf("%3d User:%d %.2f Disc:%.1f%% %s %s\n", s.id, s.userId, s.total, s.discount, s.paymentMode, s.status);
            }

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
                System.out.print("Username: ");
                String u = scanner.nextLine().trim();
                if (u.isEmpty()) {
                    System.out.println("Username cannot be empty");
                    return;
                }
                System.out.print("Password: ");
                String p = scanner.nextLine().trim();
                if (p.isEmpty()) {
                    System.out.println("Password cannot be empty");
                    return;
                }
                System.out.print("Role (ADMIN/CASHIER): ");
                String r = scanner.nextLine().toUpperCase().trim();
                if (!"ADMIN".equals(r) && !"CASHIER".equals(r)) {
                    System.out.println("Invalid role. Defaulting to CASHIER.");
                    r = "CASHIER";
                }
                UserDAO.insertDirect(new User(u, p, r));
                System.out.println("User created.");
            }

            private static void listUsers() {
                System.out.println("\n--- Users ---");
                List<User> users = UserDAO.getAll();
                if (users.isEmpty()) {
                    System.out.println("No users found.");
                    return;
                }
                for (User u : users)
                    System.out.printf("%3d %-10s %s\n", u.id, u.username, u.role);
            }

            private static void approveRegistrations() {
                List<User> all = UserDAO.getAll();
                List<User> pending = new ArrayList<>();
                for (User u : all)
                    if ("PENDING".equalsIgnoreCase(u.role)) pending.add(u);

                if (pending.isEmpty()) {
                    System.out.println("No pending registrations.");
                    return;
                }

                System.out.println("\n--- Pending Registrations ---");
                for (User u : pending)
                    System.out.printf("%3d %-12s\n", u.id, u.username);

                System.out.print("Enter ID to approve (0 to cancel): ");
                try {
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    if (id == 0) return;
                    UserDAO.approveUser(id);
                    System.out.println("User approved (role set to CASHIER).");
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input.");
                }
            }
        }
<<<<<<< HEAD
=======
    }

    private static void login() {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();
        if (UserDAO.verify(u, p)) {
            currentUser = UserDAO.getByUsername(u);
            System.out.println("Logged in as " + currentUser.username + " (" + currentUser.role + ")");
        } else System.out.println("Invalid credentials");
    }

    private static void registerCashier() {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        if (UserDAO.getByUsername(u) != null) { System.out.println("Username exists"); return; }
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();
        UserDAO.insert(new User(u, p, "CASHIER"));
        System.out.println("Cashier registered.");
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    private static void adminMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1) Manage Products\n2) View Inventory\n3) Manage Sales\n4) Manage Users\n5) Logout");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": productManagement(); break;
            case "2": listProducts(); break;
            case "3": salesManagement(); break;
            case "4": manageUsers(); break;
            case "5": logout(); break;
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

    private static void productManagement() {
        System.out.println("\n1) Add Product\n2) Update Product\n3) Delete Product\n4) List\n5) Back");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": addProduct(); break;
            case "2": updateProduct(); break;
            case "3": deleteProduct(); break;
            case "4": listProducts(); break;
            case "5": return;
        }
    }

    private static void addProduct() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        ProductDAO.insert(new Product(0, name, price, stock));
        System.out.println("Added.");
    }

    private static void updateProduct() {
        listProducts();
        System.out.print("ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        Product p = ProductDAO.getById(id);
        if (p == null) { System.out.println("Not found."); return; }
        System.out.print("New name ("+p.name+"): ");
        String n = scanner.nextLine();
        if (!n.isEmpty()) p.name = n;
        ProductDAO.update(p);
        System.out.println("Updated.");
    }

    private static void deleteProduct() {
        listProducts();
        System.out.print("ID to delete: ");
        ProductDAO.delete(Integer.parseInt(scanner.nextLine()));
        System.out.println("Deleted.");
    }

    private static void listProducts() {
        System.out.println("\n--- Products ---");
        for (Product p : ProductDAO.getAll())
            System.out.printf("%3d %-15s %8.2f %5d\n", p.id, p.name, p.price, p.stock);
    }

    private static void createSale() {
        List<SaleItem> items = new ArrayList<>();
        while (true) {
            listProducts();
            System.out.print("Enter Product ID (0 to finish): ");
            int id = Integer.parseInt(scanner.nextLine());
            if (id == 0) break;
            Product p = ProductDAO.getById(id);
            if (p == null) continue;
            System.out.print("Qty: ");
            int qty = Integer.parseInt(scanner.nextLine());
            items.add(new SaleItem(0, id, qty, p.price));
        }
        if (items.isEmpty()) return;
        double total = items.stream().mapToDouble(i -> i.qty * i.price).sum();
        Sale s = new Sale(0, currentUser.id, LocalDateTime.now().toString(), total, "COMPLETED");
        SaleDAO.insert(s, items);
        System.out.println("Sale completed. Total: " + total);
    }

    private static void listSalesForCurrentUser() {
        System.out.println("\n--- My Sales ---");
        for (Sale s : SaleDAO.getByUserId(currentUser.id))
            System.out.printf("%3d %s %.2f %s\n", s.id, s.datetime, s.total, s.status);
    }

    private static void salesManagement() {
        for (Sale s : SaleDAO.getAll())
            System.out.printf("%3d User:%d %.2f %s\n", s.id, s.userId, s.total, s.status);
    }

    private static void manageUsers() {
        System.out.println("\n1) Add User\n2) List Users\n3) Back");
        switch (scanner.nextLine().trim()) {
            case "1": createUser(); break;
            case "2": listUsers(); break;
        }
    }

    private static void createUser() {
        System.out.print("Username: ");
        String u = scanner.nextLine();
        System.out.print("Password: ");
        String p = scanner.nextLine();
        System.out.print("Role (ADMIN/CASHIER): ");
        String r = scanner.nextLine().toUpperCase();
        UserDAO.insert(new User(u, p, r));
        System.out.println("User created.");
    }

    private static void listUsers() {
        for (User u : UserDAO.getAll())
            System.out.printf("%3d %-10s %s\n", u.id, u.username, u.role);
    }
}
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
