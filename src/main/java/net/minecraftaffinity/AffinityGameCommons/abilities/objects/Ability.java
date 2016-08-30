package net.minecraftaffinity.AffinityGameCommons.abilities.objects;

import net.minecraftaffinity.AffinityCommons.utils.LoggingUtils;
import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import org.bukkit.entity.Player;

/**
 * Programmed by minecraftaffinity (Colin McDonald)
 * Creation Date: 19, 10 2013
 * Programmed for the bukkit project.
 */
public class Ability {

    //***************************//

    private Kit host;

    private String name;
    private String description;

    //***************************//

     public Ability(String name, String description) {
        this.name = name;
        this.description = description;
    }

    //***************************//

    public void apply(Player player) {
        LoggingUtils.log("Ability", "The ability " + name + " is not overriding the apply() function.");
    }

    //***************************//

    public void setHost(Kit host) {
        this.host = host;
    }

    public String getName() {
        return (name);
    }

    public String getDescription() {
        return (description);
    }

    public Kit getHost() {
        return (host);
    }

    public boolean valid(Player player) {
        if (GameManager.getInstance().getCurrentGame() == null || !GameManager.getInstance().getCurrentGame().valid(player)) {
            return (false);
        }

        return (host.getHost().getKitSelections().get(player.getName()) == host);
    }

    //***************************//

}