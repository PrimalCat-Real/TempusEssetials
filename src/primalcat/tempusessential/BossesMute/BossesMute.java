package primalcat.tempusessential.BossesMute;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BossesMute extends PacketAdapter {

    public BossesMute(Plugin plugin) {
        super(plugin, PacketType.Play.Server.WORLD_EVENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        int effectId = packet.getIntegers().read(0);
        Location loc = packet.getBlockPositionModifier().read(0).toLocation(player.getWorld());

        if(effectId == 1024 || effectId == 1023 || effectId == 1028){
            if(player.getLocation().distance(loc) > 40){
                event.setCancelled(true);
            }
        }

    }
}
