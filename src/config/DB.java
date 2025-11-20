package pos.config;

import java.sql.*;
import pos.util.PasswordUtil;

public class DB {
    private static final String DB_URL = "jdbc:sqlite:pos.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
        try (Connection conn = connect()) {

            if (!tableExists(conn, "users") ||
                !tableExists(conn, "products") ||
                !tableExists(conn, "sales") ||
                !tableExists(conn, "sale_items")) {

                System.out.println("ERROR: One or more database tables are missing!");
                System.out.println("Your existing data will NOT be modified.");
                System.out.println("Please restore your database or import the correct SQL structure.");
                return;
            }

            System.out.println("Database connected successfully.");

        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection conn, String tableName) {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next(); 
        } catch (SQLException e) {
            return false;
        }
    }
}
