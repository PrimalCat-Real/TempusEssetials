package primalcat.tempusessential.Corpes;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CorpseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки!");
            return true;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation();

        new Corpse(player);

        return true;
    }


}
