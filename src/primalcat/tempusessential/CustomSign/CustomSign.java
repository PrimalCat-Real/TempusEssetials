package primalcat.tempusessential.CustomSign;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import primalcat.tempusessential.TempusEssential;

import java.util.UUID;

public class CustomSign implements Listener {
    private Plugin plugin = TempusEssential.getPlugin();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Action action = event.getAction();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Block clickedBlock = event.getClickedBlock();
            Material blockType = clickedBlock.getType();

            if (isSignBlock(blockType) && handItem.getType() == Material.AMETHYST_SHARD) {
                if (!player.hasPermission("tempusessential.sethologram")) return;
                // Создание голограммы при наличии аметистового осколка в руке
                createTextDisplay((Sign) clickedBlock.getState(), player);
                clickedBlock.setType(Material.AIR); // Удаление таблички после создания голограммы
                if (player.getGameMode() != GameMode.CREATIVE) {
                    handItem.setAmount(handItem.getAmount() - 1); // Расход аметистового осколка, если не в режиме творчества
                }
            }

            if (handItem.getType() == Material.POTION) {
                Sign sign = (Sign) clickedBlock.getState();
                if (sign.isGlowingText()) {
                    sign.setGlowingText(false);
                    sign.update();
                    player.playSound(player.getLocation(), Sound.BLOCK_POINTED_DRIPSTONE_DRIP_WATER, 1.0f, 1.0f);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        handItem.setAmount(handItem.getAmount() - 1); // Расход пузырька воды
                        player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE)); // Возврат стеклянной бутылки
                    }
                    event.setCancelled(true);
                }
            }
        }

        if ((action == Action.RIGHT_CLICK_BLOCK) && handItem.getType() == Material.AMETHYST_SHARD) {
            if (!player.hasPermission("tempusessential.removehologram")) return;
            // Поиск и удаление ближайшей голограммы при клике в воздух или по другому блоку с аметистовым осколком в руке
            TextDisplay nearestDisplay = findNearestTextDisplay(player);
            if (nearestDisplay != null) {
                nearestDisplay.remove();
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
//                player.sendMessage(ChatColor.GREEN + "Голограмма удалена.");
                if (player.getGameMode() != GameMode.CREATIVE) {
                    handItem.setAmount(handItem.getAmount() - 1);
                }
            }
        }
    }

    private TextDisplay findNearestTextDisplay(Player player) {
        return (TextDisplay) player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5,
                        entity -> entity instanceof TextDisplay).stream()
                .min((e1, e2) -> Double.compare(e1.getLocation().distance(player.getLocation()), e2.getLocation().distance(player.getLocation())))
                .orElse(null);
    }

    private void createTextDisplay(Sign sign, Player player) {
        Vector signRotation = getSignRotation(sign.getBlockData());
        if (signRotation != null) {
            TextDisplay textDisplay = (TextDisplay) player.getWorld().spawnEntity(sign.getLocation().add(0.5, 0.5, 0.5).setDirection(signRotation), EntityType.TEXT_DISPLAY);
            applyStylesToTextDisplay(sign, textDisplay);
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1.0f, 1.0f);
        }
    }

    private Vector getSignRotation(BlockData data) {
        if (data instanceof Directional) {
            return ((Directional) data).getFacing().getDirection();
        } else if (data instanceof Rotatable) {
            return ((Rotatable) data).getRotation().getDirection();
        }
        return null;
    }

    private void applyStylesToTextDisplay(Sign sign, TextDisplay textDisplay) {
        TextColor textColor = TextColor.color(sign.getColor().getColor().asRGB());
        Component textComponent = Component.text(String.join("\n", sign.getLines()), textColor);
        textDisplay.setGlowing(sign.isGlowingText());
        textDisplay.setText(LegacyComponentSerializer.legacySection().serialize(textComponent));
    }

    private void consumeItem(Player player, ItemStack item) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }
    }


    private boolean isSignBlock(Material material) {
        return material.name().endsWith("_SIGN");
    }

}
