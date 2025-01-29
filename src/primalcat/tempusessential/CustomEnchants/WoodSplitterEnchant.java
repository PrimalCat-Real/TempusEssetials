package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusessential.TempusEssential;

import java.util.*;

public class WoodSplitterEnchant {
    private static final Set<String> validMaterials = new HashSet<>();

    static {
        validMaterials.add("crimson_stem");
        validMaterials.add("spruce_wood");
        validMaterials.add("warped_stem");
        validMaterials.add("oak_log");
        validMaterials.add("dark_oak_log");
        validMaterials.add("acacia_log");
        validMaterials.add("jungle_log");
        validMaterials.add("birch_log");
        validMaterials.add("spruce_log");
        validMaterials.add("mangrove_log");
        validMaterials.add("cherry_log");
    }

    public static void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Проверяем, что игрок не в креативе
        if (player.getGameMode() == GameMode.CREATIVE) return;

        // Проверяем, что блок естественного происхождения
        if (EnchantsUtils.isPlayerPlaced(block)) {
            return;  // Если блок был поставлен игроком, ничего не делаем
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        Integer enchantLevel = EnchantsUtils.getEnchantLevelFromHands("wood_splitter", mainHand, offHand);
        if (enchantLevel == null) return;

        // Проверка типа блока (в нижнем регистре)
        Material blockType = block.getType();
        Location blockLocation = block.getLocation();  // Сохраняем координаты блока

        if (!validMaterials.contains(blockType.toString().toLowerCase())) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(TempusEssential.getPlugin(), () -> {
            int chance = 5 * enchantLevel;  // Шанс срабатывания
            Random random = new Random();
            if (random.nextInt(100) >= chance) {
                return;  // Если шанс не сработал, ничего не делаем
            }
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                blockLocation.getWorld().dropItemNaturally(blockLocation, new ItemStack(blockType, 1));
            });
        });
    }
}
