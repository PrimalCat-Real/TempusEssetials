package primalcat.tempusessential.CustomEnchants;


import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class EnchantsUtils {

    public static boolean hasEcnhantOnItem(String enchantmentName, ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }


        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if (enchantment.getName().equalsIgnoreCase(enchantmentName)) {
                return true;
            }
        }

        return false;
    }

    public static Integer getEnchantLevelOnItem(String enchantmentName, ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null; // Предмет отсутствует или нет мета-данных
        }

        Enchantment enchantment = Enchantment.getByName(enchantmentName);
        if (enchantment == null) {
            return null; // Зачарование не найдено по имени
        }

        if (itemStack.getItemMeta().hasEnchant(enchantment)) {
            return itemStack.getItemMeta().getEnchantLevel(enchantment); // Возвращаем уровень зачарования
        }

        return null; // Зачарование отсутствует на предмете
    }

    public static boolean isPlayerPlaced(Block block) {
        List<MetadataValue> metadata = block.getMetadata("player_placed");
        for (MetadataValue value : metadata) {
            if (value.asBoolean()) {
                return true;  // Блок поставлен игроком
            }
        }
        return false;  // Блок естественного происхождения
    }

    /**
     * Возвращает уровень зачарования на предмете или null, если зачарования нет.
     */
    public static Integer getEnchantLevelFromHands(String enchantmentName, ItemStack mainHand, ItemStack offHand) {
        // Проверяем предмет в правой руке (приоритет)
        if (mainHand != null && mainHand.hasItemMeta()) {
            Enchantment enchantment = Enchantment.getByName(enchantmentName);
            if (enchantment != null && mainHand.getItemMeta().hasEnchant(enchantment)) {
                return mainHand.getItemMeta().getEnchantLevel(enchantment);
            }
        }

        // Если в правой руке нет зачарования, проверяем левую руку
        if (offHand != null && offHand.hasItemMeta()) {
            Enchantment enchantment = Enchantment.getByName(enchantmentName);
            if (enchantment != null && offHand.getItemMeta().hasEnchant(enchantment)) {
                return offHand.getItemMeta().getEnchantLevel(enchantment);
            }
        }

        return null;
    }
}


//@EventHandler
//public void onBlockBreak(BlockBreakEvent event) {
//    Player player = event.getPlayer();
//
//    // Убедимся, что игрок держит предмет в руке
//    ItemStack item = player.getInventory().getItemInMainHand();
//    if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasEnchant(CustomEnchant.YOUR_ENCHANT)) {
//        return;
//    }
//
//    // Проверяем, что ломается нужный блок
//    if (!LOGS.contains(event.getBlock().getType())) {
//        return;
//    }
//
//    // Выполняем логику зачарования
//    int level = item.getItemMeta().getEnchantLevel(CustomEnchant.YOUR_ENCHANT);
//    handleCustomEnchantLogic(event, player, level);
//}