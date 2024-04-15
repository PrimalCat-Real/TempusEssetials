package primalcat.tempus.placeholders;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempus.TempusEssentials;
import primalcat.tempus.utils.Util;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


public class PlayTimeIconPlaceholder extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return "primalcat";
    }

    @Override
    public String getIdentifier() {
        return "playtimeicon";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

//    @Override
//    public boolean persist() {
//        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
//    }

    private final Map<Player, String> lastIcons = new ConcurrentHashMap<>();
    public static String calculateIcon(int hoursPlayed) {
        String icon;
        try {
            if (hoursPlayed >= 2 && hoursPlayed < 5) {
                icon = "🐔 "; // 2h 🐔
            } else if (hoursPlayed >= 5 && hoursPlayed < 12) {
                icon = "🌳 "; // 5h 🌳
            } else if (hoursPlayed >= 12 && hoursPlayed < 24) {
                icon = "🪓 "; // 12h 🪓
            } else if (hoursPlayed >= 24 && hoursPlayed < 48) {
                icon = "⛏ "; // 24h ⛏
            } else if (hoursPlayed >= 48 && hoursPlayed < 72) {
                icon = "🎣 "; // 48h 🎣
            } else if (hoursPlayed >= 72 && hoursPlayed < 96) {
                icon = "🗡 "; // 72h 🗡
            } else if (hoursPlayed >= 96 && hoursPlayed < 120) {
                icon = "🏹 "; // 96h 🏹
            } else if (hoursPlayed >= 120 && hoursPlayed < 168) {
                icon = "🧪 "; // 120h 🧪
            } else if (hoursPlayed >= 168 && hoursPlayed < 216) {
                icon = "☄ "; // 168h ☄
            } else if (hoursPlayed >= 216 && hoursPlayed < 288) {
                icon = "♪ "; // 216h ♪
            } else if (hoursPlayed >= 288 && hoursPlayed < 360) {
                icon = "🔱 "; // 288h 🔱
            } else if (hoursPlayed >= 360 && hoursPlayed < 576) {
                icon = "₪ "; // 360h ₪
            } else if (hoursPlayed >= 576 && hoursPlayed < 720) {
                icon = "🐲 "; // 576h 🐲
            } else if (hoursPlayed >= 720 && hoursPlayed < 1000) {
                icon = "❤ "; // 720h ❤
            } else if (hoursPlayed >= 1000) {
                icon = "☯ "; // 1000h ☯
            } else {
                icon = "";
            }
            return icon;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showBossBarToAll(String title, BarColor color, BarStyle style, double progress) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setProgress(progress);

        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        // Сделать BossBar видимым
        bossBar.setVisible(true);

        // Задержка перед удалением BossBar
        new BukkitRunnable() {
            double progress = 1.0;
            final double decrement = 1.0 / 40; // Уменьшение прогресса каждые 0.2 секунды

            @Override
            public void run() {
                progress -= decrement;
                if (progress <= 0) {
                    // Возвращаемся к основному потоку для безопасного взаимодействия с API
                    Bukkit.getScheduler().runTask(TempusEssentials.plugin, () -> {
                        bossBar.removeAll();
                        bossBar.setVisible(false);
                    });
                    this.cancel(); // Отменяем дальнейшее выполнение задачи
                } else {
                    // Возвращаемся к основному потоку для безопасного взаимодействия с API
                    Bukkit.getScheduler().runTask(TempusEssentials.plugin, () -> {
                        bossBar.setProgress(progress);
                    });
                }
            }
        }.runTaskTimerAsynchronously(TempusEssentials.plugin, 0, 4L);
    }
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        Integer hoursPlayed = Util.getHoursPlayedAsync(player);
        String icon = calculateIcon(hoursPlayed);
        if (lastIcons.containsKey(player) && !lastIcons.get(player).equals(icon)) {
            Util.formatHoursAsync(hoursPlayed * 3600) // Предполагая, что hoursPlayed - это количество часов, которое нужно преобразовать
                    .thenApply(formattedHours ->player.getDisplayName() + " §7получил награду§6 " + icon + " §7за §6" + formattedHours)
                    .thenAccept(formattedMessage -> {
                        // Используйте formattedMessage в showBossBarToAll
                        showBossBarToAll(formattedMessage, BarColor.YELLOW, BarStyle.SEGMENTED_6, 1.0);
                    });
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
//            player.sendMessage("§7Получена новая награда за время: §6" + icon);
        }
        lastIcons.put(player, icon); // Обновляем или добавляем текущую иконку игрока в Map
        return icon;
            // Return your custom placeholder value here
//        Integer hoursPlayedFuture = Util.getHoursPlayedAsync(player);
//        String icon;
//        try {
//            int hoursPlayed = hoursPlayedFuture;
//            if (hoursPlayed >= 2 && hoursPlayed < 5) {
//                icon = "🐔 "; // 2h 🐔
//            } else if (hoursPlayed >= 5 && hoursPlayed < 12) {
//                icon = "🌳 "; // 5h 🌳
//            } else if (hoursPlayed >= 12 && hoursPlayed < 24) {
//                icon = "🪓 "; // 12h 🪓
//            } else if (hoursPlayed >= 24 && hoursPlayed < 48) {
//                icon = "⛏ "; // 24h ⛏
//            } else if (hoursPlayed >= 48 && hoursPlayed < 72) {
//                icon = "🎣 "; // 48h 🎣
//            } else if (hoursPlayed >= 72 && hoursPlayed < 96) {
//                icon = "🗡 "; // 72h 🗡
//            } else if (hoursPlayed >= 96 && hoursPlayed < 120) {
//                icon = "🏹 "; // 96h 🏹
//            } else if (hoursPlayed >= 120 && hoursPlayed < 168) {
//                icon = "🧪 "; // 120h 🧪
//            } else if (hoursPlayed >= 168 && hoursPlayed < 216) {
//                icon = "☄ "; // 168h ☄
//            } else if (hoursPlayed >= 216 && hoursPlayed < 288) {
//                icon = "♪ "; // 216h ♪
//            } else if (hoursPlayed >= 288 && hoursPlayed < 360) {
//                icon = "🔱 "; // 288h 🔱
//            } else if (hoursPlayed >= 360 && hoursPlayed < 576) {
//                icon = "₪ "; // 360h ₪
//            } else if (hoursPlayed >= 576 && hoursPlayed < 720) {
//                icon = "🐲 "; // 576h 🐲
//            } else if (hoursPlayed >= 720 && hoursPlayed < 1000) {
//                icon = "❤ "; // 720h ❤
//            } else if (hoursPlayed >= 1000) {
//                icon = "☯ "; // 1000h ☯
//            } else {
//                icon = "";
//            }
//            return icon;
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
        // Placeholder is unknown
    }



    public static void registerIcon(){

    }
}
