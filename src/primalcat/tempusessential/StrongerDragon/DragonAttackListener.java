package primalcat.tempusessential.StrongerDragon;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DragonAttackListener implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (damager instanceof AreaEffectCloud) {
            AreaEffectCloud cloud = (AreaEffectCloud) damager;
            if (cloud.getSource() instanceof EnderDragon) {
                // Проверка на то, что источник урона — дыхание дракона
                double originalDamage = event.getDamage();
                double newDamage = originalDamage + 2; // Увеличиваем урон на 2
                event.setDamage(newDamage);

            }
        }
    }
}
