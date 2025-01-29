package primalcat.tempusessential.CustomEnchants;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

public class FogWalkerEnchant {
    public static boolean hasFogWalker(Player player) {
        return Arrays.stream(player.getInventory().getArmorContents())
                .filter(armorPiece -> armorPiece != null && armorPiece.hasItemMeta())
                .map(armorPiece -> EnchantsUtils.getEnchantLevelOnItem("fog_walker", armorPiece))
                .anyMatch(Objects::nonNull);
    }
    public static float getSetBonus(Player player){
        int totalReductionPercentage = Arrays.stream(player.getInventory().getArmorContents())
                .filter(armorPiece -> armorPiece != null && armorPiece.hasItemMeta())
                .map(armorPiece -> EnchantsUtils.getEnchantLevelOnItem("fog_walker", armorPiece))
                .filter(Objects::nonNull)
                .mapToInt(enchantLevel -> enchantLevel)
                .sum();
        return totalReductionPercentage * 4;
    }
}
