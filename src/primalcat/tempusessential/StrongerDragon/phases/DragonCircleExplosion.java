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
        int stepDistance = 4; // Расстояние между взрывами
        List<Location> explosionLocations = new ArrayList<>();

        // Асинхронная задача для вычисления позиций взрывов
        new BukkitRunnable() {
            @Override
            public void run() {
                // Спавн частиц в круге с шагом в 3 блока и сохранение позиций для взрывов
                for (double angle = 0; angle < 360; angle += 10) {
                    double radians = Math.toRadians(angle);
                    double x = radius * Math.cos(radians);
                    double z = radius * Math.sin(radians);

                    Location particleLocation = center.clone().add(x, 0, z);
                    explosionLocations.add(particleLocation);
                }

                // Возвращаемся в основной поток для выполнения взрывов и спавна частиц
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
                        world.spawnParticle(Particle.DRAGON_BREATH, explosionLocation, 20, 0.5, 0.5, 0.5, 0.01);

                        // Создаем взрыв на этой позиции
                        world.createExplosion(explosionLocation, 2F, false, true);

                        // Переходим к следующей позиции
                        currentExplosionIndex += stepDistance; // Пропускаем шаг в 3 позиции
                    }
                }.runTaskTimer(TempusEssential.getPlugin(), 40L, 5L); // Задержка перед началом и интервал в 5 тиков между взрывами
            }
        }.runTaskAsynchronously(TempusEssential.getPlugin()); // Асинхронная обработка
    }
}