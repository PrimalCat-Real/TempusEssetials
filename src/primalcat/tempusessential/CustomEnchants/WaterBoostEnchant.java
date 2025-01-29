package primalcat.tempusessential.CustomEnchants;

import net.minecraft.world.entity.projectile.ThrownTrident;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class WaterBoostEnchant {
    public static void tridentThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownTrident trident)) {
            return;
        }
        if (!(trident.getOwner() instanceof Player player)) {
            return;
        }

        if (!player.isInWater()) {
            return;
        }
        double damageMultiplier =  1.0 + (0.2 * EnchantsUtils.getEnchantLevelOnItem("aquatic_power", trident.pickupItemStack.asBukkitCopy()));
        trident.setBaseDamage(trident.getBaseDamage() * damageMultiplier);
    }
}
