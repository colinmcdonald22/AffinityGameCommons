package net.minecraftaffinity.AffinityGameCommons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by macguy8 on 3/25/2014.
 */
public class NoWeatherToggleListener implements Listener {

    //***************************//

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    //***************************//

}