package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusessential.TempusEssential;

import java.util.Arrays;
import java.util.Objects;

public class LastHeroEnchant {
    public static void entityDamage(EntityDamageEvent event) {
        // Проверяем, что повреждение получает игрок
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        double initialDamage = event.getDamage();  // Урон до применения эффекта

        // Асинхронная задача для расчётов
        Bukkit.getScheduler().runTaskAsynchronously(TempusEssential.getPlugin(), () -> {
            double maxHealth = player.getMaxHealth();
            double currentHealth = player.getHealth();

            // Вычисляем количество недостающих сердец
            double missingHealth = (maxHealth - currentHealth) / 2;  // Переводим HP в количество сердец

            if (missingHealth <= 0) {
                return;  // Если здоровье полное, ничего не делаем
            }

            // Суммируем уровни зачарования на всех частях брони
            int totalReductionPercentage = Arrays.stream(player.getInventory().getArmorContents())
                    .filter(armorPiece -> armorPiece != null && armorPiece.hasItemMeta())
                    .map(armorPiece -> EnchantsUtils.getEnchantLevelOnItem("last_hero", armorPiece))
                    .filter(Objects::nonNull)
                    .mapToInt(enchantLevel -> enchantLevel)
                    .sum();

            // Возвращаемся в основной поток для изменения урона
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                if (totalReductionPercentage == 0) return;

                // Рассчитываем общий процент снижения урона
                double reductionMultiplier = 1 - ((totalReductionPercentage * missingHealth) / 100);

                // Ограничиваем снижение, чтобы оно не стало меньше 10% от базового урона
                reductionMultiplier = Math.max(reductionMultiplier, 0.1);

                // Новый урон после снижения
                double finalDamage = initialDamage * reductionMultiplier;

                // Логирование урона
//                Bukkit.getLogger().info(String.format("LastHeroEnchant | Игрок: %s | Базовый урон: %.2f | Урон после эффекта: %.2f | Снижение урона: %.2f%%",
//                        player.getName(), initialDamage, finalDamage, (1 - reductionMultiplier) * 100));
                // Устанавливаем новый урон
                event.setDamage(finalDamage);
            });
        });
    }
}
