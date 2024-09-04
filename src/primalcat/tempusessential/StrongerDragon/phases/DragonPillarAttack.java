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
        int step = 3;
        int heightAboveTarget = 16;

        double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 30;
        double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 30;

        Location startLocation = targetLocation.clone().add(offsetX, heightAboveTarget, offsetZ);

        List<Location> explosionLocations = new ArrayList<>();

        int currentY = startLocation.getBlockY();
        while (currentY > world.getMinHeight()) {
            Location currentLocation = new Location(world, startLocation.getX(), currentY, startLocation.getZ());
            Block block = world.getBlockAt(currentLocation);

            if (block.getType().isSolid()) {
                break;
            }

            explosionLocations.add(currentLocation);
            currentY -= step;
        }

        spawnParticles(explosionLocations, world);

        // Асинхронная часть, которая подготавливает данные
        new BukkitRunnable() {
            @Override
            public void run() {
                // Возвращаемся в основной поток для выполнения взрывов
                new BukkitRunnable() {
                    int currentIndex = 0;

                    @Override
                    public void run() {
                        if (currentIndex >= explosionLocations.size()) {
                            this.cancel();
                            return;
                        }

                        Location explosionLocation = explosionLocations.get(currentIndex);

                        // Выполняем взрыв в синхронном потоке
                        world.createExplosion(explosionLocation, 3.0f, false, true);

                        currentIndex++;
                    }
                }.runTaskTimer(TempusEssential.getPlugin(), 1L, 5L); // Возвращаемся в основной поток на следующий тик
            }
        }.runTaskAsynchronously(TempusEssential.getPlugin()); // Асинхронная часть
    }

    private static void spawnParticles(List<Location> locations, World world) {
        for (Location location : locations) {
            world.spawnParticle(Particle.DRAGON_BREATH, location, 10, 0, 0, 0, 0);
        }
    }

    public static void startMultipleDragonPillarAttacks(Location centerLocation, World world, int numberOfAttacks) {
        numberOfAttacks = Math.min(numberOfAttacks, 5);

        for (int i = 0; i < numberOfAttacks; i++) {
            CompletableFuture.runAsync(() -> {
                startDragonPillarAttack(centerLocation, world);
            });
        }
    }
}
