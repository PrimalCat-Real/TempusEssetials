package primalcat.tempusessential.StrongerDragon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEnderCrystal;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class EnderCrystalListener implements Listener {

    // Константа для дефолтного количества здоровья энд-кристалла
    private static final int DEFAULT_CRYSTAL_HP = 3;
    private final JavaPlugin plugin;
    private final Random random = new Random();

    public EnderCrystalListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Проверяем, что поврежденный объект - это энд-кристалл
        if (event.getEntity() instanceof CraftEnderCrystal) {
            CraftEnderCrystal crystal = (CraftEnderCrystal) event.getEntity();
            Location crystalLocation = crystal.getLocation();
            World world = crystalLocation.getWorld();

            // Проверка, что мы в Энде
            if (world.getEnvironment() != World.Environment.THE_END) {
                return;
            }

            // Проверка на наличие живого Эндер-дракона в мире
            List<EnderDragon> dragons = world.getEntitiesByClass(EnderDragon.class).stream().toList();
            boolean isDragonAlive = !dragons.isEmpty();

            // Проверка, находится ли кристалл в радиусе 50 блоков от центра (0, 0)
            boolean isWithinCentralIsland = crystalLocation.distance(new Location(world, 0, crystalLocation.getY(), 0)) <= 50;

            if (isDragonAlive && isWithinCentralIsland) {
                // Проигрывание звука взрыва
                world.playSound(crystalLocation, Sound.ENTITY_ENDER_DRAGON_HURT, 1.0F, 1.0F);

                // Если у кристалла еще нет метаданных "crystalHP", назначаем их
                if (!crystal.hasMetadata("crystalHP")) {
                    int initialHP = random.nextInt(2) + 1; // Генерация случайного числа от 1 до 2
                    crystal.setMetadata("crystalHP", new FixedMetadataValue(plugin, initialHP));
                }

                int currentHP = crystal.getMetadata("crystalHP").get(0).asInt();

                if (currentHP > 1) {
                    // Уменьшаем HP на 1 и обновляем метаданные
                    crystal.setMetadata("crystalHP", new FixedMetadataValue(plugin, currentHP - 1));

                    crystal.setGlowing(true);
                    CustomEnderDragon.addToPurpleTeam(crystal.getUniqueId().toString());
                    // Отменяем разрушение кристалла, так как HP ещё осталось
                    event.setCancelled(true);
                } else {
                    // Если HP меньше или равно 1, кристалл разрушится
                    crystal.removeMetadata("crystalHP", plugin);
                    triggerEndermenAttack(event);
                }
            }
        }
    }

    private void triggerEndermenAttack(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
            Entity damager = entityDamageByEntityEvent.getDamager();

            if (damager instanceof Player) {
                Player player = (Player) damager;
                Location crystalLocation = event.getEntity().getLocation();
                World world = crystalLocation.getWorld();

                // Найти всех эндерменов в радиусе 50 блоков от сломанного кристалла
                double radius = 30.0;
                List<Entity> nearbyEntities = world.getNearbyEntities(crystalLocation, radius, radius, radius).stream().toList();

                for (Entity entity : nearbyEntities) {
                    if (entity.getType() == EntityType.ENDERMAN) {
                        Enderman enderman = (Enderman) entity;
                        enderman.setTarget(player); // Установить игрока как цель для атаки
                    }
                }

            }
        }
    }
}