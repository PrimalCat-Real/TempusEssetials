package primalcat.tempusessential.Corpes;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import primalcat.tempusessential.TempusEssential;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CorpsePool implements Listener {

    private static final Random RANDOM = new Random();
    private static CorpsePool instance;
    private final Plugin plugin;

    //config options
    private final double spawnDistance;
    private final int timeRemove;
    private final boolean onDeath;
    private final boolean showTags;
    private final boolean renderArmor;


    private final Map<Integer, Corpse> corpseMap = new ConcurrentHashMap<>();

    private BukkitTask tickTask;

    @ApiStatus.Internal
    public CorpsePool() {
        this.plugin = TempusEssential.getPlugin();

        FileConfiguration config = plugin.getConfig();
        this.spawnDistance = Math.pow(20, 2);
        this.timeRemove = 1000;
        this.onDeath = true;
        this.showTags = false;
        this.renderArmor = false;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.corpseTick();
    }

    @NotNull
    public static synchronized CorpsePool getInstance() {
        if (instance == null) {
            instance = new CorpsePool();
        }
        return instance;
    }

    public boolean existCorpseWithName(@NotNull String name) {
        for(Corpse c: corpseMap.values()) {
            if(c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void corpseTick() {
        tickTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            for (Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
                for (Corpse corpse : this.corpseMap.values()) {
                    Location holoLoc = corpse.getLocation();
                    Location playerLoc = player.getLocation();
                    boolean isShown = corpse.isShownFor(player);

                    if (!holoLoc.getWorld().equals(playerLoc.getWorld())) {
                        if (isShown) {
                            corpse.hide(player);
                        }
                        continue;
                    } else if (!holoLoc.getWorld()
                            .isChunkLoaded(holoLoc.getBlockX() >> 4, holoLoc.getBlockZ() >> 4) && isShown) {
                        corpse.hide(player);
                        continue;
                    }
                    boolean inRange = holoLoc.distanceSquared(playerLoc) <= this.spawnDistance;

                    if (!inRange && isShown) {
                        corpse.hide(player);
                    } else if (inRange && !isShown) {
                        corpse.show(player);
                    }
                }
            }
        }, 20, 2);
    }

    @NotNull
    public Optional<Corpse> getCorpse(int entityId) {
        return Optional.ofNullable(this.corpseMap.get(entityId));
    }

    @NotNull
    public Optional<Corpse> getCorpse(String name) {
        return this.getCorpses()
                .stream()
                .filter(corpse -> corpse.getName().equals(name))
                .findFirst();
    }

    public void remove(int entityId) {
        this.getCorpse(entityId).ifPresent(corpse -> {
            this.corpseMap.remove(entityId);
            corpse.getSeeingPlayers()
                    .forEach(corpse::hide);
        });
    }

    public int getFreeEntityId() {
        int id;

        do {
            id = RANDOM.nextInt(Integer.MAX_VALUE);
        } while (this.corpseMap.containsKey(id));

        return id;
    }

    @NotNull
    public Collection<Corpse> getCorpses() {
        return Collections.unmodifiableCollection(this.corpseMap.values());
    }

    public void takeCareOf(@NotNull Corpse corpse) {
        // Prevent two corpses with same name and showTags is enabled
        if (this.showTags) {
            this.getCorpse(corpse.getName()).ifPresent(c -> {
                this.corpseMap.remove(c.getId());
                c.getSeeingPlayers()
                        .forEach(c::hide);
            });
        }
        this.corpseMap.put(corpse.getId(), corpse);
    }

    public int getTimeRemove() {
        return timeRemove;
    }

    public boolean isRenderArmor() {
        return renderArmor;
    }

    public boolean isShowTags() {
        return showTags;
    }

    @Nullable
    public BukkitTask getTickTask() {
        return tickTask;
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.corpseMap.values().stream()
                .filter(corpse -> corpse.isShownFor(player))
                .forEach(corpse -> corpse.hide(player));
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        this.corpseMap.values().stream()
                .filter(corpse -> corpse.isShownFor(player))
                .forEach(corpse -> corpse.hide(player));
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        //Fix player death message disappear
//        event.setDeathMessage(null);
        if (onDeath) {
            new Corpse(event.getEntity());
        }
    }


}
