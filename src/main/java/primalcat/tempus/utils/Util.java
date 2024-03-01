package primalcat.tempus.utils;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class Util {
    public static CompletableFuture<String> formatPlaytimeAsync(int totalPlaytimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            int hours = totalPlaytimeInSeconds / 3600;
            int minutes = ((totalPlaytimeInSeconds) % 3600) / 60;
            int seconds = (totalPlaytimeInSeconds) % 60;
            String hoursText;
            if (hours % 10 == 1 && hours % 100 != 11) {
                hoursText = "час";
            } else if (hours % 10 >= 2 && hours % 10 <= 4 && (hours % 100 < 10 || hours % 100 >= 20)) {
                hoursText = "часа";
            } else {
                hoursText = "часов";
            }

            String minutesText;
            if (minutes % 10 == 1 && minutes % 100 != 11) {
                minutesText = "минута";
            } else if (minutes % 10 >= 2 && minutes % 10 <= 4 && (minutes % 100 < 10 || minutes % 100 >= 20)) {
                minutesText = "минуты";
            } else {
                minutesText = "минут";
            }

            String secondsText;
            if (seconds % 10 == 1 && seconds % 100 != 11) {
                secondsText = "секунда";
            } else if (seconds % 10 >= 2 && seconds % 10 <= 4 && (seconds % 100 < 10 || seconds % 100 >= 20)) {
                secondsText = "секунды";
            } else {
                secondsText = "секунд";
            }

            return String.format("%d "+hoursText+" %d "+ minutesText+" %d "+ secondsText, hours, minutes, seconds);
        });
    }

    public static CompletableFuture<Integer> getHoursPlayedAsync(Player player) {
        return CompletableFuture.supplyAsync(() -> player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 3600);
    }

    public static CompletableFuture<Integer> getMinutesPlayedAsync(Player player) {
        return CompletableFuture.supplyAsync(() -> player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60);
    }

    public static CompletableFuture<Integer> getSecondsPlayedAsync(Player player) {
        return CompletableFuture.supplyAsync(() -> player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20);
    }
}
