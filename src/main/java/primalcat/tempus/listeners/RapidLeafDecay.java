package primalcat.tempus.listeners;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import primalcat.tempus.TempusEssentials;

import java.util.*;

public class RapidLeafDecay implements Listener {
    private long breakDelay = 5L;
    private long decayDelay = 2L;
    private static final List<Block> scheduledBlocks = new ArrayList();

    public static void clearScheduledBlocks() {
        scheduledBlocks.clear();
    }

    private static final List<BlockFace> NEIGHBORS;

    public RapidLeafDecay() {
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onBlockBreak(BlockBreakEvent event) {
        this.onBlockRemove(event.getBlock(), this.breakDelay);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onLeavesDecay(LeavesDecayEvent event) {
        this.onBlockRemove(event.getBlock(), this.decayDelay);
    }

    private void onBlockRemove(Block oldBlock, long delay) {
        if (Tag.LOGS.isTagged(oldBlock.getType()) || Tag.LEAVES.isTagged(oldBlock.getType())) {
            Collections.shuffle(NEIGHBORS);
            Iterator var4 = NEIGHBORS.iterator();

            while(var4.hasNext()) {
                BlockFace neighborFace = (BlockFace)var4.next();
                Block block = oldBlock.getRelative(neighborFace);
                if (Tag.LEAVES.isTagged(block.getType())) {
                    Leaves leaves = (Leaves)block.getBlockData();
                    if (!leaves.isPersistent() && !this.scheduledBlocks.contains(block)) {
                        TempusEssentials.getPlugin().getServer().getScheduler().runTaskLater(TempusEssentials.getPlugin(), () -> {
                            this.decay(block);
                        }, delay);
                        this.scheduledBlocks.add(block);
                    }
                }
            }

        }
    }

    private boolean decay(Block block) {
        if (!this.scheduledBlocks.remove(block)) {
            return false;
        } else if (!block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
            return false;
        } else if (!Tag.LEAVES.isTagged(block.getType())) {
            return false;
        } else {
            Leaves leaves = (Leaves)block.getBlockData();
            if (leaves.isPersistent()) {
                return false;
            } else if (leaves.getDistance() < 7) {
                return false;
            } else {
                LeavesDecayEvent event = new LeavesDecayEvent(block);
                TempusEssentials.getPlugin().getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                } else {
                    block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0.5, 0.5, 0.5), 8, 0.2, 0.2, 0.2, 0.0, block.getType().createBlockData());
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 0.05F, 1.2F);
                    block.breakNaturally();
                    return true;
                }
            }
        }
    }

    static {
        NEIGHBORS = Arrays.asList(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN);
    }
}
