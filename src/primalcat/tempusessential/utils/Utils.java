package primalcat.tempusessential.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {
    public Utils() {
    }

    public static boolean isNumeric(String string) {
        if (string == null) {
            return false;
        } else {
            try {
                Integer.parseInt(string);
                return true;
            } catch (NumberFormatException var2) {
                return false;
            }
        }
    }

    public static Integer getHoursPlayedAsync(Player player) {
        try {
            return Math.round((float)Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000.0F / 3600.0F);
        } catch (Exception var2) {
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600;
        }
    }

    public static Integer getMinutesPlayedAsync(Player player) {
        try {
            return Math.round((float)Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000.0F / 60.0F);
        } catch (Exception var2) {
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60;
        }
    }

    public static Integer getSecondsPlayedAsync(Player player) {
        try {
            return Math.round((float)Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000.0F);
        } catch (Exception var2) {
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        }
    }

    public static long getTimePlayed(Player player) {
        try {
            return (long)Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%"));
        } catch (Exception var2) {
            return (long)player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L;
        }
    }

    public static String formatTimeToSeconds(long time) {
        long seconds = getSeconds(time);
        long minutes = getMinutes(time);
        long hours = getHours(time);
        seconds %= 60L;
        minutes %= 60L;
        return String.format("%s ч. %s мин. %s сек.", hours, minutes, seconds);
    }

    public static String formatTimeToSeconds(Player player) {
        return formatTimeToSeconds(getTimePlayed(player));
    }

    public static String formatTimeToMinutes(long time) {
        long minutes = getMinutes(time);
        long hours = getHours(time);
        minutes %= 60L;
        return String.format("%s ч. %s мин.", hours, minutes);
    }

    public static String formatTimeToMinutes(Player player) {
        return formatTimeToMinutes(getTimePlayed(player));
    }

    public static String formatTimeToHours(long time) {
        return String.format("%s ч.", getHours(time));
    }

    public static String formatTimeToHours(Player player) {
        return formatTimeToHours(getTimePlayed(player));
    }

    private static long getSeconds(long time) {
        return time / 1000L;
    }

    private static long getMinutes(long time) {
        return time / 1000L / 60L;
    }

    private static long getHours(long time) {
        return time / 1000L / 3600L;
    }

    public static boolean returnMessage(CommandSender commandSender, String message, TextColor color) {
        commandSender.sendMessage(Component.text(message).color(color));
        return false;
    }
}
