package primalcat.tempusessential.StrongerDragon;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import primalcat.tempusessential.CustomEntityRegistry;
import primalcat.tempusessential.TempusEssential;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DragonEventListener implements Listener {
    private final JavaPlugin plugin;
    private NamespacedKey dragonEffectKey;
    private final NamespacedKey playerCountKey;

    public DragonEventListener(JavaPlugin plugin) {
        this.dragonEffectKey = new NamespacedKey(plugin, "dragon_effect");
        this.playerCountKey = new NamespacedKey(plugin, "player_count");
        this.plugin = plugin;
    }
    private final Random random = new Random();

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        CustomEntityRegistry.replaceEnderDragonFactory();
    }

    @EventHandler
    public void onFireballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball fireball) {
            // Ваш код здесь, например:
            //
//            // Проверяем, имеет ли фаербол нужные свойства (например, имя "UltraFireball")
            if ("UltraFireball".equals(fireball.getCustomName())) {

//                System.out.println("Hit UltraFireball ground");
                event.setCancelled(true);
                fireball.setGravity(false);
                fireball.setNoPhysics(true);
                fireball.setVelocity(fireball.getVelocity().multiply(0));
                fireball.setAcceleration(new Vector(0,0,0));

            }

//            event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 4F);
        }
    }
    @EventHandler
    public void onFireballDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getDamager();

            // Проверяем имя кастомного fireball'а
            if ("UltraFireball".equals(fireball.getCustomName())) {
//                System.out.println("UltraFireball нанес удар игроку!");

                // Отменяем урон от огненного шара
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Проверяем, является ли целью Эндер-Дракон
        if (event.getEntity() instanceof EnderDragon) {
            // Ограничиваем урон до 20 единиц
            if (event.getDamage() > 20) {
                event.setDamage(20);
            }
        }
    }
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        // Проверяем, является ли сущность Эндер-Драконом
        if (event.getEntity() instanceof EnderDragon) {
            // Отменяем телепортацию через портал
            event.setCancelled(true);
        }
    }
//    @EventHandler
//    public void onFireballHit(ProjectileHitEvent event) {
//        if (event.getEntity() instanceof Fireball) {
//            Fireball fireball = (Fireball) event.getEntity();
//
//            // Проверяем, имеет ли фаербол нужные свойства (например, имя "UltraFireball")
//            if ("UltraFireball".equals(fireball.getCustomName())) {
//                // Отключаем стандартное поведение взрыва
//                if (event.getHitBlock() != null) {
//                    // Воссоздаем файрбол на месте столкновения
//                    fireball.getWorld().spawn(fireball.getLocation(), Fireball.class, fb -> {
//                        fb.setShooter(fireball.getShooter());
//                        fb.setDirection(fireball.getDirection());
//                        fb.setYield(fireball.getYield());
//                        fb.setIsIncendiary(fireball.isIncendiary());
//                    });
//
//                    // Отменяем удаление оригинального файрбола
//                    event.getEntity().remove();
//                }
//                event.setCancelled(true); // Отменяем обработку удара снаряда
//            }
//        }
//    }
//
    @EventHandler
    public void onFireballExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();

            // Проверяем, имеет ли фаербол нужные свойства (например, имя "UltraFireball")
            if ("UltraFireball".equals(fireball.getCustomName())) {
                // Отменяем взрыв фаербола
                event.setCancelled(true);
            }
        }
    }
//
//    @EventHandler
//    public void onFireballDamage(EntityDamageEvent event) {
//        if (event.getEntity() instanceof Fireball) {
//            Fireball fireball = (Fireball) event.getEntity();
//
//            // Проверяем, имеет ли фаербол нужные свойства (например, имя "UltraFireball")
//            if ("UltraFireball".equals(fireball.getCustomName())) {
//                // Запретить урон фаерболу
//                event.setCancelled(true);
//            }
//        }
//    }

//    @EventHandler
//    public void onFireballHit(ProjectileHitEvent event) {
//        // Проверяем, является ли сущность фаерболом
//        if (event.getEntity() instanceof DragonFireball) {
//            DragonFireball fireball = (DragonFireball) event.getEntity();
//
//            // Проверяем, имеет ли фаербол нужные нам свойства (например, имя)
//            if ("UltraFireball".equals(fireball.getCustomName())) {
//                // Отменяем стандартное действие при столкновении
//                event.setCancelled(true);
//                // Останавливаем движение фаербола
//                fireball.setVelocity(fireball.getVelocity().multiply(0));
//            }
//        }
//    }

//    @EventHandler
//    public void onEntityAddToWorld(EntityAddToWorldEvent event) {
//        Entity entity = event.getEntity();
//        CustomEntityRegistry.replaceEnderDragonFactory();
//        plugin.getLogger().info("Entity added to world " + entity.getType().name());
//
//
////        if (entity.getType() == EntityType.ENDER_DRAGON) {
////
////        }
////        plugin.getLogger().info("Ender dragon");
//    }

//    @EventHandler
//    public void onCreatureSpawn(EntitySpawnEvent event) {
//        Entity entity = event.getEntity();
//        CustomEntityRegistry.replaceEnderDragonFactory();
//        plugin.getLogger().info("Summon: " + entity.getType().name());
//    }
//
//    @EventHandler
//    public void onChunkLoad(ChunkLoadEvent event) {
//        Chunk chunk = event.getChunk();
//        Entity[] entities = chunk.getEntities();
//
//        // Логируем сущности в загружаемом чанке
//        for (Entity entity : entities) {
//            plugin.getLogger().info("Entity loaded: " + entity.getType().name());
//        }
//    }


//    @EventHandler
//    public void DragonKill(EntityDeathEvent event) {
//        Entity entity = event.getEntity();
//        if (entity instanceof EnderDragon) {
//            entity.getPersistentDataContainer().remove(dragonEffectKey);
//        }
//        if (entity instanceof EnderDragon && event.getEntity().getKiller() != null) {
//            if (random.nextInt(10) == 0) {
//                ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
//                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) is.getItemMeta();
//                enchantmentStorageMeta.addStoredEnchant(Enchantment.MENDING, 1, false);
//                is.setItemMeta(enchantmentStorageMeta);
//                plugin.getLogger().info(event.getEntity().getKiller() + " got mending");
//                event.getDrops().add(is);
//            }
//        }
//    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        // Запретить дракона садить в вагонетку
        if (event.getEntered() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        // Проверка, чтобы поршень не двигал дракона
        for (Entity entity : event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 2, 2, 2)) {
            if (entity instanceof EnderDragon) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onItemBurn(EntityCombustEvent event) {
        Entity entity = event.getEntity();

        // Проверяем, что сжигаемая сущность - это предмет и что мир - это The End
        if (entity instanceof Item && entity.getWorld().getEnvironment() == World.Environment.THE_END && CustomEnderDragon.isDragonAlive) {
            // Отменяем сгорание предмета
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        // Здесь можно добавить логику при выбрасывании предмета
        if (event.getItemDrop().getWorld().getEnvironment() == World.Environment.THE_END && CustomEnderDragon.isDragonAlive) {
            event.getItemDrop().setHealth(9999);
        }

    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        // Проверка, чтобы поршень не двигал дракона
        for (Entity entity : event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 2, 2, 2)) {
            if (entity instanceof EnderDragon) {
                event.setCancelled(true);
                break;
            }
        }
    }

//    @EventHandler
//    public void onItemSpawn(EntitySpawnEvent event) {
//        if (event.getEntity() instanceof Item item) {
//            // Проверяем, что событие произошло в мире "The End"
//            if (item.getWorld().getEnvironment() == World.Environment.THE_END) {
//                // Присваиваем максимальное количество здоровья предметам
//                item.setHealth(9999);
//            }
//        }
//    }

    @EventHandler
    public void onShulkerDeath(EntityDeathEvent event) {
        // Проверяем, что умершая сущность - это шалкер
        if (event.getEntity() instanceof Shulker) {
            Shulker shulker = (Shulker) event.getEntity();

            // Проверяем наличие нашего persist data
            NamespacedKey key = new NamespacedKey(TempusEssential.getPlugin(), "dragon_shulker");
            if (shulker.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                // Удаляем дроп, если persist data присутствует
                event.getDrops().clear();
            }
        }
    }



//    @EventHandler
//    public void onCreatureSpawn(CreatureSpawnEvent event) {
//        if (event.getEntity() instanceof EnderDragon) {
//            EnderDragon dragon = (EnderDragon) event.getEntity();
//
//            // Проверяем наличие метки, чтобы избежать повторной замены
//            if (!dragon.getPersistentDataContainer().has(new NamespacedKey(plugin, "custom_dragon"), PersistentDataType.BYTE)) {
//                dragon.remove();
//
//                CustomEnderDragon customDragon = new CustomEnderDragon(EntityType.ENDER_DRAGON, ((CraftWorld) dragon.getWorld()).getHandle());
//                customDragon.setPos(dragon.getLocation().getX(), dragon.getLocation().getY(), dragon.getLocation().getZ());
//
//                // Добавляем метку кастомному дракону
//                customDragon.getBukkitEntity().getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_dragon"), PersistentDataType.BYTE, (byte) 1);
//
//                ((CraftWorld) dragon.getWorld()).getHandle().addFreshEntity(customDragon);
//                Bukkit.getLogger().info("Replaced Ender Dragon with Custom Ender Dragon.");
//            }
//        }
//    }

//    @EventHandler
//    public void onChunkLoad(ChunkLoadEvent event) {
//        // Перебираем всех сущностей в загруженном чанке
//        for (org.bukkit.entity.Entity entity : event.getChunk().getEntities()) {
//            if (entity instanceof EnderDragon) {
//                EnderDragon dragon = (EnderDragon) entity;
//
//                System.out.println("dragon");
//                // Проверяем наличие метки, чтобы избежать повторной замены
//                if (!dragon.getPersistentDataContainer().has(new NamespacedKey(plugin, "custom_dragon"), PersistentDataType.BYTE)) {
//                    // Удаляем стандартного дракона
//                    dragon.remove();
//
//                    // Спавним кастомного дракона на месте стандартного
//                    CustomEnderDragon customDragon = new CustomEnderDragon(EntityType.ENDER_DRAGON, ((CraftWorld) dragon.getWorld()).getHandle());
//                    customDragon.setPos(dragon.getLocation().getX(), dragon.getLocation().getY(), dragon.getLocation().getZ());
//
//                    // Добавляем метку кастомному дракону
//                    customDragon.getBukkitEntity().getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_dragon"), PersistentDataType.BYTE, (byte) 1);
//
//                    // Добавляем кастомного дракона в мир
//                    ((CraftWorld) dragon.getWorld()).getHandle().addFreshEntity(customDragon);
//                    Bukkit.getLogger().info("Replaced Ender Dragon with Custom Ender Dragon.");
//                }
//            }
//        }
//    }

//    @EventHandler
//    public void onWorldInit(WorldInitEvent event) {
//        System.out.println("WorldInitEvent dragon");
//        if (event.getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END) {
//            try {
//                // Используем рефлексию для замены фабрики Ender Dragon
//                Field field = EntityType.ENDER_DRAGON.getClass().getDeclaredField("bF");
//                field.setAccessible(true);
//                field.set(EntityType.ENDER_DRAGON, (EntityType.EntityFactory<CustomEnderDragon>) CustomEnderDragon::new);
//
//                // Логируем успех
//                plugin.getLogger().info("Ender dragon factory replaced.");
//            } catch (Exception e) {
//                e.printStackTrace();
//                plugin.getLogger().severe("Failed to replace Ender Dragon factory.");
//            }
//        }
//    }


}
