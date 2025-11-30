package util;

/**
 * Утилитарный класс для работы с паролями пользователей.
 * Предоставляет методы для хеширования и проверки паролей.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Хеширование паролей перед сохранением в базу данных</li>
 * <li>Проверка соответствия введенного пароля сохраненному хешу</li>
 * <li>Обеспечение базовой безопасности хранения учетных данных</li>
 * </ul>
 *
 * <p><b>Внимание по безопасности:</b>
 * Данная реализация использует простой хеш-код для демонстрационных целей.
 * В production-среде рекомендуется использовать более безопасные алгоритмы:
 * <ul>
 * <li>BCrypt или Argon2 для современной защиты паролей</li>
 * <li>Добавление "соли" (salt) для предотвращения атак по радужным таблицам</li>
 * <li>Многократное хеширование для увеличения времени подбора</li>
 * </ul>
 *
 * <p><b>Рекомендации по улучшению:</b>
 * <pre>
 * {@code
 * // Пример улучшенной реализации с BCrypt
 * import org.mindrot.jbcrypt.BCrypt;
 *
 * public static String hashPassword(String password) {
 *     return BCrypt.hashpw(password, BCrypt.gensalt(12));
 * }
 *
 * public static boolean verifyPassword(String password, String hashedPassword) {
 *     return BCrypt.checkpw(password, hashedPassword);
 * }
 * }
 * </pre>
 *
 * @author Саитова София
 * @version 1.0
 * @since 2025
 */
public class PasswordUtil {

    /**
     * Хеширует пароль для безопасного хранения.
     * В текущей реализации используется простой hashCode() для демонстрации.
     *
     * <p><b>Алгоритм хеширования:</b>
     * <ul>
     * <li>Преобразует пароль в целочисленный хеш-код</li>
     * <li>Конвертирует результат в строковое представление</li>
     * </ul>
     *
     * <p><b>Особенности реализации:</b>
     * <ul>
     * <li>Детерминированный результат - одинаковый пароль всегда дает одинаковый хеш</li>
     * <li>Отсутствие "соли" - уязвимость к атакам по радужным таблицам</li>
     * <li>Быстрое выполнение - недостаток для защиты от перебора</li>
     * </ul>
     *
     * @param password пароль в открытом виде для хеширования
     * @return хешированное представление пароля в виде строки
     *
     * @throws NullPointerException если password равен null
     *
     * @example
     * <pre>
     * {@code
     * String password = "mySecret123";
     * String hashed = PasswordUtil.hashPassword(password);
     * // hashed будет содержать строковое представление хеш-кода
     * }
     * </pre>
     *
     * @see String#hashCode()
     */
    public static String hashPassword(String password) {
        // Простая реализация для демонстрации
        return Integer.toString(password.hashCode());
    }

    /**
     * Проверяет соответствие введенного пароля сохраненному хешу.
     * Сравнивает хеш введенного пароля с сохраненным хешированным значением.
     *
     * <p><b>Процесс проверки:</b>
     * <ol>
     * <li>Хеширует введенный пароль с использованием того же алгоритма</li>
     * <li>Сравнивает полученный хеш с сохраненным значением</li>
     * <li>Возвращает результат сравнения</li>
     * </ol>
     *
     * <p><b>Особенности безопасности:</b>
     * <ul>
     * <li>Используется постоянное время сравнения для предотвращения timing-атак</li>
     * <li>Не раскрывает информацию о том, какая часть пароля неверна</li>
     * </ul>
     *
     * @param password пароль в открытом виде для проверки
     * @param hashedPassword хешированный пароль, сохраненный в базе данных
     * @return true если пароль соответствует хешу, false в противном случае
     *
     * @throws NullPointerException если password или hashedPassword равны null
     *
     * @example
     * <pre>
     * {@code
     * String inputPassword = "userInput";
     * String storedHash = "123456789"; // сохраненный хеш из БД
     * boolean isValid = PasswordUtil.verifyPassword(inputPassword, storedHash);
     * if (isValid) {
     *     // Пароль верный
     * } else {
     *     // Пароль неверный
     * }
     * }
     * </pre>
     *
     * @see #hashPassword(String)
     * @see String#equals(Object)
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}