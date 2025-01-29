package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import primalcat.tempusessential.TempusEssential;

import java.util.Random;

public class EndlessQuiverEnchant {

    public static void projectileLaunch(ProjectileLaunchEvent event) {
        // Проверяем, что снаряд выпустил игрок
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (!(shooter instanceof Player player)) {
            return;
        }

        ItemStack crossbow = player.getInventory().getItemInMainHand();

        // Проверяем, что игрок использует арбалет
        if (crossbow == null || crossbow.getType() != Material.CROSSBOW) {
            return;
        }

        // Проверяем наличие зачарования "endless_quiver"
        Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("endless_quiver", crossbow);
        if (enchantLevel == null) return;

        // Проверяем, что выпущен снаряд типа "стрела"
        if (!(projectile instanceof AbstractArrow arrow)) {
            return;
        }

        // Устанавливаем бесконечность — стрелу нельзя будет подобрать


        // Проверяем шанс срабатывания
        int chance = 10 * enchantLevel;
        Random random = new Random();
        if (random.nextInt(100) >= chance) {
            return;
        }

        // Перезаряжаем арбалет стрелой, которая была выпущена
        ItemStack arrowItem = createArrowItem(projectile);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        if (arrowItem == null) return;

        Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
            if (crossbow.getItemMeta() instanceof CrossbowMeta crossbowMeta) {
                crossbowMeta.addChargedProjectile(arrowItem);  // Заряжаем арбалет копией выпущенной стрелы
                crossbow.setItemMeta(crossbowMeta);

            }
        });
    }

    // Создаёт ItemStack стрелы из Projectile
    private static ItemStack createArrowItem(Projectile projectile) {
        if (projectile instanceof Arrow tippedArrow) {
            // Создаём стрелу с эффектами (TIPPED_ARROW)
            ItemStack arrowItem = new ItemStack(Material.TIPPED_ARROW);
            var meta = (org.bukkit.inventory.meta.PotionMeta) arrowItem.getItemMeta();

            // Устанавливаем базовый эффект (если он есть)
            if (tippedArrow.hasCustomEffects()) {
                tippedArrow.getCustomEffects().forEach(effect -> meta.addCustomEffect(effect, true));
            }

            // Устанавливаем цвет стрелы, если задан (для красоты)
            if (tippedArrow.getColor() != null) {
                meta.setColor(tippedArrow.getColor());
            }

            arrowItem.setItemMeta(meta);
            return arrowItem;
        }
        if (projectile.getType().toString().equalsIgnoreCase("SPECTRAL_ARROW")) {
            return new ItemStack(Material.SPECTRAL_ARROW);  // Спектральная стрела
        }
        return null;  // Неподдерживаемый тип
    }
}
