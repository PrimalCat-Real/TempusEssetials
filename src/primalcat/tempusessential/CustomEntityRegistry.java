package primalcat.tempusessential;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import primalcat.tempusessential.StrongerDragon.CustomEnderDragon;
import primalcat.tempusessential.StrongerDragon.CustomPhaseRegistry;

import java.lang.reflect.Field;

public class CustomEntityRegistry {


//    ENDER_DRAGON = register("ender_dragon", EntityType.Builder.of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).passengerAttachments(3.0F).clientTrackingRange(10));
    public static void replaceEnderDragonFactory() {
        try {
            Field field = EntityType.ENDER_DRAGON.getClass().getDeclaredField("bF");
            field.setAccessible(true);
            field.set(EntityType.ENDER_DRAGON, (EntityType.EntityFactory<EnderDragon>) CustomEnderDragon::new);
            CustomPhaseRegistry.replaceDragonPhase();
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Ender dragon replaced");
            Bukkit.getLogger().info("Successfully added Custom Ender Dragon factory.");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to replace Ender Dragon factory.");
        }
    }
}
