package net.minecraftaffinity.AffinityGameCommons.map.objects;

import net.minecraft.util.com.google.gson.annotations.SerializedName;
import net.minecraftaffinity.AffinityCommons.utils.Cuboid;
import net.minecraftaffinity.AffinityCommons.utils.MathUtils;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Programmed by minecraftaffinity (Colin McDonald)
 * Creation Date: 13, 10 2013
 * Programmed for the AffinityGameCommons project.
 */
public class Map {

    //***************************//

    @SerializedName("Name")
    private String name = "Unknown";
    @SerializedName("Authors")
    private List<String> authors = new ArrayList<String>();
    @SerializedName("GameMode")
    private String gameMode = "Unknown";

    @SerializedName("Image")
    private String image = "";

    private transient World world;

    @SerializedName("Locations")
    private HashMap<String, ArrayList<BlockVector>> mapLocations = new HashMap<String, ArrayList<BlockVector>>();
    @SerializedName("Regions")
    private HashMap<String, Cuboid> mapRegions = new HashMap<String, Cuboid>();

    //***************************//

    public Map(String name) {
        this.name = name;
    }

    public void initWorld() {
        this.world = AffinityGameCommons.getInstance().getServer().createWorld(new WorldCreator(name + "-" + gameMode).generateStructures(false));
        this.world.setAutoSave(false);
    }

    public void reset() {
        AffinityGameCommons.getInstance().getServer().unloadWorld(world, false);
        this.world = AffinityGameCommons.getInstance().getServer().createWorld(new WorldCreator(name + "-" + gameMode));
    }

    //***************************//

    public String getName() {
        return (name);
    }

    public void setName(final String name) {
        this.name = name;
        MapManager.getInstance().saveMap(this);
    }

    public List<String> getAuthors() {
        return (authors);
    }

    public void setAuthors(final ArrayList<String> authors) {
        this.authors = authors;
        MapManager.getInstance().saveMap(this);
    }

    public void setAuthorsCommaSplit(String authors) {
        setAuthors(new ArrayList<String>(Arrays.asList(authors.split(","))));
    }

    public String getAuthorsFormatted() {
        StringBuilder builder = new StringBuilder();
        int i = 0;

        for (String author : authors) {
            if (i != 0) {
                if (i == getAuthors().size() - 1) {
                    builder.append(authors.size() == 2 ? " and " : ", and ");
                } else {
                    builder.append(", ");
                }
            }

            builder.append(author);

            i++;
        }

        return (builder.toString());
    }

    public HashMap<String, ArrayList<BlockVector>> getMapLocations() {
        return (mapLocations);
    }

    public HashMap<String, Cuboid> getMapRegions() {
        return (mapRegions);
    }

    public String getGameMode() {
        return (gameMode);
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
        MapManager.getInstance().saveMap(this);
    }

    public void setImage(String image) {
        this.image = image;
        MapManager.getInstance().saveMap(this);
    }

    public Location getLocationRandom(String name) {
        return (mapLocations.get(name).get(MathUtils.getRandom().nextInt(mapLocations.get(name).size())).toLocation(world));
    }

    public Location getLocationNumber(String name, int id) {
        return (mapLocations.get(name).get(id).toLocation(world));
    }

    public void setLocation(final String name, ArrayList<BlockVector> locations) {
        mapLocations.put(name, locations);
        MapManager.getInstance().saveMap(this);
    }

    public World getWorld() {
        return (world);
    }

    public void addLocation(String name, Location toAdd) {
        mapLocations.get(name).add(toAdd.toVector().toBlockVector());
        MapManager.getInstance().saveMap(this);
    }

    public void removeLocation(String name, Location toRemove) {
        mapLocations.get(name).remove(toRemove.toVector().toBlockVector());
        MapManager.getInstance().saveMap(this);
    }

    public ArrayList<Location> getLocations(String name) {
        if (mapLocations.containsKey(name)) {
            ArrayList<Location> locations = new ArrayList<Location>();

            for (BlockVector vector :  mapLocations.get(name)) {
                locations.add(vector.toLocation(world));
            }

            return (locations);
        } else {
            return (new ArrayList<Location>());
        }
    }

    public void addRegion(String name, Cuboid cuboid) {
        mapRegions.put(name, cuboid);
        MapManager.getInstance().saveMap(this);
    }

    public Cuboid getRegion(String name) {
        return (mapRegions.get(name));
    }

    //***************************//

}