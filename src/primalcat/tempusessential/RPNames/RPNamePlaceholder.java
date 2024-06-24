package primalcat.tempusessential.RPNames;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import primalcat.tempusessential.TempusEssential;

public class RPNamePlaceholder extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "playerrpname";
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
        String nickname = TempusEssential.customRpNicks.getOrDefault(player.getName(), player.getName());

        return nickname;
    }

}
