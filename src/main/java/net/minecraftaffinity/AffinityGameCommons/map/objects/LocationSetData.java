package net.minecraftaffinity.AffinityGameCommons.map.objects;

import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.map.listeners.MapLocationSetListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 02, 03 2014
 * Programmed for the AffinityGameAPI project.
 */
public class LocationSetData {

    //***************************//

    public Map host;
    public String locationName;

    public ArrayList<Location> set = new ArrayList<Location>();

    public void onBreak(BlockBreakEvent event) {
          if (set.contains(event.getBlock().getLocation())) {
            set.remove(event.getBlock().getLocation());

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1F, 1F);
            event.getPlayer().sendMessage(ChatUtils.GRAY + "Successfully removed location from set " + ChatUtils.encapsulate(locationName) + " for map " + ChatUtils.encapsulate(host.getName()) + ".");
        }
    }

    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == MapLocationSetListener.LOCATION_SET.getType() && event.getBlock().getData() == MapLocationSetListener.LOCATION_SET.getData().getData() && !set.contains(event.getBlock().getLocation())) {
            set.add(event.getBlock().getLocation());

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1F, 1F);
            event.getPlayer().sendMessage(ChatUtils.GRAY + "Successfully added location to set " + ChatUtils.encapsulate(locationName) + " for map " + ChatUtils.encapsulate(host.getName()) + ".");
        }
    }

    public void confirm(Player player) {
        ArrayList<BlockVector> vectors = new ArrayList<BlockVector>();

        for (Location location : set) {
            location.getBlock().setType(Material.AIR);
            vectors.add(location.toVector().toBlockVector());
        }

        host.setLocation(locationName, vectors);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);

        player.sendMessage(ChatUtils.GRAY + "Successfully configured location set " + ChatUtils.encapsulate(locationName)+ " for map " + ChatUtils.encapsulate(host.getName()) + ".");
    }

    //***************************//

}