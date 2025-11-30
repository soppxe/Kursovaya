package auth;

import view.MainFrame;
import service.DatabaseService;
import util.PasswordUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Окно аутентификации пользователя для приложения "Калькулятор металлурга".
 * Предоставляет интерфейс для входа в систему и регистрации новых пользователей.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Вход существующих пользователей в систему</li>
 * <li>Регистрация новых пользователей через диалоговое окно</li>
 * <li>Валидация вводимых данных</li>
 * <li>Интеграция с сервисом аутентификации {@link AuthService}</li>
 * <li>Переход к главному окну приложения после успешной аутентификации</li>
 * </ul>
 *
 * <p><b>Элементы интерфейса:</b>
 * <ul>
 * <li>Поле ввода имени пользователя</li>
 * <li>Поле ввода пароля с маскировкой символов</li>
 * <li>Кнопка входа в систему</li>
 * <li>Кнопка регистрации нового пользователя</li>
 * <li>Диалоговое окно регистрации с дополнительными полями</li>
 * </ul>
 *
 * @author Саитова София
 * @version 1.0
 * @see JFrame
 * @see AuthService
 * @see MainFrame
 * @since 2025
 */
public class LoginFrame extends JFrame {

    /**
     * Поле для ввода имени пользователя.
     * Поддерживает ввод буквенно-цифровых символов.
     */
    private JTextField usernameField;

    /**
     * Поле для ввода пароля с маскировкой символов.
     * Обеспечивает безопасный ввод конфиденциальных данных.
     */
    private JPasswordField passwordField;

    /**
     * Сервис аутентификации для проверки учетных данных.
     * Используется для входа и регистрации пользователей.
     */
    private AuthService authService;

    /**
     * Конструктор по умолчанию.
     * Инициализирует сервис аутентификации и создает пользовательский интерфейс.
     *
     * <p><b>Последовательность инициализации:</b>
     * <ol>
     * <li>Создание экземпляра {@link AuthService}</li>
     * <li>Инициализация графического интерфейса</li>
     * <li>Настройка обработчиков событий</li>
     * </ol>
     *
     * @see #initializeUI()
     */
    public LoginFrame() {
        authService = new AuthService();
        initializeUI();
    }

    /**
     * Инициализирует пользовательский интерфейс окна входа.
     *
     * <p><b>Настройки окна:</b>
     * <ul>
     * <li>Заголовок "Калькулятор металлурга - Вход"</li>
     * <li>Фиксированный размер 400x250 пикселей</li>
     * <li>Центрирование на экране</li>
     * <li>Запрет изменения размера</li>
     * <li>Завершение приложения при закрытии окна</li>
     * </ul>
     *
     * <p><b>Структура интерфейса:</b>
     * <ul>
     * <li>Заголовок приложения</li>
     * <li>Панель формы с полями ввода</li>
     * <li>Кнопки входа и регистрации</li>
     * </ul>
     *
     * @see BorderLayout
     * @see GridLayout
     */
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

    /**
     * Обрабатывает попытку входа пользователя в систему.
     *
     * <p><b>Процесс аутентификации:</b>
     * <ol>
     * <li>Получение введенных данных из полей формы</li>
     * <li>Проверка заполненности обязательных полей</li>
     * <li>Вызов сервиса аутентификации для проверки учетных данных</li>
     * <li>При успешной аутентификации - открытие главного окна приложения</li>
     * <li>При неудаче - отображение сообщения об ошибке</li>
     * </ol>
     *
     * <p><b>Валидация данных:</b>
     * <ul>
     * <li>Проверка что поля имени пользователя и пароля не пустые</li>
     * <li>Автоматическое удаление пробелов в начале и конце строк</li>
     * </ul>
     *
     * @see AuthService#login(String, String)
     * @see MainFrame
     *
     * @example
     * <pre>
     * {@code
     * // Пользователь вводит:
     * // Имя пользователя: "metal_user"
     * // Пароль: "secure123"
     * // При успешной проверке открывается MainFrame
     * }
     * </pre>
     */
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

    /**
     * Отображает диалоговое окно для регистрации нового пользователя.
     *
     * <p><b>Элементы диалога регистрации:</b>
     * <ul>
     * <li>Поле для ввода логина (обязательное)</li>
     * <li>Поле для ввода пароля (обязательное)</li>
     * <li>Поле для ввода email (опциональное)</li>
     * <li>Кнопки подтверждения или отмены регистрации</li>
     * </ul>
     *
     * <p><b>Процесс регистрации:</b>
     * <ol>
     * <li>Отображение диалогового окна с полями ввода</li>
     * <li>Проверка заполненности обязательных полей</li>
     * <li>Вызов сервиса регистрации для создания учетной записи</li>
     * <li>Отображение результата операции пользователю</li>
     * </ol>
     *
     * @see AuthService#register(String, String, String)
     * @see JOptionPane
     *
     * @example
     * <pre>
     * {@code
     * // Пользователь заполняет:
     * // Логин: "new_user"
     * // Пароль: "password123"
     * // Email: "user@example.com"
     * // При успешной регистрации появляется сообщение об успехе
     * }
     * </pre>
     */
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