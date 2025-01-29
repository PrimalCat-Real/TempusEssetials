package primalcat.tempusessential.ResourcepackServer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import primalcat.tempusessential.TempusEssential;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import static primalcat.tempusessential.ResourcepackServer.ForceResourcePackOnJoin.RESOURCE_PACK_URL;

public class PackExemptCommand implements CommandExecutor {
    private final Plugin plugin;
    private final Set<UUID> playersWithResourcePack;
    private final Logger logger;

    public PackExemptCommand(Plugin plugin, Set<UUID> playersWithResourcePack) {
        this.plugin = plugin;
        this.playersWithResourcePack = playersWithResourcePack;
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            UUID playerId = player.getUniqueId();

            if (!playersWithResourcePack.contains(playerId)) {
                playersWithResourcePack.add(playerId);
                player.setResourcePack(RESOURCE_PACK_URL, ResourcePackServer.getResourcePackHash());
                logger.info(player.getName() + " installed resource pack");
            }
            return true;
        } else {
            sender.sendMessage("Only player can run this command");
            return false;
        }
    }
}

