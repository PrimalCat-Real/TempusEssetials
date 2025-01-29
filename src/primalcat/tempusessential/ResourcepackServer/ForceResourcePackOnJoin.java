package primalcat.tempusessential.ResourcepackServer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.RegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import primalcat.tempusessential.TempusEssential;

import java.util.*;
import java.util.logging.Logger;

public class ForceResourcePackOnJoin implements Listener {
    public static final String RESOURCE_PACK_URL = "http://proxy.dicerp.fun/resourcepack";
    private static final Set<UUID> excludedPlayers = new HashSet<>();
    private static final int PACKET_TIMEOUT = 3 * 20; // 3 seconds timeout

    private final Logger logger = TempusEssential.getPlugin().getLogger();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Проверяем, зарегистрирован ли игрок через AuthMe
        boolean isRegistered = AuthMeApi.getInstance().isRegistered(player.getName());
//        if (isRegistered) {
//            // Если не зарегистрирован, добавляем в исключения
//            excludePlayer(playerId);
//            logger.info("Player " + player.getName() + " is not registered. Resource pack will not be applied.");
//            return; // Прерываем выполнение метода
//        }

        includePlayer(playerId);
        scheduleResourcePack(player, playerId);

        // Если зарегистрирован, применяем ресурспак
        scheduleResourcePack(player, playerId);
    }

//    @EventHandler
//    public void onPlayerRegister(RegisterEvent event) {
//        Player player = Bukkit.getPlayer(event.getPlayer().getName());
//        if (player != null) {
//            UUID playerId = player.getUniqueId();
//            includePlayer(playerId);
//            scheduleResourcePack(player, playerId);
//            logger.info("Player " + player.getName() + " registered successfully. Forcing resource pack.");
//        }
//    }

    private void scheduleResourcePack(Player player, UUID playerId) {
        Bukkit.getScheduler().runTaskLater(TempusEssential.getPlugin(), () -> {
            if (!excludedPlayers.contains(playerId)) {
                player.setResourcePack(RESOURCE_PACK_URL, ResourcePackServer.getResourcePackHash());
                logger.info("Player " + player.getName() + " did not respond in time. Forcing resource pack.");
            }
        }, PACKET_TIMEOUT);
    }

    public static void excludePlayer(UUID playerId) {
        excludedPlayers.add(playerId);
    }

    public static void includePlayer(UUID playerId) {
        excludedPlayers.remove(playerId);
    }
}
