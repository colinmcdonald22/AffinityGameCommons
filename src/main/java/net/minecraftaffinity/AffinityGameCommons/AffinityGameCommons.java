package net.minecraftaffinity.AffinityGameCommons;

import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.ScoreboardManager;
import net.minecraftaffinity.AffinityGames.GameLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Programmed by minecraftaffinity (Colin McDonald)
 * Creation Date: 13, 10 2013
 * Programmed for the AffinityGameCommons project.
 */
public class AffinityGameCommons extends JavaPlugin {

    //***************************//

    private static AffinityGameCommons instance;

    //***************************//

    @Override
    public void onEnable() {
        setInstance(this);
        saveDefaultConfig();

        GameLoader.registerRepositoryItems();

        MapManager.getInstance();
        ScoreboardManager.getInstance();
        GameManager.getInstance().start();
    }

    @Override
    public void onDisable() {
        ScoreboardManager.reset();
        setInstance(null);
    }

    //***************************//

    public static void setInstance(AffinityGameCommons instance) {
        AffinityGameCommons.instance = instance;
    }

    public static AffinityGameCommons getInstance() {
        return (instance);
    }

    //***************************//

}