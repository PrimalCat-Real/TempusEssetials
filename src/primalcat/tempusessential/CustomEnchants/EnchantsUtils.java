package primalcat.tempusessential.CustomEnchants;

import com.willfp.ecoenchants.enchant.EcoEnchantLike;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantsUtils {

    public static boolean hasEcnhantOnItem(String enchantmentName, ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        // Преобразуем имя чара в верхний регистр для сравнения
        String normalizedEnchantmentName = enchantmentName.toUpperCase();

        // Проверяем, есть ли на предмете зачарования с заданным именем
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if (enchantment.getName().equalsIgnoreCase(normalizedEnchantmentName)) {
                return true;
            }
        }

        return false;
    }
}
