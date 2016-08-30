package net.minecraftaffinity.AffinityGames.games.arrowrain;

import net.minecraftaffinity.AffinityCommons.utils.MathUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.abilities.objects.Ability;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameToggle;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.ScoreboardManager;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.enums.ScoreboardType;
import net.minecraftaffinity.AffinityGameCommons.teams.enums.FriendlyFireSetting;
import net.minecraftaffinity.AffinityGameCommons.teams.objects.Team;
import net.minecraftaffinity.AffinityGameCommons.utils.TeamUtils;
import net.minecraftaffinity.AffinityGames.kits.PlayerKit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 31, 01 2014
 * Programmed for the AffinityGames project.
 */
public class ArrowRainGame extends Game {

    //***************************//

    public ArrowRainGame() {
        super("Arrow Rain", new String[] { "Avoid the falling arrows.", "Be the last one standing.", "Look up!" }, new Kit[] {
                new PlayerKit(new Ability[] { })
        }, new Team[] {
                new Team("Players", "Players", ChatColor.GRAY, FriendlyFireSetting.DISALLOW)
        });
    }

    //***************************//

    int wave = 1;

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

                int numberOfArrows = wave * 3;
                Iterator<Block> iterator = getMap().getRegion("ArrowSpawn").blocks(getMap().getWorld());

                  while (iterator.hasNext()) {
                    Block block = iterator.next();

                    if (MathUtils.getRandom().nextInt(100) < numberOfArrows) {
                        block.getWorld().spawnEntity(block.getLocation().add(0.5, 0, 0.5), EntityType.ARROW);
                    }
                }

                wave++;
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 100L, 60L);
    }

    //***************************//

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.GREEN + "Alive", getPlayers(true).size() + ChatColor.values()[0].toString());
        valuesMap.put(ChatColor.RED + "Dead", getPlayers(false).size() - getPlayers(true).size() + ChatColor.values()[1].toString());
        valuesMap.put(ChatColor.YELLOW + "Wave", wave + ChatColor.values()[2].toString());

        ScoreboardManager.getInstance().setGlobalScoreboard(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Arrow Rain", valuesMap, ScoreboardType.NORMAL);
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

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        event.getEntity().remove();
    }

    //***************************//

}