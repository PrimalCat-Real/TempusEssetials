package primalcat.tempus.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Bukkit.getLogger;

public class ChatPacketModifier extends PacketAdapter {
    public ChatPacketModifier(Plugin plugin) {
        super(plugin, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        WrappedChatComponent originalComponent = packet.getChatComponents().read(0);
//        String originalText = originalComponent.getJson();

        // Создание JSON для сообщения с всплывающей подсказкой
        String hoverMessageJson = "{\"text\":\"Нажми здесь!\",\"color\":\"gold\",\"bold\":true,\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"Это подсказка!\",\"color\":\"green\"}}}";

        // Замена оригинального сообщения на сообщение с всплывающей подсказкой
        packet.getChatComponents().write(0, WrappedChatComponent.fromJson(hoverMessageJson));

        // Установите ChatMessageType в CHAT, если нужно, чтобы сообщение отображалось в чате
        packet.getChatTypes().write(0, EnumWrappers.ChatType.CHAT);

        getLogger().info("test packed chat " + originalComponent);
    }
}
