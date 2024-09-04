package primalcat.tempusessential.MultiworldJoinFix;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import primalcat.tempusessential.TempusEssential;
import primalcat.tempusessential.utils.SQLUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


// FIX desync player world on join
public class PlayerLocationListener implements Listener {

    private final File dataFolder;

    public PlayerLocationListener(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    // Событие выхода игрока
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        // Сохраняем данные об игроке асинхронно
        CompletableFuture.runAsync(() -> {
            savePlayerLocation(player.getUniqueId(), worldName, x, y, z);
        });
    }

    // Событие входа игрока
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Получаем данные о последнем положении игрока асинхронно
        CompletableFuture.runAsync(() -> {
            Location lastLocation = getPlayerLastLocation(player.getUniqueId());

            if (lastLocation != null && !player.getWorld().getName().equals(lastLocation.getWorld().getName())) {
                TempusEssential.getPlugin().getLogger().info("Player join missworld, was: " + lastLocation.getWorld().getName() + " - current: " + player.getWorld().getName());
                // Телепортируем игрока в его предыдущий мир
                Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                    player.teleport(lastLocation);
                });
            }
        });
    }

    private void savePlayerLocation(UUID playerUUID, String world, double x, double y, double z) {
        String dbPath = dataFolder + "/database.db";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = SQLUtils.connectToDB(dbPath);
            String sql = "REPLACE INTO player_locations (player_uuid, world, x, y, z) VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, world);
            pstmt.setDouble(3, x);
            pstmt.setDouble(4, y);
            pstmt.setDouble(5, z);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            Bukkit.getLogger().severe("Произошла ошибка при сохранении данных о местоположении игрока: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                Bukkit.getLogger().severe("Произошла ошибка при закрытии соединения с базой данных: " + ex.getMessage());
            }
        }
    }

    private Location getPlayerLastLocation(UUID playerUUID) {
        String dbPath = dataFolder + "/database.db";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = SQLUtils.connectToDB(dbPath);
            String sql = "SELECT world, x, y, z FROM player_locations WHERE player_uuid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playerUUID.toString());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String worldName = rs.getString("world");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");

                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    return new Location(world, x, y, z);
                }
            }

        } catch (SQLException e) {
            Bukkit.getLogger().severe("Произошла ошибка при получении данных о местоположении игрока: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                Bukkit.getLogger().severe("Произошла ошибка при закрытии соединения с базой данных: " + ex.getMessage());
            }
        }

        return null;
    }
}
