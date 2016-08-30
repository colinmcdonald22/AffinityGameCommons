package net.minecraftaffinity.AffinityGameCommons.utils;

import net.minecraftaffinity.AffinityCommons.utils.LoggingUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.teams.objects.Team;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 4/20/2014.
 */
public class TeamUtils {

    //***************************//

    public static Team findSmallestTeam(Game game) {
        Team smallest = null;
        int smallestTeamSize = 99999;

        for (Team team : game.getTeams()) {
            int players = countPlayers(team);

            if (players < smallestTeamSize) {
                smallestTeamSize = players;
                smallest = team;
            }
        }

        return (smallest);
    }

    public static int countPlayers(Team team) {
        int playersOnTeam = 0;

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            if (team.getHost().getTeamSelections().containsKey(player.getName()) && team.getHost().getTeamSelections().get(player.getName()).getName().equals(team.getName())) {
                playersOnTeam++;
            }
        }

        return (playersOnTeam);
    }

    public static void renderTeams(Game game) {
        if (!MapManager.getInstance().getLobbyMap().getMapLocations().containsKey("LobbyTeamDisplay")) {
            LoggingUtils.log("Teams", "No lobby team display location found.");
            return;
        }

        BlockFace face = BlockFace.EAST;
        Block start = MapManager.getInstance().getLobbyMap().getLocationNumber("LobbyTeamDisplay", 0).getBlock().getRelative(face, game.getTeams().length * 4 / 2);

        for (Team team : game.getTeams()) {
            // TODO: Render teams
            start = start.getRelative(face, 4);
        }
    }

    //***************************//

}