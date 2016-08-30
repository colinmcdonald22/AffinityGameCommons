package net.minecraftaffinity.AffinityGameCommons.game.enums;

import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGames.games.arrowrain.ArrowRainGame;
import net.minecraftaffinity.AffinityGames.games.basewars.BaseWarsGame;
import net.minecraftaffinity.AffinityGames.games.cookiechaser.CookieChaserGame;
import net.minecraftaffinity.AffinityGames.games.decay.DecayGame;
import net.minecraftaffinity.AffinityGames.games.islanddefense.IslandDefenseGame;
import net.minecraftaffinity.AffinityGames.games.redlightgreenlight.RedLightGreenLightGame;
import net.minecraftaffinity.AffinityGames.games.suicidesurvival.SuicideSurvivalGame;

/**
 * Created by macguy8 on 6/28/2014.
 */
public enum GameType {

    //***************************//

    ARROW_RAIN("ArrowRain", ArrowRainGame.class),
    BASE_WARS("BaseWars", BaseWarsGame.class),
    COOKIE_CHASER("CookieChaser", CookieChaserGame.class),
    //DEATHRUN("Deathrun", DeathrunGame.class),
    DECAY("Decay", DecayGame.class),
    ISLAND_DEFENSE("IslandDefense", IslandDefenseGame.class),
    RED_LIGHT_GREEN_LIGHT("RedLightGreenLight", RedLightGreenLightGame.class),
    SUICIDE_SURVIVAL("SuicideSurvival", SuicideSurvivalGame.class);

    //***************************//

    private String configName;
    private Class<? extends Game> gameClass;

    //***************************//

    GameType(String configName, Class<? extends Game> gameClass) {
        this.configName = configName;
        this.gameClass = gameClass;
    }

    //***************************//

    public String getConfigName() {
        return (configName);
    }

    public Class<? extends Game> getGameClass() {
        return (gameClass);
    }

    //***************************//
}