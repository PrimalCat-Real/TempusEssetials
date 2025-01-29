package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusessential.TempusEssential;

import java.util.Random;

public class GeomancerEnchant {
    public static void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        var block = event.getBlock();
        var location = block.getLocation();
        var world = location.getWorld();

        // Получаем уровень зачарования "geomancer" с обеих рук (с приоритетом правой)
        Integer geomancerLevel = EnchantsUtils.getEnchantLevelFromHands(
                "geomancer",
                player.getInventory().getItemInMainHand(),
                player.getInventory().getItemInOffHand()
        );

        // Если зачарования нет, ничего не делаем
        if (geomancerLevel == null || block.getType() != Material.STONE) {
            return;
        }

        // Рассчитываем шанс дропа
        int chance = 2 * geomancerLevel;
        Random random = new Random();
        if (random.nextInt(100) >= chance) {
            return;
        }

        // Асинхронная задача для генерации дропа
        Bukkit.getScheduler().runTaskAsynchronously(TempusEssential.getPlugin(), () -> {
            Material[] drops = {
                    Material.CALCITE,
                    Material.DIORITE,
                    Material.ANDESITE,
                    Material.GRANITE,
                    Material.BLACKSTONE,
                    Material.BASALT
            };
            Material drop = drops[random.nextInt(drops.length)];

            // Возвращаемся в основной поток для работы с API Bukkit
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                event.setDropItems(false);

                if (world != null) {
                    world.dropItem(location, new ItemStack(drop));
                }
            });
        });
    }
}
