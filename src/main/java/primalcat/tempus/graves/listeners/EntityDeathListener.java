package primalcat.tempus.graves.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import primalcat.tempus.graves.LocationUtil;

import java.util.ArrayList;
import java.util.List;

public class EntityDeathListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return; // Выходим из метода, если умершая сущность не является игроком
        }
        List<ItemStack> droppedItems = event.getDrops();
        if (!droppedItems.isEmpty()) {
            Location deathLocation = event.getEntity().getLocation();

            for (ItemStack itemStack : droppedItems) {
                Item item = event.getEntity().getWorld().dropItem(deathLocation, itemStack);
                item.setVelocity(new Vector(0, 0, 0));
            }
            droppedItems.clear();
        }
    }

}
