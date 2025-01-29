package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusessential.TempusEssential;

import java.util.Random;

public class AncientSeekerEnchant {

    public static void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Проверяем, что игрок не в креативе
        if (player.getGameMode() == GameMode.CREATIVE) return;

        // Проверяем, что блок не был поставлен игроком
        if (EnchantsUtils.isPlayerPlaced(block)) {
            return;  // Блок поставлен игроком, не увеличиваем шанс дропа
        }

        // Проверяем инструмент в руках
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("ancient_seeker", mainHand);
        if (enchantLevel == null) return;

        // Проверяем, что блок — древний обломок
        if (block.getType() != Material.ANCIENT_DEBRIS) {
            return;
        }

        // Сохраняем данные блока перед асинхронной задачей
        Material blockType = block.getType();

        Bukkit.getScheduler().runTaskAsynchronously(TempusEssential.getPlugin(), () -> {
            // Расчёт шанса выпадения дополнительных древних обломков
            double chance = Math.ceil(((1.0 / (enchantLevel + 2)) + ((enchantLevel + 1) / 2.0)) * 100 - 100);
            Random random = new Random();
            if (random.nextDouble() * 100 >= chance) {
                return;  // Шанс не сработал
            }

            // Возвращаемся в основной поток для изменения дропа
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(blockType, 1));  // Дополнительный дроп
            });
        });
    }
}
