package primalcat.tempus.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.Bukkit.getLogger;

public class Util {
    public static CompletableFuture<String> formatPlaytimeAsync(int totalPlaytimeInSeconds) {
        CompletableFuture<String> hoursFuture = formatHoursAsync(totalPlaytimeInSeconds);
        CompletableFuture<String> minutesFuture = formatMinutesAsync(totalPlaytimeInSeconds);
        CompletableFuture<String> secondsFuture = formatSecondsAsync(totalPlaytimeInSeconds);

        return hoursFuture.thenCombine(minutesFuture, (hours, minutes) -> hours + " " + minutes)
                .thenCombine(secondsFuture, (hoursMinutes, seconds) -> hoursMinutes + " " + seconds);
    }

    public static CompletableFuture<String> formatHoursAsync(int totalPlaytimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            int hours = totalPlaytimeInSeconds / 3600;
            String hoursText;
            if (hours % 10 == 1 && hours % 100 != 11) {
                hoursText = "час";
            } else if (hours % 10 >= 2 && hours % 10 <= 4 && (hours % 100 < 10 || hours % 100 >= 20)) {
                hoursText = "часа";
            } else {
                hoursText = "часов";
            }
            return hours + " " + hoursText;
        });
    }

    public static CompletableFuture<String> formatMinutesAsync(int totalPlaytimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            int minutes = (totalPlaytimeInSeconds % 3600) / 60;
            String minutesText;
            if (minutes % 10 == 1 && minutes % 100 != 11) {
                minutesText = "минута";
            } else if (minutes % 10 >= 2 && minutes % 10 <= 4 && (minutes % 100 < 10 || minutes % 100 >= 20)) {
                minutesText = "минуты";
            } else {
                minutesText = "минут";
            }
            return minutes + " " + minutesText;
        });
    }

    public static CompletableFuture<String> formatSecondsAsync(int totalPlaytimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            int seconds = totalPlaytimeInSeconds % 60;
            String secondsText;
            if (seconds % 10 == 1 && seconds % 100 != 11) {
                secondsText = "секунда";
            } else if (seconds % 10 >= 2 && seconds % 10 <= 4 && (seconds % 100 < 10 || seconds % 100 >= 20)) {
                secondsText = "секунды";
            } else {
                secondsText = "секунд";
            }
            return seconds + " " + secondsText;
        });
    }

    public static Integer getHoursPlayedAsync(Player player) {
//        getLogger().info(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%"));

//        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600;
        try{
            return Math.round((float) Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000 / 3600);
        }catch (Exception exception){
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600;
        }
//        return CompletableFuture.supplyAsync(() -> player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600);
//        try{
//            return  Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 3600;
//        }catch (Exception exception) {
//            return Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 3600;
//        }
    }

    public static Integer getMinutesPlayedAsync(Player player) {
//        return CompletableFuture.supplyAsync(() -> player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60);
        try{
            return Math.round((float) Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000 / 60);
        }catch (Exception exception){
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60;
        }
    }

    public static Integer getSecondsPlayedAsync(Player player) {
        try{
            return Math.round((float) Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000);
        }catch (Exception exception){
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        }
//        return CompletableFuture.supplyAsync(() -> player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 );
    }

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


    private static Connection connectToDB(String dbPath) {
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
