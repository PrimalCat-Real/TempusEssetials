package primalcat.tempus.structures;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class CustomEndStructureGenerator implements Listener {
    private boolean meetsCriteria(Chunk chunk) {
        // Implement your criteria here
        return true;
    }

    @EventHandler
    public void onChunkGenerate(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();

        // Check if the chunk is in the End and meets your criteria
        if (world.getEnvironment() == World.Environment.THE_END && meetsCriteria(chunk)) {
            generateCustomStructure(chunk);
        }
    }

    private void generateCustomStructure(Chunk chunk) {
        // Generate your custom structure here
        // Place an end gateway portal at a specific location in the structure
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        // Check if the player is going through an end gateway
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {

            Location destination = findRandomTeleportLocation(player.getLocation());
            player.teleport(destination);

        }
        event.setCancelled(true);
        System.out.println("Teleported");

    }

    private Location findRandomTeleportLocation(Location originalLocation) {
        // Implement logic to find a random location within a 3000 block radius that is on solid ground
        return originalLocation; // Placeholder, replace with actual logic
    }
}
