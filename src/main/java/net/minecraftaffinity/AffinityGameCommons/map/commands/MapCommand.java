package net.minecraftaffinity.AffinityGameCommons.map.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.MultiCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;

/**
 * Created by macguy8 on 6/27/2014.
 */
public class MapCommand extends MultiCommand {

    //***************************//

    public MapCommand() {
        super(PermissionsRank.ADMIN, "Map");

        addCommand(new RegisterCommand());
        addCommand(new SetAuthorCommand());
        addCommand(new SetGameModeCommand());
        addCommand(new SetLocationCommand());
        addCommand(new SetRegionCommand());
        addCommand(new TPCommand());
        addCommand(new WorldCommand());
    }

    //***************************//

}