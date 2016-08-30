package net.minecraftaffinity.AffinityGameCommons.teams.objects;

import net.minecraftaffinity.AffinityGameCommons.game.objects.Game;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import net.minecraftaffinity.AffinityGameCommons.teams.enums.FriendlyFireSetting;
import org.bukkit.ChatColor;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 04, 01 2014
 * Programmed for the AffinityGameAPI project.
 */
public class Team {

    //***************************//

    private Game host;

    private String name = "Undefined";
    private String description = "Undefined";

    private ChatColor color = ChatColor.GRAY;

    private Kit[] kitRestrict;
    private FriendlyFireSetting ffSetting = FriendlyFireSetting.DISALLOW;

    //***************************//

    public Team(String name, String description, ChatColor color, FriendlyFireSetting ffSetting) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.ffSetting = ffSetting;
    }

    //***************************//

    public void setHost(Game host) {
        this.host = host;
    }

    //***************************//

    public String getName() {
        return (name);
    }

    public String getDescription() {
        return (description);
    }

    public Game getHost() {
        return (host);
    }

    public String getDisplayName() {
        return (color.toString() + ChatColor.BOLD + name);
    }

    public ChatColor getColor() {
        return (color);
    }

    public void setKitRestrict(Kit[] kits) {
        this.kitRestrict = kits;
    }

    public boolean canAccess(Kit kit) {
        if (kitRestrict == null) {
            return (true);
        }

        for (Kit k : kitRestrict) {
            if (k.getName().equals(kit.getName())) {
                return (false);
            }
        }

        return (true);
    }

    public void setFriendlyFireSetting(FriendlyFireSetting setting) {
        this.ffSetting = setting;
    }

    public FriendlyFireSetting getFriendlyFireSetting() {
        return (ffSetting);
    }

    //***************************//

}