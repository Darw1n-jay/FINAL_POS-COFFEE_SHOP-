package pos.dao;

import pos.config.DB;
import pos.model.*;
import java.sql.*;
import java.util.*;

public class SaleDAO {
<<<<<<< HEAD

=======
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
    public static void insert(Sale s, List<SaleItem> items) {
        String insertSale = "INSERT INTO sales (user_id, datetime, total, discount, payment_mode, paid, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO sale_items (sale_id, product_id, qty, price) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE products SET stock = stock - ? WHERE id = ?";

        try (Connection conn = DB.connect()) {
            conn.setAutoCommit(false);
<<<<<<< HEAD

            int saleId = 0;
            try (PreparedStatement ps = conn.prepareStatement(insertSale, Statement.RETURN_GENERATED_KEYS)) {
=======
            int saleId = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sales (user_id, datetime, total, status) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
                ps.setInt(1, s.userId);
                ps.setString(2, s.datetime);
                ps.setDouble(3, s.total);
                ps.setDouble(4, s.discount);
                ps.setString(5, s.paymentMode);
                ps.setInt(6, s.paid ? 1 : 0);
                ps.setString(7, s.status);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) saleId = rs.getInt(1);
            }

<<<<<<< HEAD
            try (PreparedStatement psi = conn.prepareStatement(insertItem);
                 PreparedStatement pst = conn.prepareStatement(updateStock)) {
=======
            try (PreparedStatement psi = conn.prepareStatement(
                        "INSERT INTO sale_items (sale_id, product_id, qty, price) VALUES (?, ?, ?, ?)");
                 PreparedStatement pst = conn.prepareStatement(
                        "UPDATE products SET stock = stock - ? WHERE id = ?")) {
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
                for (SaleItem si : items) {
                    psi.setInt(1, saleId);
                    psi.setInt(2, si.productId);
                    psi.setInt(3, si.qty);
                    psi.setDouble(4, si.price);
                    psi.executeUpdate();
<<<<<<< HEAD

                    if (s.paid) {
                        pst.setInt(1, si.qty);
                        pst.setInt(2, si.productId);
                        pst.executeUpdate();
                    }
=======
                    pst.setInt(1, si.qty);
                    pst.setInt(2, si.productId);
                    pst.executeUpdate();
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
                }
            }

            conn.commit();
<<<<<<< HEAD
=======
            System.out.println("Sale recorded. ID: " + saleId);
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Sale> getAll() {
        List<Sale> list = new ArrayList<>();
        try (Connection conn = DB.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, user_id, datetime, total, discount, payment_mode, paid, status FROM sales ORDER BY datetime DESC")) {
            while (rs.next()) {
                Sale s = new Sale(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("datetime"),
                        rs.getDouble("total"),
                        rs.getDouble("discount"),
                        rs.getString("payment_mode"),
                        rs.getInt("paid") == 1,
                        rs.getString("status")
                );
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Sale> getByUserId(int userId) {
        List<Sale> list = new ArrayList<>();
        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT id, user_id, datetime, total, discount, payment_mode, paid, status FROM sales WHERE user_id=? ORDER BY datetime DESC")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Sale(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("datetime"),
                        rs.getDouble("total"),
                        rs.getDouble("discount"),
                        rs.getString("payment_mode"),
                        rs.getInt("paid") == 1,
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
