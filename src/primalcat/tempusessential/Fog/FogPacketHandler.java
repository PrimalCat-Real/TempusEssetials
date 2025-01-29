package primalcat.tempusessential.Fog;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.biome.BiomeEffects;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypeRef;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
//import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkBiomes;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.CustomEnchants.FogWalkerEnchant;
import primalcat.tempusessential.TempusEssential;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FogPacketHandler extends PacketListenerAbstract{

    private final Plugin plugin;
    private static final int VIEW_DISTANCE = 3;
    private static final int VIEW_DISTANCE_DEFAULT = 16;

    private static int MAX_TIME_IN_ZONE;

    private static double BORDER_PERCENTAGE;
    private static HashSet<String> allowedDimensions = new HashSet<>();
    private static HashMap<UUID, UUID> playerBossBars = new HashMap<>();
    public static final HashSet<UUID> offlinePlayersInZone = new HashSet<>();
    public static String FOG_MESSAGE;

    public static HashMap<UUID, Integer> dangerZonePlayers = new HashMap<>();

    public FogPacketHandler(Plugin plugin) {
        this.plugin = plugin;
        MAX_TIME_IN_ZONE = plugin.getConfig().getInt("modules.fog-danger.max-time-in-zone", 10) * 60;
        BORDER_PERCENTAGE = plugin.getConfig().getDouble("modules.fog-danger.border-percentage", 0.2);
        allowedDimensions = new HashSet<>(plugin.getConfig().getStringList("modules.fog-danger.allowed-dimensions"));
        FOG_MESSAGE =  plugin.getConfig().getString("modules.fog-danger.fog-message", "Время до того как туман вас поглотит");
        PacketEvents.getAPI().getEventManager().registerListener(this);

//        allowedDimensions.add("world_the_end");

        new BukkitRunnable() {
            @Override
            public void run() {
                handleDangerFogPlayers();
                updatePlayerTimers();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 200L);
    }




    private void handleDangerFogPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("dangerfog.bypass")) {
                continue;
            }
            World world = player.getWorld();


            if (!allowedDimensions.contains(world.getName())) {
                continue;
            }

            // Получаем границу мира и рассчитываем пороговое расстояние
            double worldBorderSize = world.getWorldBorder().getSize() / 2; // Радиус границы
            double thresholdDistance = worldBorderSize * BORDER_PERCENTAGE; // 20% от радиуса

            Location center = world.getWorldBorder().getCenter();
            double distanceFromCenter = player.getLocation().distance(center); // Расстояние игрока от центра

            // Проверяем, если игрок дальше, чем пороговое расстояние
            if (distanceFromCenter > thresholdDistance) {
                if (!dangerZonePlayers.containsKey(player.getUniqueId())) {
                    // Добавляем игрока в список с максимальным временем
                    dangerZonePlayers.put(player.getUniqueId(), MAX_TIME_IN_ZONE);
                    plugin.getLogger().info("Player " + player.getName() + " entered the danger zone with " + MAX_TIME_IN_ZONE + " seconds remaining.");

                    sendBossBar(player, MAX_TIME_IN_ZONE, false);
                    player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_ANGRY,0.5f, 0.5f);
                }
            } else {
                // Если игрок вышел из зоны, удаляем его из списка и убираем бос-бар
                if (dangerZonePlayers.containsKey(player.getUniqueId())) {
                    dangerZonePlayers.remove(player.getUniqueId());
                    removeBossBar(player);
                    plugin.getLogger().info("Player " + player.getName() + " left the danger zone.");
                }
            }
        }
    }

    private void updatePlayerTimers() {
        for (UUID uuid : new HashSet<>(dangerZonePlayers.keySet())) {
            if (offlinePlayersInZone.contains(uuid)) {
                continue;
            }

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (FogWalkerEnchant.hasFogWalker(player)) {
                    float fogWalkerValue = FogWalkerEnchant.getSetBonus(player);
                    int randomChance = (int) (Math.random() * 100);
                    if (randomChance < fogWalkerValue) {
                        continue;
                    }
                }
            }

            int remainingTime = dangerZonePlayers.get(uuid) - 10; // Уменьшаем время на 10 секунд

            if (remainingTime <= 0) {
                // Удаляем игрока из списка, если его время истекло
                dangerZonePlayers.remove(uuid);
//                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.getLogger().info("Player " + player.getName() + " ran out of time in the danger zone and has been removed.");
                        World world = player.getWorld();
                        Location spawnLocation = world.getSpawnLocation();
                        player.setBedSpawnLocation(spawnLocation, true);
                        player.setHealth(0); // Убиваем игрока
//                        String deathMessage = player.getName() + " был поглощен туманом!";
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL,0.5f, 0.5f);
                        removeBossBar(player);
                    });
                }else {
                    offlinePlayersInZone.remove(uuid);
                }
            } else {
                dangerZonePlayers.put(uuid, remainingTime);
//                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    updateBossBar(player, remainingTime);

                    double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    double currentHealth = player.getHealth();

                    // Рассчитываем дистанцию от игрока до границы мира
                    World world = player.getWorld();
                    double worldBorderRadius = world.getWorldBorder().getSize() / 2; // Радиус границы
                    Location center = world.getWorldBorder().getCenter();
                    double distanceFromCenter = player.getLocation().distance(center);

                    double borderThreshold = worldBorderRadius * BORDER_PERCENTAGE; // Пороговая дистанция начала тумана
                    double proximityFactor = (distanceFromCenter - borderThreshold) / (worldBorderRadius - borderThreshold);
                    proximityFactor = Math.max(0, Math.min(proximityFactor, 1)); // Ограничиваем значение от 0 до 1

                    // **Новый расчет урона:**
                    // - Урон на краю границы тумана (borderThreshold) — 0.5 сердечка
                    // - Урон на границе мира — 19 единиц (оставляем 0.5 сердечка)
                    double minDamage = 1.0; // 0.5 сердечка
                    double maxDamage = maxHealth - 1.0; // 19 урона для оставления 0.5 сердечка
                    double damageAmount = minDamage + (maxDamage - minDamage) * proximityFactor;

                    // Наносим урон, только если текущее здоровье выше 0
                    if (currentHealth > 0) {
                        double finalHealth = currentHealth - damageAmount;
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            player.setHealth(Math.max(1.0, finalHealth)); // Здоровье не может быть ниже 0

                            // Воспроизведение звука
                            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.5f, 0.5f);
                        });

                    }
                }
            }
        }
    }

    public static void sendBossBar(Player player, int remainingTime, boolean force) {
        UUID existingBossBarUUID = playerBossBars.get(player.getUniqueId());

        if (!allowedDimensions.contains(player.getWorld().getName())) {
            return;
        }
        if (existingBossBarUUID != null && !force) {
            // Если у игрока уже есть бос-бар, не создаём новый
            return;
        }

        UUID bossBarUUID = UUID.randomUUID(); // Уникальный идентификатор для бос-бара

        EnumSet<BossBar.Flag> flags = EnumSet.of(
                BossBar.Flag.DARKEN_SCREEN,
                BossBar.Flag.CREATE_WORLD_FOG
        );

        // Вычисляем прогресс и текст для бос-бара
        float progress = (float) remainingTime / MAX_TIME_IN_ZONE;
        int minutesLeft = remainingTime / 60; // Оставшееся время в минутах
        Component title = Component.text(FOG_MESSAGE + minutesLeft + " мин");

        WrapperPlayServerBossBar bossBarPacket = new WrapperPlayServerBossBar(
                bossBarUUID,
                WrapperPlayServerBossBar.Action.ADD
        );
        bossBarPacket.setTitle(title);
        bossBarPacket.setHealth(progress);
        bossBarPacket.setColor(BossBar.Color.RED);
        bossBarPacket.setOverlay(BossBar.Overlay.PROGRESS);
        bossBarPacket.setFlags(flags);

        // Сохраняем бос-бар для игрока
        playerBossBars.put(player.getUniqueId(), bossBarUUID);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, bossBarPacket);
    }

    private void updateBossBar(Player player, int remainingTime) {
        UUID bossBarUUID = playerBossBars.get(player.getUniqueId());
        if (bossBarUUID == null) {
            sendBossBar(player, remainingTime, false); // Создаём бос-бар, если его нет
            return;
        }

        float progress = (float) remainingTime / MAX_TIME_IN_ZONE; // Вычисляем прогресс бос-бара
        int minutesLeft = remainingTime / 60; // Оставшееся время в минутах

        EnumSet<BossBar.Flag> flags = EnumSet.of(
                BossBar.Flag.DARKEN_SCREEN,
                BossBar.Flag.CREATE_WORLD_FOG
        );

        Component title = Component.text("Время до того как туман вас поглотит: " + minutesLeft + " мин");

        // Обновляем прогресс и текст
        WrapperPlayServerBossBar bossBarUpdatePacket = new WrapperPlayServerBossBar(
                bossBarUUID,
                WrapperPlayServerBossBar.Action.UPDATE_HEALTH
        );
        bossBarUpdatePacket.setFlags(flags);
        bossBarUpdatePacket.setHealth(progress);


        WrapperPlayServerBossBar bossBarTitleUpdatePacket = new WrapperPlayServerBossBar(
                bossBarUUID,
                WrapperPlayServerBossBar.Action.UPDATE_TITLE
        );
        bossBarTitleUpdatePacket.setTitle(title);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, bossBarUpdatePacket);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, bossBarTitleUpdatePacket);
    }

    public static void removeBossBar(Player player) {
        UUID bossBarUUID = playerBossBars.remove(player.getUniqueId());
        if (bossBarUUID == null) return;

        WrapperPlayServerBossBar bossBarRemovePacket = new WrapperPlayServerBossBar(
                bossBarUUID,
                WrapperPlayServerBossBar.Action.REMOVE
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, bossBarRemovePacket);
    }

    public static void disableFog() {
        for (UUID playerUUID : new HashSet<>(dangerZonePlayers.keySet())) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                removeBossBar(player);
            }
        }

        // Очистка всех данных
        dangerZonePlayers.clear();
        playerBossBars.clear();
        offlinePlayersInZone.clear();

        // Остановка текущего ранера
        Bukkit.getScheduler().cancelTasks(TempusEssential.getPlugin());
        TempusEssential.getPlugin().getLogger().info("FogPacketHandler disabled: All boss bars removed, and scheduler stopped.");
    }

}
