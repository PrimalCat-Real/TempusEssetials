package primalcat.tempusessential.ResourcepackServer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import primalcat.tempusessential.TempusEssential;

import java.nio.ByteBuffer;

public class CustomPayloadMessageListener implements PluginMessageListener {
    private static final int NO_RESOURCEPACK = 1;
    private static final int FORCE_RESOURCEPACK = 0;

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals(TempusEssential.CUSTOM_PAYLOAD_CHANNEL)) {
            Plugin plugin = TempusEssential.getPlugin();

            try {
                // Parse message
                ByteBuffer buffer = ByteBuffer.wrap(message);
                int receivedValue = buffer.getInt();

//                plugin.getLogger().info("Received message from channel: " + channel +
//                        " by player: " + player.getName() +
//                        ", value: " + receivedValue);

                // Handle packet logic
                if (receivedValue == NO_RESOURCEPACK) {
                    ForceResourcePackOnJoin.excludePlayer(player.getUniqueId());
//                    plugin.getLogger().info("Player " + player.getName() +
//                            " opted out of the resource pack.");
                } else {
                    ForceResourcePackOnJoin.includePlayer(player.getUniqueId());
//                    plugin.getLogger().info("Player " + player.getName() +
//                            " confirmed to use the resource pack.");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error processing message from channel " + channel +
                        ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
