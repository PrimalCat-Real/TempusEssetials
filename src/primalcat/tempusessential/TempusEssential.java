package primalcat.tempusessential;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.willfp.ecoenchants.enchant.EcoEnchant;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import primalcat.tempusessential.BookManager.CopyBook;
//import primalcat.tempusessential.BossesMute.BossesMute;
import primalcat.tempusessential.BossesMute.BossesMute;
import primalcat.tempusessential.Corpes.CorpseCommand;
import primalcat.tempusessential.Corpes.CorpsePool;
import primalcat.tempusessential.CustomEnchants.EnchantmentListener;
import primalcat.tempusessential.CustomMobDrops.MobDrops;
import primalcat.tempusessential.CustomMobDrops.ShardCommand;
import primalcat.tempusessential.CustomSign.CustomSign;
import primalcat.tempusessential.DropChanceFix.DropChanceFix;
import primalcat.tempusessential.Fog.FogCommand;
import primalcat.tempusessential.Fog.FogListener;
import primalcat.tempusessential.Fog.FogPacketHandler;
import primalcat.tempusessential.KillEmptyBoats.KillEmptyBoats;
import primalcat.tempusessential.MultiworldJoinFix.PlayerLocationListener;
import primalcat.tempusessential.MultiworldJoinFix.SaveAllPlayerLocationsCommand;
import primalcat.tempusessential.NetherPortal.CustomNetherPortalListener;
import primalcat.tempusessential.PlayTime.PlayTimeCommand;
import primalcat.tempusessential.PlayTime.PlayTimeIconPlaceholder;
import primalcat.tempusessential.QuestItems.FateCrystal;
import primalcat.tempusessential.RPNames.RPNamePlaceholder;
import primalcat.tempusessential.RPNames.SetRpNickCommand;
import primalcat.tempusessential.RapidLeafDecay.RapidLeafDecay;
import primalcat.tempusessential.ResourcepackServer.*;
import primalcat.tempusessential.RightClickFarmland.RightClickFarmland;
import primalcat.tempusessential.StopItemsOnDeath.StopItemsOnDeath;
import primalcat.tempusessential.StrongerDragon.*;
import primalcat.tempusessential.TABaddon.ColorTabNameCommand;
import primalcat.tempusessential.TSponsorFix.TSponsorFix;
import primalcat.tempusessential.VillagerTradeModifier.VillagerTradeModifier;
import primalcat.tempusessential.placeholder.LocalPlaceholder;
import primalcat.tempusessential.utils.SQLUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class TempusEssential extends JavaPlugin {

    private static Plugin plugin;
    public static Map<String, String> customRpNicks;
    public static ProtocolManager protocolManager;

    public static Plugin getPlugin() {
        return plugin;
    }
    public static final String CUSTOM_PAYLOAD_CHANNEL = "dicepack:custom_channel";
    private ResourcePackServer resourcePackServer;

    @Override
    public void onLoad() {
        PacketEventsAPI<?> packetEvents = SpigotPacketEventsBuilder.build(this);
        PacketEvents.setAPI(packetEvents);
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();

        initProtocolLib();
        // register placeholder
        papiHook();
        registerModules();
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("template");
       if(resourcePackServer != null){
           Bukkit.getMessenger().registerOutgoingPluginChannel(this, CUSTOM_PAYLOAD_CHANNEL);
           Bukkit.getMessenger().registerIncomingPluginChannel(this, CUSTOM_PAYLOAD_CHANNEL, new CustomPayloadMessageListener());
       }
//        protocolManager.addPacketListener(new CustomPayloadListener(this));
    }


    @Override
    public void onDisable() {
        RapidLeafDecay.clearScheduledBlocks();
        papiUnhook();
        PacketEvents.getAPI().terminate();
        if (resourcePackServer != null) {
            resourcePackServer.stop();
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, CUSTOM_PAYLOAD_CHANNEL);
            getServer().getMessenger().unregisterIncomingPluginChannel(this, CUSTOM_PAYLOAD_CHANNEL);
        }

        if (protocolManager != null) {
            protocolManager.removePacketListeners(this);
        }

        if (getConfig().getBoolean("modules.fog-danger.enabled")) {
            FogPacketHandler.disableFog();
        }

    }


    private void papiHook() {
        System.out.println("hooking " + Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayTimeIconPlaceholder(this).register();
            new RPNamePlaceholder().register();
        }
    }

    private void papiUnhook() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayTimeIconPlaceholder(this).unregister();
            new RPNamePlaceholder().unregister();
        }
    }

    private void initProtocolLib(){
        protocolManager = ProtocolLibrary.getProtocolManager();
        if (getConfig().getBoolean("modules.bosses-mute")) {
            protocolManager.addPacketListener(new BossesMute(this));
        }
        if (getConfig().getBoolean("modules.fog-danger.enabled")) {

            PacketEvents.getAPI().init();
            PacketEvents.getAPI().getEventManager().registerListener(new FogPacketHandler(this));
        }
//        protocolManager.addPacketListener(new ChatPacketModifier(this));

    }

    private void registerModules(){
        if(getConfig().getBoolean("modules.quest-items.enabled")){
            getServer().getPluginManager().registerEvents(new FateCrystal(), this);
        }
        if(getConfig().getBoolean("modules.custom-enchanting.enabled")){
            getServer().getPluginManager().registerEvents(new EnchantmentListener(), this);
        }

        //PluginManager pluginManager = Bukkit.getPluginManager();
        if (getConfig().getBoolean("modules.custom-drops.enabled")) {

            getServer().getPluginManager().registerEvents(new MobDrops(), this);
            getCommand("givemobshard").setExecutor(new ShardCommand());
        }

        if (getConfig().getBoolean("modules.fog-danger.enabled")){
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new FogListener(), this);
            getCommand("removefog").setExecutor(new FogCommand());
        }
        if(getConfig().getBoolean("modules.mending-from-enchanting.enabled")){
            getServer().getPluginManager().registerEvents(new EnchantmentListener(), this);
        }
        if (getConfig().getBoolean("modules.resource-pack.enabled")) {
//            getServer().getMessenger().registerOutgoingPluginChannel(this, CUSTOM_PAYLOAD_CHANNEL);
//            getServer().getMessenger().registerIncomingPluginChannel(this, CUSTOM_PAYLOAD_CHANNEL, new ForceResourcePackOnJoin());
            File configFolder = getDataFolder();
            resourcePackServer = new ResourcePackServer(configFolder);
            try {
                resourcePackServer.start();
            } catch (IOException e) {
                getLogger().severe("Не удалось запустить сервер для ресурс-пака: " + e.getMessage());
            }
            getServer().getPluginManager().registerEvents(new ForceResourcePackOnJoin(), this);
//            getCommand("packexempt").setExecutor(new PackExemptCommand(getPlugin(), ForceResourcePackOnJoin.playersWithResourcePack));
        }
        if (getConfig().getBoolean("modules.tab-addon")) {
            getCommand("ttabname").setExecutor(new ColorTabNameCommand());
        }
        if (getConfig().getBoolean("modules.corpse.enabled")) {
//            getCommand("spawncorpse").setExecutor(new CorpseCommand());
//            getServer().getPluginManager().registerEvents(new CorpsePool(), this);
        }
//        if (getConfig().getBoolean("modules.multiworld-join-fix")) {
//            getServer().getPluginManager().registerEvents(new PlayerLocationListener(this.getDataFolder()), this);
//            this.getCommand("savealllocations").setExecutor(new SaveAllPlayerLocationsCommand(getDataFolder()));
//        }
        if (getConfig().getBoolean("modules.rapid-leaf-decay")) {
            getServer().getPluginManager().registerEvents(new RapidLeafDecay(), this);
        }
        if (getConfig().getBoolean("modules.drop-chances.enabled")) {
            getServer().getPluginManager().registerEvents(new DropChanceFix(), this);
        }
        if (getConfig().getBoolean("modules.custom-sign")) {
            getServer().getPluginManager().registerEvents(new CustomSign(), this);
        }
        if (getConfig().getBoolean("modules.stop-items-on-death")) {
            getServer().getPluginManager().registerEvents(new StopItemsOnDeath(), this);
        }

        if (getConfig().getBoolean("modules.villager-trade-modifier.enabled")) {
            getServer().getPluginManager().registerEvents(new VillagerTradeModifier(), this);
        }
        if (getConfig().getBoolean("modules.kill-empty-boats")) {
            new KillEmptyBoats().start();
        }
        if (getConfig().getBoolean("modules.copybook")) {
            getCommand("copybook").setExecutor(new CopyBook());
        }

        if (getConfig().getBoolean("modules.playtime-rewards.enabled")) {
            getCommand("playtime").setExecutor(new PlayTimeCommand(this));
        }

        if(getConfig().getBoolean("modules.rp-nicks")){
            SQLUtils.createDB(this.getDataFolder());
            customRpNicks = SQLUtils.readPlayersAndCreateMap(this.getDataFolder() + "/database.db");
            getCommand("setrpnick").setExecutor(new SetRpNickCommand());
        }

        if (getConfig().getBoolean("modules.right-click-farmland")) {
            getServer().getPluginManager().registerEvents(new RightClickFarmland(), this);
        }

        if(getConfig().getBoolean("modules.custom-nether-portal")){
            Bukkit.getPluginManager().registerEvents(new CustomNetherPortalListener(this), this);
        }

        if(getConfig().getBoolean("modules.stronger-dragon")){
//            getServer().getPluginManager().registerEvents(new CustomDragonSpawnListener(), this);
//            this.getCommand("spawndragon").setExecutor(new SpawnDragonCommand());

            getServer().getPluginManager().registerEvents(new DragonAttackListener(), this);
            getServer().getPluginManager().registerEvents(new DragonEventListener(this), this);
            getServer().getPluginManager().registerEvents(new EnderCrystalListener(this), this);
            this.getCommand("blackhole").setExecutor(new BlackHoleCommand());
            CustomEntityRegistry.replaceEnderDragonFactory();

        }

        
        // @TODO allot of dupes, bags and etc, needs to be fixed
//        if (getConfig().getBoolean("modules.shullker-bag")) {
//            getServer().getPluginManager().registerEvents(new ShullkerBag(), this);
//        }
//        Bukkit.getPluginManager().registerEvents(new RemoveMending(), this);
    }




}
