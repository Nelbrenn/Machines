package me.nelbrenn.machines;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.io.ObjectOutputStream;
import java.util.*;

public class BuildStructure implements Listener {
    public static HashMap<UUID, ArrayList<Machine>> machines = new HashMap<>();
    private static Plugin plugin = Machines.getPlugin(Machines.class);

    @EventHandler
    public void playerMove(PlayerMoveEvent event){
        event.getPlayer().sendMessage(machines.toString());
    }

    @EventHandler
    public void machineLogic(BlockPlaceEvent event) {

        if (event.getBlock().getType().equals(Material.STONE)) {
            Player player = event.getPlayer();
            if (!checkPlayerHasMachine(player)) { //Add a new entry if the player is not in the hashmap.
                machines.put(player.getUniqueId(), new ArrayList<>());
            }
            if (canPlaceMachine(event.getBlock())) {
                Integer i = machines.get(player.getUniqueId()).size();
                machines.get(player.getUniqueId()).add(new Machine(event.getBlock(), player, i.toString()));
                player.sendMessage(ChatColor.GREEN + "Structure Placed!");
            } else {
                player.sendMessage(ChatColor.RED + "Cannot place Machine!");
                event.setCancelled(true);
            }
        }

        //Check machine Break
        if (event.getBlock().getType().equals(Material.LOG) && checkPlayerHasMachine(event.getPlayer())) {
            for (Machine machine : machines.get(event.getPlayer().getUniqueId())) {
                machine.delete(event.getPlayer());
            }
            machines.get(event.getPlayer().getUniqueId()).clear();
        }

    }

    @EventHandler
    public void checkMachineBreak(BlockBreakEvent event) {
        if (checkPlayerHasMachine(event.getPlayer())) {
            int location = -1;
            for (Machine machine : machines.get(event.getPlayer().getUniqueId())) {
                location++;
                if (machine.checkMachineBreak(event.getBlock())) {
                    machine.delete(event.getPlayer());
                    machines.get(event.getPlayer().getUniqueId()).remove(location);
                    return;
                }
            }
        }
    }


    private boolean checkPlayerHasMachine(Player player) {
        return machines.get(player.getUniqueId()) != null;
    }

    private boolean canPlaceMachine(Block block) {
        return block.getRelative(BlockFace.UP).getType().equals(Material.AIR) && block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().equals(Material.AIR);
    }

    public static void save(File f){
        try{
            if(!f.exists()){
                f.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(machines);
            oos.flush();
            oos.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void load(File f){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            machines = (HashMap<UUID, ArrayList<Machine>>)ois.readObject();
            ois.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    }



