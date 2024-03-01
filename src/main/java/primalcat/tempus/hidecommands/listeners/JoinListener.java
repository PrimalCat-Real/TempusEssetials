package primalcat.tempus.hidecommands.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import primalcat.tempus.TempusEssentials;
import primalcat.tempus.hidecommands.Util;

import java.util.List;

public class JoinListener implements Listener {
    public JoinListener() {
    }

    @EventHandler
    public void onLoginOpCheck(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (p.isOp() && TempusEssentials.getPlugin().getConfig().getBoolean("op-protection")) {
            List<String> allowedOperatorList = TempusEssentials.getPlugin().getConfig().getStringList("allowed-operators");
            if (!allowedOperatorList.contains(p.getName())) {
                String message = Util.getColoredConfigString("unauthorized-operator-kick-message");
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(message);
                TempusEssentials.getPlugin().getServer().getConsoleSender().sendMessage(message);
                p.setOp(false);
            }
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
//        if (TempusEssentials.getPlugin().getConfig().getBoolean("update-notify")) {
//            Updater.updatecheck(e.getPlayer(), false);
//        }

    }
}
