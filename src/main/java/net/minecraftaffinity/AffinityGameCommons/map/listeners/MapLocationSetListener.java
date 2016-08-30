package net.minecraftaffinity.AffinityGameCommons.map.listeners;

import net.minecraftaffinity.AffinityCommons.utils.ItemUtils;
import net.minecraftaffinity.AffinityGameCommons.map.objects.LocationSetData;
import net.minecraftaffinity.AffinityGameCommons.map.objects.RegionSetData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 02, 03 2014
 * Programmed for the AffinityGameAPI project.
 */
public class MapLocationSetListener implements Listener {

    //***************************//

    public static final ItemStack LOCATION_SET = ItemUtils.name(new ItemStack(Material.WOOL, 1, (byte) 2), ChatColor.YELLOW.toString() + ChatColor.BOLD + "Location Point Modifier");
    public static final ItemStack REGION_SET = ItemUtils.name(new ItemStack(Material.WOOL, 1, (byte) 5), ChatColor.YELLOW.toString() + ChatColor.BOLD + "Region Modifier");

    public static HashMap<String, LocationSetData> locationSetData = new HashMap<String, LocationSetData>();
    public static HashMap<String, RegionSetData> regionSetData = new HashMap<String, RegionSetData>();

    //***************************//

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
         if (locationSetData.containsKey(event.getPlayer().getName())) {
               locationSetData.get(event.getPlayer().getName()).onPlace(event);
        } else if (regionSetData.containsKey(event.getPlayer().getName())) {
            regionSetData.get(event.getPlayer().getName()).onPlace(event);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (locationSetData.containsKey(event.getPlayer().getName())) {
            locationSetData.get(event.getPlayer().getName()).onBreak(event);
        } else if (regionSetData.containsKey(event.getPlayer().getName())) {
            regionSetData.get(event.getPlayer().getName()).onBreak(event);
        }
    }

    @EventHandler
    public void onClientUnload(PlayerQuitEvent event) {
        locationSetData.remove(event.getPlayer().getName());
        regionSetData.remove(event.getPlayer().getName());
    }

    //***************************//

}