package primalcat.tempus.listeners;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import de.tr7zw.nbtapi.NBTContainer;

public class LightBlockListener implements Listener {

    @EventHandler
    private void PlayerRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        if (!player.hasPermission("lightblock.change.interact")) {
            return;
        }

        // Check if the player is holding the LIGHT block
        if (event.getItem() == null || event.getItem().getType() != Material.LIGHT) {
            return;
        }

        BlockData blockData = block.getBlockData();
        if (blockData instanceof Levelled) {
            Levelled levelled = (Levelled) blockData;

            if (levelled.getLevel() == 0) {
                levelled.setLevel(15);
            } else {
                levelled.setLevel(levelled.getLevel() - 1);
            }

            // Create a new block with the updated block data
            World world = block.getWorld();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            Block newBlock = world.getBlockAt(x, y, z);
            newBlock.setBlockData(levelled);
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void PlayerLeftClick(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (block == null){
            return;
        }
        if (event.getAction() != Action.LEFT_CLICK_BLOCK){
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND){
            return;
        }
        if (event.getItem() == null){
            return;
        }
        if (event.getItem().getType() != Material.LIGHT){
            return;
        }
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL){
            return;
        }
        if (block.getType() == Material.LIGHT){
            if (CanBreakBlock(block, player)){
                block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5F, 1.0F);
                block.getWorld().playEffect(block.getLocation(), Effect.EXTINGUISH, 0);

                block.breakNaturally();
            }
        }
    }

    private boolean CanBreakBlock(Block block, Player player){
        BlockBreakEvent event = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

//    @EventHandler
//    public void onItemHeld(PlayerItemHeldEvent event) {
//        Player player = event.getPlayer();
//        ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
//        if (newItem != null && newItem.getType() == Material.LIGHT) {
//            sendLightBlockPacket(player);
//        }
//    }
//
//    private void sendLightBlockPacket(Player player) {
//        // Create a new ItemStack with LIGHT block
//        ItemStack lightBlock = new ItemStack(Material.LIGHT);
//
//        // Send the packet to the player to display the light block
////        player.sendBlockChange(player.getLocation().add(0, 2, 0), lightBlock.get());
//    }
}
