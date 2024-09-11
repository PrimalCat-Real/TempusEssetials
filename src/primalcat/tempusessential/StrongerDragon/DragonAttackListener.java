package primalcat.tempusessential.StrongerDragon;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class DragonAttackListener implements Listener {
    private final Random random = new Random(); // Генератор случайных чисел

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity victim = event.getEntity();

        // Проверяем, что поврежденный объект - это Эндер-дракон
        if (victim instanceof EnderDragon) {

            // Проверяем, не был ли урон нанесен взрывом
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                    || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                // Отменяем событие, если урон нанесен взрывом (включая взрыв кровати)
                event.setCancelled(true);
            }
        }
    }
//    net.minecraft.world.entity.boss.enderdragon.EnderDragon
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        // Проверяем, что поврежденный объект - это Эндер-дракон
        if (victim instanceof EnderDragon) {

            // Проверяем, не был ли урон нанесен взрывом
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                // Отменяем событие, если урон нанесен взрывом
                event.setCancelled(true);
                return;
            }

            // Проверяем, что атака была произведена снарядом (например, стрелой)
            if (damager instanceof Arrow || damager instanceof SpectralArrow) {

                // Уменьшаем урон в 3 раза
                event.setDamage(event.getDamage() / 2);
            }
        }

        // Проверка на то, что источник урона — дыхание дракона
        if (damager instanceof AreaEffectCloud) {
            AreaEffectCloud cloud = (AreaEffectCloud) damager;
            if (cloud.getSource() instanceof EnderDragon) {
                double originalDamage = event.getDamage();
                double newDamage = originalDamage + 3; // Увеличиваем урон на 2
                event.setDamage(newDamage);
            }
        }

        // Применение эффектов на игрока, если его атакует Эндер-дракон
        if (victim instanceof Player && damager instanceof EnderDragon) {
            victim.setVelocity(damager.getVelocity().multiply(2));
            double originalDamage = event.getDamage();
            double newDamage = originalDamage + 2;
            event.setDamage(newDamage);
            ((Player) victim).addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 2));
            ((Player) victim).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
            ((Player) victim).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 120, 2));
        }
    }
}
