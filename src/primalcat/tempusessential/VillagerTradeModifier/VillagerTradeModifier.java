package primalcat.tempusessential.VillagerTradeModifier;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import primalcat.tempusessential.TempusEssential;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VillagerTradeModifier implements Listener {
    private Plugin plugin = TempusEssential.getPlugin();

    @EventHandler
    private void onAttemptTrade(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof Villager)) return;
        if (!(event.getInventory() instanceof MerchantInventory)) return;


        MerchantInventory inventory = (MerchantInventory) event.getInventory();
        Merchant merchant = inventory.getMerchant();
        Villager villager = (Villager) inventory.getHolder();
        String professionKey = villager.getProfession().toString().toLowerCase();
//        System.out.println("Opening villager trade menu for profession: " + professionKey);

        // Получаем список замен из конфигурации для данной профессии
        List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
        List<MerchantRecipe> newRecipes = new ArrayList<>();

        List<Map<?, ?>> trades = plugin.getConfig().getMapList("modules.villager-trade-modifier." + professionKey + ".trades");

        // Проверяем каждый рецепт на наличие соответствия конфигурации
        recipes.forEach(recipe -> {
            ItemStack result = recipe.getResult();
            boolean replaced = false;

            for (Map<?, ?> trade : trades) {
                Map<?, ?> originalItemConfig = (Map<?, ?>) trade.get("originalItem");
                if (originalItemConfig != null && Material.valueOf((String) originalItemConfig.get("item")) == result.getType()) {
                    ItemStack newItem = createNewItem(trade, result);
                    if (newItem != null) {
                        MerchantRecipe newRecipe = new MerchantRecipe(newItem, recipe.getUses(), recipe.getMaxUses(), recipe.hasExperienceReward());
                        newRecipe.setIngredients(recipe.getIngredients());
                        newRecipes.add(newRecipe);
                        replaced = true;
                        break; // Stop once replaced
                    }
                }
            }

            if (!replaced) {
                newRecipes.add(recipe); // Add the original recipe if no replacement was found
            }
        });

        merchant.setRecipes(newRecipes);


    }

    private ItemStack createNewItem(Map<?, ?> trade, ItemStack originalItem) {
        Map<?, ?> newItemConfig = (Map<?, ?>) trade.get("newItem");
        if (newItemConfig == null) return null;

        // Проверяем, нужно ли учитывать зачарования у исходного предмета
        Map<?, ?> originalItemConfig = (Map<?, ?>) trade.get("originalItem");
        if (originalItemConfig.containsKey("enchantments")) {
            List<Map<?, ?>> requiredEnchantments = (List<Map<?, ?>>) originalItemConfig.get("enchantments");
            boolean enchantmentsMatch = checkEnchantments(originalItem, requiredEnchantments);
            if (!enchantmentsMatch) {
                return null; // Если зачарования не совпадают, не создаем новый предмет
            }
        }

        // Создание нового предмета
        Material material = Material.valueOf((String) newItemConfig.get("item"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // Назначение новых лоров и зачарований
        if (newItemConfig.containsKey("lore")) {
            List<String> lore = (List<String>) newItemConfig.get("lore");
            meta.setLore(lore);
        }

        if (newItemConfig.containsKey("enchantments")) {
            List<Map<?, ?>> enchantments = (List<Map<?, ?>>) newItemConfig.get("enchantments");
            for (Map<?, ?> enchantment : enchantments) {
                Enchantment ench = Enchantment.getByName((String) enchantment.get("type"));
                int level = (Integer) enchantment.get("level");
                meta.addEnchant(ench, level, true);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private boolean checkEnchantments(ItemStack item, List<Map<?, ?>> requiredEnchantments) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof EnchantmentStorageMeta)) {
            if (!item.getEnchantments().isEmpty()) {
                for (Map<?, ?> enchantment : requiredEnchantments) {
                    Enchantment requiredEnchantment = Enchantment.getByName((String) enchantment.get("type"));
                    int requiredLevel = (Integer) enchantment.get("level");
                    if (!item.containsEnchantment(requiredEnchantment) || item.getEnchantmentLevel(requiredEnchantment) != requiredLevel) {
                        return false; // Необходимое зачарование отсутствует или уровень не соответствует
                    }
                }
            } else {
                return false; // Нет зачарований на предмете
            }
        } else {
            EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) meta;
            for (Map<?, ?> enchantment : requiredEnchantments) {
                Enchantment requiredEnchantment = Enchantment.getByName((String) enchantment.get("type"));
                int requiredLevel = (Integer) enchantment.get("level");
                if (!enchantmentMeta.hasStoredEnchant(requiredEnchantment) || enchantmentMeta.getStoredEnchantLevel(requiredEnchantment) != requiredLevel) {
                    return false; // Необходимое зачарование отсутствует или уровень не соответствует
                }
            }
        }
        return true; // Все зачарования соответствуют требованиям
    }
}
