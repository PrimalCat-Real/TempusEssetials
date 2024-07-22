package primalcat.tempusessential.StrongerDragon;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DragonEventListener {
    private final JavaPlugin plugin;
    private NamespacedKey dragonEffectKey;

    public DragonEventListener(JavaPlugin plugin) {
        this.dragonEffectKey = new NamespacedKey(plugin, "dragon_effect");
        this.plugin = plugin;
    }
    @EventHandler
    public void onDragonSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon) event.getEntity();
            dragon.getPersistentDataContainer().set(dragonEffectKey, PersistentDataType.INTEGER, 1);

            // Начинаем проверку игроков в радиусе 200 блоков
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (!dragon.isDead() && dragon.getPersistentDataContainer().has(dragonEffectKey, PersistentDataType.INTEGER)) {
                    applyEffectToPlayers(dragon.getWorld());
                }
            }, 0L, 60L); // проверка каждые 20 тиков (1 секунда)
        }
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EnderDragon) {
            entity.getPersistentDataContainer().remove(dragonEffectKey);
        }
    }

    private void applyEffectToPlayers(World world) {
        if (world.getEnvironment() == World.Environment.THE_END) {
            world.getPlayers().forEach(player -> {
                double distance = player.getLocation().distance(world.getSpawnLocation());
                if (distance <= 300 && distance >= 30) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 300, 1, true, true, true));
//                    player.setFreezeTicks(300); // 5 секунд заморозки (100 тиков)
                }

                if(distance <= 300 && distance >= 70){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 3, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 2, true, true, true));
                }
            });
        }
    }
}
