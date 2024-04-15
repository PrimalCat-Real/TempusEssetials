package primalcat.tempus.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getLogger;


public class DropChanceFix implements Listener {

    private final Random random = new Random();
    private final double DROP_CHANCE_TOTEM = 0.9;
    private final double DROP_CHANCE_EMERALD = 0.8;
    private final double DROP_GOLDEN_NUGGET = 0.8;
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Evoker) {
            if(random.nextDouble() < DROP_CHANCE_TOTEM){
                event.getDrops().clear();
            }
//            Iterator<ItemStack> iterator = event.getDrops().iterator();
//            if (random.nextDouble() < DROP_CHANCE_TOTEM) {
////            event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
//                while (iterator.hasNext()) {
//                    ItemStack drop = iterator.next();
////                if (drop.getType() != Material.EMERALD) { // Remove default drop
////                    iterator.remove();
////                }
//                    iterator.remove();
//                }
//            }
        }
        if (event.getEntity().getType() == EntityType.ZOMBIFIED_PIGLIN) {
            if(random.nextDouble() < DROP_GOLDEN_NUGGET){
                event.getDrops().clear();
            }
//            Iterator<ItemStack> iterator = event.getDrops().iterator();
//            while (iterator.hasNext()) {
//                ItemStack drop = iterator.next();
//                if (drop.getType() == Material.GOLD_NUGGET && random.nextDouble() < DROP_GOLDEN_NUGGET) {
//                    iterator.remove();
//                    break; // Remove only one gold nugget
//                }
//            }
        }

        if (event.getEntity().getType() == EntityType.VINDICATOR) {
            if(random.nextDouble() < DROP_CHANCE_EMERALD){
                event.getDrops().clear();
            }
//            Iterator<ItemStack> iterator = event.getDrops().iterator();
//            while (iterator.hasNext()) {
//                ItemStack drop = iterator.next();
//                if (drop.getType() == Material.GOLD_NUGGET && random.nextDouble() < DROP_GOLDEN_NUGGET) {
//                    iterator.remove();
//                    break; // Remove only one gold nugget
//                }
//            }
        }
    }
}
