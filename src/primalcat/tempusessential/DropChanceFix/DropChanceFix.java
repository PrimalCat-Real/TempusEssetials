package primalcat.tempusessential.DropChanceFix;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import primalcat.tempusessential.TempusEssential;

import java.util.Map;
import java.util.Random;

public class DropChanceFix implements Listener {
    private final Random random = new Random();
    private Plugin plugin = TempusEssential.getPlugin();
    private final String CONFIG_PATH = "modules.drop-chances.mobs."; // Базовый путь к конфигурации мобов

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType type = entity.getType();
        String entityTypeName = type.name();
        String path = CONFIG_PATH + entityTypeName; // Полный путь к конфигурации для типа моба

        // Получаем конфигурацию для данного типа моба
        if (plugin.getConfig().contains(path)) {
            Map<String, Object> drops = plugin.getConfig().getConfigurationSection(path).getValues(false);


            for (String dropMaterial : drops.keySet()) {
                Material material = Material.valueOf(dropMaterial);
                double dropChance = plugin.getConfig().getDouble(path + "." + dropMaterial);



                // Учитываем зачарование Looting
                int lootingLevel = entity.getKiller() != null ? entity.getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOTING) : 0;
                dropChance += 0.01 * lootingLevel; // Увеличиваем шанс на 1% за уровень Looting

                double dropTry = random.nextDouble();
                boolean roll = dropTry > dropChance;
                if (roll) {
                    event.getDrops().removeIf(item -> item.getType() == material);
                }
            }
        }
    }
}
