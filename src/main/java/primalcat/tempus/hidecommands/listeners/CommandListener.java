package primalcat.tempus.hidecommands.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import primalcat.tempus.TempusEssentials;
import primalcat.tempus.hidecommands.CommandBlocker;
import primalcat.tempus.hidecommands.Util;

public class CommandListener implements Listener {
    public CommandListener() {
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String[] command = e.getMessage().split(" ");
        String message;
        if (!command[0].equalsIgnoreCase("/pl") && !command[0].equalsIgnoreCase("/bukkit:pl") && !command[0].equalsIgnoreCase("/plugins") && !command[0].equalsIgnoreCase("/bukkit:plugins")) {
            if (e.getMessage().equalsIgnoreCase("/nononitas")) {
                e.setCancelled(true);
                p.sendMessage("§ePL-Hide " + TempusEssentials.getPlugin().getDescription().getVersion() + " by Nononitas");
            } else {
                if (CommandBlocker.isBlocked(e.getMessage(), Util.getGroup(p))) {
                    message = Util.getColoredConfigString("blocked-command-message");
                    if (!message.equals("none")) {
                        p.sendMessage(message);
                    }

                    e.setCancelled(true);
                }

            }
        } else if (!p.hasPermission("*") && !p.hasPermission("plhide.bypass")) {
            message = Util.getColoredConfigString("/pl-message");
            if (!message.equals("none")) {
                p.sendMessage(message);
            }

            e.setCancelled(true);
        }
    }
}
