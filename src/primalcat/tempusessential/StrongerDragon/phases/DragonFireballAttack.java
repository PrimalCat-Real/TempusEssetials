package primalcat.tempusessential.StrongerDragon.phases;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import primalcat.tempusessential.StrongerDragon.CustomEnderDragon;

import java.util.List;

public class DragonFireballAttack {
    /**
     * Запускает фаерболы по игрокам из указанной точки.
     *
     * @param players Список игроков, по которым будут запущены фаерболы.
     * @param fireballOriginLocation Местоположение, откуда будут запускаться фаерболы.
     * @param world Мир, в котором будет происходить атака.
     */
    public static void shootFireballsAtPlayers(List<Player> players, Location fireballOriginLocation, World world, double speed) {
        for (Player player : players) {
            double offsetDistance = 2 + CustomEnderDragon.randomBetween(5, 10);

            // Позиция игрока с учетом его роста (на уровне глаз)
            Location playerLocation = player.getLocation().add(0, player.getEyeHeight(), 0);

            // Вычисляем направление от дракона к игроку
            Vector direction = playerLocation.toVector().subtract(fireballOriginLocation.toVector()).normalize();

            // Смещение по направлению и подъем по оси Y на 10 блоков
            Location fireballSpawnLocation = fireballOriginLocation.clone()
                    .add(direction.multiply(offsetDistance))
                    .add(0, 20, 0); // Поднимаем на 10 блоков вверх

            // Спавн фаербола
            Fireball fireball = world.spawn(fireballSpawnLocation, Fireball.class);
            fireball.setVelocity(direction.multiply(speed));
            fireball.setDirection(direction); // Задаем направление движения фаербола
            fireball.setIsIncendiary(false); // Отключаем поджог
            fireball.setYield(4.0F); // Задаем радиус взрыва
        }
    }
}
