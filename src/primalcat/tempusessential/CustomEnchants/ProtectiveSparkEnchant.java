package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ProtectiveSparkEnchant {
    public static void entityDamage(EntityDamageByEntityEvent event) {
        // Проверяем, что атакуемый — игрок
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        // Проверяем, что нападающий — живое существо
        if (!(event.getDamager() instanceof LivingEntity attacker)) {
            return;
        }

        // Проверяем наличие зачарования "protective_spark" на броне
        int totalLevel = 0;
        for (ItemStack armorPiece : victim.getInventory().getArmorContents()) {
            if (armorPiece != null) {
                Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("protective_spark", armorPiece);
                if (enchantLevel != null) {
                    totalLevel += enchantLevel;
                }
            }
        }

        if (totalLevel == 0) return;

        // Рассчитываем шанс поджога
        int chance = 10 * totalLevel;
        Random random = new Random();
        if (random.nextInt(100) >= chance) {
            return;  // Если шанс не сработал, ничего не делаем
        }

        // Поджигаем атакующего
        Bukkit.getScheduler().runTask(primalcat.tempusessential.TempusEssential.getPlugin(), () -> {
            attacker.setFireTicks(40);  // Поджог на 2 секунды
        });
    }
}
