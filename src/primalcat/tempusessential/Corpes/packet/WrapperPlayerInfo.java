/*
 * Corpse - Dead bodies in bukkit
 * Copyright (C) unldenis <https://github.com/unldenis>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package primalcat.tempusessential.Corpes.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class WrapperPlayerInfo implements IPacket {

  private final boolean add;
  private final WrappedGameProfile gameProfile;
  private final String name;
  private PacketContainer packet;

  public WrapperPlayerInfo(boolean add, @NotNull WrappedGameProfile gameProfile,
      @NotNull String name) {
    this.add = add;
    this.gameProfile = gameProfile;
    this.name = name;
  }

  @Override
  public void load() {

      PlayerInfoData data = new PlayerInfoData(this.gameProfile, 1,
          EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(this.name));
      List<PlayerInfoData> dataList = new ArrayList<PlayerInfoData>();
      dataList.add(data);

      if (this.add) {
          packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
          packet.getPlayerInfoActions()
              .write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
          packet.getPlayerInfoDataLists()
              .write(1, dataList);
      } else {
          packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
          packet.getUUIDLists().write(0, Collections.singletonList(this.gameProfile.getUUID()));
      }

  }

  @Override
  public PacketContainer get() {
    return packet;
  }
}
