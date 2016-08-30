package net.minecraftaffinity.AffinityGameCommons.game.commands;

import net.minecraftaffinity.AffinityCommons.commands.objects.SingleCommand;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import org.bukkit.entity.Player;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 03, 02 2014
 * Programmed for the AffinityGameAPI project.
 */
public class EndGameCommand extends SingleCommand {

    //***************************//

    public EndGameCommand() {
        super(PermissionsRank.ADMIN, "EndGame", "EG", "FEG", "ForceEndGame");
    }

    //***************************//

    public void execute(Player player, String[] args) {
        Game game = GameManager.getInstance().getCurrentGame();

        if (game == null) {
            player.sendMessage(ChatUtils.GRAY + "There is no game to end.");
            return;
        }

        if (game.getState() != GameState.IN_GAME) {
            player.sendMessage(ChatUtils.GRAY + "The game cannot be force ended at this time.");
            return;
        }

        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatUtils.GRAY + "The game has been forcibly ended by " + ChatUtils.encapsulate(player.getName()) + ".");
        GameManager.getInstance().getCurrentGame().onGameEnd();
        GameManager.getInstance().getCurrentGame().returnToLobby();
    }

    //***************************//

}