package primalcat.tempus.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import primalcat.tempus.TempusEssentials;
import ru.mrbrikster.chatty.api.ChattyApi;
import ru.mrbrikster.chatty.api.chats.Chat;
import ru.mrbrikster.chatty.api.ChattyApi.ChattyApiHolder;
import ru.mrbrikster.chatty.api.events.ChattyMessageEvent;
import ru.mrbrikster.chatty.chat.ChatListener;
import ru.mrbrikster.chatty.json.FormattedMessage;

import java.util.Collection;
import java.util.Optional;

import static org.bukkit.Bukkit.getLogger;

public class ChattyChatHook implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChattyMessage(ChattyPreMessageEvent event) {
        getLogger().info("chatty event " + event.getMessage());
//        TempusEssentials.getPlugin().processChatMessage(event.getPlayer(), event.getMessage(), event.getChat().getName(), false, event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(ChattyMessageEvent event) {


        getLogger().info("originalFormat " + event.getChat().getFormat());
        getLogger().info("originalMessage " + event.getMessage());
        event.getChat().sendFormattedMessage(new FormattedMessage("test"));
//        event.getChat().
    }
}
