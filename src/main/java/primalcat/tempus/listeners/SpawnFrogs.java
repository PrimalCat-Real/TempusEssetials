package primalcat.tempus.listeners;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRecipeBookClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static primalcat.tempus.TempusEssentials.plugin;

public class SpawnFrogs implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.STICK) {
            Location clickLocation = event.getPlayer().getLocation();
            System.out.println("spawn");
            spawnFrog(clickLocation);
        }
    }

    private void spawnFrog(Location location) {
        World world = location.getWorld();
        Phantom phantom = (Phantom) world.spawnEntity(location, EntityType.FROG);
        phantom.setCustomName("Frog");
        phantom.setCustomNameVisible(true);
        phantom.setAI(false);
        phantom.setSilent(true);
        phantom.setInvulnerable(true);
        phantom.setFireTicks(0);

        world.playSound(location, Sound.ENTITY_FROG_DEATH, 1.0f, 1.0f);

        new BukkitRunnable() {
            double height = 0.0;
            double maxHeight = 12.0;
            double speed = 0.1;
            double scale = 0.1;
            @Override
            public void run() {
                if (height >= maxHeight || !phantom.isValid()) {
                    phantom.remove();
                    explodeFirework(phantom.getLocation());
                    world.playSound(location, Sound.ENTITY_FROG_HURT, 1.0f, 1.0f);
                    cancel();
                    return;
                }
                phantom.setFireTicks(0);
                height += speed;


//                phantom.teleport(phantom.getLocation().add(0, speed, 0));
                Location newLocation = phantom.getLocation().add(0, speed, 0);
                newLocation.setYaw(0); // Set the yaw back to the stored value
                newLocation.setPitch(0); // Set the pitch back to the stored value
                phantom.teleport(newLocation);

                phantom.setSize((int) scale); // Set size of the phantom
                scale += 0.2; // Adjust this value as needed
            }
        }.runTaskTimer(plugin, 0, 1); // Run every tick
    }

    private void explodeFirework(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect effect = getRandomFireworkEffect();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);

        firework.detonate();
    }

    private FireworkEffect getRandomFireworkEffect() {
        Random random = new Random();
        FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
        Color color = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color fade = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        boolean flicker = random.nextBoolean();
        boolean trail = random.nextBoolean();
        return FireworkEffect.builder()
                .with(type)
                .withColor(color)
                .withFade(fade)
                .flicker(flicker)
                .trail(trail)
                .build();
    }
}
