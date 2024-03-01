package primalcat.tempus.commands;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primalcat.tempus.utils.Util;

import java.util.concurrent.CompletableFuture;

import static primalcat.tempus.utils.Util.*;

public class PlaytimeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tempusessentials.playtime")) {
            return true;
        }

        int totalPlaytimeInSeconds = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;

        sender.sendMessage("§с===================================================");
        CompletableFuture<String> formattedPlaytimeFuture = Util.formatPlaytimeAsync(totalPlaytimeInSeconds);
        formattedPlaytimeFuture.thenAccept(formattedPlaytime -> {
            // Use the formatted playtime string
            player.sendMessage("§7Проведено в игре: §6" + formattedPlaytime);
        });

        CompletableFuture<Integer> hoursPlayedFuture = Util.getHoursPlayedAsync(player);
        CompletableFuture<Integer> minutesPlayedFuture = Util.getMinutesPlayedAsync(player);
        CompletableFuture<Integer> secondsPlayedFuture = Util.getSecondsPlayedAsync(player);

        CompletableFuture.allOf(hoursPlayedFuture, minutesPlayedFuture, secondsPlayedFuture).thenRun(() -> {
            int hoursPlayed = hoursPlayedFuture.join(); // .join() to get the result
            int minutesPlayed = minutesPlayedFuture.join();
            int secondsPlayed = secondsPlayedFuture.join();

            int nextMilestoneHours = -1;
            String nextMilestoneIcon = "";
            if (hoursPlayed < 2) {
                nextMilestoneHours = 2;
                nextMilestoneIcon = "🐔"; // 2h 🐔
            } else if (hoursPlayed < 5) {
                nextMilestoneHours = 5;
                nextMilestoneIcon = "🌳"; // 5h 🌳
            } else if (hoursPlayed < 12) {
                nextMilestoneHours = 12;
                nextMilestoneIcon = "🪓"; // 12h 🪓
            } else if (hoursPlayed < 24) {
                nextMilestoneHours = 24;
                nextMilestoneIcon = "⛏"; // 24h ⛏
            } else if (hoursPlayed < 48) {
                nextMilestoneHours = 48;
                nextMilestoneIcon = "🎣"; // 48h 🎣
            } else if (hoursPlayed < 72) {
                nextMilestoneHours = 72;
                nextMilestoneIcon = "🗡"; // 72h 🗡
            } else if (hoursPlayed < 96) {
                nextMilestoneHours = 96;
                nextMilestoneIcon = "🏹"; // 96h 🏹
            } else if (hoursPlayed < 120) {
                nextMilestoneHours = 120;
                nextMilestoneIcon = "🧪"; // 120h 🧪
            } else if (hoursPlayed < 168) {
                nextMilestoneHours = 168;
                nextMilestoneIcon = "☄"; // 168h ☄
            } else if (hoursPlayed < 216) {
                nextMilestoneHours = 216;
                nextMilestoneIcon = "♪"; // 216h ♪
            } else if (hoursPlayed < 288) {
                nextMilestoneHours = 288;
                nextMilestoneIcon = "🔱"; // 288h 🔱
            } else if (hoursPlayed < 360) {
                nextMilestoneHours = 360;
                nextMilestoneIcon = "₪"; // 360h ₪
            } else if (hoursPlayed < 576) {
                nextMilestoneHours = 576;
                nextMilestoneIcon = "🐲"; // 576h 🐲
            } else if (hoursPlayed < 720) {
                nextMilestoneHours = 720;
                nextMilestoneIcon = "❤"; // 720h ❤
            } else if (hoursPlayed < 1000) {
                nextMilestoneHours = 1000;
                nextMilestoneIcon = "☯"; // 1000h ☯
            }

            // Use the hoursPlayed, minutesPlayed, and secondsPlayed variables
            if (nextMilestoneHours != -1) {
                int totalPlayedSeconds = nextMilestoneHours * 3600 - secondsPlayed;
                int hoursRemaining = totalPlayedSeconds / 3600;
                int minutesRemaining = (totalPlayedSeconds % 3600) / 60;
                int secondsRemaining = totalPlayedSeconds % 60;

                String hoursText;
                if (hoursRemaining % 10 == 1 && hoursRemaining % 100 != 11) {
                    hoursText = "час";
                } else if (hoursRemaining % 10 >= 2 && hoursRemaining % 10 <= 4 && (hoursRemaining % 100 < 10 || hoursRemaining % 100 >= 20)) {
                    hoursText = "часа";
                } else {
                    hoursText = "часов";
                }

                String minutesText;
                if (minutesRemaining % 10 == 1 && minutesRemaining % 100 != 11) {
                    minutesText = "минута";
                } else if (minutesRemaining % 10 >= 2 && minutesRemaining % 10 <= 4 && (minutesRemaining % 100 < 10 || minutesRemaining % 100 >= 20)) {
                    minutesText = "минуты";
                } else {
                    minutesText = "минут";
                }

                String secondsText;
                if (secondsRemaining % 10 == 1 && secondsRemaining % 100 != 11) {
                    secondsText = "секунда";
                } else if (secondsRemaining % 10 >= 2 && secondsRemaining % 10 <= 4 && (secondsRemaining % 100 < 10 || secondsRemaining % 100 >= 20)) {
                    secondsText = "секунды";
                } else {
                    secondsText = "секунд";
                }

                sender.sendMessage("§7Времени до следущего значка (§6" + nextMilestoneIcon + "§7): §6"
                        + hoursRemaining + " " + hoursText + " "
                        + minutesRemaining + " " + minutesText + " "
                        + secondsRemaining + " " + secondsText);
            } else {
                sender.sendMessage("«§6Поздравляем! §7 Вы достигли последней иконки. §cВаша преданность §l§qTEMPUS VANILLA §cзаслуживает похвалы, но не забывайте трогать траву или снег! §6#PressFForPersonalLife»");
            }
            sender.sendMessage("§с===================================================");
            // по хорошему надо было сделать мапу с этими значками и временем
            sender.sendMessage("§7Список значков за часы в игре: §6🐔§7-2 §6🌳§7-5 §6🪓§7-12 §6⛏§7-24 §6🎣§7-48 §6🗡§7-72 §6🏹§7-96 §6🧪§7-120 §6☄§7-168 §6♪§7-216 §6🔱§7-288 §6₪§7-360 §6🐲§7-576 §6❤§7-720 §6☯§7-1000" );
            sender.sendMessage("§с===================================================");
        });





        return true;
    }

}
