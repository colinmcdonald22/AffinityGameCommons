package net.minecraftaffinity.AffinityGames.games.islanddefense;

import net.minecraftaffinity.AffinityCommons.utils.BarUtils;
import net.minecraftaffinity.AffinityCommons.utils.LocationUtils;
import net.minecraftaffinity.AffinityCommons.utils.LoggingUtils;
import net.minecraftaffinity.AffinityCommons.utils.TimeUtils;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by macguy8 on 4/18/2014.
 */
public class IslandDefenseGame extends Game {

    //***************************//

    public IslandDefenseGame() {
        super("Island Defense", new String[] { "Level up your items in the shop.", "Protect the island from monsters.", "If all of your beacons are taken out, you lose!" }, new Kit[] {
                new PlayerKit(new Ability[] { })
        }, new Team[] {
                new Team("Players", "Players", ChatColor.GREEN, FriendlyFireSetting.DISALLOW)
        });
    }

    //***************************//

    int timeUntilStart = 10;
    int timeIngame = 0;
    HashMap<Location, Boolean> beacons = new HashMap<Location, Boolean>();
    List<LivingEntity> spawnedMobs = new ArrayList<LivingEntity>();

    //***************************//

    @Override
    public void onGameLoad() {
        enableToggle(GameToggle.HIDE_TEAMS);
    }

    @Override
    public void onGameStart() {
        enableToggle(GameToggle.ENABLE_MOB_SPAWN);

        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Monsters will begin to spawn in 10 seconds...");

        getMap().getWorld().setTime(18000L);
        getMap().getWorld().setGameRuleValue("doDaylightCycle", "false");

        for (Location beacon : getMap().getLocations("Beacons")) {
            beacons.put(beacon, true);
            beacon.getBlock().setType(Material.BEACON);
            LoggingUtils.log(getName(), "Registered beacon at " + LocationUtils.convertToString(beacon));
        }

        new BukkitRunnable() {

            public void run() {
                if (getState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }

                if (timeUntilStart >= 1) {
                    timeUntilStart--;
                    updateScoreboard();
                    return;
                }

                timeIngame++;

                while (spawnedMobs.size() < timeIngame / 5) {
                    //spawnMob(getMap().getLocationRandom("MobSpawn"), chooseNextEntityType());
                }

                garbageCollectMobList();
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 0L, 20L);
    }

    //***************************//

    @EventHandler
    public void onCreatureDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE || event.getEntity().getType() == EntityType.SKELETON || event.getEntity().getType() == EntityType.CREEPER || event.getEntity().getType() == EntityType.SPIDER) {
            updateMobCount();
            spawnedMobs.remove(event.getEntity());
        }
    }

    //***************************//

    public void updateMobCount() {
        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            BarUtils.displayTextBar(ChatColor.GREEN + "Mobs left: " + ChatColor.YELLOW + spawnedMobs.size(), player);
        }
    }

    public void garbageCollectMobList() {
        for (LivingEntity entity : new ArrayList<LivingEntity>(spawnedMobs)) {
            if (!entity.isValid() || entity.isDead()) {
                spawnedMobs.remove(entity);
            }
        }
    }

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        if (timeUntilStart >= 1) {
            valuesMap.put(ChatColor.GREEN.toString(), null);
            valuesMap.put(ChatColor.GREEN.toString() + ChatColor.BOLD + "Mobs Spawn", TimeUtils.formatIntoMMSS(timeUntilStart));
        } else {
            valuesMap.put(ChatColor.GREEN + "Alive", getPlayers(true).size() + ChatColor.GRAY.toString());
            valuesMap.put(ChatColor.RED + "Dead", getPlayers(false).size() - getPlayers(true).size());
        }

        ScoreboardManager.getInstance().setGlobalScoreboard(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Island Defense", valuesMap, ScoreboardType.NORMAL);
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