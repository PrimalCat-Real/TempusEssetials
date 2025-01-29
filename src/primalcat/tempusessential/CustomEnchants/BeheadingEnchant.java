package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BeheadingEnchant {
    private static final Map<EntityType, Material> mobHeads = new HashMap<>();

    static {
        // Мобы, для которых предусмотрены головы
        mobHeads.put(EntityType.ZOMBIE, Material.ZOMBIE_HEAD);
        mobHeads.put(EntityType.SKELETON, Material.SKELETON_SKULL);
        mobHeads.put(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SKULL);
        mobHeads.put(EntityType.CREEPER, Material.CREEPER_HEAD);
        mobHeads.put(EntityType.PIGLIN, Material.PIGLIN_HEAD);
        mobHeads.put(EntityType.ENDER_DRAGON, Material.DRAGON_HEAD);
    }

    public static void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Entity killer = entity.getKiller();

        // Проверяем, что убийцей является игрок
        if (!(killer instanceof Player player)) {
            return;
        }

        // Проверяем, что игрок использует зачарованный топор или трезубец
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Integer enchantLevel = EnchantsUtils.getEnchantLevelOnItem("beheading", mainHand);
        if (enchantLevel == null) return;

        // Проверяем, что у моба есть голова
        Material mobHead = mobHeads.get(entity.getType());
        if (mobHead == null) {
            return;  // Если голова не предусмотрена для этого моба, ничего не делаем
        }

        // Рассчитываем шанс выпадения головы
        int chance = 5 * enchantLevel;  // Шанс выпадения головы (5% за уровень зачарования)
        Random random = new Random();
        if (random.nextInt(100) >= chance) {
            return;  // Если шанс не сработал, ничего не делаем
        }

        // Добавляем голову в дроп
        event.getDrops().add(new ItemStack(mobHead));
    }
}
