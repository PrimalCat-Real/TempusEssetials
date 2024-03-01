package primalcat.tempus.hidecommands;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Iterator;

import static primalcat.tempus.TempusEssentials.getPlugin;

public class Util {
    public static void checkGroups() {
        ConfigurationSection groupsSection = getPlugin().getConfig().getConfigurationSection("groups");
        if (groupsSection == null || !groupsSection.isSet("default")) {
            getPlugin().getLogger().severe("default group is missing");
        }

        Iterator var1 = groupsSection.getKeys(false).iterator();

        while(var1.hasNext()) {
            String group = (String)var1.next();
            if (!groupsSection.isSet(group + ".group-mode")) {
                getPlugin().getLogger().severe("The option group-mode is missing for the group " + group);
            }

            if (!groupsSection.isSet(group + ".priority")) {
                getPlugin().getLogger().severe("The option priority is missing for the group " + group);
            }

            if (!groupsSection.isSet(group + ".included-groups")) {
                getPlugin().getLogger().severe("The option included-groups is missing for the group " + group);
            }
        }

    }

    public static String getColoredConfigString(String path) {
        String message = getPlugin().getConfig().getString(path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getGroup(Player p) {
        if (p.hasPermission("*")) {
            return null;
        } else {
            int priority = 0;
            String groupName = "";
            Iterator var3 = getPlugin().getConfig().getConfigurationSection("groups").getKeys(false).iterator();

            while(var3.hasNext()) {
                String group = (String)var3.next();
                if (p.hasPermission("plhide.group." + group)) {
                    int currentPriority = getPlugin().getConfig().getInt("groups." + group + ".priority");
                    if (currentPriority > priority) {
                        priority = currentPriority;
                        groupName = group;
                    }
                }
            }

            if (!groupName.isEmpty()) {
                return groupName;
            } else {
                return "default";
            }
        }
    }

}
