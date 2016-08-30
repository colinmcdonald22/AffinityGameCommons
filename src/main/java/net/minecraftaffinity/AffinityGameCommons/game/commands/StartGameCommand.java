package net.minecraftaffinity.AffinityGameCommons.game.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import org.bukkit.entity.Player;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 29, 11 2013
 * Programm ed for the AffinityGameAPI project.
 */
public class StartGameCommand extends SingleCommand {

    //***************************//

    public StartGameCommand() {
        super(PermissionsRank.ADMIN, "StartGame", "Start", "BeginGame", "Begin", "SG", "BG", "B");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        Game game = GameManager.getInstance().getCurrentGame();

        if (game == null) {
            player.sendMessage(ChatUtils.GRAY + "There is no game to start!");
            return;
        }

        if (game.getState() != GameState.WAITING_FOR_PLAYERS) {
            player.sendMessage(ChatUtils.GRAY + "The game cannot be started at this time!");
            return;
        }

        player.sendMessage(ChatUtils.GRAY + "Force-starting game...");

        if (args.length != 0 && args[0].equalsIgnoreCase("Now")) {
            game.startGame();
        } else {
            game.startCountdown(true, 5);
        }
    }

    //***************************//

}