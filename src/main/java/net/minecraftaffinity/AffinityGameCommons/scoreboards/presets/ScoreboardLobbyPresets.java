package net.minecraftaffinity.AffinityGameCommons.scoreboards.presets;

import net.minecraftaffinity.AffinityCommons.utils.TimeUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.ScoreboardManager;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.enums.ScoreboardType;
import org.bukkit.ChatColor;

import java.util.LinkedHashMap;

/**
 * Created by macguy8 on 3/26/2014.
 */
public class ScoreboardLobbyPresets {

    //***************************//

    public static void updateLobbyScoreboard(Game game, int seconds) {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.YELLOW.toString(), null);
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Game", game.getName());
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Map", game.getMap().getName());
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Online", AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length + ChatColor.GRAY.toString() + "/" + ChatColor.WHITE + AffinityGameCommons.getInstance().getServer().getMaxPlayers());
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Needed", game.getNeededPlayers());

        ScoreboardManager.getInstance().setGlobalScoreboard("Starting in " + ChatColor.GREEN + TimeUtils.formatIntoMMSS(seconds), valuesMap, ScoreboardType.NORMAL);
    }

    public static void updateEndingScoreboard(Game game, int seconds) {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.YELLOW.toString(), null);
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Game", game.getName());
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Map", game.getMap().getName());

        ScoreboardManager.getInstance().setGlobalScoreboard("Ending in " + ChatColor.RED + TimeUtils.formatIntoMMSS(seconds), valuesMap, ScoreboardType.NORMAL);
    }

    public static void updateWaitingScoreboard(Game game) {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.YELLOW.toString(), null);
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Game", game.getName());
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Map", game.getMap().getName());
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Online", AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length + ChatColor.GRAY.toString() + "/" + ChatColor.WHITE + AffinityGameCommons.getInstance().getServer().getMaxPlayers());
        valuesMap.put(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Needed", game.getNeededPlayers());

        ScoreboardManager.getInstance().setGlobalScoreboard("Waiting for " + ChatColor.YELLOW + (game.getNeededPlayers() - AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length) + " player" + ((game.getNeededPlayers() - AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length) == 0 ? "" : "s"), valuesMap, ScoreboardType.NORMAL);
    }

    //***************************//

}