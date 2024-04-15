package primalcat.tempus.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import primalcat.tempus.TempusEssentials;

public class OriginalPlayerName extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "playeroriginalname";
    }

    @Override
    public String getAuthor() {
        return "primalcat";
    }


    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String nickname = TempusEssentials.customRpNicks.getOrDefault(player.getName(), player.getName());
        final TextComponent textComponent = Component.text()
                .content("Hello ")
                .color(NamedTextColor.GOLD)
                .append(Component.text("world", NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text("!", NamedTextColor.RED))
                .build();

// Converts textComponent to the JSON form used for serialization by Minecraft.
        final String json = JSONComponentSerializer.json().serialize(textComponent);
        return nickname;
//        MiniMessage mm = MiniMessage.miniMessage();
//
//        MiniMessage serializer = MiniMessage.builder()
//                .tags(TagResolver.builder()
//                        .resolver(StandardTags.color())
//                        .build()
//                )
//                .build();
//
//        Component parsed = serializer.deserialize("<green><bold>Hai");
//        String result = MiniMessage.miniMessage().serialize(parsed);
////        return result;
////        return TextAdapter.sendComponent(recipient, MiniMessageParser.parseFormat(message));
//        Component component = MiniMessage.get().parse("<red>Это красное сообщение</red>");
//
//        return MiniMessage.miniMessage().serialize(parsed);

//        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
}
