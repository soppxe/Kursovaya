package util;

public class PasswordUtil {

    public static String hashPassword(String password) {
        // Простая реализация для демонстрации
        return Integer.toString(password.hashCode());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}