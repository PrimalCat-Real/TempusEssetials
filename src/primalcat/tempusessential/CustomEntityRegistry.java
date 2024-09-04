package primalcat.tempusessential;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import primalcat.tempusessential.StrongerDragon.CustomEnderDragon;
import primalcat.tempusessential.StrongerDragon.CustomFireball;
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

//        try {
//            // Получаем доступ к фабричной функции для создания огненного шара
//            Field field = EntityType.FIREBALL.getClass().getDeclaredField("bF"); // В некоторых версиях может быть другой Field
//            field.setAccessible(true);
//
//            // Заменяем фабрику на создание CustomFireball
//            field.set(EntityType.FIREBALL, (EntityType.EntityFactory<LargeFireball>) (type, world) -> new CustomFireball(type, world));
//
//            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Fireball replaced with CustomFireball.");
//            Bukkit.getLogger().info("Successfully replaced Fireball with CustomFireball.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Bukkit.getLogger().severe("Failed to replace Fireball factory.");
//        }
    }

}
