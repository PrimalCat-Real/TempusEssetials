package primalcat.tempusessential.PlayTime;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.TempusEssential;
import primalcat.tempusessential.utils.TimeUtils;

import java.util.concurrent.ConcurrentHashMap;

public class PlayTimeIconPlaceholder extends PlaceholderExpansion {
    private PlaytimeRewardsManager rewardsManager;
    private ConcurrentHashMap<Player, String> lastIcons = new ConcurrentHashMap<>();

    public PlayTimeIconPlaceholder(Plugin plugin) {
        this.rewardsManager = new PlaytimeRewardsManager(plugin);
    }

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
        return "1.0.1";
    }

//    @Override
//    public String onPlaceholderRequest(Player player, String identifier) {
//        // Предполагаем, что Util.getHoursPlayedAsync уже реализован в вашем плагине
//        int hoursPlayed = TimeUtils.getHoursPlayedAsync(player);
//        String icon = this.rewardsManager.getIconForHours(hoursPlayed);
//        return icon != null ? icon : "";
//    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        int hoursPlayed = TimeUtils.getHoursPlayedAsync(player);
        String icon = rewardsManager.getIconForHours(hoursPlayed);

//        && lastIcons.get(player) != null
//        !lastIcons.get(player).equals(icon)
        if (lastIcons.get(player) != null && !lastIcons.get(player).equals(icon)) {
            String totalHoursPlayedText = PlayTimeCommand.getCorrectForm(hoursPlayed, "час", "часа", "часов");
            String formattedMessage = player.getName() + " §7Получил награду§6 " + icon + " §7за §6" + hoursPlayed + " " + totalHoursPlayedText;
            showBossBarToAll(formattedMessage, BarColor.YELLOW, BarStyle.SEGMENTED_6, 1.0);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
//            player.sendMessage("§7Получена новая награда за время: §6" + icon);
        }
        if(icon != null){
            lastIcons.put(player, icon);
        }

        // TODO move color to config
        return icon != null ? "§6"+icon+"§r" : "";
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
                    Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                        bossBar.removeAll();
                        bossBar.setVisible(false);
                    });
                    this.cancel(); // Отменяем дальнейшее выполнение задачи
                } else {
                    // Возвращаемся к основному потоку для безопасного взаимодействия с API
                    Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                        bossBar.setProgress(progress);
                    });
                }
            }
        }.runTaskTimerAsynchronously(TempusEssential.getPlugin(), 0, 4L);
    }

    private void showBossBarToPlayer(Player player, String title, BarColor color, BarStyle style) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        Bukkit.getScheduler().runTaskLaterAsynchronously(TempusEssential.getPlugin(), () -> {
            bossBar.removePlayer(player);
            bossBar.setVisible(false);
        }, 120L); // Display for 5 seconds
    }


}
