package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;
import org.bukkit.entity.Player;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 29, 11 2013
 * Programmed for the AffinityGameAPI project.
 */
public class SetGameModeCommand extends SingleCommand {

    //***************************//

    public SetGameModeCommand() {
        super(PermissionsRank.ADMIN, "SetGameMode");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatUtils.GRAY + "Usage: /Map SetGameMode <Map Name> <Game Mode>");
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

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        found.setGameMode(builder.toString().trim());

        player.sendMessage(ChatUtils.GRAY + "Changed map gamemode to " + ChatUtils.encapsulate(builder.toString().trim()) + " for map " + ChatUtils.encapsulate(found.getName()) + ".");
    }

    //***************************//

}