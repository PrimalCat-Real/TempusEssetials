package primalcat.tempusessential.StrongerDragon.phases;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import primalcat.tempusessential.TempusEssential;

public class DragonHorizontalBeamAttack {

    private static final int MAX_LENGTH = 32; // Максимальная длина лучей
    private static final int EXPLOSION_STEP = 3; // Шаг взрыва в блоках

    public static void startHorizontalBeamAttack(Location CENTER_OF_ISLAND, World world) {
        // 8 направлений: восток, запад, север, юг и диагональные
        Vector[] directions = new Vector[]{
                new Vector(1, 0, 0),   // Восток
                new Vector(-1, 0, 0),  // Запад
                new Vector(0, 0, 1),   // Север
                new Vector(0, 0, -1),  // Юг
                new Vector(1, 0, 1),   // Северо-восток
                new Vector(1, 0, -1),  // Юго-восток
                new Vector(-1, 0, 1),  // Северо-запад
                new Vector(-1, 0, -1)  // Юго-запад
        };

        // Создаем лучи частиц во всех 8 направлениях
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Vector direction : directions) {
                    createBeam(world, CENTER_OF_ISLAND.clone(), direction);
                }

                // Через 1.5 секунды запускаем цепной взрыв
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Vector direction : directions) {
                            startChainExplosion(world, CENTER_OF_ISLAND.clone(), direction);
                        }
                    }
                }.runTaskLater(TempusEssential.getPlugin(), 30); // Задержка 1.5 секунды (30 тиков)
            }
        }.runTask(TempusEssential.getPlugin());
    }

    // Метод для создания лучей частиц
    private static void createBeam(World world, Location startLocation, Vector direction) {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            Location particleLocation = startLocation.clone().add(direction.clone().multiply(i));
            world.spawnParticle(Particle.DRAGON_BREATH, particleLocation, 20, 0.3, 0.3, 0.3, 0.02); // Увеличено количество частиц и их размер
        }
    }

    // Метод для создания цепного взрыва
    private static void startChainExplosion(World world, Location startLocation, Vector direction) {
        new BukkitRunnable() {
            int currentDistance = MAX_LENGTH;

            @Override
            public void run() {
                if (currentDistance <= 0) {
                    this.cancel();
                    return;
                }

                Location explosionLocation = startLocation.clone().add(direction.clone().multiply(currentDistance));
                world.createExplosion(explosionLocation, 3.0f, false, true); // Взрыв силой 6

                // Уменьшаем текущую дистанцию на 3 блока
                currentDistance -= EXPLOSION_STEP;
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 0, 10); // Взрыв каждые 10 тиков (ускорено)
    }
}
