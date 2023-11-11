package metrics;

import command.ModeCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class MetricsManager {
    private MetricsManager() {}

    private static int twoTeamsGamesPlayed = 0;
    private static int fourTeamsGamesPlayed = 0;

    public static void init(JavaPlugin plugin) {
        Metrics metrics = new Metrics(plugin, 20246);  //bStats
        metrics.addCustomChart(new Metrics.AdvancedPie("recently_played_games",() -> {
            Map<String,Integer> map = new HashMap<>();
            map.put("Two Teams", twoTeamsGamesPlayed);
            map.put("Four Teams", fourTeamsGamesPlayed);
            return map;
        }));
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            twoTeamsGamesPlayed = 0;
            fourTeamsGamesPlayed = 0;
        }, 1728000L, 172800L); // every 24h
    }

    public static void logMode() {
        if(ModeCommand.mode() == 2) twoTeamsGamesPlayed++;
        else fourTeamsGamesPlayed++;
    }
}
