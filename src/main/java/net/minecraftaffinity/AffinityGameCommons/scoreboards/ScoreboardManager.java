package net.minecraftaffinity.AffinityGameCommons.scoreboards;

import net.minecraftaffinity.AffinityCommons.utils.ChatUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.enums.ScoreboardType;
import net.minecraftaffinity.AffinityGameCommons.utils.ScoreboardUtils;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 03, 03 2014
 * Programmed for the AffinityGameAPI project.
 */
public class ScoreboardManager {

    //***************************//

    private static ScoreboardManager instance;

    private HashMap<String, Integer> last = new HashMap<String, Integer>();

    private Scoreboard board = null;
    private Objective objective;

    //***************************//

    public ScoreboardManager() {
        board = AffinityGameCommons.getInstance().getServer().getScoreboardManager().getMainScoreboard();
        objective = board.getObjective("Info") == null ? board.registerNewObjective("Info", "Info") : board.getObjective("Info");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Undefined");
    }

    //***************************//

    public static ScoreboardManager getInstance() {
        if (instance == null) {
            instance = new ScoreboardManager();
        }

        return (instance);
    }

    public static void reset() {
        for (String string : instance.last.keySet()) {
            instance.board.resetScores(AffinityGameCommons.getInstance().getServer().getOfflinePlayer(string));
        }

        instance = null;
    }

    //***************************//

    public Scoreboard getScoreboard() {
        return (board);
    }

    public Objective getSidebarObjective() {
        return (objective);
    }

    public void setGlobalScoreboard(String title, HashMap<String, Object> valueMap, ScoreboardType type) {
        objective.setDisplayName(ScoreboardUtils.makeFitForScoreboard(title, 32));

        if (valueMap == null) {
            return;
        }

        int count = 16;
        HashMap<String, Integer> finalValues = new HashMap<String, Integer>();

        switch (type) {
            case NORMAL:
                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                    if (entry.getValue() instanceof String) {
                        finalValues.put(ScoreboardUtils.makeFitForScoreboard(entry.getKey()), count--);
                        finalValues.put(ScoreboardUtils.makeFitForScoreboard((String) entry.getValue()), count--);
                    } else if (entry.getValue() instanceof Integer) {
                        finalValues.put(ScoreboardUtils.makeFitForScoreboard(entry.getKey()), count--);
                        finalValues.put(ScoreboardUtils.makeFitForScoreboard(String.valueOf((Integer) entry.getValue())), count--);
                    } else if (entry.getValue() instanceof Boolean) {
                        finalValues.put(ScoreboardUtils.makeFitForScoreboard(entry.getKey()), count--);
                        finalValues.put(ScoreboardUtils.makeFitForScoreboard(ChatUtils.colorYesNo((Boolean) entry.getValue())), count--);
                    } else if (entry.getValue() instanceof Collection) {
                        Collection<String> collection = (Collection<String>) entry.getValue();

                        finalValues.put(ScoreboardUtils.makeFitForScoreboard(entry.getKey()), count--);

                        for (String value : collection) {
                            finalValues.put(ScoreboardUtils.makeFitForScoreboard(value), count--);
                        }
                    } else if (entry.getValue() == null) {
                        finalValues.put(ScoreboardUtils.makeFitForScoreboard(entry.getKey()), count--);
                    }
                }

                break;
            case DISPLAY_EXACT_VALUES:
                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                    finalValues.put(entry.getKey(), (Integer) entry.getValue());
                }

                break;
        }

        for (String oldValue : last.keySet()) {
            if (!finalValues.containsKey(oldValue)) {
                board.resetScores(AffinityGameCommons.getInstance().getServer().getOfflinePlayer(oldValue));
            }
        }

        int done = 0;

        for (Map.Entry<String, Integer> finalValue : finalValues.entrySet()) {
            objective.getScore(AffinityGameCommons.getInstance().getServer().getOfflinePlayer(finalValue.getKey())).setScore(finalValue.getValue());

            if (++done == 15) {
                break;
            }
        }

        last = finalValues;
    }

    public void debug() {
        for (Map.Entry<String, Integer> e : last.entrySet()) {
            System.out.println(e.getKey() + " -  " + e.getValue() + " - " + e.getKey().length());
        }
    }

    //***************************//

}