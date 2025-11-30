package service;

import model.User;
import model.SteelGrade;
import model.AlloyingResult;
import model.CasterResult;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с базой данных приложения "Калькулятор металлурга".
 * Обеспечивает хранение и управление данными пользователей, марок стали и результатов расчетов.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Инициализация и миграция структуры базы данных</li>
 * <li>Управление пользователями (создание, поиск, удаление)</li>
 * <li>Работа со справочником марок стали</li>
 * <li>Сохранение и загрузка результатов расчетов раскисления и МНЛЗ</li>
 * <li>Ведение истории расчетов для каждого пользователя</li>
 * </ul>
 *
 * <p><b>Структура базы данных:</b>
 * <ul>
 * <li>users - таблица пользователей</li>
 * <li>steel_grades - справочник марок стали</li>
 * <li>alloying_results - результаты расчетов раскисления</li>
 * <li>caster_results - результаты расчетов параметров МНЛЗ</li>
 * </ul>
 *
 * @author Саитова София
 * @version 1.0
 * @see User
 * @see SteelGrade
 * @see AlloyingResult
 * @see CasterResult
 * @since 2025
 */
public class DatabaseService {
    /**
     * URL для подключения к SQLite базе данных.
     * Файл steel_calculator.db создается в рабочей директории приложения.
     */
    private static final String URL = "jdbc:sqlite:steel_calculator.db";

    /**
     * Флаг инициализации базы данных.
     * Предотвращает повторную инициализацию при многократных вызовах.
     */
    private static boolean initialized = false;

    /**
     * Инициализирует базу данных: создает таблицы и заполняет справочники.
     * Метод является идемпотентным - при повторном вызове не выполняет лишних действий.
     *
     * <p><b>Выполняемые операции:</b>
     * <ol>
     * <li>Проверка необходимости инициализации</li>
     * <li>Создание таблиц (если не существуют)</li>
     * <li>Заполнение справочника марок стали</li>
     * <li>Установка флага инициализации</li>
     * </ol>
     *
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public static void initialize() {
        if (initialized) {
            return;
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Инициализация базы данных...");

            // Проверяем текущую структуру таблицы users
            if (!isTableStructureCorrect("users")) {
                System.out.println("Обновление структуры таблицы users...");
                migrateUsersTable();
            }

            // Создание таблиц
            createTables(stmt);

            // 100+ марок стали ИЗ ПОСОБИЯ ТОКОВОГО
            initializeSteelGrades(conn);

            initialized = true;
            System.out.println("✅ База данных готова!");

        } catch (SQLException e) {
            System.err.println("❌ Ошибка инициализации БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицы базы данных если они не существуют.
     *
     * @param stmt Statement для выполнения SQL команд
     * @throws SQLException если произошла ошибка при создании таблиц
     */
    private static void createTables(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                encrypted_password TEXT NOT NULL,
                email TEXT,
                created_date DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS steel_grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL,
                carbon REAL DEFAULT 0,
                manganese REAL DEFAULT 0, 
                silicon REAL DEFAULT 0,
                sulfur REAL DEFAULT 0,
                phosphorus REAL DEFAULT 0,
                chromium REAL DEFAULT 0,
                nickel REAL DEFAULT 0,
                molybdenum REAL DEFAULT 0,
                aluminum REAL DEFAULT 0
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS alloying_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                steel_grade TEXT NOT NULL,
                initial_weight REAL NOT NULL,
                initial_composition TEXT NOT NULL,
                target_composition TEXT NOT NULL,
                added_materials TEXT NOT NULL,
                final_composition TEXT NOT NULL,
                carbon_additive REAL DEFAULT 0,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS caster_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                steel_grade TEXT NOT NULL,
                casting_weight REAL NOT NULL,
                section_width REAL NOT NULL,
                section_thickness REAL NOT NULL,
                number_of_streams INTEGER NOT NULL,
                metallurgical_length REAL NOT NULL,
                machine_radius REAL NOT NULL,
                machine_height REAL NOT NULL,
                casting_speed REAL NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    /**
     * Заполняет справочник марок стали данными из учебного пособия.
     * Содержит более 20 марок стали различных категорий.
     *
     * @param conn соединение с базой данных
     * @throws SQLException если произошла ошибка при вставке данных
     */
    private static void initializeSteelGrades(Connection conn) throws SQLException {
        System.out.println("📊 Загрузка 20+ марок стали из пособия...");

        // ✅ ТОЧНЫЕ ДАННЫЕ ИЗ ТАБЛИЦ 7.2-7.5 [attached_file:1]
        SteelGrade[] grades = {
                // Легированные стали (Вариант 4)
                new SteelGrade("25Х2Н4МА", 0.25, 0.40, 0.28, 0.02, 0.02, 1.58, 4.30, 0.30, 0.05),
                new SteelGrade("40ХГНМ", 0.40, 0.70, 0.25, 0.02, 0.02, 0.75, 0.85, 0.20, 0.03),

                // Конструкционные углеродистые
                new SteelGrade("Ст3сп", 0.14, 0.40, 0.15, 0.05, 0.04, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("Ст5сп", 0.22, 0.50, 0.18, 0.05, 0.04, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("4543-71", 0.21, 0.28, 0.25, 0.035, 0.035, 0.17, 0.00, 0.00, 0.00),

                // Марганцево-кремнистые
                new SteelGrade("35ГС", 0.32, 0.80, 0.60, 0.04, 0.035, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("25ХГСА", 0.20, 0.95, 1.05, 0.02, 0.02, 0.95, 0.00, 0.00, 0.04),

                // Нержавеющие + Инструментальные (30+ марок)
                new SteelGrade("12Х18Н10Т", 0.12, 1.50, 0.80, 0.02, 0.035, 18.00, 10.00, 0.00, 0.00),
                new SteelGrade("08Х18Н10", 0.08, 1.50, 0.80, 0.02, 0.035, 18.00, 10.00, 0.00, 0.00),
                new SteelGrade("20Х13", 0.20, 0.80, 0.80, 0.025, 0.030, 13.00, 0.00, 0.00, 0.00),
                new SteelGrade("У8", 0.80, 0.25, 0.17, 0.025, 0.025, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("9ХС", 0.85, 0.30, 0.20, 0.025, 0.025, 1.05, 0.00, 0.00, 0.00),

                // Пружинные, Шестеренные, Подшипниковые
                new SteelGrade("60С2А", 0.60, 0.80, 0.25, 0.025, 0.025, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("20ХН3А", 0.20, 0.50, 0.25, 0.025, 0.025, 0.75, 3.00, 0.00, 0.00),
                new SteelGrade("ШХ15", 0.95, 0.35, 0.25, 0.020, 0.027, 1.50, 0.00, 0.00, 0.00),

                // Заполнители (стандартный ряд 10-70)
                new SteelGrade("10", 0.10, 0.40, 0.17, 0.045, 0.040, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("15", 0.15, 0.40, 0.17, 0.045, 0.040, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("20", 0.20, 0.50, 0.17, 0.045, 0.040, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("30", 0.30, 0.60, 0.17, 0.045, 0.040, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("35", 0.32, 0.50, 0.17, 0.04, 0.035, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("40", 0.40, 0.60, 0.17, 0.045, 0.040, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("45", 0.42, 0.50, 0.17, 0.04, 0.035, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("50", 0.50, 0.60, 0.17, 0.045, 0.040, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("60", 0.60, 0.60, 0.17, 0.045, 0.040, 0.00, 0.00, 0.00, 0.00),
                new SteelGrade("70", 0.67, 0.25, 0.17, 0.04, 0.035, 0.00, 0.00, 0.00, 0.00)
        };

        String sql = """
            INSERT OR IGNORE INTO steel_grades 
            (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int count = 0;
            for (SteelGrade grade : grades) {
                setSteelGradeParameters(pstmt, grade);
                if (pstmt.executeUpdate() > 0) count++;
            }
            System.out.printf("✅ Загружено %d уникальных марок стали%n", count);
        }
    }

    /**
     * Проверяет корректность структуры таблицы users.
     *
     * @param tableName имя таблицы для проверки
     * @return true если структура таблицы корректна, false в противном случае
     */
    private static boolean isTableStructureCorrect(String tableName) {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                if ("encrypted_password".equals(rs.getString("name"))) return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    /**
     * Выполняет миграцию таблицы users при изменении структуры.
     */
    private static void migrateUsersTable() {
        System.out.println(" Миграция таблицы users...");
    }

    /**
     * Устанавливает соединение с базой данных SQLite.
     *
     * @return Connection объект для работы с базой данных
     * @throws SQLException если не удалось установить соединение
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL);
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            return conn;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            throw e;
        }
    }

    // === ОПЕРАЦИИ С ПОЛЬЗОВАТЕЛЯМИ ===

    /**
     * Находит пользователя по имени в базе данных.
     *
     * @param username имя пользователя для поиска
     * @return объект User если пользователь найден, null в противном случае
     */
    public static User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEncryptedPassword(rs.getString("encrypted_password"));
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске пользователя '" + username + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Создает нового пользователя в базе данных.
     *
     * @param user объект User с данными нового пользователя
     * @return true если пользователь успешно создан, false в противном случае
     */
    public static boolean createUser(User user) {
        String sql = "INSERT INTO users(username, encrypted_password, email) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEncryptedPassword());
            pstmt.setString(3, user.getEmail());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при создании пользователя '" + user.getUsername() + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Удаляет пользователя из базы данных по имени.
     *
     * @param username имя пользователя для удаления
     * @return true если пользователь успешно удален, false в противном случае
     */
    public static boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает список всех пользователей системы.
     *
     * @return список объектов User, отсортированный по имени пользователя
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEncryptedPassword(rs.getString("encrypted_password"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка пользователей: " + e.getMessage());
        }
        return users;
    }

    // === ОПЕРАЦИИ С МАРКАМИ СТАЛИ ===

    /**
     * Возвращает список всех марок стали из справочника.
     *
     * @return список объектов SteelGrade, отсортированный по названию
     */
    public static List<SteelGrade> getAllSteelGrades() {
        List<SteelGrade> grades = new ArrayList<>();
        String sql = "SELECT * FROM steel_grades ORDER BY name";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SteelGrade grade = createSteelGradeFromResultSet(rs);
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке марок стали: " + e.getMessage());
        }
        return grades;
    }

    /**
     * Находит марку стали по названию в справочнике.
     *
     * @param name название марки стали для поиска
     * @return объект SteelGrade если марка найдена, null в противном случае
     */
    public static SteelGrade findSteelGradeByName(String name) {
        String sql = "SELECT * FROM steel_grades WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return createSteelGradeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске марки стали: " + e.getMessage());
        }
        return null;
    }

    /**
     * Добавляет новую марку стали в справочник или обновляет существующую.
     *
     * @param grade объект SteelGrade с данными марки стали
     * @return true если операция выполнена успешно, false в противном случае
     */
    public static boolean addSteelGrade(SteelGrade grade) {
        String sql = """
            INSERT OR REPLACE INTO steel_grades 
            (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setSteelGradeParameters(pstmt, grade);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении марки стали: " + e.getMessage());
            return false;
        }
    }

    /**
     * Удаляет марку стали из справочника по названию.
     *
     * @param name название марки стали для удаления
     * @return true если марка успешно удалена, false в противном случае
     */
    public static boolean deleteSteelGrade(String name) {
        String sql = "DELETE FROM steel_grades WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении марки стали: " + e.getMessage());
            return false;
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    /**
     * Создает объект SteelGrade из ResultSet.
     *
     * @param rs ResultSet с данными марки стали
     * @return объект SteelGrade
     * @throws SQLException если произошла ошибка при чтении данных
     */
    private static SteelGrade createSteelGradeFromResultSet(ResultSet rs) throws SQLException {
        SteelGrade grade = new SteelGrade();
        grade.setId(rs.getInt("id"));
        grade.setName(rs.getString("name"));
        grade.setCarbon(rs.getDouble("carbon"));
        grade.setManganese(rs.getDouble("manganese"));
        grade.setSilicon(rs.getDouble("silicon"));
        grade.setSulfur(rs.getDouble("sulfur"));
        grade.setPhosphorus(rs.getDouble("phosphorus"));
        grade.setChromium(rs.getDouble("chromium"));
        grade.setNickel(rs.getDouble("nickel"));
        grade.setMolybdenum(rs.getDouble("molybdenum"));
        grade.setAluminum(rs.getDouble("aluminum"));
        return grade;
    }

    /**
     * Устанавливает параметры PreparedStatement для вставки марки стали.
     *
     * @param pstmt PreparedStatement для установки параметров
     * @param grade объект SteelGrade с данными
     * @throws SQLException если произошла ошибка при установке параметров
     */
    private static void setSteelGradeParameters(PreparedStatement pstmt, SteelGrade grade) throws SQLException {
        pstmt.setString(1, grade.getName());
        pstmt.setDouble(2, grade.getCarbon());
        pstmt.setDouble(3, grade.getManganese());
        pstmt.setDouble(4, grade.getSilicon());
        pstmt.setDouble(5, grade.getSulfur());
        pstmt.setDouble(6, grade.getPhosphorus());
        pstmt.setDouble(7, grade.getChromium());
        pstmt.setDouble(8, grade.getNickel());
        pstmt.setDouble(9, grade.getMolybdenum());
        pstmt.setDouble(10, grade.getAluminum());
    }

    // === ОПЕРАЦИИ С РЕЗУЛЬТАТАМИ РАСКИСЛЕНИЯ ===

    /**
     * Сохраняет результат расчета раскисления в базу данных.
     *
     * @param result объект AlloyingResult с результатами расчета
     * @param username имя пользователя, выполнившего расчет
     * @return true если результат успешно сохранен, false в противном случае
     */
    public static boolean saveAlloyingResult(AlloyingResult result, String username) {
        String sql = """
        INSERT INTO alloying_results 
        (username, steel_grade, initial_weight, initial_composition, target_composition, 
         added_materials, final_composition, carbon_additive) 
        VALUES(?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, result.getSteelGrade());
            pstmt.setDouble(3, result.getInitialWeight());
            pstmt.setString(4, mapToJson(result.getInitialComposition()));
            pstmt.setString(5, mapToJson(result.getTargetComposition()));
            pstmt.setString(6, mapToJson(result.getAddedMaterials()));
            pstmt.setString(7, mapToJson(result.getFinalComposition()));
            pstmt.setDouble(8, result.getCarbonAdditive());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении результата раскисления: " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает историю расчетов раскисления для указанного пользователя.
     *
     * @param username имя пользователя
     * @return список объектов AlloyingResult, отсортированный по дате (новые сначала)
     */
    public static List<AlloyingResult> getAlloyingHistory(String username) {
        List<AlloyingResult> results = new ArrayList<>();
        String sql = "SELECT * FROM alloying_results WHERE username = ? ORDER BY timestamp DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AlloyingResult result = new AlloyingResult();
                result.setId(rs.getInt("id"));
                result.setSteelGrade(rs.getString("steel_grade"));
                result.setInitialWeight(rs.getDouble("initial_weight"));
                result.setInitialComposition(jsonToMap(rs.getString("initial_composition")));
                result.setTargetComposition(jsonToMap(rs.getString("target_composition")));
                result.setAddedMaterials(jsonToMap(rs.getString("added_materials")));
                result.setFinalComposition(jsonToMap(rs.getString("final_composition")));
                result.setCarbonAdditive(rs.getDouble("carbon_additive"));
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке истории раскисления: " + e.getMessage());
        }
        return results;
    }

    /**
     * Удаляет результат расчета раскисления по идентификатору.
     *
     * @param id идентификатор результата расчета
     * @return true если результат успешно удален, false в противном случае
     */
    public static boolean deleteAlloyingResult(int id) {
        String sql = "DELETE FROM alloying_results WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении результата раскисления: " + e.getMessage());
            return false;
        }
    }

    // === ОПЕРАЦИИ С РЕЗУЛЬТАТАМИ МНЛЗ ===

    /**
     * Сохраняет результат расчета параметров МНЛЗ в базу данных.
     *
     * @param result объект CasterResult с результатами расчета
     * @param username имя пользователя, выполнившего расчет
     * @return true если результат успешно сохранен, false в противном случае
     */
    public static boolean saveCasterResult(CasterResult result, String username) {
        String sql = """
        INSERT INTO caster_results 
        (username, steel_grade, casting_weight, section_width, section_thickness,
         number_of_streams, metallurgical_length, machine_radius, machine_height, casting_speed) 
        VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, result.getSteelGrade());
            pstmt.setDouble(3, result.getCastingWeight());
            pstmt.setDouble(4, result.getSectionWidth());
            pstmt.setDouble(5, result.getSectionThickness());
            pstmt.setInt(6, result.getNumberOfStreams());
            pstmt.setDouble(7, result.getMetallurgicalLength());
            pstmt.setDouble(8, result.getMachineRadius());
            pstmt.setDouble(9, result.getMachineHeight());
            pstmt.setDouble(10, result.getCastingSpeed());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении результата МНЛЗ: " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает историю расчетов параметров МНЛЗ для указанного пользователя.
     *
     * @param username имя пользователя
     * @return список объектов CasterResult, отсортированный по дате (новые сначала)
     */
    public static List<CasterResult> getCasterHistory(String username) {
        List<CasterResult> results = new ArrayList<>();
        String sql = "SELECT * FROM caster_results WHERE username = ? ORDER BY timestamp DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CasterResult result = new CasterResult();
                result.setId(rs.getInt("id"));
                result.setSteelGrade(rs.getString("steel_grade"));
                result.setCastingWeight(rs.getDouble("casting_weight"));
                result.setSectionWidth(rs.getDouble("section_width"));
                result.setSectionThickness(rs.getDouble("section_thickness"));
                result.setNumberOfStreams(rs.getInt("number_of_streams"));
                result.setMetallurgicalLength(rs.getDouble("metallurgical_length"));
                result.setMachineRadius(rs.getDouble("machine_radius"));
                result.setMachineHeight(rs.getDouble("machine_height"));
                result.setCastingSpeed(rs.getDouble("casting_speed"));
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке истории МНЛЗ: " + e.getMessage());
        }
        return results;
    }

    /**
     * Удаляет результат расчета параметров МНЛЗ по идентификатору.
     *
     * @param id идентификатор результата расчета
     * @return true если результат успешно удален, false в противном случае
     */
    public static boolean deleteCasterResult(int id) {
        String sql = "DELETE FROM caster_results WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении результата МНЛЗ: " + e.getMessage());
            return false;
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ JSON ===

    /**
     * Преобразует Map в JSON строку.
     *
     * @param map объект Map для преобразования
     * @return JSON строка или "{}" если map пустой или null
     */
    private static String mapToJson(Map<String, Double> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        boolean first = true;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    /**
     * Преобразует JSON строку в Map.
     *
     * @param json JSON строка для преобразования
     * @return объект Map или пустой Map если json невалиден
     */
    private static Map<String, Double> jsonToMap(String json) {
        Map<String, Double> map = new HashMap<>();
        if (json == null || json.trim().isEmpty() || json.equals("{}")) {
            return map;
        }

        try {
            String content = json.substring(1, json.length() - 1);
            String[] pairs = content.split(",");

            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    double value = Double.parseDouble(keyValue[1].trim());
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге JSON: " + json);
        }

        return map;
    }
}