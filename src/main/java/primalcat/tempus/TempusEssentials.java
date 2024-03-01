package primalcat.tempus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import primalcat.tempus.commands.PlaytimeCommand;
import primalcat.tempus.graves.listeners.EntityDeathListener;
import primalcat.tempus.hidecommands.PlHideCmd;
import primalcat.tempus.hidecommands.listeners.CommandListener;
import primalcat.tempus.hidecommands.listeners.CommandSuggestionListener;
import primalcat.tempus.hidecommands.listeners.JoinListener;
import primalcat.tempus.hidecommands.listeners.OpListener;
import primalcat.tempus.items.ItemManager;
import primalcat.tempus.listeners.*;
import primalcat.tempus.modules.PlayerParticles;
import primalcat.tempus.packets.SoundPacketListener;
import primalcat.tempus.placeholders.PlayTimeIconPlaceholder;
import primalcat.tempus.structures.CustomEndStructureGenerator;
import primalcat.tempus.villagers.RemoveMending;

import java.io.File;

import static primalcat.tempus.hidecommands.Util.checkGroups;

public final class TempusEssentials extends JavaPlugin {

    // сделано для адовцов
    private static PlayerParticles playerParticles = new PlayerParticles();
    public static TempusEssentials plugin;
    public static TempusEssentials getPlugin() {
        return plugin;
    }

    public static ProtocolManager protocolManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
//        this.createConfig();
//        this.reloadConfig();


        initProtocolLib();



        // commands
        getCommand("playtime").setExecutor(new PlaytimeCommand());
        // hide commands
//        checkGroups();
//        this.initListeners();
//        this.getCommand("plhide").setExecutor(new PlHideCmd());
//        this.getCommand("plhide").setTabCompleter(new PlHideCmd());


        Bukkit.getPluginManager().registerEvents(new RemoveMending(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new RapidLeafDecay(), this);

        // structures
//        Bukkit.getPluginManager().registerEvents(new CustomEndStructureGenerator(), this);

        // register placeholder
        papiHook();

        // change drop chance
        Bukkit.getPluginManager().registerEvents(new DropChanceFix(), this);
//        Bukkit.getPluginManager().registerEvents(new SpawnFrogs(), this);
//        Bukkit.getPluginManager().registerEvents(new RemoveDragonSound(), this);

        // items
        ItemManager.init();
        Bukkit.getPluginManager().registerEvents(new ItemManager(), this);
        Bukkit.getPluginManager().registerEvents(new LightBlockListener(), this);


//        Bukkit.getScheduler().runTaskTimer(this, playerParticles::spawnParticlesForAllPlayers, 0L, 20L); // 20 ticks = 1 second
    }

    private void initProtocolLib(){
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new SoundPacketListener(this));
    }

    // plhide
  /*  private void initListeners() {
        PluginManager pw = Bukkit.getPluginManager();
        pw.registerEvents(new JoinListener(), this);
        pw.registerEvents(new CommandSuggestionListener(), this);

        pw.registerEvents(new CommandListener(), this);
        pw.registerEvents(new OpListener(), this);
    }*/
    public void createConfig() {
        File customConfigFile = new File(this.getDataFolder(), "config.yml");
        if (!customConfigFile.exists()) {
            this.saveDefaultConfig();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ItemManager.removeCrafts();
        RapidLeafDecay.clearScheduledBlocks();
//        protocolManager.removePacketListener(listener);
        papiUnhook();
    }

    private void papiHook() {
//        System.out.println("hooking " + Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayTimeIconPlaceholder().register();
        }
    }

    private void papiUnhook() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayTimeIconPlaceholder().unregister();
        }
    }
}
