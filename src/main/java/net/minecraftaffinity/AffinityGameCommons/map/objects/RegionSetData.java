package net.minecraftaffinity.AffinityGameCommons.map.objects;

import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityCommons.utils.Cuboid;
import net.minecraftaffinity.AffinityGameCommons.map.listeners.MapLocationSetListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 02, 03 2014
 * Programmed for the AffinityGameAPI project.
 */
public class RegionSetData {

    //***************************//

    public Map host;
    public String regionName;

    public Location set1;
    public Location set2;

    public void onBreak(BlockBreakEvent event) {
        if (set1 != null && set1.equals(event.getBlock().getLocation())) {
            set1 = null;

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1F, 1F);
            event.getPlayer().sendMessage(ChatUtils.GRAY + "Removed point one from region config.");
        } else if (set2 != null && set2.equals(event.getBlock().getLocation())) {
            set2 = null;

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1F, 1F);
            event.getPlayer().sendMessage(ChatUtils.GRAY + "Removed point two from region config.");
        }
    }

    public void onPlace(BlockPlaceEvent event) {
        if (!(event.getBlock().getType() == MapLocationSetListener.REGION_SET.getType() && event.getBlock().getData() == MapLocationSetListener.REGION_SET.getData().getData())) {
            return;
        }

        if (set1 == null) {
              set1 = event.getBlock().getLocation();
            event.getPlayer().sendMessage(ChatUtils.GRAY + "Point one set.");
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1F, 1F);
        } else if (set2 == null) {
            set2 = event.getBlock().getLocation();
            event.getPlayer().sendMessage(ChatUtils.GRAY + "Point two set.");
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1F, 1F);
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatUtils.GRAY + "Point 1 and Point 2 are both already set. Break one to reset a point!");
        }
    }

    public void confirm(Player player) {
        if (set1 == null || set2 == null) {
            player.sendMessage(ChatUtils.GRAY + "Invalid region configuration detected. Please set points 1 and 2 to continue.");
            return;
        }

        set1.getBlock().setType(Material.AIR);
        set2.getBlock().setType(Material.AIR);

        host.addRegion(regionName, new Cuboid(set1.getBlockX(), set1.getBlockY(), set1.getBlockZ(), set2.getBlockX(), set2.getBlockY(), set2.getBlockZ()));
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);

        player.sendMessage(ChatUtils.GRAY + "Successfully configured region " + ChatUtils.encapsulate(regionName) + " for map " + ChatUtils.encapsulate(host.getName()) + ".");
    }

    //***************************//

}