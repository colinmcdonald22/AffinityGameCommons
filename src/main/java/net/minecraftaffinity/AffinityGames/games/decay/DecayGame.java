package net.minecraftaffinity.AffinityGames.games.decay;

import net.minecraftaffinity.AffinityCommons.utils.MathUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.abilities.objects.Ability;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameToggle;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.ScoreboardManager;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.enums.ScoreboardType;
import net.minecraftaffinity.AffinityGameCommons.teams.enums.FriendlyFireSetting;
import net.minecraftaffinity.AffinityGameCommons.teams.objects.Team;
import net.minecraftaffinity.AffinityGameCommons.utils.TeamUtils;
import net.minecraftaffinity.AffinityGames.kits.PlayerKit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 04, 02 2014
 * Programmed for the AffinityGames project.
 */
public class DecayGame extends Game {

    //***************************//

    public DecayGame() {
        super("Decay", new String[] { "Don't fall." }, new Kit[] {
                new PlayerKit(new Ability[] { })
        }, new Team[] {
                new Team("Players", "Players", ChatColor.GRAY, FriendlyFireSetting.DISALLOW)
        });
    }

    //***************************//

    @Override
    public void onGameLoad() {
        enableToggle(GameToggle.HIDE_TEAMS);
    }

    @Override
    public void onGameStart() {
        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "You have 5 seconds to prepare before the first wave.");

        new BukkitRunnable() {

            public void run() {
                if (getState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }

                Iterator<Block> iterator = getMap().getRegion("Floor").blocks(getMap().getWorld());

                while (iterator.hasNext()) {
                    Block block = iterator.next();

                    if (block.getType() == Material.STAINED_CLAY && MathUtils.getRandom().nextInt(100) < 5) {
                        if (block.getData() == DyeColor.GREEN.getWoolData())  {
                            block.setData(DyeColor.YELLOW.getWoolData());
                        } else if (block.getData() == DyeColor.YELLOW.getWoolData())  {
                            block.setData(DyeColor.RED.getWoolData());
                        } else if (block.getData() == DyeColor.RED.getWoolData())  {
                            block.setType(Material.AIR);
                            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.HARD_CLAY);
                        }
                    }
                }

                updateScoreboard();
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 100L, 60L);
    }

    //***************************//

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.GREEN + "Alive", getPlayers(true).size() + ChatColor.values()[0].toString());
        valuesMap.put(ChatColor.RED + "Dead", getPlayers(false).size() - getPlayers(true).size() + ChatColor.values()[1].toString());

        ScoreboardManager.getInstance().setGlobalScoreboard(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Decay", valuesMap, ScoreboardType.NORMAL);
    }

    //***************************//

    @Override
    public boolean canPlayerRespawn(Player player) {
        return (false);
    }

    @Override
    public void onPlayerDeath(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 0) {
            endGame(Arrays.asList(player));
        }
    }

    @Override
    public void onPlayerDisconnect(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 0) {
            endGame(Arrays.asList(player));
        }
    }

    //***************************//

}