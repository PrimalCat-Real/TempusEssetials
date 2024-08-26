package primalcat.tempusessential.StrongerDragon.phases;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.TempusEssential;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class DragonPillarAttack {

    public static void startDragonPillarAttack(Location targetLocation, World world) {
        int step = 3; // Шаг для каждого прохода по столбу
        int heightAboveTarget = 16; // Поднимаемся на 16 блоков над целевой точкой

        // Генерируем случайное смещение для атаки
        double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 30; // Случайное смещение по X в пределах 30 блоков
        double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 30; // Случайное смещение по Z в пределах 30 блоков

        // Обновляем целевую локацию с учетом случайного смещения
        Location startLocation = targetLocation.clone().add(offsetX, heightAboveTarget, offsetZ);

        // Список всех точек для взрывов и частиц
        List<Location> explosionLocations = new ArrayList<>();

        // Собираем все точки от вершины до первого солидного блока
        int currentY = startLocation.getBlockY();
        while (currentY > world.getMinHeight()) {
            Location currentLocation = new Location(world, startLocation.getX(), currentY, startLocation.getZ());
            Block block = world.getBlockAt(currentLocation);

            // Если найден солидный блок, прекращаем сбор точек
            if (block.getType().isSolid()) {
                break;
            }

            // Добавляем точку для взрыва
            explosionLocations.add(currentLocation);

            // Опускаемся ниже на шаг
            currentY -= step;
        }

        // Спавним частицы на всех точках
        spawnParticles(explosionLocations, world);

        // Запускаем цепной взрыв через 2 секунды после спавна частиц
        new BukkitRunnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex >= explosionLocations.size()) {
                    this.cancel();
                    return;
                }

                // Получаем текущую позицию для взрыва
                Location explosionLocation = explosionLocations.get(currentIndex);
                world.createExplosion(explosionLocation, 3.0f, false, true); // Взрыв силой 6


                // Переходим к следующей позиции
                currentIndex++;
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 60L, 5L); // Задержка перед первым взрывом 40 тиков (2 секунды), потом взрывы каждые 5 тиков
    }

    // Спавним частицы по всем точкам
    private static void spawnParticles(List<Location> locations, World world) {
        for (Location location : locations) {
            world.spawnParticle(Particle.DRAGON_BREATH, location, 10, 0, 0, 0, 0);
            // Выводим координаты частиц
        }
    }

    // Метод для запуска до 5 атак асинхронно
    public static void startMultipleDragonPillarAttacks(Location centerLocation, World world, int numberOfAttacks) {
        // Убедимся, что количество атак не превышает 5
        numberOfAttacks = Math.min(numberOfAttacks, 5);

        for (int i = 0; i < numberOfAttacks; i++) {
            CompletableFuture.runAsync(() -> {
                // Запускаем атаку с рандомным смещением
                startDragonPillarAttack(centerLocation, world);
            });
        }
    }
}

