package me.nelbrenn.machines;

import com.google.gson.GsonBuilder;
import javafx.util.Builder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class Machines extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");

    static {
        ConfigurationSerialization.registerClass(Machine.class, "Machine");
    }

    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Structures plugin Enabled!");
        getServer().getPluginManager().registerEvents(new BuildStructure(), this);

        File dir = getDataFolder();

        if(!dir.exists()){
            if(!dir.mkdir()){
                System.out.println("Could not create direction for Machines!");
            }
        }

        BuildStructure.load(new File(getDataFolder(), "machines.dat"));

    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        Economy econ = rsp.getProvider();
        return econ != null;
    }

    public void onDisable() {
        BuildStructure.save(new File(getDataFolder(), "machines.dat"));
    }



//    public void save() {
//        getConfig().options().copyDefaults(true);
//        for (Player player : structures.machines.keySet()) {
//            int machineCounter = 0;
//            for (Machine machine : structures.machines.get(player)) {
//                int blockCounter = 0;
//                for (Block block : machine.blocks) {
//                    getConfig().set(player.getUniqueId() + "." + machineCounter + "." + blockCounter + '.', block.getLocation().getX());
//                    getConfig().set(player.getUniqueId() + "." + machineCounter + "." + blockCounter + '.', block.getLocation().getY());
//                    getConfig().set(player.getUniqueId() + "." + machineCounter + "." + blockCounter + '.', block.getLocation().getZ());
//                    blockCounter++;
//                }
//                machineCounter++;
//            }
//        }
//    }

}
