package primalcat.tempus.hidecommands.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import primalcat.tempus.TempusEssentials;
import primalcat.tempus.hidecommands.Util;

import java.util.Iterator;
import java.util.List;

public class OpListener implements Listener {
    private BukkitTask opChecker;
    public static List<String> allowedOperators = TempusEssentials.getPlugin().getConfig().getStringList("allowed-operators");

    public OpListener() {
    }

    @EventHandler
    public void onOPServer(ServerCommandEvent e) {
        if ((e.getCommand().toLowerCase().startsWith("op ") || e.getCommand().toLowerCase().startsWith("minecraft:op ")) && TempusEssentials.getPlugin().getConfig().getBoolean("op-protection") && this.isForbiddenOperator(e.getCommand())) {
            e.getSender().sendMessage("§3Pl§8-§3Hide §8▎ §r" + Util.getColoredConfigString("op-message"));
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onOpPlayer(PlayerCommandPreprocessEvent e) {
        if ((e.getPlayer().isOp() || e.getPlayer().hasPermission("*")) && (e.getMessage().toLowerCase().startsWith("/op ") || e.getMessage().toLowerCase().startsWith("/minecraft:op ")) && TempusEssentials.getPlugin().getConfig().getBoolean("op-protection") && this.isForbiddenOperator(e.getMessage())) {
            e.getPlayer().sendMessage("§3Pl§8-§3Hide §8▎ §r" + Util.getColoredConfigString("op-message"));
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onServerStart(ServerLoadEvent e) {
        if (this.opChecker == null) {
            this.opChecker = (new BukkitRunnable() {
                public void run() {
                    if (TempusEssentials.getPlugin().getConfig().getBoolean("op-protection")) {
                        Bukkit.getOperators().forEach((offlinePlayer) -> {
                            if (!OpListener.allowedOperators.contains(offlinePlayer.getName())) {
                                offlinePlayer.setOp(false);
                                if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                                    offlinePlayer.getPlayer().kickPlayer(Util.getColoredConfigString("unauthorized-operator-kick-message"));
                                }
                            }

                        });
                    }

                }
            }).runTaskTimer(TempusEssentials.getPlugin(), 300L, 40L);
        }
    }

    private boolean isForbiddenOperator(String command) {
        if (command.charAt(0) == '/') {
            command = command.replaceFirst("/", "");
        }

        Iterator var2 = allowedOperators.iterator();

        String allowedOperator;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            allowedOperator = (String)var2.next();
        } while(!command.equalsIgnoreCase("op " + allowedOperator) && !command.equalsIgnoreCase("minecraft:op " + allowedOperator));

        return false;
    }
}
