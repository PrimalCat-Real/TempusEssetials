package primalcat.tempusessential.RightClickFarmland;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RightClickFarmland implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("tempusessentials.rightclickfarmland")) {
            return; // Если нет разрешения
        }
        // Проверяем, что действие - это правый клик по блоку
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Block clickedBlock = event.getClickedBlock();

            // Проверяем, что блок - это растение, способное к росту
            if (clickedBlock.getBlockData() instanceof Ageable) {
                Ageable ageable = (Ageable) clickedBlock.getBlockData();

                // Проверяем, достигло ли растение максимального возраста
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    // Убираем урожай
                    harvestCrops(event, clickedBlock, ageable);
                }
            }
        }
    }

    private void harvestCrops(PlayerInteractEvent event, Block block, Ageable ageable) {
        // Получаем выпадающие предметы при уборке

        for (ItemStack drop : block.getDrops()) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
        // Сбрасываем возраст растения до начального
        ageable.setAge(0);
        block.setBlockData(ageable, true);

        // Проигрываем звук сбора урожая
        block.getWorld().playSound(block.getLocation(), Sound.ITEM_CROP_PLANT, 1.0F, 1.0F);

        //необходимо для логики уборки
        if (!event.getPlayer().isSneaking()) { // Например, если игрок не приседает
            PlayerInventory inventory = event.getPlayer().getInventory();
            ItemStack seed = new ItemStack(Material.WHEAT_SEEDS); // Замените на нужный вид семян
            if (inventory.contains(seed.getType())) {
                inventory.removeItem(seed);
            }
        }

        // Отменяем стандартное взаимодействие (например, чтобы не трамбовать землю)
        event.setCancelled(true);
    }
}
