package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityCommons.utils.Cuboid;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.map.listeners.MapLocationSetListener;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;
import net.minecraftaffinity.AffinityGameCommons.map.objects.RegionSetData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 29, 11 2013
 * Programmed for the AffinityGameAPI project.
 */
public class SetRegionCommand extends SingleCommand {

    //***************************//

    public SetRegionCommand() {
        super(PermissionsRank.ADMIN, "SetRegion");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ChatUtils.GRAY + "Usage: /Map SetRegion <Map Name> <Name>");
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

        if (MapLocationSetListener.regionSetData.containsKey(player.getName())) {
            MapLocationSetListener.regionSetData.get(player.getName()).confirm(player);
            MapLocationSetListener.regionSetData.remove(player.getName());
            return;
        }

        if (!player.getWorld().getName().equals(found.getWorld().getName())) {
            player.sendMessage(ChatUtils.GRAY + "You must be in a map's world to edit it.");
            return;
        }

        player.setItemInHand(MapLocationSetListener.REGION_SET);
        player.sendMessage(ChatUtils.GRAY + "Giving you the region manipulation block...");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);

        RegionSetData rsd = new RegionSetData();

        rsd.host = found;
        rsd.regionName = args[1];

        Cuboid existing = found.getRegion(rsd.regionName);

          if (existing  != null) {
            player.sendMessage(ChatUtils.GRAY + "Region " + ChatUtils.encapsulate(args[1]) + " already exists. Editing region...");

            rsd.set1 = existing.getLowerNE(found.getWorld());
            rsd.set2 = existing.getUpperSW(found.getWorld());

            player.teleport(rsd.set1.clone().add(0, 2, 0));

            rsd.set1.getBlock().setTypeIdAndData(MapLocationSetListener.REGION_SET.getTypeId(), MapLocationSetListener.REGION_SET.getData().getData(), true);
            rsd.set2.getBlock().setTypeIdAndData(MapLocationSetListener.REGION_SET.getTypeId(), MapLocationSetListener.REGION_SET.getData().getData(), true);
        }

        MapLocationSetListener.regionSetData.put(player.getName(), rsd);
    }

    //***************************//

}