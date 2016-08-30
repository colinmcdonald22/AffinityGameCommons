package net.minecraftaffinity.AffinityGameCommons.kits.objects;

import net.minecraftaffinity.AffinityGameCommons.abilities.objects.Ability;
import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 04, 01 2014
 * Programmed for the AffinityGameAPI project.
 */
public class Kit {

    //***************************//

    private Game host;

    private String name;
    private String[] description;
    private Ability[] abilities;

    //***************************//

    public Kit(String name, String[] description, Ability[] abilities) {
        this.name = name;
        this.description = description;
        this.abilities = abilities;
    }

    //***************************//

    public void setHost(Game host) {
        this.host = host;
    }

    //***************************//

    public String getName() {
        return (name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getDescription() {
        return (description);
    }

    public Ability[] getAbilities() {
        return (abilities);
    }

    public Game getHost() {
        return (host);
    }

    public void equipNPC(LivingEntity npc) {

    }

    public void equipPlayer(Player player) {

    }

    //***************************//

}