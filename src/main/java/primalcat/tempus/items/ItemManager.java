package primalcat.tempus.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import primalcat.tempus.TempusEssentials;

public class ItemManager implements Listener {

    private static final ItemStack light = new ItemStack(Material.LIGHT);
    private static final NamespacedKey light_craft_key = new NamespacedKey(TempusEssentials.getPlugin(), "light_recipe");

    private static final ItemStack bundle = new ItemStack(Material.BUNDLE);
    private static final NamespacedKey bundle_craft_key = new NamespacedKey(TempusEssentials.getPlugin(), "bundle_recipe");

    public static void init(){
        createCrafts();
    }

    private static void createCrafts(){
        ShapedRecipe recipeLight = new ShapedRecipe(light_craft_key, light);
        recipeLight.shape(
                "GGG",
                "GBG",
                " I "
        );
        recipeLight.setIngredient('B', Material.GLOW_BERRIES);
        recipeLight.setIngredient('I', Material.IRON_BLOCK);
        recipeLight.setIngredient('G', Material.GLOWSTONE_DUST);

        ShapedRecipe bundleLight = new ShapedRecipe(bundle_craft_key, bundle);
        bundleLight.shape(
                "SRS",
                "R R",
                "RRR"
        );
        bundleLight.setIngredient('S', Material.STRING);
        bundleLight.setIngredient('R', Material.RABBIT_HIDE);

        Bukkit.getServer().addRecipe(bundleLight);
        Bukkit.getServer().addRecipe(recipeLight);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        event.getPlayer().discoverRecipe(light_craft_key);
        event.getPlayer().discoverRecipe(bundle_craft_key);
    }

    public static void removeCrafts() {
        Bukkit.getServer().removeRecipe(light_craft_key);
        Bukkit.getServer().removeRecipe(bundle_craft_key);
    }
}
