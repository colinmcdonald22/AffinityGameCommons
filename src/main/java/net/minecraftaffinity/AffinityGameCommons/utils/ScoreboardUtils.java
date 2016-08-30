package net.minecraftaffinity.AffinityGameCommons.utils;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 05, 01 2014
 * Programmed for the AffinityGameAPI project.
 */
public class ScoreboardUtils {

    //***************************//

    public static String makeFitForScoreboard(String string, int length) {
        string = string.trim();

        if (string.length() > length) {
            string = string.substring(0, length - 1);
        }

        return (string);
    }

    public static String makeFitForScoreboard(String string) {
        return (makeFitForScoreboard(string, 16));
    }

    //***************************//

}