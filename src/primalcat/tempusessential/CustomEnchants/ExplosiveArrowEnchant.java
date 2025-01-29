package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ExplosiveArrowEnchant {
    public static void projectileHit(ProjectileHitEvent event) {
        Entity projectile = event.getEntity();

        // Проверяем, что снаряд — стрела, выпущенная игроком
        if (!(projectile instanceof Arrow arrow)) {
            return;
        }
        if (!(arrow.getShooter() instanceof Player player)) {
            return;
        }

        // Проверяем, что игрок использует лук с зачарованием "explosive_arrow"
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("explosive_arrow", mainHand);
        if (enchantLevel == null) return;

        // Рассчитываем шанс взрыва
        int chance = 5 * enchantLevel;  // Шанс взрыва (5% * уровень зачарования)
        Random random = new Random();
        if (random.nextInt(100) >= chance) {
            return;  // Если шанс не сработал, ничего не делаем
        }

        // Возвращаемся в основной поток для создания взрыва
        Bukkit.getScheduler().runTask(primalcat.tempusessential.TempusEssential.getPlugin(), () -> {
            Location hitLocation = arrow.getLocation();
            arrow.remove();
            hitLocation.getWorld().createExplosion(hitLocation, enchantLevel, false, false);
        });
    }
}
