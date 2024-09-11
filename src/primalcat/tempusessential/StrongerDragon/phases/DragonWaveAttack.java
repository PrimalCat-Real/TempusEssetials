package primalcat.tempusessential.StrongerDragon.phases;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.TempusEssential;

import java.util.List;

public class DragonWaveAttack {

    // Функция для создания круга частиц и нанесения урона
    public static void spawnDragonBreathAttack(Location center, double radius, int particleCount, World world) {
        double angleStep = 2 * Math.PI / particleCount;

        for (int i = 0; i < particleCount; i++) {
            double angle = i * angleStep;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location particleLocation = new Location(world, x, center.getY(), z);

            // Проверка на воздух выше и ниже
            particleLocation = adjustParticleLocation(particleLocation, world);

            // Спавним частицу дыхания дракона только на границе
            world.spawnParticle(Particle.DRAGON_BREATH, particleLocation, 12, 0.1, 0.1, 0.1, 0.02);
            damagePlayersInRadius(particleLocation, world);

        }
    }

    private static void damagePlayersInRadius(Location particleLocation, World world) {
        double damageRadius = 1; // Радиус поражения
        List<Player> players = world.getNearbyPlayers(particleLocation, damageRadius).stream().toList();

        for (Player player : players) {
            player.damage(3.0); // Наносим 8 единиц урона игрокам
        }
    }

    // Функция для корректировки позиции спавна частицы
    private static Location adjustParticleLocation(Location location, World world) {
        // Проверяем блоки выше на наличие воздуха (до 8 блоков)
        for (int i = 0; i < 8; i++) {
            if (world.getBlockAt(location).getType() == Material.AIR) {
                break;
            }
            location.add(0, 1, 0); // Поднимаем частицу на один блок вверх
        }

        // Проверяем блоки ниже на наличие твёрдой поверхности (до 8 блоков)
        for (int i = 0; i < 8; i++) {
            if (world.getBlockAt(location.clone().add(0, -1, 0)).getType() != Material.AIR) {
                break;
            }
            location.add(0, -1, 0); // Опускаем частицу на один блок вниз
        }

        return location.add(0,1,0);
    }

    // Запускаем атаку с расширяющимся кругом частиц
    public static void startDragonAttack(Location center, World world) {
        new BukkitRunnable() {
            double currentRadius = 1; // Начальный радиус (может быть 0, если хотите начать с центра)
            final double maxRadius = 32;

            final int particleCount = 36;
            @Override
            public void run() {
                if (currentRadius > maxRadius) {
                    this.cancel(); // Останавливаем задачу, если достигнут максимальный радиус
                    return;
                }

                // Спавним круг частиц только один раз на текущем радиусе
                spawnDragonBreathAttack(center, currentRadius, particleCount, world);

                // Увеличиваем радиус круга для следующей итерации
                currentRadius += 1.0; // Увеличиваем радиус на 1 блок каждую секунду

                // Выводим текущий радиус в консоль для проверки
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 0L, 20L); // Задача выполняется каждые 20 тиков (1 секунда)
    }
}
