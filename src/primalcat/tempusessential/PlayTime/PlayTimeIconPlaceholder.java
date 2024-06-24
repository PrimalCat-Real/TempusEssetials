package primalcat.tempusessential.PlayTime;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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

        if (lastIcons.put(player, icon) != null && !icon.equals(lastIcons.get(player))) {
            showBossBarToPlayer(player, "Получена новая награда за время: " + icon, BarColor.YELLOW, BarStyle.SOLID);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }

        return icon != null ? icon : "Нет значка";
    }

    private void showBossBarToPlayer(Player player, String title, BarColor color, BarStyle style) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        Bukkit.getScheduler().runTaskLaterAsynchronously(TempusEssential.getPlugin(), () -> {
            bossBar.removePlayer(player);
            bossBar.setVisible(false);
        }, 100L); // Display for 5 seconds
    }


}
