package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.map.listeners.MapLocationSetListener;
import net.minecraftaffinity.AffinityGameCommons.map.objects.LocationSetData;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 29, 11 2013
 * Programmed for the AffinityGameAPI project.
 */
public class SetLocationCommand extends SingleCommand {

    //***************************//

    public SetLocationCommand() {
        super(PermissionsRank.ADMIN, "SetLocation");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ChatUtils.GRAY + "Usage: /Map SeLocation <Map Name> <Location Name>");
            return;
        }

        Map found = null;

        for (Map map : MapManager.getInstance().getMaps()) {
            if (map.getName().equalsIgnoreCase(args[0])) {
                found = map;
                break;
            }
        }

        if (found == null) {
            player.sendMessage(ChatUtils.GRAY + "No maps matched the query for " + ChatUtils.encapsulate(args[0]) + ".");
            return;
        }

        if (MapLocationSetListener.locationSetData.containsKey(player.getName())) {
            MapLocationSetListener.locationSetData.get(player.getName()).confirm(player);
            MapLocationSetListener.locationSetData.remove(player.getName());
            return;
        }

        if (!player.getWorld().getName().equals(found.getWorld().getName())) {
            player.sendMessage(ChatUtils.GRAY + "You must be in a map's world to edit it.");
            return;
        }

        player.setItemInHand(MapLocationSetListener.LOCATION_SET);
        player.sendMessage(ChatUtils.GRAY + "Giving you the location set manipulation block...");
        player.sendMessage(ChatUtils.GRAY + "Once all locations are set, type '" + ChatUtils.encapsulate("/SetMapLocation " + found.getName() + " " + args[1]) + "' to confirm.");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);

        LocationSetData lsd = new LocationSetData();

        lsd.host = found;
        lsd.locationName = args[1];

        List<Location> existing = found.getLocations(lsd.locationName);

        if (existing != null && existing.size() != 0) {
            player.sendMessage(ChatUtils.GRAY + "Location set " + ChatUtils.encapsulate(args[0]) + " already exists. Editing set...");

            boolean first = false;

            for (Location loc : existing) {
                if (!first) {
                    first = true;
                    player.teleport(loc.clone().add(0.5, 2, 0.5));
                }

                lsd.set.add(loc.getBlock().getLocation());

                loc.getBlock().setTypeIdAndData(MapLocationSetListener.LOCATION_SET.getTypeId(), MapLocationSetListener.LOCATION_SET.getData().getData(), true);
            }
        }

        MapLocationSetListener.locationSetData.put(player.getName(), lsd);
    }

    //***************************//

}