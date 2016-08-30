package net.minecraftaffinity.AffinityGameCommons.game.listeners;

import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameToggle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by macguy8 on 3/25/2014.
 */
public class FollowGameTogglesListener implements Listener {

    //***************************//

    @EventHandler
    public void onBlockPlace2(BlockPlaceEvent event) {
        if (GameManager.getInstance().getCurrentGame().valid(event.getPlayer()) && !GameManager.getInstance().getCurrentGame().isToggleEnabled(GameToggle.ENABLE_BLOCK_PLACE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak2(BlockBreakEvent event) {
        if (GameManager.getInstance().getCurrentGame().valid(event.getPlayer()) && !GameManager.getInstance().getCurrentGame().isToggleEnabled(GameToggle.ENABLE_BLOCK_BREAK)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop2(PlayerDropItemEvent event) {
        if (GameManager.getInstance().getCurrentGame().valid(event.getPlayer()) && !GameManager.getInstance().getCurrentGame().isToggleEnabled(GameToggle.ENABLE_ITEM_DROP)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup2(PlayerPickupItemEvent event) {
        if (GameManager.getInstance().getCurrentGame().valid(event.getPlayer()) && !GameManager.getInstance().getCurrentGame().isToggleEnabled(GameToggle.ENABLE_ITEM_PICKUP)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            return;
        }

        if (!GameManager.getInstance().getCurrentGame().isToggleEnabled(GameToggle.ENABLE_MOB_SPAWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!GameManager.getInstance().getCurrentGame().isToggleEnabled(GameToggle.ENABLE_FOOD_CHANGE)) {
            event.setFoodLevel(20);
        }
    }

    //***************************//

}