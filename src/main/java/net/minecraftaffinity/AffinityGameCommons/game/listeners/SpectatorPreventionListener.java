package net.minecraftaffinity.AffinityGameCommons.game.listeners;

import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by macguy8 on 3/25/2014.
 */
public class SpectatorPreventionListener implements Listener {

    //***************************//

    @EventHandler
    public void preventDamaging(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && GameManager.getInstance().getCurrentGame().isSpec((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
      public void preventBlockPlace(BlockPlaceEvent event) {
        if (GameManager.getInstance().getCurrentGame().isSpec(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventBlockBreak(BlockBreakEvent event) {
        if (GameManager.getInstance().getCurrentGame().isSpec(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventItemDrop(PlayerDropItemEvent event) {
        if (GameManager.getInstance().getCurrentGame().isSpec(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventItemPickup(PlayerPickupItemEvent event) {
        if (GameManager.getInstance().getCurrentGame().isSpec(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventRotatingItemFrame(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame && GameManager.getInstance().getCurrentGame().isSpec(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventBreakingHanging(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player && GameManager.getInstance().getCurrentGame().isSpec((Player) event.getRemover())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventPlacingHanging(HangingPlaceEvent event) {
        if (GameManager.getInstance().getCurrentGame().isSpec(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    //***************************//

}