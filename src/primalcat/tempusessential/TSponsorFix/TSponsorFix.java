package primalcat.tempusessential.TSponsorFix;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TSponsorFix implements Listener {

    // Создаем аналогичный NamespacedKey для проверки мета-данных
    private static final NamespacedKey bigHeadKey = new NamespacedKey("tsponsors", "bighead");

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // Проверяем, является ли взаимодействие с рамкой
        Entity entity = event.getRightClicked();
        if (entity instanceof ItemFrame) {
            // Проверяем, с какой рукой произошло взаимодействие
            EquipmentSlot hand = event.getHand();

            // Получаем предмет из соответствующей руки (основная или вторичная)
            ItemStack itemInHand;
            if (hand == EquipmentSlot.HAND) {
                itemInHand = event.getPlayer().getInventory().getItemInMainHand(); // Основная рука
            } else {
                itemInHand = event.getPlayer().getInventory().getItemInOffHand(); // Вторая рука
            }

            // Проверяем, что предмет в руке — головной элемент
            if (itemInHand != null && itemInHand.getType() == Material.PLAYER_HEAD && itemInHand.hasItemMeta()) {
                // Извлекаем мета-данные предмета
                PersistentDataContainer container = itemInHand.getItemMeta().getPersistentDataContainer();

                // Проверяем наличие нашего ключа в мета-данных
                if (container.has(bigHeadKey, PersistentDataType.BOOLEAN)) {
                    Boolean isBigHead = container.get(bigHeadKey, PersistentDataType.BOOLEAN);

                    // Если это специальный предмет, отменяем событие
                    if (Boolean.TRUE.equals(isBigHead)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("Этот предмет нельзя вставить в рамку!");
                    }
                }
            }
        }
    }
}
