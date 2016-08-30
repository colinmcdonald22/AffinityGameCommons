package net.minecraftaffinity.AffinityGameCommons.game.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.MultiCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;

/**
 * Created by macguy8 on 6/27/2014.
 */
public class GameCommand extends MultiCommand {

    //***************************//

    public GameCommand() {
        super(PermissionsRank.ADMIN, "Game");

        addCommand(new StartGameCommand());
        addCommand(new EndGameCommand());
    }

    //***************************//

}