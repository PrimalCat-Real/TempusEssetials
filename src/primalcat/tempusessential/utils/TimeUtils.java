package primalcat.tempusessential.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class TimeUtils {
    public static CompletableFuture<String> formatPlaytimeAsync(int totalPlaytimeInSeconds) {
        CompletableFuture<String> hoursFuture = formatHoursAsync(totalPlaytimeInSeconds);
        CompletableFuture<String> minutesFuture = formatMinutesAsync(totalPlaytimeInSeconds);
        CompletableFuture<String> secondsFuture = formatSecondsAsync(totalPlaytimeInSeconds);

        return hoursFuture.thenCombine(minutesFuture, (hours, minutes) -> hours + " " + minutes)
                .thenCombine(secondsFuture, (hoursMinutes, seconds) -> hoursMinutes + " " + seconds);
    }

    public static CompletableFuture<String> formatHoursAsync(int totalPlaytimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            int hours = totalPlaytimeInSeconds / 3600;
            String hoursText;
            if (hours % 10 == 1 && hours % 100 != 11) {
                hoursText = "час";
            } else if (hours % 10 >= 2 && hours % 10 <= 4 && (hours % 100 < 10 || hours % 100 >= 20)) {
                hoursText = "часа";
            } else {
                hoursText = "часов";
            }
            return hours + " " + hoursText;
        });
    }

    public static CompletableFuture<String> formatMinutesAsync(int totalPlaytimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            int minutes = (totalPlaytimeInSeconds % 3600) / 60;
            String minutesText;
            if (minutes % 10 == 1 && minutes % 100 != 11) {
                minutesText = "минута";
            } else if (minutes % 10 >= 2 && minutes % 10 <= 4 && (minutes % 100 < 10 || minutes % 100 >= 20)) {
                minutesText = "минуты";
            } else {
                minutesText = "минут";
            }
            return minutes + " " + minutesText;
        });
    }

    public static CompletableFuture<String> formatSecondsAsync(int totalPlaytimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            int seconds = totalPlaytimeInSeconds % 60;
            String secondsText;
            if (seconds % 10 == 1 && seconds % 100 != 11) {
                secondsText = "секунда";
            } else if (seconds % 10 >= 2 && seconds % 10 <= 4 && (seconds % 100 < 10 || seconds % 100 >= 20)) {
                secondsText = "секунды";
            } else {
                secondsText = "секунд";
            }
            return seconds + " " + secondsText;
        });
    }

    public static Integer getHoursPlayedAsync(Player player) {
//        try{
//            return Math.round((float) Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000 / 3600);
//        }catch (Exception exception){
//            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600;
//        }
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600;
    }

    public static Integer getMinutesPlayedAsync(Player player) {
//        try{
//            return Math.round((float) Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000 / 60);
//        }catch (Exception exception){
//            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60;
//        }
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60;
    }

    public static Integer getSecondsPlayedAsync(Player player) {
//        try{
//            return Math.round((float) Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000);
//        }catch (Exception exception){
//            return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
//        }
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
    }
}
