package pos.config;

import java.sql.*;
<<<<<<< HEAD
import pos.util.PasswordUtil;
=======
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d

public class DB {
    private static final String DB_URL = "jdbc:sqlite:pos.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
<<<<<<< HEAD
        try (Connection conn = connect(); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "role TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE, " +
                    "price REAL, " +
                    "stock INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS sales (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "datetime TEXT, " +
                    "total REAL, " +
                    "discount REAL DEFAULT 0, " +
                    "payment_mode TEXT, " +
                    "paid INTEGER DEFAULT 0, " +
                    "status TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS sale_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "sale_id INTEGER, " +
                    "product_id INTEGER, " +
                    "qty INTEGER, " +
                    "price REAL)");
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role='ADMIN'")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String hash = PasswordUtil.hash("admin123");
                    st.execute("INSERT INTO users (username,password,role) VALUES ('admin','" + hash + "','ADMIN')");
                    System.out.println("Default admin created: admin / admin123");
                }
=======
        try (Connection conn = connect()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Connected to database: " + meta.getURL());
                System.out.println("Database Driver: " + meta.getDriverName());
                System.out.println("Initialization complete.");
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
            }
            System.out.println("Database initialized (pos.db)");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }
}
