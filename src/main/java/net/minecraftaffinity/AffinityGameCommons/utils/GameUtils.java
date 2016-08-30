package net.minecraftaffinity.AffinityGameCommons.utils;

import net.minecraftaffinity.AffinityCommons.user.UserManager;
import net.minecraftaffinity.AffinityCommons.utils.PlayerUtils;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import net.minecraftaffinity.AffinityGameCommons.teams.objects.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 4/20/2014.
 */
public class GameUtils {

    //***************************//

    public static String getRanking(int place) {
        switch (place) {
            case 1:
                return (ChatColor.AQUA.toString() + ChatColor.BOLD + ordinal(place));
            case 2:
                return (ChatColor.GOLD.toString() + ChatColor.BOLD + ordinal(place));
            case 3:
                return (ChatColor.GRAY.toString() + ChatColor.BOLD + ordinal(place));
            default:
                return (ChatColor.GREEN.toString() + ChatColor.BOLD + ordinal(place));
        }
    }

    public static String ordinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };

        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return (i + "th");
            default:
                return (i + sufixes[i % 10]);
        }
    }

    public static void selectDefaultTeam(Game game, Player player, boolean force, boolean silent) {
        selectTeam(game, player, TeamUtils.findSmallestTeam(game), force, silent);
    }

    public static void selectDefaultKit(Game game, Player player, boolean force, boolean silent) {
        for (Kit kit : game.getKits()) {
            if (game.getTeamSelections().get(player.getName()).canAccess(kit)) {
                selectKit(game, player, kit, force, silent);
                break;
            }
        }
    }

    public static void selectTeam(Game game, Player player, Team team, boolean force, boolean silent) {
        if (!force && !game.canJoinTeam(player, team)) {
            if (!silent) {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
                player.sendMessage(ChatColor.RED + team.getDisplayName() + " is full! You cannot join this team.");
            }

            return;
        }

        game.getTeamSelections().put(player.getName(), team);
        UserManager.getInstance().getUser(player).setNametagColor(team.getColor());

        if (!silent) {
            player.sendMessage(ChatColor.GREEN + "You have joined " + team.getDisplayName() + ChatColor.GREEN + ".");
        }

        if (!game.getTeamSelections().get(player.getName()).canAccess(game.getKitSelections().get(player.getName()))) {
            selectDefaultKit(game, player, false, true);
        }
    }

    public static void selectKit(Game game, Player player, Kit kit, boolean force, boolean silent) {
        if (!force && !game.getTeamSelections().get(player.getName()).canAccess(kit)) {
            if (!silent) {
                player.sendMessage(ChatColor.RED + kit.getName() + " cannot be used by players on the " + game.getTeamSelections().get(player.getName()).getDisplayName() + ChatColor.GREEN + ".");
            }

            return;
        }

        game.getKitSelections().put(player.getName(), kit);

        if (!silent) {
            player.sendMessage(ChatColor.GOLD + "You have selected " + kit.getName() + ".");
        }

        if (game.getState() == GameState.IN_GAME) {
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
            game.giveSpawnItems(player);
        }
    }


    //***************************//

}