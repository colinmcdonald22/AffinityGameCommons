package net.minecraftaffinity.AffinityGameCommons.listeners;

import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Created by macguy8 on 3/25/2014.
 */
public class NoChunkUnloadListener implements Listener {

    //***************************//

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (MapManager.getInstance().getLobbyMap() == null || MapManager.getInstance().getLobbyMap().getWorld() == null) {
            return;
        }

        if (event.getWorld().equals(MapManager.getInstance().getLobbyMap().getWorld())) {
            event.setCancelled(true);
        }
    }

    //***************************//

}
