package primalcat.tempus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;

public class BoatPlace implements Listener {
    @EventHandler
    private boolean placeEvent(BlockDispenseEvent event){
        if(!event.getItem().getType().name().endsWith("BOAT")) return true;

        event.setCancelled(true);
        return false;
    }
}
