package primalcat.tempus.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Evoker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class DropChanceFix implements Listener {

    private final Random random = new Random();
    private final double DROP_CHANCE_TOTEM = 0.85;
    @EventHandler
    public void onEvokerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Evoker)) {
            return;
        }
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        if (random.nextDouble() < DROP_CHANCE_TOTEM) {
//            event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
            while (iterator.hasNext()) {
                ItemStack drop = iterator.next();
                if (drop.getType() != Material.EMERALD) { // Remove default drop
                    iterator.remove();
                }
            }
        }
    }
}
