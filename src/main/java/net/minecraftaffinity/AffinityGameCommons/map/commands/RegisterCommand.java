package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;
import org.bukkit.entity.Player;

/**
 * Programmed by minecraftaffinity (Colin McDonald)
 * Creation Date: 07, 11 2013
 * Programmed for the AffinityGameCommons project.
 */
public class RegisterCommand extends SingleCommand {

    //***************************//

    public RegisterCommand() {
        super(PermissionsRank.ADMIN, "Register");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(ChatUtils.GRAY + "Usage: /Map Register <Map Name>");
            return;
        }

        Map map = new Map(args[0]);
        MapManager.getInstance().getMaps().add(map);

        player.sendMessage(ChatUtils.GRAY + "You have added a new map named " + ChatUtils.encapsulate(args[0]) + " to the system.");
    }

    //***************************//

}