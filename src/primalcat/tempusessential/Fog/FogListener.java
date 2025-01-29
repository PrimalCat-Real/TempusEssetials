package primalcat.tempusessential.Fog;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import primalcat.tempusessential.TempusEssential;

import java.util.UUID;

public class FogListener  implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (FogPacketHandler.dangerZonePlayers.containsKey(playerUUID)) {
            int remainingTime = FogPacketHandler.dangerZonePlayers.get(playerUUID);
            FogPacketHandler.sendBossBar(player, remainingTime, true);
            FogPacketHandler.offlinePlayersInZone.remove(playerUUID);
            TempusEssential.getPlugin().getLogger().info("Player " + player.getName() + " rejoined in the danger zone with " + remainingTime + " seconds remaining.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (FogPacketHandler.dangerZonePlayers.containsKey(playerUUID)) {
            FogPacketHandler.offlinePlayersInZone.add(playerUUID);
            TempusEssential.getPlugin().getLogger().info("Player " + player.getName() + " left the server while in the danger zone.");
        }
    }
}
