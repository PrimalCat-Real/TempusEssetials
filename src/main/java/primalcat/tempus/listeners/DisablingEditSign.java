package primalcat.tempus.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;
import static primalcat.tempus.TempusEssentials.plugin;

public class DisablingEditSign implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        Sign sign = (Sign) block.getState();
        PersistentDataContainer container = sign.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "textdisplay");

        if (container.has(key, PersistentDataType.STRING)) {
            UUID textDisplayId = UUID.fromString(container.get(key, PersistentDataType.STRING));
            removeTextDisplay(textDisplayId, block.getWorld());
            container.remove(key);
            sign.update();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = e.getClickedBlock();
            if (isSignBlock(clickedBlock.getType())) {
                Sign sign = (Sign) clickedBlock.getState();
                if (!sign.isEditable()) return;
                Player player = e.getPlayer();
                ItemStack mainHandItem = player.getInventory().getItemInMainHand();

                if(mainHandItem.getType() == Material.POTION){
                    e.setCancelled(true);
                    PersistentDataContainer container = sign.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey(plugin, "textdisplay");
                    if (container.has(key, PersistentDataType.STRING)) {
                        // Удаляем существующий TextDisplay
                        UUID textDisplayId = UUID.fromString(container.get(key, PersistentDataType.STRING));
                        removeTextDisplay(textDisplayId, player.getWorld());
                    }
                    if(sign.isGlowingText()){
                        sign.setGlowingText(false);
                        sign.update();
                    }
                    if(player.getGameMode() != GameMode.CREATIVE){
                        player.getInventory().setItemInMainHand(new ItemStack(Material.GLASS_BOTTLE));
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_POINTED_DRIPSTONE_DRIP_WATER, 1.0f, 1.0f);

                }

                if (mainHandItem.getType() != Material.AMETHYST_SHARD) return;
                if (!player.hasPermission("tempusessential.sethologram")) return;

                e.setCancelled(true);
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    if (mainHandItem.getAmount() > 1) {
                        mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                }


                PersistentDataContainer container = sign.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "textdisplay");
                if (container.has(key, PersistentDataType.STRING)) {
                    // Удаляем существующий TextDisplay
                    UUID textDisplayId = UUID.fromString(container.get(key, PersistentDataType.STRING));
                    removeTextDisplay(textDisplayId, player.getWorld());
                }

                // Создаем новый TextDisplay
                String text = String.join("\n", sign.getLines());

                Vector signRotation = null; // Инициализация вектора направления
                BlockData signData = sign.getBlockData();

                if (signData instanceof Directional) {
                    // Это настенная табличка
                    Directional directional = (Directional) signData;
                    signRotation = directional.getFacing().getDirection();
                } else if (signData instanceof Rotatable) {
                    // Это напольная табличка
                    Rotatable rotatable = (Rotatable) signData;
                    signRotation = rotatable.getRotation().getDirection();
                }
//                System.out.println(sign.getBlockData() instanceof Directional);

                if (signRotation != null) {
                    // Создание TextDisplay только если установлено направление
                    TextDisplay textDisplay = (TextDisplay) player.getWorld().spawnEntity(sign.getLocation().add(0.5, 1.3, 0.5).setDirection(signRotation), EntityType.TEXT_DISPLAY);
                    applyStylesToTextDisplay(sign, textDisplay, text);
                    container.set(key, PersistentDataType.STRING, textDisplay.getUniqueId().toString());
                }
                sign.update();
            }
        }
    }


    private void removeTextDisplay(UUID textDisplayId, World world) {
        for (Entity entity : world.getEntitiesByClass(TextDisplay.class)) {
            if (entity.getUniqueId().equals(textDisplayId)) {
                entity.remove();
                break;
            }
        }
    }

    private void applyStylesToTextDisplay(Sign sign, TextDisplay textDisplay, String text) {
        DyeColor color = sign.getColor();
        boolean isGlowing = sign.isGlowingText();

        // @TODO вообще можно через persistence data установить хекс цвет

        // Преобразование DyeColor в java.awt.Color и затем в TextColor
        java.awt.Color awtColor = new java.awt.Color(color.getColor().asRGB());
        TextColor textColor = TextColor.color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());

        // Создание компонента с нужным текстом и цветом
        Component component = Component.text(text).color(textColor);

        // Настройка сериализатора для использования символа '§'
        LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
                .character('§')
                .build();

        // Применение свечения и сериализация текста с использованием сериализатора
        textDisplay.setGlowing(isGlowing);
        textDisplay.setGlowColorOverride(color.getColor());
        textDisplay.setText(serializer.serialize(component));

    }

    private boolean isSignBlock(Material material) {
        return material.name().endsWith("_SIGN");
    }
}
