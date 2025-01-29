package primalcat.tempusessential.CustomEnchants;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import primalcat.tempusessential.TempusEssential;

import java.util.Random;

public class EnchantmentListener implements Listener {

    private final float mendingChance = 1;

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Random random = new Random();
        int randomNumber = random.nextInt(100);
        ItemStack item = event.getItem();
        if (event.getExpLevelCost() >= 30 && event.getEnchanter().getLevel() >= 100) {
            if (!item.containsEnchantment(Enchantment.INFINITY)) {
                if (randomNumber < this.mendingChance) {
                    item.addUnsafeEnchantment(Enchantment.MENDING, 1);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event){
        RestorationEnchant.itemDamage(event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        BeheadingEnchant.onEntityDeath(event);
        SyphonEnchant.entityDeath(event);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        ProtectiveSparkEnchant.entityDamage(event);
        FriendlyEndermanEnchant.entityDamage(event);
        TerroristEnchant.entityDamage(event);
    }

//    https://yadi.sk/d/2HZrXC7DUhAwQA

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        LastHeroEnchant.entityDamage(event);
        AirbagEnchant.playerFall(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Вызываем логику для чара Geomancer
        GeomancerEnchant.blockBreak(event);
        MinerInspirationEnchant.bockBreak(event);
        WoodSplitterEnchant.blockBreak(event);
        AncientSeekerEnchant.blockBreak(event);
    }

    @EventHandler
    public void  onProjectileLaunchEvent(ProjectileLaunchEvent event){
        EndlessQuiverEnchant.projectileLaunch(event);
        WaterBoostEnchant.tridentThrow(event);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event){

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        ExplosiveArrowEnchant.projectileHit(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {


        // marks block
        final Block block = event.getBlockPlaced();
//        final Material type = block.getType();
//
//        if (!(type.equals(Material.ANCIENT_DEBRIS))) {
//            return;
//        }

        if (block.hasMetadata("player_placed")) {
            return;
        }

        block.setMetadata("player_placed", new FixedMetadataValue(TempusEssential.getPlugin(), true));
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        HarpoonEnchant.playerFish(event);
    }

}
