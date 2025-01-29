package primalcat.tempusessential.Fog;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FogCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fog.remove")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Использование: /removefog <игрок>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Игрок с именем " + args[0] + " не найден или не в сети.");
            return true;
        }

        UUID targetUUID = targetPlayer.getUniqueId();

        // Удаляем игрока из списка тумана
        if (FogPacketHandler.dangerZonePlayers.containsKey(targetUUID)) {
            FogPacketHandler.dangerZonePlayers.remove(targetUUID);
            FogPacketHandler.removeBossBar(targetPlayer);
            sender.sendMessage(ChatColor.GREEN + "Игрок " + targetPlayer.getName() + " был удалён из списка тумана.");
            targetPlayer.sendMessage(ChatColor.YELLOW + "Вы больше не в опасной зоне.");
        } else {
            sender.sendMessage(ChatColor.RED + "Игрок " + targetPlayer.getName() + " не находится в зоне тумана.");
        }

        return true;
    }
}
