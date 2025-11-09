import auth.LoginFrame;
import service.DatabaseService;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Инициализация БД
        DatabaseService.initialize();

        // Запуск GUI
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}