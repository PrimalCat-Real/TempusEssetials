package primalcat.tempusessential.ShulkerBag;

import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import static com.cryptomorin.xseries.XBlock.isSimilar;

public class ShullkerBag implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!event.getPlayer().isSneaking())
            return;

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if(isShulkerBox(itemInHand)){
            event.setCancelled(true);  // Отменяем событие, чтобы предотвратить стандартные действия
            openShulkerInventory(player, itemInHand);
        }
    }
    private boolean isShulkerBox(ItemStack item) {
        return item != null && item.getType().toString().endsWith("SHULKER_BOX");
    }

    private void openShulkerInventory(Player player, ItemStack shulkerBoxItem) {
        BlockStateMeta meta = (BlockStateMeta) shulkerBoxItem.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        Inventory shulkerInventory = shulkerBox.getInventory();
        player.openInventory(shulkerInventory);
        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.SHULKER_BOX && event.getInventory().getHolder() instanceof ShulkerBox) {
            Player player = (Player) event.getPlayer();
            ItemStack current = player.getInventory().getItemInMainHand();
            if (current != null && isShulkerBox(current)) { // Проверяем, что предмет является шалкером
                updateShulkerBoxItem(event.getInventory(), current, player);
            }
        }
    }

    private void updateShulkerBoxItem(Inventory inventory, ItemStack shulkerBoxItem, Player player) {
        BlockStateMeta meta = (BlockStateMeta) shulkerBoxItem.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        shulkerBox.getInventory().setContents(inventory.getContents()); // Сохраняем содержимое обратно в шалкер
        meta.setBlockState(shulkerBox); // Обновляем BlockStateMeta
        shulkerBoxItem.setItemMeta(meta); // Сохраняем обновлённый meta в ItemStack
        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1.0F, 1.0F);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Inventory openInventory = event.getPlayer().getOpenInventory().getTopInventory();
        if (openInventory.getHolder() instanceof ShulkerBox) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ShulkerBox) { // Проверяем, является ли холдер инвентаря шалкером
            event.setCancelled(true); // Отменяем все действия с инвентарем шалкера
        }
    }
}
