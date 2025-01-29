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
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

public class WrapperEntityMetadata implements IPacket {

  private static final byte FLAGS =
          (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40 | 0x80);

  private final int id;
  private PacketContainer packet;

  public WrapperEntityMetadata(int entityID) {
    this.id = entityID;
  }

  @Override
  public void load() {
      packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
      packet.getIntegers().write(0, this.id);

      WrappedDataWatcher watcher = new WrappedDataWatcher();
      WrappedDataWatcher.WrappedDataWatcherObject visible = new WrappedDataWatcher.WrappedDataWatcherObject(
              6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()));
      watcher.setObject(visible, EnumWrappers.EntityPose.SLEEPING.toNms());

      int indexSkinLayer = 17;
      WrappedDataWatcher.WrappedDataWatcherObject skinLayers = new WrappedDataWatcher.WrappedDataWatcherObject(
              indexSkinLayer, WrappedDataWatcher.Registry.get(Byte.class));
      final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
      watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
          final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
          wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
      });
      wrappedDataValueList.add(new WrappedDataValue(skinLayers.getIndex(), skinLayers.getSerializer(), FLAGS));
      packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);

  }

  @Override
  public PacketContainer get() {
    return packet;
  }
}
