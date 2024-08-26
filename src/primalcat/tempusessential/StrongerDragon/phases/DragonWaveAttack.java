package primalcat.tempusessential.StrongerDragon.phases;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.TempusEssential;

import java.util.List;

public class DragonWaveAttack {
    public static void spawnDragonBreathAttack(Location center, double startRadius, double expandRate, int particleCount, World world) {
        // Спавним частицы по окружности
        for (int i = 0; i < 360; i += 360 / particleCount) {
            double angle = Math.toRadians(i);
            double x = center.getX() + startRadius * Math.cos(angle);
            double z = center.getZ() + startRadius * Math.sin(angle);

            Location particleLocation = new Location(world, x, center.getY(), z);

            // Спавн частицы (dragon_breath) на позиции
            world.spawnParticle(Particle.DRAGON_BREATH, particleLocation, 0, 0, 0.00002, 0, 1);

            // Проверяем игроков в радиусе атаки
            List<Player> players = world.getNearbyPlayers(particleLocation, 1.5).stream().toList(); // Радиус поражения
            for (Player player : players) {
                // Наносим урон игрокам
                player.damage(8.0); // Урон драконьим дыханием
            }
        }
    }

    // Метод для старта волны атаки дракона
    public static void startDragonAttack(Location center, World world) {
        new BukkitRunnable() {
            double currentRadius = 0;
            final double expandRate = 0.5; // Скорость расширения
            final double maxRadius = 32; // Максимальный радиус
            final int particleCount = 36; // Количество точек для частиц

            @Override
            public void run() {
                if (currentRadius > maxRadius) {
                    this.cancel(); // Прекращаем выполнение, когда радиус достиг максимального
                    return;
                }

                // Вызываем метод атаки
                spawnDragonBreathAttack(center, currentRadius, expandRate, particleCount, world);

                // Увеличиваем радиус для следующего цикла
                currentRadius += expandRate;
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 0, 2); // Интервал между расширениями - 2 тика
    }
}
