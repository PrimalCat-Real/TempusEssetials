package primalcat.tempusessential.PlayTime;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import primalcat.tempusessential.utils.TimeUtils;

import java.util.Map;
import java.util.TreeMap;

public class PlayTimeCommand implements CommandExecutor {
    private PlaytimeRewardsManager rewardsManager;

    public PlayTimeCommand(Plugin plugin) {
        this.rewardsManager = new PlaytimeRewardsManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("Only players can use this command without specifying a player name.");
            return true;
        }

        Player player;

        if (args.length > 0) {
            // Попытка найти игрока по нику, указанному в аргументах
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("Игрок не найден");
                return true;
            }
        } else {
            player = (Player) sender;
        }
        if (!player.hasPermission("tempusessentials.playtime")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        int secondsPlayed;
        try{
            secondsPlayed = Math.round((float) Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%plan_player_time_active_raw%")) / 1000);
        }catch (Exception e){
            secondsPlayed = TimeUtils.getSecondsPlayedAsync(player);
        }

        TreeMap<Integer, String> milestones = rewardsManager.getMilestones(); // This needs to be implemented in your PlaytimeRewardsManager
        Integer nextMilestoneHours = milestones.higherKey(secondsPlayed / 3600);


        int totalHoursPlayed = secondsPlayed / 3600;
        int totalMinutesPlayed = (secondsPlayed % 3600) / 60;
        int totalRemainingSeconds = secondsPlayed % 60;

        String totalHoursPlayedText = getCorrectForm(totalHoursPlayed, "час", "часа", "часов");
        String totalMinutesPlayedText = getCorrectForm(totalMinutesPlayed, "минута", "минуты", "минут");
        String totalSecondsPlayedText = getCorrectForm(totalRemainingSeconds, "секунда", "секунды", "секунд");

        sender.sendMessage("§7Проведено в игре: §6"
                + totalHoursPlayed + " " + totalHoursPlayedText + " "
                + totalMinutesPlayed + " " + totalMinutesPlayedText + " "
                + totalRemainingSeconds + " " + totalSecondsPlayedText);

        if (nextMilestoneHours != null) {
            int nextMilestoneSeconds = nextMilestoneHours * 3600;
            if (secondsPlayed < nextMilestoneSeconds) {
                String icon = rewardsManager.getIconForHours(nextMilestoneHours);
                displayTimeUntilNextMilestone(sender, secondsPlayed, nextMilestoneSeconds, icon);
            } else {
                sender.sendMessage("«§6Поздравляем! §7 Вы достигли последней иконки. §cВаша преданность §l§qTEMPUS VANILLA §cзаслуживает похвалы, но не забывайте трогать траву или снег! §6#PressFForPersonalLife»");
            }
        } else {
            sender.sendMessage("Ошибка: Не удалось определить следующий значек.");
        }

//        sender.sendMessage("You have played for " + hoursPlayed);

        // Dynamically generate the list of icons from configuration

        StringBuilder iconsList = new StringBuilder("§7Список значков за часы в игре:");
        for (Map.Entry<Integer, String> entry : milestones.entrySet()) {
            iconsList.append(String.format(" §6%s§7-%d", entry.getValue(), entry.getKey()));
        }

        sender.sendMessage("§с===================================================");
        sender.sendMessage(iconsList.toString());
        sender.sendMessage("§с===================================================");

        return true;
//        String icon = this.rewardsManager.getIconForHours(hoursPlayed);
//        System.out.println(icon);

//        sender.sendMessage("You have played for " + hoursPlayed + " hours and earned icon: " + icon);
    }
    private void displayTimeUntilNextMilestone(CommandSender sender, int secondsPlayed, int nextMilestoneSeconds, String nextMilestoneIcon) {
        int secondsRemaining = nextMilestoneSeconds - secondsPlayed;

        int hoursRemaining = secondsRemaining / 3600;
        int minutesRemaining = (secondsRemaining % 3600) / 60;
        int seconds = secondsRemaining % 60;

        String hoursText = getCorrectForm(hoursRemaining, "час", "часа", "часов");
        String minutesText = getCorrectForm(minutesRemaining, "минута", "минуты", "минут");
        String secondsText = getCorrectForm(seconds, "секунда", "секунды", "секунд");

        sender.sendMessage("§7Времени до следующего значка (§6" + nextMilestoneIcon + "§7): §6" +
                hoursRemaining + " " + hoursText + " " +
                minutesRemaining + " " + minutesText + " " +
                seconds + " " + secondsText);
    }

    public static String getCorrectForm(int number, String one, String few, String many) {
        if (number % 10 == 1 && number % 100 != 11) {
            return one;
        } else if (number % 10 >= 2 && number % 10 <= 4 && (number % 100 < 10 || number % 100 >= 20)) {
            return few;
        } else {
            return many;
        }
    }
}
