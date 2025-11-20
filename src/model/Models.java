            package pos.model;

            import java.util.ArrayList;
            import java.util.List;

            public class Models {

                public static class User {
                    public enum Role { ADMIN, CASHIER, PENDING }

                    public int id;
                    public String username;
                    public String password;
                    public Role role;

                    public User() {}
                    public User(String username, String password, Role role) {
                        this.username = username;
                        this.password = password;
                        this.role = role;
                    }

                    public boolean isAdmin() {
                        return role == Role.ADMIN;
                    }
                }

                public static class Product {
                    public int id;
                    public String name;
                    public double price;
                    public int stock;

                    public Product() {}
                    public Product(int id, String name, double price, int stock) {
                        this.id = id;
                        this.name = name;
                        this.price = price;
                        this.stock = stock;
                    }
                }

                public static class Sale {
                    public int id;
                    public int userId;
                    public String datetime;
                    public double total;
                    public double discount;
                    public String paymentMode;
                    public boolean paid;
                    public String status;
                    public List<SaleItem> items = new ArrayList<>();

                    public Sale() {}
                    public Sale(int id, int userId, String datetime, double total, double discount,
                                String paymentMode, boolean paid, String status) {
                        this.id = id;
                        this.userId = userId;
                        this.datetime = datetime;
                        this.total = total;
                        this.discount = discount;
                        this.paymentMode = paymentMode;
                        this.paid = paid;
                        this.status = status;
                    }

                    public static class SaleItem {
                        public int productId;
                        public int qty;
                        public double price;

                        public SaleItem() {}
                        public SaleItem(int productId, int qty, double price) {
                            this.productId = productId;
                            this.qty = qty;
                            this.price = price;
                        }
                    }
                }
            }
