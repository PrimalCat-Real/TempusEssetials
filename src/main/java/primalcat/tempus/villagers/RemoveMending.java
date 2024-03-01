package primalcat.tempus.villagers;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.BOOK;

public class RemoveMending implements Listener {
    @EventHandler
    private void onAttemptTrade(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof Villager) {
            if (inventory instanceof MerchantInventory) {
                Merchant merchant = ((MerchantInventory)inventory).getMerchant();
                List<MerchantRecipe> recipes = new ArrayList(merchant.getRecipes());
                merchant.getRecipes().forEach((recipe) -> {

                    ItemStack result = recipe.getResult();
                    if (result.getType() == Material.DIAMOND_HELMET ||
                            result.getType() == Material.DIAMOND_CHESTPLATE ||
                            result.getType() == Material.DIAMOND_LEGGINGS ||
                            result.getType() == Material.DIAMOND_BOOTS ||
                            result.getType() == Material.DIAMOND_SWORD ||
                            result.getType() == Material.DIAMOND_PICKAXE ||
                            result.getType() == Material.DIAMOND_AXE ||
                            result.getType() == Material.DIAMOND_SHOVEL ||
                            result.getType() == Material.DIAMOND_HOE) {


                        List<Material> golden_items = Arrays.asList(
                                Material.GOLDEN_HELMET,
                                Material.GOLDEN_CHESTPLATE,
                                Material.GOLDEN_LEGGINGS,
                                Material.GOLDEN_BOOTS,
                                Material.GOLDEN_SWORD,
                                Material.GOLDEN_PICKAXE,
                                Material.GOLDEN_AXE,
                                Material.GOLDEN_SHOVEL,
                                Material.GOLDEN_HOE
                        );

                        Material randomGoldenItem = golden_items.get(new Random().nextInt(golden_items.size()));
                        ItemStack goldenItem = new ItemStack(randomGoldenItem);
                        MerchantRecipe goldenItemRecipe = new MerchantRecipe(goldenItem, 1);
                        for (ItemStack ingredient : recipe.getIngredients()) {
                            if (ingredient != null) {
                                goldenItemRecipe.addIngredient(ingredient);
                            }
                        }
                        recipes.add(goldenItemRecipe);
                        recipes.remove(recipe);
                    }

                    if (result.getType() == Material.ENCHANTED_BOOK) {
                        ItemMeta itemMeta = result.getItemMeta();
                        if (itemMeta instanceof EnchantmentStorageMeta) {
                            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)itemMeta;
                            if (enchantmentStorageMeta.hasStoredEnchant(Enchantment.MENDING)) {
                                ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
                                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
                                meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 6, true);
                                enchantedBook.setItemMeta(meta);
                                MerchantRecipe enchantedBookRecipe = new MerchantRecipe(enchantedBook, 1);
                                for (ItemStack ingredient : recipe.getIngredients()) {
                                    if (ingredient != null) {
                                        enchantedBookRecipe.addIngredient(ingredient);
                                    }
                                }
                                recipes.add(enchantedBookRecipe);
                                recipes.remove(recipe);
                            }
                        }
                    }

                });
                merchant.setRecipes(recipes);
            }
        }
    }
}
