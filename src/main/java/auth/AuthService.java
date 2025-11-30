package auth;

import model.User;
import service.DatabaseService;
import util.PasswordUtil;

/**
 * Сервис аутентификации и регистрации пользователей.
 * Предоставляет методы для регистрации новых пользователей и аутентификации существующих.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Регистрация новых пользователей с валидацией данных</li>
 * <li>Аутентификация пользователей по логину и паролю</li>
 * <li>Интеграция с сервисом базы данных для хранения пользователей</li>
 * <li>Использование утилит шифрования для безопасного хранения паролей</li>
 * </ul>
 *
 * <p><b>Процесс регистрации:</b>
 * <ol>
 * <li>Валидация входных данных</li>
 * <li>Проверка уникальности имени пользователя</li>
 * <li>Хеширование пароля</li>
 * <li>Создание записи пользователя в базе данных</li>
 * </ol>
 *
 * <p><b>Процесс аутентификации:</b>
 * <ol>
 * <li>Поиск пользователя по имени</li>
 * <li>Проверка соответствия пароля</li>
 * <li>Возврат результата аутентификации</li>
 * </ol>
 *
 * @author Саитова София
 * @version 1.0
 * @see User
 * @see DatabaseService
 * @see PasswordUtil
 * @since 2025
 */
public class AuthService {

    /**
     * Регистрирует нового пользователя в системе.
     *
     * <p>Выполняет следующие проверки:
     * <ul>
     * <li>Проверяет, что имя пользователя и пароль не пустые</li>
     * <li>Проверяет уникальность имени пользователя</li>
     * <li>Хеширует пароль перед сохранением</li>
     * <li>Создает запись пользователя в базе данных</li>
     * </ul>
     *
     * @param username имя пользователя для регистрации, не может быть null или пустым
     * @param plainPassword пароль в открытом виде, не может быть null или пустым
     * @param email электронная почта пользователя, может быть null
     * @return true если регистрация прошла успешно, false в следующих случаях:
     *         <ul>
     *         <li>Имя пользователя или пароль пустые</li>
     *         <li>Пользователь с таким именем уже существует</li>
     *         <li>Ошибка при сохранении в базу данных</li>
     *         </ul>
     *
     * @throws IllegalArgumentException если username или plainPassword равны null
     *
     * @example
     * <pre>
     * {@code
     * AuthService auth = new AuthService();
     * boolean success = auth.register("john_doe", "securePassword123", "john@example.com");
     * if (success) {
     *     System.out.println("Регистрация успешна");
     * } else {
     *     System.out.println("Регистрация не удалась");
     * }
     * }
     * </pre>
     *
     * @see DatabaseService#findUserByUsername(String)
     * @see DatabaseService#createUser(User)
     * @see PasswordUtil#hashPassword(String)
     */
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

    /**
     * Выполняет аутентификацию пользователя по имени и паролю.
     *
     * <p>Процесс аутентификации включает:
     * <ul>
     * <li>Поиск пользователя в базе данных по имени</li>
     * <li>Проверку соответствия введенного пароля хешированному паролю в базе</li>
     * <li>Использование безопасного сравнения паролей для предотвращения timing-атак</li>
     * </ul>
     *
     * @param username имя пользователя для аутентификации, не может быть null или пустым
     * @param plainPassword пароль в открытом виде для проверки, не может быть null
     * @return true если аутентификация успешна, false в следующих случаях:
     *         <ul>
     *         <li>Пользователь с указанным именем не найден</li>
     *         <li>Введен неверный пароль</li>
     *         <li>Имя пользователя или пароль пустые</li>
     *         </ul>
     *
     *
     * @example
     * <pre>
     * {@code
     * AuthService auth = new AuthService();
     * boolean authenticated = auth.login("john_doe", "securePassword123");
     * if (authenticated) {
     *     System.out.println("Вход выполнен успешно");
     * } else {
     *     System.out.println("Неверное имя пользователя или пароль");
     * }
     * }
     * </pre>
     *
     * @see DatabaseService#findUserByUsername(String)
     * @see PasswordUtil#verifyPassword(String, String)
     * @see User#getEncryptedPassword()
     */
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

    /**
     * Проверяет, доступно ли имя пользователя для регистрации.
     *
     * @param username имя пользователя для проверки, не может быть null
     * @return true если имя пользователя доступно (не занято), false если занято или невалидно
     *
     * @throws IllegalArgumentException если username равен null
     */
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        User existingUser = DatabaseService.findUserByUsername(username.trim());
        return existingUser == null;
    }

    /**
     * Проверяет валидность имени пользователя согласно правилам системы.
     *
     * <p>Базовые правила валидации:
     * <ul>
     * <li>Длина от 3 до 20 символов</li>
     * <li>Может содержать только буквы, цифры и символ подчеркивания</li>
     * <li>Не может начинаться с цифры</li>
     * </ul>
     *
     * @param username имя пользователя для проверки
     * @return true если имя пользователя соответствует правилам системы
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String trimmedUsername = username.trim();
        return trimmedUsername.matches("^[a-zA-Z_][a-zA-Z0-9_]{2,19}$");
    }

    /**
     * Проверяет валидность пароля согласно политике безопасности.
     *
     * <p>Требования к паролю:
     * <ul>
     * <li>Минимальная длина 8 символов</li>
     * <li>Должен содержать хотя бы одну цифру</li>
     * <li>Должен содержать хотя бы одну букву</li>
     * <li>Может содержать специальные символы</li>
     * </ul>
     *
     * @param password пароль для проверки
     * @return true если пароль соответствует политике безопасности
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Проверка наличия хотя бы одной цифры и одной буквы
        boolean hasDigit = false;
        boolean hasLetter = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLetter(c)) {
                hasLetter = true;
            }

            if (hasDigit && hasLetter) {
                break;
            }
        }

        return hasDigit && hasLetter;
    }
}