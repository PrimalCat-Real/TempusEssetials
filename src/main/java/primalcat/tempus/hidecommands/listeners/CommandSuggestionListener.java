package primalcat.tempus.hidecommands.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import primalcat.tempus.TempusEssentials;
import primalcat.tempus.hidecommands.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommandSuggestionListener implements Listener {
    public CommandSuggestionListener() {
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onTab(PlayerCommandSendEvent event) {
        Player p = event.getPlayer();
        if (Util.getGroup(p) != null) {
            String section = "groups." + Util.getGroup(p);
            boolean isListBlocking = TempusEssentials.getPlugin().getConfig().getString(section + ".group-mode").equalsIgnoreCase("blacklist");
            if (!isListBlocking) {
                event.getCommands().clear();
            }

            Iterator var5;
            String includedGroup;
            String command;
            if (!TempusEssentials.getPlugin().getConfig().getStringList(section + ".included-groups").isEmpty()) {
                var5 = TempusEssentials.getPlugin().getConfig().getStringList(section + ".included-groups").iterator();

                while(var5.hasNext()) {
                    includedGroup = (String)var5.next();
                    Iterator var7 = TempusEssentials.getPlugin().getConfig().getStringList("groups." + includedGroup + ".commands").iterator();

                    while(var7.hasNext()) {
                        command = (String)var7.next();
                        List<String> cmdArgs = new ArrayList(Arrays.asList(command.split(" ")));
                        String command2 = (String)cmdArgs.get(0);
                        if (TempusEssentials.getPlugin().getConfig().getString("groups." + includedGroup + ".group-mode").equalsIgnoreCase("blacklist")) {
                            if (isListBlocking) {
                                event.getCommands().remove(command2);
                            }
                        } else if (!isListBlocking) {
                            event.getCommands().add(command2);
                        }
                    }
                }
            }

            ArrayList cmdArgs;
            if (isListBlocking) {
                var5 = TempusEssentials.getPlugin().getConfig().getStringList(section + ".commands").iterator();

                while(var5.hasNext()) {
                    includedGroup = (String)var5.next();
                    cmdArgs = new ArrayList(Arrays.asList(includedGroup.split(" ")));
                    command = (String)cmdArgs.get(0);
                    command = command.replace("%space%", " ");
                    event.getCommands().remove(command);
                }
            } else {
                var5 = TempusEssentials.getPlugin().getConfig().getStringList(section + ".commands").iterator();

                while(var5.hasNext()) {
                    includedGroup = (String)var5.next();
                    cmdArgs = new ArrayList(Arrays.asList(includedGroup.split(" ")));
                    command = (String)cmdArgs.get(0);
                    command = command.replace("%space%", " ");
                    event.getCommands().add(command);
                }
            }

        }
    }
}
