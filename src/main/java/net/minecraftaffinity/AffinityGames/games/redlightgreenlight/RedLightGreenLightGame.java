package net.minecraftaffinity.AffinityGames.games.redlightgreenlight;

import net.minecraftaffinity.AffinityCommons.utils.MathUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.abilities.objects.Ability;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by macguy8 on 3/28/2014.
 */
public class RedLightGreenLightGame extends Game {

    //***************************//

    public RedLightGreenLightGame() {
        super("Red Light Green Light", new String[] { "Race to the end", "Don't move while the light is red", "Yellow is a warning!" }, new Kit[] {
                new PlayerKit(new Ability[] { })
        }, new Team[] {
                new Team("Players", "Players", ChatColor.GRAY, FriendlyFireSetting.DISALLOW)
        });
    }

    //***************************//

    public static final DyeColor GREEN = DyeColor.GREEN;
    public static final DyeColor YELLOW = DyeColor.YELLOW;
    public static final DyeColor RED = DyeColor.RED;

    //***************************//

    DyeColor setColor = DyeColor.GREEN;
    int redTick = 0;
    HashMap<DyeColor, ItemStack> itemMap = new HashMap<DyeColor, ItemStack>();

    //***************************//

    @Override
    public void onGameLoad() {
        enableToggle(GameToggle.HIDE_TEAMS);
    }

    @Override
    public void onGameStart() {
        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Make sure to look up!");
        setIndiciatorLight(GREEN);

        itemMap.put(GREEN, new ItemStack(Material.WOOL, 1, GREEN.getWoolData()));
        itemMap.put(YELLOW, new ItemStack(Material.WOOL, 1, YELLOW.getWoolData()));
        itemMap.put(RED, new ItemStack(Material.WOOL, 1, RED.getWoolData()));

        new BukkitRunnable() {

            public void run() {
                if (getState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }

                if (setColor == GREEN && MathUtils.getRandom().nextInt(100) <= 40) {
                    setIndiciatorLight(YELLOW);
                } else if (setColor == YELLOW) {
                    setIndiciatorLight(RED);
                } else if (setColor == RED) {
                    if (redTick++ == 3) {
                        redTick = 0;
                        setIndiciatorLight(GREEN);
                    }
                }

                for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                    if (getMap().getRegion("Finish").contains(player.getLocation()) && valid(player)) {
                        endGame(Arrays.asList(player));
                        break;
                    }
                }
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 40L, 20L);
    }

    //***************************//

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.GREEN + "To-Do", null);

        ScoreboardManager.getInstance().setGlobalScoreboard(ChatColor.RED.toString() + ChatColor.BOLD + "Red Light " + ChatColor.GREEN.toString() + ChatColor.BOLD + "Green Light", valuesMap, ScoreboardType.NORMAL);
    }

    //***************************//

    @Override
    public boolean canPlayerRespawn(Player player) {
        return (true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return;
        }

        if (setColor == RED && valid(event.getPlayer())) {
            event.getPlayer().teleport(getMap().getLocationRandom("Spawn"));
        }
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

    public void setIndiciatorLight(DyeColor dyeColor) {
        this.setColor = dyeColor;

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            if (valid(player)) {
                for (int i = 0; i < 8; i++) {
                    player.getInventory().setItem(i, itemMap.get(dyeColor));
                }
            }
        }

        Iterator<Block> lightIterator = getMap().getRegion("Light").blocks(getMap().getWorld());

        while (lightIterator.hasNext()) {
            Block next = lightIterator.next();

            if (next.getType() == Material.WOOL) {
                next.setData(dyeColor.getWoolData());
            }
        }
    }

    //***************************//

}