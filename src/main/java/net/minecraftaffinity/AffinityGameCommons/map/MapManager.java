package net.minecraftaffinity.AffinityGameCommons.map;

import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import net.minecraftaffinity.AffinityCommons.commands.CommandManager;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.map.commands.MapCommand;
import net.minecraftaffinity.AffinityGameCommons.map.listeners.MapLocationSetListener;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 24, 12 2013
 * Programmed for the AffinityGameAPI project.
 */
public class MapManager {

    //***************************//

    private static MapManager instance;

    private Set<Map> loadedMaps = new HashSet<Map>();
    private Map lobbyMap;

    //***************************//

    public MapManager() {
        CommandManager.getInstance().addCommand(new MapCommand());

        for (File file : new File(".").listFiles()) {
            if (file.isDirectory()) {
                File json = new File(file, file.getName() + ".json");

                if (json.exists()) {
                    try {
                        BufferedInputStream e = new BufferedInputStream(new FileInputStream(json));

                        StringWriter writer = new StringWriter();
                        IOUtils.copy(e, writer, "utf-8");

                        loadedMaps.add((new Gson()).fromJson(writer.toString(), Map.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (Map m : loadedMaps) {
            if (m.getName().equalsIgnoreCase("Lobby") && m.getGameMode().equalsIgnoreCase("Lobby")) {
                lobbyMap = m;
                break;
            }
        }

        if (lobbyMap != null) {
            lobbyMap.initWorld();

            lobbyMap.getWorld().setGameRuleValue("doDaylightCycle", "false");
            lobbyMap.getWorld().setTime(6000L);
        }

        AffinityGameCommons.getInstance().getServer().getPluginManager().registerEvents(new MapLocationSetListener(), AffinityGameCommons.getInstance());
    }

    //***************************//

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }

        return (instance);
    }

    //***************************//

    public void saveMaps() {
        for (Map map : loadedMaps) {
            saveMap(map);
        }
    }

    public void saveMap(Map map) {
        try {
            File mapFile = new File(map.getName() + "-" + map.getGameMode(), map.getName() + "-" + map.getGameMode() + ".json");

            (new File(map.getName() + "-" + map.getGameMode())).mkdirs();

            if (!mapFile.exists()) {
                mapFile.createNewFile();
            }

            FileWriter e = new FileWriter(mapFile);

            e.write((new Gson()).toJson(map));

            e.flush();
            e.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //***************************//

    public Map getMap(String name) {
           for (Map map : loadedMaps) {
            if (map.getName().equals(name)) {
                return (map);
            }
        }

        return (null);
    }

    public Map findAppropriateMap(Game game) {
        for (Map map : loadedMaps) {
            if (map.getName().equals("Lobby")) {
                continue;
            }

            if (map.getGameMode() != null && map.getGameMode().equalsIgnoreCase(game.getName())) {
                return (map);
            }
        }

        return (null);
    }

    public Set<Map> getMaps() {
        return (loadedMaps);
    }

    public Map getLobbyMap() {
        return (lobbyMap);
    }

    //***************************//

}