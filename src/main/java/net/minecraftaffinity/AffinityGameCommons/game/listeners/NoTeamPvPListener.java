package net.minecraftaffinity.AffinityGameCommons.game.listeners;

import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 05, 01 2014
 * Programmed for the AffinityGameAPI project.
 */
public class NoTeamPvPListener implements Listener {

    //***************************//

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || GameManager.getInstance().getCurrentGame() == null) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager == null || !GameManager.getInstance().getCurrentGame().valid(damager) || !GameManager.getInstance().getCurrentGame().valid(damaged)) {
            return;
        }

        if (!GameManager.getInstance().getCurrentGame().canDamage(damager, damaged)) {
            event.setCancelled(true);
        }
    }

    //***************************//

}