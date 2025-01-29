package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusessential.TempusEssential;

public class RestorationEnchant {

    public static void itemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        // Асинхронная задача для вычисления
        Bukkit.getScheduler().runTaskAsynchronously(TempusEssential.getPlugin(), () -> {
            int currentDurability = item.getType().getMaxDurability() - item.getDurability();

            Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("restoration", item);
            if (enchantLevel == null) return;

            // Если предмет полностью сломался
            if (currentDurability > event.getDamage()) return;

            // Переходим в основной поток для изменения предмета
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                event.setCancelled(true);  // Отменяем поломку предмета

                // Восстанавливаем 50% прочности
                int restoredDurability = (int) (item.getType().getMaxDurability() * 0.2);
                item.setDurability((short) (item.getType().getMaxDurability() - restoredDurability));

                item.removeEnchantment(Enchantment.getByName("restoration"));

            });
        });
    }
}
