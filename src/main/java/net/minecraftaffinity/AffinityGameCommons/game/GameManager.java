package net.minecraftaffinity.AffinityGameCommons.game;

import net.minecraftaffinity.AffinityCommons.AffinityCommons;
import net.minecraftaffinity.AffinityCommons.commands.CommandManager;
import net.minecraftaffinity.AffinityCommons.utils.LoggingUtils;
import net.minecraftaffinity.AffinityCommons.utils.MathUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.game.commands.GameCommand;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameType;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 04, 01 2014
 * Programmed for the AffinityGameAPI project.
 */
public class GameManager {

    //***************************//

    private static GameManager instance;

    private Game currentGame = null;
    private HashMap<GameType, Boolean> possibleGames = new HashMap<GameType, Boolean>();

    //***************************//

    public GameManager() {
        CommandManager.getInstance().addCommand(new GameCommand());

        for (GameType gameType : GameType.values()) {
            possibleGames.put(gameType, AffinityGameCommons.getInstance().getConfig().getBoolean("Games." + gameType.getConfigName(), false));
        }
    }

    //***************************//

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return (instance);
    }

    //***************************//

    public void start() {
        int enabledGames = 0;

        for (GameType gameType : possibleGames.keySet()) {
            if (possibleGames.get(gameType)) {
                enabledGames++;
            }
        }

        if (AffinityGameCommons.getInstance().getConfig().getBoolean("DisableGames", false) || enabledGames == 0) {
            LoggingUtils.log("Game Cycle", "Disabling the game cycle...");
            return;
        }

        startGame(selectNext());
    }

    //***************************//

    public boolean startGame(GameType gameType) {
        Game game;

        try {
            game = gameType.getGameClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            rebootServer();
            return (false);
        }

        AffinityGameCommons.getInstance().getServer().getPluginManager().registerEvents(game, AffinityGameCommons.getInstance());

        this.currentGame = game;
        return (true);
    }

    public void endCurrentGame() {
        startGame(selectNext());
    }

    //***************************//

    public Game getCurrentGame() {
        return (currentGame);
    }

    public HashMap<GameType, Boolean> getPossibleGames() {
        return (possibleGames);
    }

    //***************************//

    public GameType selectNext() {
        ArrayList<GameType> possible = new ArrayList<GameType>();

        for (GameType type : possibleGames.keySet()) {
            if (possibleGames.get(type)) {
                possible.add(type);
            }
        }

        return (possible.get(MathUtils.getRandom().nextInt(possible.size())));
    }

    private void rebootServer() {
        this.currentGame = null;
        AffinityCommons.getInstance().rebootServer();
    }

    //***************************//

}