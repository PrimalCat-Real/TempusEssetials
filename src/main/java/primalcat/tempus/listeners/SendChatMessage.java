package primalcat.tempus.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getLogger;
import ru.mrbrikster.chatty.api.events.ChattyMessageEvent;

public class SendChatMessage implements Listener {
//    @EventHandler(priority = EventPriority.HIGH)
//    public void onChat(ChattyMessageEvent event) {
//        getLogger().info("originalFormat " + event.getChat().getFormat());
//    }
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onPlayerChat(AsyncPlayerChatEvent event) {
//        // Формат сообщения, где %player_name% будет заменено на имя игрока, а %vault_rank% на его звание
//        String chatFormat = "<%player_name%> [%vault_rank%]: %message%";
//
//        // Получаем сообщение, отправленное игроком
//        String originalMessage = event.getMessage();
//
//        getLogger().info("message from event " + originalMessage);
//        // Заменяем плейсхолдер %message% на оригинальное сообщение игрока
////        chatFormat = chatFormat.replace("%message%", originalMessage);
////
////        // Заменяем остальные плейсхолдеры с помощью PlaceholderAPI
////        String formattedMessage = PlaceholderAPI.setPlaceholders(event.getPlayer(), chatFormat);
////
////        // Устанавливаем форматированное сообщение как сообщение события
////        event.setFormat(formattedMessage);
//    }

//    @EventHandler(priority = EventPriority.HIGH)
//    public void onChat(AsyncPlayerChatEvent event) {
//        String message = event.getMessage();
//        String playerName = event.getPlayer().getName();
//
//        // Логирование сообщения и имени игрока
//        getLogger().info("message from event " + message);
//        String originalFormat = event.getFormat();
//        getLogger().info("originalFormat " + originalFormat);
//
//        Component nameComponent = MiniMessage.miniMessage().deserialize("<hover:show_text:'<green>Это всплывающая подсказка'>"
//                + event.getPlayer().getName() + "</hover>");
//
//        // Создаем компонент для всего сообщения
//        Component messageComponent = Component.text()
//                .append(nameComponent)
//                .append(Component.text(": " + event.getMessage()))
//                .build();
////        chatFormat.replace("%message%", originalMessage);
////        event.setFormat(messageComponent);
//        event.getPlayer().sendMessage(messageComponent);
//
//        // Для более сложного логирования используйте Logger
//        // вашего плагина или внешнюю систему логирования
//    }
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onJoin(PlayerJoinEvent event) {
//        String joinText = "%player_name% &ajoined the server! They are rank &f%vault_rank%";
//
//        /*
//         * We parse the placeholders using "setPlaceholders"
//         * This would turn %vault_rank% into the name of the Group, that the
//         * joining player has.
//         */
//        joinText = PlaceholderAPI.setPlaceholders(event.getPlayer(), joinText);
//
//        event.setJoinMessage(joinText);
//    }
}
