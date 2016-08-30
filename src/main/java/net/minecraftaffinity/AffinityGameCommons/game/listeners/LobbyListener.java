package net.minecraftaffinity.AffinityGameCommons.game.listeners;

import net.minecraftaffinity.AffinityCommons.user.UserManager;
import net.minecraftaffinity.AffinityCommons.user.enums.PermissionsRank;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by macguy8 on 3/25/2014.
 */
public class LobbyListener implements Listener {

    //***************************//

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !UserManager.getInstance().getUser(event.getPlayer()).getRank().isGreaterOrEqualTo(PermissionsRank.DEVELOPER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !UserManager.getInstance().getUser(event.getPlayer()).getRank().isGreaterOrEqualTo(PermissionsRank.DEVELOPER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getY() <= 0) {
            event.getPlayer().teleport(MapManager.getInstance().getLobbyMap().getLocationRandom("Spawn"));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    //***************************//

}