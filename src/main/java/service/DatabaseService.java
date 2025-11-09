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

public class DatabaseService {
    private static final String URL = "jdbc:sqlite:steel_calculator.db";
    private static boolean initialized = false;

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

            // Создаем таблицы, если их нет
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

            // Добавляем марки стали
            initializeSteelGrades();

            initialized = true;
            System.out.println("База данных готова к работе!");

        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Проверяет, есть ли в таблице users колонка encrypted_password
     */
    private static boolean isTableStructureCorrect(String tableName) {
        String sql = "PRAGMA table_info(" + tableName + ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean hasEncryptedPassword = false;
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("encrypted_password".equals(columnName)) {
                    hasEncryptedPassword = true;
                }
            }
            return hasEncryptedPassword;

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке структуры таблицы: " + e.getMessage());
            return false;
        }
    }

    /**
     * Миграция таблицы users на новую структуру
     */
    private static void migrateUsersTable() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Создаем временную таблицу с правильной структурой
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    encrypted_password TEXT NOT NULL,
                    email TEXT,
                    created_date DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Если старая таблица существует, переносим данные
            if (tableExists("users")) {
                System.out.println("Перенос данных из старой таблицы users...");

                // Получаем все данные из старой таблицы
                List<User> oldUsers = getUsersFromOldTable();

                // Переносим данные в новую таблицу
                for (User user : oldUsers) {
                    // Если у пользователя был пароль в старой колонке, хешируем его
                    String encryptedPassword = PasswordUtil.hashPassword("default123"); // или другой логика миграции
                    insertUserIntoNewTable(user.getUsername(), encryptedPassword, user.getEmail());
                }

                // Удаляем старую таблицу
                stmt.execute("DROP TABLE users");
            }

            // Переименовываем новую таблицу
            stmt.execute("ALTER TABLE users_new RENAME TO users");

            System.out.println("Миграция таблицы users завершена");

        } catch (SQLException e) {
            System.err.println("Ошибка при миграции таблицы users: " + e.getMessage());
        }
    }

    /**
     * Получает пользователей из старой таблицы (если есть)
     */
    private static List<User> getUsersFromOldTable() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                // Старая таблица может иметь другую структуру, поэтому осторожно
                try {
                    user.setEncryptedPassword(rs.getString("password")); // старая колонка
                } catch (SQLException e) {
                    user.setEncryptedPassword(""); // если колонки нет
                }
                try {
                    user.setEmail(rs.getString("email"));
                } catch (SQLException e) {
                    user.setEmail("");
                }
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Старая таблица users не найдена или имеет другую структуру");
        }
        return users;
    }

    /**
     * Вставляет пользователя в новую таблицу
     */
    private static void insertUserIntoNewTable(String username, String encryptedPassword, String email) {
        String sql = "INSERT INTO users_new (username, encrypted_password, email) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, encryptedPassword);
            pstmt.setString(3, email);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при вставке пользователя: " + e.getMessage());
        }
    }

    /**
     * Проверяет существование таблицы
     */
    private static boolean tableExists(String tableName) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Принудительное обновление марок стали с правильными разделителями
     */
    public static void updateSteelGradesWithCorrectFormat() {
        System.out.println("Принудительное обновление марок стали с правильными разделителями...");

        // Сначала удаляем старые данные
        String deleteSql = "DELETE FROM steel_grades";

        // Затем добавляем с правильными разделителями (точки вместо запятых)
        String[] grades = {
                "INSERT INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('25Х2Н4МА', 0.25, 0.40, 0.28, 0.02, 0.02, 1.58, 4.30, 0.30, 0.05)",
                "INSERT INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, aluminum) VALUES ('25ХГСА', 0.20, 0.95, 1.05, 0.02, 0.02, 0.95, 0.04)",
                "INSERT INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('40ХГНМ', 0.40, 0.70, 0.25, 0.02, 0.02, 0.75, 0.85, 0.20, 0.03)",
                "INSERT INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus) VALUES ('Ст3сп', 0.14, 0.40, 0.15, 0.05, 0.04)",
                "INSERT INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus) VALUES ('35ГС', 0.32, 0.80, 0.60, 0.04, 0.035)",
                "INSERT INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus) VALUES ('35', 0.32, 0.50, 0.17, 0.04, 0.035)",
                "INSERT INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus) VALUES ('70', 0.67, 0.25, 0.17, 0.04, 0.035)"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Удаляем старые данные
            stmt.execute(deleteSql);
            System.out.println("Старые данные удалены");

            // Добавляем новые данные с правильными разделителями
            int addedCount = 0;
            for (String sql : grades) {
                try {
                    stmt.executeUpdate(sql);
                    addedCount++;
                } catch (SQLException e) {
                    System.err.println("Ошибка при добавлении марки: " + e.getMessage());
                }
            }

            System.out.println("Успешно добавлено марок стали: " + addedCount);

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении марок стали: " + e.getMessage());
        }
    }

    private static void initializeSteelGrades() {
        System.out.println("Добавление марок стали...");

        // ИСПРАВЛЕННЫЕ ДАННЫЕ: добавлены недостающие элементы и скорректированы значения
        String[] grades = {
                // 25Х2Н4МА - полный состав
                "INSERT OR IGNORE INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('25Х2Н4МА', 0.25, 0.40, 0.28, 0.02, 0.02, 1.58, 4.30, 0.30, 0.05)",

                // 25ХГСА - хромомарганцево-кремнистая сталь, нет Ni и Mo
                "INSERT OR IGNORE INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('25ХГСА', 0.20, 0.95, 1.05, 0.02, 0.02, 0.95, 0.0, 0.0, 0.04)",

                // 40ХГНМ - полный состав
                "INSERT OR IGNORE INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('40ХГНМ', 0.40, 0.70, 0.25, 0.02, 0.02, 0.75, 0.85, 0.20, 0.03)",

                // Ст3сп - обычная конструкционная сталь
                "INSERT OR IGNORE INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('Ст3сп', 0.14, 0.40, 0.15, 0.05, 0.04, 0.0, 0.0, 0.0, 0.0)",

                // 35ГС - марганцево-кремнистая
                "INSERT OR IGNORE INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('35ГС', 0.32, 0.80, 0.60, 0.04, 0.035, 0.0, 0.0, 0.0, 0.0)",

                // 35 - углеродистая сталь
                "INSERT OR IGNORE INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('35', 0.32, 0.50, 0.17, 0.04, 0.035, 0.0, 0.0, 0.0, 0.0)",

                // 70 - высокоуглеродистая сталь
                "INSERT OR IGNORE INTO steel_grades (name, carbon, manganese, silicon, sulfur, phosphorus, chromium, nickel, molybdenum, aluminum) VALUES ('70', 0.67, 0.25, 0.17, 0.04, 0.035, 0.0, 0.0, 0.0, 0.0)"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            int addedCount = 0;
            for (String sql : grades) {
                try {
                    int result = stmt.executeUpdate(sql);
                    if (result > 0) {
                        addedCount++;
                    }
                } catch (SQLException e) {
                    // Игнорируем ошибки дублирования
                    System.out.println("Марка стали уже существует: " + e.getMessage());
                }
            }
            System.out.println("Добавлено/обновлено марок стали: " + addedCount);

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении марок стали: " + e.getMessage());
        }
    }

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