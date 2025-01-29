package primalcat.tempusessential.CustomMobDrops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import primalcat.tempusessential.TempusEssential;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MobDrops implements Listener {
    private FileConfiguration config = TempusEssential.getPlugin().getConfig();
    private final Map<EntityType, MobConfig> mobConfigMap = new HashMap<>();

    public MobDrops() {
        // Инициализация при загрузке конфигурации
        FileConfiguration config = TempusEssential.getPlugin().getConfig();
        for (String key : config.getConfigurationSection("modules.custom-drops.loots.mobs").getKeys(false)) {
            String path = "modules.custom-drops.loots.mobs." + key;
            EntityType entityType = EntityType.valueOf(config.getString(path + ".type").toUpperCase());
            boolean ignoreSpawner = config.getBoolean(path + ".ignore_spawner");
            String itemId = config.getString(path + ".items.shard_drop.item");
            int minAmount = config.getInt(path + ".items.shard_drop.min_amount");
            int maxAmount = config.getInt(path + ".items.shard_drop.max_amount");
            double chance = config.getDouble(path + ".items.shard_drop.chance");

            mobConfigMap.put(entityType, new MobConfig(ignoreSpawner, itemId, minAmount, maxAmount, chance));
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = event.getEntity();
        EntityType entityType = entity.getType();

        if (!mobConfigMap.containsKey(entityType)) {
            return;
        }

        MobConfig mobConfig = mobConfigMap.get(entityType);

        // Проверяем, был ли моб заспавнен спавнером
        boolean spawnedFromSpawner = entity.fromMobSpawner();
        if (!mobConfig.ignoreSpawner && spawnedFromSpawner) {
            return;
        }

        Random random = new Random();
        if (random.nextDouble() * 100 <= mobConfig.chance) {
            int amount = mobConfig.minAmount + random.nextInt(mobConfig.maxAmount - mobConfig.minAmount + 1);
            ItemStack customItem = createCustomItem(mobConfig.itemId, amount);

            if (customItem != null) {
                entity.getWorld().dropItemNaturally(entity.getLocation(), customItem);
            }
        }
    }


    public static final NamespacedKey oraxenKey = new NamespacedKey("oraxen", "id");

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && item.hasItemMeta()) {
                PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
                if (dataContainer.has(oraxenKey, PersistentDataType.STRING) && "mob_shard".equals(dataContainer.get(oraxenKey, PersistentDataType.STRING))) {
                    event.getInventory().setResult(null); // Блокируем крафт
                    return;
                }
            }
        }
    }

    public static ItemStack createCustomItem(String itemId, int amount) {
        if ("dicerp:mobshard".equalsIgnoreCase(itemId)) {
            ItemStack shard = new ItemStack(Material.AMETHYST_SHARD, amount);
            ItemMeta meta = shard.getItemMeta();

            if (meta != null) {
                // Создаем компонент текста с градиентом
                String text = "<gradient:#42e9f5:#F39DFF><bold><italic:false>Шард</italic:false></bold></gradient>";

                // Конвертация MiniMessage в компонент
                Component displayName = MiniMessage.miniMessage().deserialize(text);

//                meta.displayName(displayName);

                meta.setCustomModelData(1001003);
                NamespacedKey oraxenKey = new NamespacedKey("oraxen", "id");
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                dataContainer.set(oraxenKey, PersistentDataType.STRING, "mob_shard");

                shard.setItemMeta(meta);
            }
            return shard;
        }
        return null;
    }

    private static class MobConfig {
        boolean ignoreSpawner;
        String itemId;
        int minAmount;
        int maxAmount;
        double chance;

        public MobConfig(boolean ignoreSpawner, String itemId, int minAmount, int maxAmount, double chance) {
            this.ignoreSpawner = ignoreSpawner;
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
        }
    }
}
