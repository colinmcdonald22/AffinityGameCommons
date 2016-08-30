package net.minecraftaffinity.AffinityGameCommons.utils;

import net.minecraftaffinity.AffinityCommons.utils.LoggingUtils;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by macguy8 on 4/20/2014.
 */
public class KitUtils {

    //***************************//

    public static void renderKits(Game game) {
        if (!MapManager.getInstance().getLobbyMap().getMapLocations().containsKey("LobbyKitDisplay")) {
            LoggingUtils.log("Kits", "No lobby team display location found.");
            return;
        }

        BlockFace face = BlockFace.EAST;
        Block start = MapManager.getInstance().getLobbyMap().getLocationNumber("LobbyKitDisplay", 0).getBlock().getRelative(face.getOppositeFace(), game.getKits().length);
        Location spawn = MapManager.getInstance().getLobbyMap().getLocationRandom("Spawn");

        for (Kit kit : game.getKits()) {
            // TODO: Render kits
            start = start.getRelative(face, 2);
        }
    }

    //***************************//

}