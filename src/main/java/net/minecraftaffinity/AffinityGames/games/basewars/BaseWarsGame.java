package net.minecraftaffinity.AffinityGames.games.basewars;

import net.minecraftaffinity.AffinityCommons.nametag.NametagEditManager;
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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class BaseWarsGame extends Game {

    //***************************//

    public BaseWarsGame() {
        super("Base Wars", new String[] { "Gear up and build your base,", "PvP and keep your team alive!", "Be the last team standing!" }, new Kit[] {
                new PlayerKit(new Ability[] { })
        }, new Team[] {
                new Team("Red", "Red", ChatColor.RED, FriendlyFireSetting.DISALLOW),
                new Team("Blue", "Blue", ChatColor.BLUE, FriendlyFireSetting.DISALLOW)
        });
    }

    //***************************//

    int timeUntilWalls = 300;

    //***************************//

    @Override
    public void onGameStart() {
        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "You have 5 minutes until the wall drops.");

        enableToggle(GameToggle.ENABLE_BLOCK_BREAK);
        enableToggle(GameToggle.ENABLE_BLOCK_PLACE);
        enableToggle(GameToggle.ENABLE_ITEM_PICKUP);
        enableToggle(GameToggle.ENABLE_ITEM_DROP);
        enableToggle(GameToggle.ENABLE_ITEM_DROPS_ON_DEATH);
        enableToggle(GameToggle.ENABLE_ITEM_DROPS_ON_LOGOUT);

        new BukkitRunnable() {

            public void run() {
                if (getState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }

                timeUntilWalls--;

                if (timeUntilWalls == 0) {
                    Iterator<Block> iterator = getMap().getRegion("Wall").blocks(getMap().getWorld());

                    while (iterator.hasNext()) {
                        Block block = iterator.next();

                        if (block.getType() != Material.AIR) {
                            block.setType(Material.AIR);
                        }
                    }

                    AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "The wall is now dropped! PvP is now enabled! Go and fight!");

                    for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1F, 2F);
                    }

                    cancel();
                } else if ((timeUntilWalls <= 60 && timeUntilWalls % 15 == 0) || (timeUntilWalls <= 30 && timeUntilWalls % 5 == 0) || (timeUntilWalls <= 10)) {
                    for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Walls in " + ChatColor.GOLD.toString() + ChatColor.BOLD + TimeUtils.formatIntoMMSS(timeUntilWalls));
                        player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1F, 1F);
                    }
                }

                updateScoreboard();
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 0L, 20L);
    }

    //***************************//

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        if (timeUntilWalls == 0) {
            NametagEditManager.getInstance().setPrefixAndSuffix("Wall is", "", " dropped!");
            valuesMap.put("Wall is", null);
        } else {
            NametagEditManager.getInstance().setPrefixAndSuffix("Wall drops in", "", ChatColor.AQUA + " " + TimeUtils.formatIntoMMSS(timeUntilWalls) + ChatColor.WHITE + ".");
            valuesMap.put("Wall drops in", null);
        }

        ScoreboardManager.getInstance().setGlobalScoreboard(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Base Wars", valuesMap, ScoreboardType.NORMAL);
    }

    //***************************//

    @Override
    public boolean canPlayerRespawn(Player player) {
        return (false);
    }

    public void giveSpawnItems(Player player) {
        if (getKitSelections().containsKey(player.getName())) {
            getKitSelections().get(player.getName()).equipPlayer(player);
        }

        player.getInventory().addItem(
                new ItemStack(Material.IRON_PICKAXE),
                new ItemStack(Material.IRON_SPADE),
                new ItemStack(Material.STONE, 32),
                new ItemStack(Material.SMOOTH_STAIRS, 16),
                new ItemStack(Material.SMOOTH_BRICK, 64),
                new ItemStack(Material.BUCKET, 1),
                new ItemStack(Material.DIRT, 20)
        );
    }

    @Override
    public void onPlayerDeath(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 0) {
            endGame(getTeams()[1]);
        } else if (TeamUtils.countPlayers(getTeams()[1]) == 0) {
            endGame(getTeams()[0]);
        }
    }

    @Override
    public void onPlayerDisconnect(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 0) {
            endGame(getTeams()[1]);
        } else if (TeamUtils.countPlayers(getTeams()[1]) == 0) {
            endGame(getTeams()[0]);
        }
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent event) {
        if (!valid(event.getPlayer()) || timeUntilWalls == 0) {
            return;
        }

        boolean cancel = false;

        if (!getMap().getRegion("RedBase").contains(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation()) && getTeamSelections().get(event.getPlayer().getName()).getName().equals(getTeams()[0].getName())) {
            cancel = true;
        } else if (!getMap().getRegion("BlueBase").contains(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation()) && getTeamSelections().get(event.getPlayer().getName()).getName().equals(getTeams()[1].getName())) {
            cancel = true;
        }

        if (cancel) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You can't use buckets outside of your base!");
            event.getPlayer().damage(1F);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!valid(event.getPlayer()) || timeUntilWalls == 0) {
            return;
        }

        boolean cancel = false;

        if (!getMap().getRegion("RedBase").contains(event.getBlock().getLocation()) && getTeamSelections().get(event.getPlayer().getName()).getName().equals(getTeams()[0].getName())) {
            cancel = true;
        } else if (!getMap().getRegion("BlueBase").contains(event.getBlock().getLocation()) && getTeamSelections().get(event.getPlayer().getName()).getName().equals(getTeams()[1].getName())) {
            cancel = true;
        }

        if (cancel) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You can't place blocks outside of your base!");
            event.getPlayer().damage(1F);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!valid(event.getPlayer()) || timeUntilWalls == 0) {
            return;
        }

        boolean cancel = false;

        if (!getMap().getRegion("RedBase").contains(event.getBlock().getLocation()) && getTeamSelections().get(event.getPlayer().getName()).getName().equals(getTeams()[0].getName())) {
            cancel = true;
        } else if (!getMap().getRegion("BlueBase").contains(event.getBlock().getLocation()) && getTeamSelections().get(event.getPlayer().getName()).getName().equals(getTeams()[1].getName())) {
            cancel = true;
        }

        if (cancel) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You can't break blocks outside of your base!");
            event.getPlayer().damage(1F);
        }
    }

    //***************************//

}