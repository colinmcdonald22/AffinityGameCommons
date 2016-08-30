package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;
import org.bukkit.entity.Player;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 02, 02 2014
 * Programmed for the AffinityGameAPI project.
 */
public class TPCommand extends SingleCommand {

    //***************************//

    public TPCommand() {
        super(PermissionsRank.ADMIN, "TP", "Teleport");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(ChatUtils.GRAY + "Usage: /Map TP <Map Name>");
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
            player.sendMessage(ChatUtils.GRAY + "No maps matched the query for " + ChatUtils.CYAN + args[0] + ChatUtils.GRAY + ".");
            return;
        }

        if (found.getWorld() == null) {
            player.sendMessage(ChatUtils.GRAY + "No map loaded for " + ChatUtils.CYAN + found.getName() + ChatUtils.GRAY + ". Loading/generating one now...");
            found.initWorld();
        }

        player.sendMessage(ChatUtils.GRAY + "Generated map for " + ChatUtils.CYAN + found.getName() + ChatUtils.GRAY + ". Teleporting you there now...");
        player.teleport(found.getWorld().getSpawnLocation().add(0.5, 0.5, 0.5));
    }

    //***************************//

}