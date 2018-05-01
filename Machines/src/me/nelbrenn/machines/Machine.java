package me.nelbrenn.machines;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.sk89q.worldguard.bukkit.WGBukkit.getPlugin;
import static org.bukkit.Bukkit.getServer;

public class Machine{

    public ArrayList<Block> blocks = new ArrayList<>();
    private Dispenser dispenser;
    private int taskID;
    private int ID;
    private Block baseBlock;
    

    public Machine(Block baseBlock_, Player player, String ID) {
        baseBlock = baseBlock_;
        Block block1 = baseBlock.getRelative(BlockFace.UP);
        Block block2 = block1.getRelative(BlockFace.UP);
        block2.setType(Material.DISPENSER);
        block2.setData(getDispenserByte(player));
        dispenser = (Dispenser) block2.getState();

        baseBlock.setType(Material.DIAMOND_BLOCK);
        block1.setType(Material.FENCE);
        dispenser.setType(Material.DISPENSER);

        block1.getLocation().getWorld().playEffect(block1.getLocation(), Effect.STEP_SOUND, 5);
        dispenser.getLocation().getWorld().playEffect(dispenser.getLocation(), Effect.STEP_SOUND, 2);

        dispenser.setLock("Hello");

        taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(), new Runnable() {
            public void run() {
                dispenser.getInventory().addItem(new ItemStack(Material.DIAMOND));
                dispenser.dispense();
            }
        }, 1, 1);

        blocks.add(baseBlock);
        blocks.add(block1);
        save(player, ID);
    }


    public boolean checkMachineBreak(Block blockBroken) {
        for (Block block : blocks) {
            if (blockBroken.equals(block)) {
                return true;
            }
        }
        if (blockBroken.equals(dispenser.getBlock())) {
            return true;
        }
        return false;
    }

    public void delete(Player player) {
        for (Block block : blocks) {
            block.setType(Material.AIR);
        }
//        plugin.getConfig().set(player.getUniqueId().toString() + "." + ID, null);
//        plugin.saveConfig();
        blocks.clear();
        dispenser.getBlock().setType(Material.AIR);
        dispenser = null;
        Bukkit.getScheduler().cancelTask(taskID);
    }

    private Byte getDispenserByte(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (45 <= rotation && rotation < 135) {
            return 0x3; //North 0x2
        } else if (135 <= rotation && rotation < 225) {
            return 0x4; // East 0x5
        } else if (225 <= rotation && rotation < 315) {
            return 0x2; //South 0x3
        } else if (315 <= rotation && rotation <= 360 || rotation >= 0 && rotation < 45) {
            return 0x5; //West 0x4
        } else {
            return null;
        }
    }


    public void save(Player player, String ID){
        FileConfiguration config = getPlugin().getConfig();
        config.addDefault("players."+ player.getUniqueId() + "." + ID + ".Base" + ".Location",new int[]{baseBlock.getX(),baseBlock.getY(),baseBlock.getZ()});
        config.addDefault("players." + player.getUniqueId() + "." + ID + ".Dispenser" + ".Location",new int[]{dispenser.getX(),dispenser.getY(),dispenser.getZ()});

        config.options().copyDefaults(true);
        getPlugin().saveConfig();
    }


}
