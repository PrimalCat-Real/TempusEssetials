package primalcat.tempus.placeholders;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import primalcat.tempus.utils.Util;

import java.util.concurrent.CompletableFuture;


public class PlayTimeIconPlaceholder extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return "primalcat";
    }

    @Override
    public String getIdentifier() {
        return "playtimeicon";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

//    @Override
//    public boolean persist() {
//        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
//    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

            // Return your custom placeholder value here
        CompletableFuture<Integer> hoursPlayedFuture = Util.getHoursPlayedAsync(player);
        String icon;
        try {
            int hoursPlayed = hoursPlayedFuture.get();
            if (hoursPlayed >= 2 && hoursPlayed < 5) {
                icon = "🐔 "; // 2h 🐔
            } else if (hoursPlayed >= 5 && hoursPlayed < 12) {
                icon = "🌳 "; // 5h 🌳
            } else if (hoursPlayed >= 12 && hoursPlayed < 24) {
                icon = "🪓 "; // 12h 🪓
            } else if (hoursPlayed >= 24 && hoursPlayed < 48) {
                icon = "⛏ "; // 24h ⛏
            } else if (hoursPlayed >= 48 && hoursPlayed < 72) {
                icon = "🎣 "; // 48h 🎣
            } else if (hoursPlayed >= 72 && hoursPlayed < 96) {
                icon = "🗡 "; // 72h 🗡
            } else if (hoursPlayed >= 96 && hoursPlayed < 120) {
                icon = "🏹 "; // 96h 🏹
            } else if (hoursPlayed >= 120 && hoursPlayed < 168) {
                icon = "🧪 "; // 120h 🧪
            } else if (hoursPlayed >= 168 && hoursPlayed < 216) {
                icon = "☄ "; // 168h ☄
            } else if (hoursPlayed >= 216 && hoursPlayed < 288) {
                icon = "♪ "; // 216h ♪
            } else if (hoursPlayed >= 288 && hoursPlayed < 360) {
                icon = "🔱 "; // 288h 🔱
            } else if (hoursPlayed >= 360 && hoursPlayed < 576) {
                icon = "₪ "; // 360h ₪
            } else if (hoursPlayed >= 576 && hoursPlayed < 720) {
                icon = "🐲 "; // 576h 🐲
            } else if (hoursPlayed >= 720 && hoursPlayed < 1000) {
                icon = "❤ "; // 720h ❤
            } else if (hoursPlayed >= 1000) {
                icon = "☯ "; // 1000h ☯
            } else {
                icon = "";
            }
            return icon;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        // Placeholder is unknown
    }



    public static void registerIcon(){

    }
}
