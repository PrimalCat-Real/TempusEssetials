package primalcat.tempus.hidecommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import primalcat.tempus.TempusEssentials;
import primalcat.tempus.hidecommands.listeners.OpListener;

import java.util.ArrayList;
import java.util.List;

public class PlHideCmd implements CommandExecutor, TabCompleter {
    public PlHideCmd() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§cNot enough arguments!");
            sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§a/plhide help");
            return true;
        } else {
            switch (args[0]) {
                case "reload":
                case "rl":
                    rl(sender);
                    break;
                case "help":
                    sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§aAuthor: §7Nononitas");
                    sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§aVersion:§7 " + TempusEssentials.getPlugin().getName());
                    sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§a/plhide reload §8|§7 Reloads the config");
                    sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§a/plhide updatecheck §8|§7 Checks if there is a newer version");
                    break;
                default:
                    sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§cWrong Command");
                    sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§a/plhide help");
            }

            return true;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> commands = new ArrayList();
            if (sender.hasPermission("plhide.reload") && sender.isOp() && TempusEssentials.getPlugin().getConfig().getBoolean("op-can-use-plugin")) {
                commands.add("rl");
                commands.add("reload");
                commands.add("updatecheck");
            }

            return commands;
        } else {
            return null;
        }
    }

    private static void rl(CommandSender sender) {
        if (sender.hasPermission("plhide.reload")) {
            TempusEssentials.getPlugin().createConfig();
            TempusEssentials.getPlugin().reloadConfig();
            OpListener.allowedOperators = TempusEssentials.getPlugin().getConfig().getStringList("allowed-operators");
            sender.sendMessage("§3Pl§8-§3Hide §8▎ §r§areloading complete");
            Util.checkGroups();
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }

    }
}