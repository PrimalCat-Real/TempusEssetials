package primalcat.tempusessential.utils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class SQLUtils {
    public static void createDB(File dataFolder){
        try {
            // Путь к файлу базы данных
            String dbPath = dataFolder + "/database.db";

            // Проверка существования файла базы данных и создание, если не существует
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                dataFolder.mkdirs(); // Создание папок, если они не существуют
                dbFile.createNewFile(); // Создание файла базы данных
            }

            // Установка соединения с базой данных
            Connection conn = connectToDB(dbPath);

            // Создание таблицы, если она не существует
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS players " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " gameName TEXT NOT NULL, " +
                    " customName TEXT NOT NULL)";
            stmt.executeUpdate(sql);

            String sqlLocations = "CREATE TABLE IF NOT EXISTS player_locations " +
                    "(player_uuid TEXT PRIMARY KEY, " +
                    " world TEXT NOT NULL, " +
                    " x REAL NOT NULL, " +
                    " y REAL NOT NULL, " +
                    " z REAL NOT NULL)";
            stmt.executeUpdate(sqlLocations);

            // Закрытие соединения
            stmt.close();
            conn.close();
        } catch (SQLException | IOException e) {
            getLogger().severe("Произошла ошибка при создании базы данных: " + e.getMessage());
        }
    }

    public static Map<String, String> readPlayersAndCreateMap(String dbPath) {
        Map<String, String> playersMap = new HashMap<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectToDB(dbPath); // Подключение к базе данных
            String sql = "SELECT gameName, customName FROM players"; // SQL-запрос
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Чтение результатов запроса и заполнение HashMap
            while (rs.next()) {
                String gameName = rs.getString("gameName");
                String customName = rs.getString("customName");
                playersMap.put(gameName, customName);
            }
        } catch (SQLException e) {
            getLogger().severe("Произошла ошибка при чтении из базы данных: " + e.getMessage());
        } finally {
            // Закрытие ресурсов
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                getLogger().severe("Произошла ошибка при закрытии соединения с базой данных: " + ex.getMessage());
            }
        }

        return playersMap;
    }


    public static Connection connectToDB(String dbPath) {
        try {
            // Установка соединения с базой данных
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            getLogger().severe("Не удалось установить соединение с базой данных: " + e.getMessage());
            return null;
        }
    }

    public static void savePlayerData(String dbPath, String targetPlayerName, String rpNick) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Установка соединения с базой данных
            conn = connectToDB(dbPath);

            // SQL-запрос для вставки данных игрока
            String sql = "INSERT INTO players (gameName, customName) VALUES (?, ?)";

            // Подготовка SQL-запроса
            pstmt = conn.prepareStatement(sql);

            // Установка значений в SQL-запрос
            pstmt.setString(1, targetPlayerName); // Установка gameName
            pstmt.setString(2, rpNick); // Установка customName

            // Выполнение SQL-запроса
            pstmt.executeUpdate();

        } catch (SQLException e) {
            getLogger().severe("Произошла ошибка при сохранении данных игрока: " + e.getMessage());
        } finally {
            // Закрытие ресурсов
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                getLogger().severe("Произошла ошибка при закрытии соединения с базой данных: " + ex.getMessage());
            }
        }
    }
}
