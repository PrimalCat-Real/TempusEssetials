package primalcat.tempusessential.Corpes.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class WrapperEntityTeleport implements IPacket {

  private final int id;
  private final Location loc;
  private PacketContainer packet;

  public WrapperEntityTeleport(int entityID, Location location) {
    this.id = entityID;
    this.loc = location;
  }

  @Override
  public void load() {
    packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
    packet.getIntegers().write(0, this.id);

    packet.getDoubles().write(0, loc.getX());
    packet.getDoubles().write(1, loc.getY() + 0.15);
    packet.getDoubles().write(2, loc.getZ());

  }

  @Nullable
  @Override
  public PacketContainer get() {
    return this.packet;
  }
}
