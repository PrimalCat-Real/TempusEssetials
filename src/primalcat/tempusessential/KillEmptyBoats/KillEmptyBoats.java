package primalcat.tempusessential.KillEmptyBoats;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.TempusEssential;

import java.util.HashMap;
import java.util.UUID;

public class KillEmptyBoats {
    private Plugin plugin = TempusEssential.getPlugin();
    private final HashMap<UUID, Long> emptyBoats = new HashMap<>();
    private static final long EMPTY_TIME_LIMIT = 300000; // 5 минут в миллисекундах

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntitiesByClass(Boat.class)) {
                        Boat boat = (Boat) entity;
                        if (boat.isEmpty()) {
                            emptyBoats.putIfAbsent(boat.getUniqueId(), System.currentTimeMillis());
                        } else {
                            emptyBoats.remove(boat.getUniqueId());
                        }
                    }

                    emptyBoats.entrySet().removeIf(entry -> {
                        if (System.currentTimeMillis() - entry.getValue() > EMPTY_TIME_LIMIT) {
                            Entity boat = Bukkit.getEntity(entry.getKey());
                            if (boat != null) {
                                boat.remove();
                            }
                            return true;
                        }
                        return false;
                    });
                }
            }
        }.runTaskTimer(plugin, 1200, 1200); // Запуск задачи каждые 60 секунд
    }
}
