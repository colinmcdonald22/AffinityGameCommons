package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 29, 11 2013
 * Programmed for the AffinityGameAPI project.
 */
public class SetAuthorCommand extends SingleCommand {

    //***************************//

    public SetAuthorCommand() {
        super(PermissionsRank.ADMIN, "SetAuthor");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ChatUtils.GRAY + "Usage: /Map SetAuthor <Map Name> <Author>");
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

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        String authors = builder.toString().trim();

        found.setAuthors(new ArrayList<String>(Arrays.asList(authors.split(","))));

        player.sendMessage(ChatUtils.GRAY + "Changed map author to " + ChatUtils.encapsulate(found.getAuthorsFormatted()) + " for map " + ChatUtils.encapsulate(found.getName()) + ".");
    }

    //***************************//

}