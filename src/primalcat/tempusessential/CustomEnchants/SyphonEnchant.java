package primalcat.tempusessential.CustomEnchants;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SyphonEnchant {
    public static void entityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) return;

        ItemStack mainHand = killer.getInventory().getItemInMainHand();
        Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("syphon", mainHand);
        if (enchantLevel == null) return;

        // Восстанавливаем здоровье игрока
        double healthToRestore = 2 + enchantLevel;  // Восстанавливаем 2 + уровень зачарования
        double newHealth = Math.min(killer.getHealth() + healthToRestore, killer.getMaxHealth());  // Не превышаем максимум
        killer.setHealth(newHealth);
    }
}
