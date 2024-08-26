package primalcat.tempusessential.StrongerDragon.phases;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import primalcat.tempusessential.TempusEssential;

public class DragonUltraFireballAttack {

    // Основной метод для вызова атаки
    public static void startUltraFireballAttack(EnderDragon dragon, Player targetPlayer, World world) {
        Location dragonLocation = dragon.getLocation();

        // Создаем файербол (сферу)
        Fireball fireball = (Fireball) world.spawnEntity(dragonLocation.add(0, 2, 0), EntityType.FIREBALL);
        fireball.setCustomName("UltraFireball");
        fireball.setIsIncendiary(false);
        fireball.setYield(0); // Пока файербол не взрывается
        fireball.setGravity(false); // Отключаем гравитацию для концентрации
        fireball.setVelocity(new Vector(0, 0, 0)); // Фиксируем позицию при кастовании


        // Частицы и звуки для фазы зарядки
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 60) { // 3 секунды концентрации
                    startTrackingPlayer(fireball, targetPlayer, dragon, world); // Запускаем преследование игрока

                    // Проигрываем звук активации маяка, когда фаербол начинает движение
                    fireball.getWorld().playSound(fireball.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 40f, 1.0f);

                    this.cancel();
                } else {
                    world.spawnParticle(Particle.DRAGON_BREATH, fireball.getLocation(), 30, 0.3, 0.3, 0.3, 0);
                    world.spawnParticle(Particle.ELECTRIC_SPARK, fireball.getLocation(), 10, 0.3, 0.3, 0.3, 0);
                    world.playSound(fireball.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 1.0f);
                    ticks++;
                }
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 0, 1); // Частицы и звук каждую тик (20 раз в секунду)
    }

    // Метод для начала преследования игрока
    private static void startTrackingPlayer(Fireball fireball, Player targetPlayer, EnderDragon dragon, World world) {
        fireball.setGravity(false); // Отключаем гравитацию для плавного движения

        new BukkitRunnable() {
            int timeAlive = 0; // Счетчик времени жизни сферы

            @Override
            public void run() {
                // Проверка на завершение или уничтожение сферы
                if (fireball.isDead() || !fireball.isValid()) {
                    this.cancel();
                    return;
                }

                // Если цель умерла, сфера немедленно взрывается
                if (!targetPlayer.isOnline() || targetPlayer.isDead()) {
                    fireball.getWorld().createExplosion(fireball.getLocation(), 6.0f, false, true); // Взрыв силы 6
                    fireball.remove();
                    this.cancel();
                    return;
                }

                // Проверяем на окончание времени жизни сферы (10 секунд)
                if (timeAlive >= 200) { // 10 секунд преследования
                    fireball.getWorld().createExplosion(fireball.getLocation(), 12.0f, false, true); // Взрыв силы 20
                    fireball.remove();
                    this.cancel();
                    return;
                }

                // Направление к игроку
                Vector direction = targetPlayer.getLocation().toVector().subtract(fireball.getLocation().toVector()).normalize();
                fireball.setVelocity(direction.multiply(0.5)); // Сфера медленно преследует игрока

                // Частицы вокруг сферы
                world.spawnParticle(Particle.DRAGON_BREATH, fireball.getLocation(), 20, 0.1, 0.1, 0.1, 0.01);
                world.spawnParticle(Particle.LARGE_SMOKE, fireball.getLocation(), 10, 0.2, 0.2, 0.2, 0.01);

                // Проверяем игроков в радиусе 2 блоков от сферы
                for (Player player : world.getPlayers()) {
                    if (player.getLocation().distance(fireball.getLocation()) <= 2) {
                        // Наносим 3 единицы урона игроку
                        player.damage(2.0, dragon);
                    }
                }

                timeAlive++;
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 0, 1); // Каждую тик преследование и проверка на урон

        // Взрыв через 10 секунд, если игрок не умер раньше
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fireball.isDead() && fireball.isValid()) {
                    fireball.getWorld().createExplosion(fireball.getLocation(), 12.0f, false, true); // Взрыв силы 20
                    fireball.remove();
                }
            }
        }.runTaskLater(TempusEssential.getPlugin(), 200); // Взрыв через 10 секунд (200 тиков)
    }
}

