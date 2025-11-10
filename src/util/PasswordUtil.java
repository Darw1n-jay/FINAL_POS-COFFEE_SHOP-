package pos.util;

import java.security.MessageDigest;
<<<<<<< HEAD
import java.nio.charset.StandardCharsets;

public class PasswordUtil {
    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
=======

public class PasswordUtil {
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
>>>>>>> e526182121cd690ea3e452877257c67a2e831e0d
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
