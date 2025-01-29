package primalcat.tempusessential.CustomMobDrops;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Команда доступна только игрокам!");
            return true;
        }
        if (!sender.hasPermission("tempusessential.givemobshard")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack shard = MobDrops.createCustomItem("dicerp:mobshard", 1);

        if (shard != null) {
            player.getInventory().addItem(shard);
            player.sendMessage("§aВы получили кастомный шард!");
        } else {
            player.sendMessage("§cПроизошла ошибка при создании предмета!");
        }

        return true;
    }
}
