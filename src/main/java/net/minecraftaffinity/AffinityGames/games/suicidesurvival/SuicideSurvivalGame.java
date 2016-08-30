package net.minecraftaffinity.AffinityGames.games.suicidesurvival;

import net.minecraftaffinity.AffinityCommons.cooldown.CooldownManager;
import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityCommons.utils.ItemUtils;
import net.minecraftaffinity.AffinityCommons.utils.TimeUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.abilities.objects.Ability;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.ScoreboardManager;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.enums.ScoreboardType;
import net.minecraftaffinity.AffinityGameCommons.teams.enums.FriendlyFireSetting;
import net.minecraftaffinity.AffinityGameCommons.teams.objects.Team;
import net.minecraftaffinity.AffinityGameCommons.utils.GameUtils;
import net.minecraftaffinity.AffinityGameCommons.utils.TeamUtils;
import net.minecraftaffinity.AffinityGames.kits.PlayerKit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 03, 02 2014
 * Programmed for the AffinityGames project.
 */
public class SuicideSurvivalGame extends Game {

    //***************************//

    public SuicideSurvivalGame() {
        super("Suicide Survival", new String[] { "Avoid the... suiciding crafting tables. Yeah." }, new Kit[] {
                new PlayerKit(new Ability[] { }),
        }, new Team[] {
                new Team("Survivors", "Survivors", ChatColor.GREEN, FriendlyFireSetting.DISALLOW),
                new Team("Suicider", "Survivors", ChatColor.RED, FriendlyFireSetting.DISALLOW)
        });
    }

    //***************************//

    ItemStack tnt = ItemUtils.name(Material.TNT, ChatColor.GOLD.toString() + ChatColor.BOLD + "Right click to blow up!");
    ItemStack snowballs = new ItemStack(Material.SNOW_BALL, 10);
    int time = 150;

    //***************************//

    @Override
    public void onGameStart() {
        AffinityGameCommons.getInstance().getServer().broadcastMessage("Game starting... Good luck!");

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            if (getTeamSelections().get(player.getName()).getName().equals(getTeams()[1].getName())) {
                //DisguiseAPI.disguiseEntity(player, new MiscDisguise(DisguiseType.FALLING_BLOCK, Material.WORKBENCH.getId(), 0));
            }
        }

        new BukkitRunnable() {

            int tick = 0;

            public void run() {
                if (getState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }

                if (++tick % 30 == 0) {
                    tick = 0;

                    for (Location loc : getMap().getLocations("Chests")) {
                        BlockState bS = loc.getBlock().getState();

                        if (bS instanceof Chest) {
                            Chest chest = (Chest) bS;

                            chest.getInventory().clear();
                            chest.getInventory().addItem(snowballs);
                            chest.update();
                        }
                    }

                    AffinityGameCommons.getInstance().getServer().broadcastMessage("Chests have restocked!");
                }

                if (time-- == 0) {
                    endGame(getTeams()[0]);
                }

                updateScoreboard();
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 20L, 20L);
    }

    @Override
    public void onGameEnd() {
        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            //if (DisguiseAPI.isDisguised(player)) {
               // DisguiseAPI.undisguiseToAll(player);
            //}
        }
    }

    //***************************//

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<String, Object>();

        valuesMap.put(ChatColor.RED.toString(), null);
        valuesMap.put(ChatColor.BOLD + "Time", ChatColor.GOLD + TimeUtils.formatIntoMMSS(time));

        ScoreboardManager.getInstance().setGlobalScoreboard(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Suicide Survival", valuesMap, ScoreboardType.NORMAL);
    }

    @Override
    public void giveSpawnItems(Player player) {
        if (getTeamSelections().get(player.getName()).getName().equals(getTeams()[1].getName())) {
            for (int i = 0; i <= 8; i++) {
                player.getInventory().setItem(i, tnt);
            }
        } else {
               player.getInventory().setItem(0, snowballs);
        }

        if (getKitSelections().containsKey(player.getName())) {
            getKitSelections().get(player.getName()).equipPlayer(player);
        }
    }

    //***************************//

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.WORKBENCH) {
            event.setCancelled(true);
            return;
        }

         if (valid(event.getPlayer()) && event.getAction() != Action.PHYSICAL && event.getItem() != null && event.getItem().getType() == Material.TNT ) {
            if (CooldownManager.getInstance().use(event.getPlayer(), "Explode", 3000, false)) {
                event.getPlayer().getWorld().createExplosion(event.getPlayer().getLocation(), 0F);

                for (Entity entity : event.getPlayer().getNearbyEntities(3, 3, 3)) {
                    if (entity instanceof Player && valid((Player) entity) && getTeamSelections().get(((Player) entity).getName()).getName().equals(getTeams()[0].getName())) {
                        ((Player) entity).damage(100);
                    }
                }

                event.getPlayer().damage(100);
            } else {
                event.getPlayer().sendMessage(ChatUtils.GRAY + "You cannot explode again this soon!");
            }
        }
    }

    @Override
    public boolean canPlayerRespawn(final Player player) {
        if (!getTeamSelections().get(player.getName()).getName().equals(getTeams()[1].getName())) {
            getTeamSelections().put(player.getName(), getTeams()[1]);
            GameUtils.selectTeam(this, player, getTeams()[1], true, true);

            //DisguiseAPI.disguiseEntity(player, new MiscDisguise(DisguiseType.FALLING_BLOCK, Material.WORKBENCH.getId(), 0));
        }

        return (true);
    }

    @Override
    public void onPlayerDeath(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 0) {
            endGame(getTeams()[1]);
        }
    }

    @Override
    public void onPlayerDisconnect(Player player) {
        updateScoreboard();

        if (TeamUtils.countPlayers(getTeams()[0]) == 0) {
            endGame(getTeams()[1]);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        for (Entity entity : event.getEntity().getNearbyEntities(2, 2, 2)) {
            if (entity instanceof Player && valid((Player) entity) && getTeamSelections().get(((Player) entity).getName()).getName().equals(getTeams()[1].getName())) {
                   ((Player) entity).damage(100);
            }
        }
    }

    //***************************//

}