package primalcat.tempusessential.MultiworldJoinFix;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primalcat.tempusessential.TempusEssential;
import primalcat.tempusessential.utils.SQLUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SaveAllPlayerLocationsCommand implements CommandExecutor {

    private final File dataFolder;

    public SaveAllPlayerLocationsCommand(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tempus.saveall")) {
            sender.sendMessage("У вас нет прав для выполнения этой команды.");
            return true;
        }

        sender.sendMessage("Начинается сохранение всех местоположений игроков...");

        // Асинхронно сохраняем все местоположения
        CompletableFuture.runAsync(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                savePlayerLocation(player);
            }

            // После завершения сохраняем данные
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                sender.sendMessage("Местоположения всех игроков успешно сохранены.");
            });
        });

        return true;
    }

    // Метод для сохранения местоположения одного игрока
    private void savePlayerLocation(Player player) {
        UUID playerUUID = player.getUniqueId();
        Location location = player.getLocation();
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        String dbPath = dataFolder + "/database.db";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = SQLUtils.connectToDB(dbPath);
            String sql = "REPLACE INTO player_locations (player_uuid, world, x, y, z) VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, worldName);
            pstmt.setDouble(3, x);
            pstmt.setDouble(4, y);
            pstmt.setDouble(5, z);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            Bukkit.getLogger().severe("Произошла ошибка при сохранении местоположения игрока: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                Bukkit.getLogger().severe("Произошла ошибка при закрытии соединения с базой данных: " + ex.getMessage());
            }
        }
    }
}
