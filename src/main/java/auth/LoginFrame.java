package auth;

import view.MainFrame;
import service.DatabaseService;
import util.PasswordUtil;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Калькулятор металлурга - Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Заголовок
        JLabel titleLabel = new JLabel("Калькулятор металлурга", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель формы
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Вход в систему"));

        formPanel.add(new JLabel("Имя пользователя:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        JButton loginBtn = new JButton("Войти");
        JButton registerBtn = new JButton("Регистрация");

        formPanel.add(loginBtn);
        formPanel.add(registerBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Обработчики событий
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> showRegistrationDialog());

        // Enter для входа
        getRootPane().setDefaultButton(loginBtn);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authService.login(username, password)) {
            new MainFrame(username).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Неверное имя пользователя или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Логин:"));
        panel.add(usernameField);
        panel.add(new JLabel("Пароль:"));
        panel.add(passwordField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Регистрация",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Логин и пароль обязательны!");
                return;
            }

            if (authService.register(username, password, email)) {
                JOptionPane.showMessageDialog(this, "Регистрация успешна! Теперь вы можете войти.");
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка регистрации! Возможно, пользователь уже существует.");
            }
        }
    }
}