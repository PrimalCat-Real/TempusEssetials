package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import primalcat.tempusessential.TempusEssential;

public class HarpoonEnchant {
    public static void playerFish(PlayerFishEvent event) {
        // Проверяем, что игрок тянет сущность
        if (event.getCaught() == null || !(event.getCaught() instanceof LivingEntity caughtEntity)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack fishingRod = player.getInventory().getItemInMainHand();

        // Проверяем наличие зачарования "harpoon" на удочке
        Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("harpoon", fishingRod);
        if (enchantLevel == null) return;

        // Усиление силы притягивания
        double basePullStrength = 1;  // Базовая сила притягивания
        double bonusStrength = basePullStrength * (0.3 * enchantLevel);  // Дополнительная сила в зависимости от уровня

        Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
//            FishHook hook = event.getHook();

            // Рассчитываем направление вектора от сущности к игроку
            Vector directionToPlayer = player.getLocation().toVector().subtract(caughtEntity.getLocation().toVector()).normalize();

            // Добавляем базовый вектор вверх, чтобы избежать дерганий
            directionToPlayer.setY(1);

            // Рассчитываем итоговую силу
            Vector finalVelocity = directionToPlayer.multiply(bonusStrength);

            // Применяем импульс к сущности
            caughtEntity.setVelocity(finalVelocity);

        });
    }
}
