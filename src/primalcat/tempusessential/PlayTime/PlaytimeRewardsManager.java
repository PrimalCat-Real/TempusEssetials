package primalcat.tempusessential.PlayTime;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PlaytimeRewardsManager {
    private TreeMap<Integer, String> milestones;

    public PlaytimeRewardsManager(Plugin plugin) {
        this.milestones = new TreeMap<>();
        this.loadMilestones(plugin);
    }

    private void loadMilestones(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        List<Map<?, ?>> configs = config.getMapList("modules.playtime-rewards.milestones");
        for (Map<?, ?> configItem : configs) {
            Integer hours = (Integer) configItem.get("hours");
            String icon = (String) configItem.get("icon");
            this.milestones.put(hours, icon);
        }
    }

    public TreeMap<Integer, String> getMilestones() {
        return this.milestones;
    }

    public String getIconForHours(int hoursPlayed) {
        // Возвращает иконку для последнего ключа, который меньше или равен hoursPlayed
        return this.milestones.floorEntry(hoursPlayed).getValue();
    }
}
