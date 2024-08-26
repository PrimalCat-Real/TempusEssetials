package primalcat.tempusessential.StrongerDragon.phases;

import net.minecraft.core.BlockPos;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.TempusEssential;

import java.util.*;

public class DragonCircleExplosion {

    public static void explodeInCircleAround(Location center, int radius, World world) {
        int stepDistance = 3; // Расстояние между взрывами
        List<Location> explosionLocations = new ArrayList<>();

        // Спавн частиц в круге с шагом в 3 блока и сохранение позиций для взрывов
        for (double angle = 0; angle < 360; angle += 10) {
            double radians = Math.toRadians(angle);
            double x = radius * Math.cos(radians);
            double z = radius * Math.sin(radians);

            Location particleLocation = center.clone().add(x, 0, z); // Смещаем на 3 блока выше (y + 3)
            world.spawnParticle(Particle.DRAGON_BREATH, particleLocation, 10, 0.5, 0.5, 0.5, 0.01); // Увеличиваем размер и продолжительность частиц

            // Добавляем позицию для взрыва в список
            explosionLocations.add(particleLocation);
        }

        // Задержка перед началом взрывов (40 тиков = 2 секунды)
        new BukkitRunnable() {
            int currentExplosionIndex = 0;

            @Override
            public void run() {
                // Проверяем, есть ли еще позиции для взрывов
                if (currentExplosionIndex >= explosionLocations.size()) {
                    this.cancel(); // Если все взрывы выполнены, отменяем задачу
                    return;
                }

                // Получаем текущую позицию для взрыва
                Location explosionLocation = explosionLocations.get(currentExplosionIndex);

                // Спавн частиц перед взрывом
                world.spawnParticle(Particle.DRAGON_BREATH, explosionLocation, 20, 0.5, 0.5, 0.5, 0.01); // Повторный спавн частиц

                // Создаем взрыв на этой позиции
                world.createExplosion(explosionLocation, 2F, false, true);

                // Переходим к следующей позиции
                currentExplosionIndex += stepDistance; // Пропускаем шаг в 3 позиции
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 40L, 5L); // Задержка перед началом и интервал в 10 тиков между взрывами
    }
}