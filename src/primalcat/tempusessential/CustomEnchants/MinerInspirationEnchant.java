package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import primalcat.tempusessential.TempusEssential;

import java.util.Random;

public class MinerInspirationEnchant {


    public static void bockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Проверяем, что игрок не в креативе
        if (player.getGameMode() == GameMode.CREATIVE) return;

        // Проверяем инструмент в руках
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        // Получаем уровень зачарования
        Integer enchantLevel = EnchantsUtils.getEnchantLevelFromHands("miner_inspiration", mainHand, offHand);

        if (enchantLevel == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(TempusEssential.getPlugin(), () -> {
            int chance = 5 * enchantLevel; // Шанс срабатывания
            int durationSeconds = 1 + enchantLevel; // Длительность эффекта (секунды)
            int durationTicks = durationSeconds * 20;
            int amplifier = enchantLevel - 1;

            Random random = new Random();
            if (random.nextInt(100) >= chance) {
                return;
            }

            // Возвращаемся в основной поток для применения эффекта
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, durationTicks, amplifier));
            });
        });
    }
}
