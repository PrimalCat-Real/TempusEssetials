package primalcat.tempusessential.StrongerDragon.phases;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import primalcat.tempusessential.StrongerDragon.CustomEnderDragon;
import primalcat.tempusessential.TempusEssential;

import java.util.Collections;
import java.util.List;
public class DragonFireballAttack {
    /**
     * Спавнит фаерболы над головами игроков и направляет их к игрокам.
     *
     * @param players Список игроков, по которым будут запущены фаерболы.
     * @param world Мир, в котором будет происходить атака.
     * @param speed Скорость движения фаерболов.
     */
    public static void shootFireballsAtPlayers(List<Player> players, World world, double speed) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Перемешиваем список игроков
                Collections.shuffle(players);

                // Берем максимум 3 случайных игроков (если их меньше 3, просто берем всех)
                List<Player> selectedPlayers = players.subList(0, Math.min(3, players.size()));

                for (Player player : selectedPlayers) {
                    // Позиция над головой игрока на высоте 50 блоков
                    Location fireballSpawnLocation = player.getLocation().add(0, 50, 0);

                    // Направление от точки спавна к игроку (вниз)
                    Vector direction = player.getLocation().toVector().subtract(fireballSpawnLocation.toVector()).normalize();

                    // Возвращаемся в основной поток для спавна фаербола
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Спавним фаербол над игроком
                            Fireball fireball = world.spawn(fireballSpawnLocation, Fireball.class);
                            fireball.setVelocity(direction.multiply(speed)); // Задаем скорость и направление
                            fireball.setDirection(direction); // Направляем фаербол к игроку
                            fireball.setIsIncendiary(false); // Отключаем поджог
                            fireball.setYield(1.0F); // Радиус взрыва фаербола
                        }
                    }.runTask(TempusEssential.getPlugin());
                }
            }
        }.runTaskAsynchronously(TempusEssential.getPlugin()); // Асинхронная часть
    }
}
