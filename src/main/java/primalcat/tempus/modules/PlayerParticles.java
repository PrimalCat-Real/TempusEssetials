package primalcat.tempus.modules;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerParticles {
    private List<String> playerNames = new ArrayList<>();
    public PlayerParticles(){
        playerNames.add("Dev");
    }

    public void spawnParticlesAbovePlayer(Player player) {
        // Get player's location
        double x = player.getLocation().getX();
        double y = player.getLocation().getY() + 1.5; // Adjust height to spawn above player
        double z = player.getLocation().getZ();

        // Spawn particles above player
        player.spawnParticle(Particle.FLAME, x, y, z, 50);
//        player.spawnParticle(Particle.EXPLOSION_LARGE, x, y, z, 1, 0, 0, 0, 1, new Particle.DustOptions(Color.PURPLE, 1));

    }

    public void spawnParticlesForAllPlayers() {
        for (String playerName : playerNames) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null && player.isOnline()) {
                spawnParticlesAbovePlayer(player);
            }
        }
    }
}
