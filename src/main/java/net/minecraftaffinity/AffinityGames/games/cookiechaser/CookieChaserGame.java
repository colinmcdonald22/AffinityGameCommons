package net.minecraftaffinity.AffinityGames.games.cookiechaser;

import net.minecraftaffinity.AffinityCommons.utils.ItemUtils;
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
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 18, 03 2014
 * Programmed for the AffinityGameAPI project.
 */
public class CookieChaserGame extends Game {

    //***************************//

    public CookieChaserGame() {
        super("Cookie Chaser", new String[] { "Chase the cookies to gain time!", "Be the last one standing.", "Keep eating those cookies!" }, new Kit[] {
                new PlayerKit(new Ability[] { })
        }, new Team[] {
                new Team("Cookie Eater", "Cookie Eater", ChatColor.GRAY, FriendlyFireSetting.DISALLOW)
        });
    }

    //***************************//

    int cookie = 0;

    //***************************//

    @Override
    public void onGameLoad() {
        enableToggle(GameToggle.HIDE_TEAMS);
    }

    @Override
    public void onGameStart() {
        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "You have 5 seconds before cookies will start spawning.");

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            if (valid(player)) {
                player.setLevel(20);
            }
        }

        new BukkitRunnable() {

            public void run() {
                if (getState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }

                if (countCookies() < (getPlayers(true).size() * 2) + 1) {
                    for (Location loc : getMap().getLocations("CookieSpawn")) {
                        if (MathUtils.getRandom().nextInt(100) < 1) {
                            loc.getWorld().dropItem(loc, ItemUtils.name(Material.COOKIE, String.valueOf(cookie++)));
                        }
                    }
                }

                for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                    if (valid(player)) {
                        if (player.getLevel() == 0) {
                            player.damage(20F);
                            player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                        } else {
                            player.setLevel(player.getLevel() - 1);
                        }
                    }
                }
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 100L, 20L);
    }

    //***************************//

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.GREEN + "Alive", getPlayers(true).size() + ChatColor.values()[0].toString());
        valuesMap.put(ChatColor.RED + "Dead", getPlayers(false).size() - getPlayers(true).size() + ChatColor.values()[1].toString());

        ScoreboardManager.getInstance().setGlobalScoreboard(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Cookie Chaser", valuesMap, ScoreboardType.NORMAL);
    }

    //***************************//

    public int countCookies() {
        return (getMap().getWorld().getEntitiesByClass(Item.class).size());
    }

    @Override
    public boolean canPlayerRespawn(Player player) {
        return (false);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (valid(event.getPlayer()) && event.getItem().getItemStack().getType() == Material.COOKIE) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 1F, 1F);
            event.getItem().remove();
            event.setCancelled(true);
            event.getPlayer().setLevel(event.getPlayer().getLevel() + 2);
        }
    }

    @Override
    public void onPlayerDeath(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 1) {
            endGame(Arrays.asList(Bukkit.getPlayer(getPlayers(true).iterator().next())));
        }
    }

    @Override
    public void onPlayerDisconnect(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 1) {
            endGame(Arrays.asList(Bukkit.getPlayer(getPlayers(true).iterator().next())));
        }
    }

    //***************************//

}