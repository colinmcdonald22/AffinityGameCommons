package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 02, 02 2014
 * Programmed for the AffinityGameAPI project.
 */
public class WorldCommand extends SingleCommand {

    //***************************//

    public WorldCommand() {
        super(PermissionsRank.DEVELOPER, "World");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        player.sendMessage(ChatUtils.GRAY + "You are in the world " + ChatUtils.encapsulate(player.getWorld().getName()) + ".");
    }

    //***************************//

}