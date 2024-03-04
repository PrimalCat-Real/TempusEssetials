package primalcat.tempus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static primalcat.tempus.TempusEssentials.plugin;

public class DisablingEditSign implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = e.getClickedBlock();
            if (isSignBlock(clickedBlock.getType())) {
                Sign sign = (Sign) clickedBlock.getState();
                if (!sign.isEditable()) return;
                Player player = e.getPlayer();
                ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                if (mainHandItem.getType() == Material.AMETHYST_SHARD) {
                    if (mainHandItem.getAmount() == 1) {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    } else {
                        mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                    }
                    sign.setEditable(false);
                    sign.update();
                }
            }
        }
    }

    private boolean isSignBlock(Material material) {
        return material.name().endsWith("_SIGN");
    }
}
