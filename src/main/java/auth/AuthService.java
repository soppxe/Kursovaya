package auth;

import model.User;
import service.DatabaseService;
import util.PasswordUtil;

public class AuthService {

    public boolean register(String username, String plainPassword, String email) {
        // Проверка на пустые поля
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            return false;
        }

        // Проверка существования пользователя
        User existingUser = DatabaseService.findUserByUsername(username.trim());
        if (existingUser != null) {
            return false;
        }

        // Хеширование пароля
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        User newUser = new User(username.trim(), hashedPassword, email);

        return DatabaseService.createUser(newUser);
    }

    public boolean login(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || plainPassword == null) {
            return false;
        }

        User user = DatabaseService.findUserByUsername(username.trim());
        if (user == null) {
            return false;
        }

        return PasswordUtil.verifyPassword(plainPassword, user.getEncryptedPassword());
    }
}