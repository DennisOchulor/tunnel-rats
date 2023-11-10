package metrics;

import command.ModeCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class MetricsManager {

    private static final Preferences pref = Preferences.userNodeForPackage(MetricsManager.class);

    public static void init(JavaPlugin plugin) {
        Metrics metrics = new Metrics(plugin, 20246);  //bStats
        metrics.addCustomChart(new Metrics.AdvancedPie("games_played",() -> {
            Map<String,Integer> map = new HashMap<>();
            map.put("Two Teams", pref.getInt("Two Teams",0));
            map.put("Four Teams", pref.getInt("Four Teams",0));
            return map;
        }));
    }

    public static void logMode() {
        if(ModeCommand.mode() == 2) {
            int value = pref.getInt("Two Teams", 0);
            pref.putInt("Two Teams", ++value);
        }
        else {
            int value = pref.getInt("Four Teams", 0);
            pref.putInt("Four Teams", ++value);
        }
    }
}
