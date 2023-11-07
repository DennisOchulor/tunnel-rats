package event;

import command.StartCommand;
import main.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnPlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!StartCommand.running) return;
        String name = event.getPlayer().getName();
        if(TeamManager.getAllAlivePlayers().contains(name)) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("tunnel-rats-plugin"), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"effect give " + name + " haste infinite 1 true");
            }, 20);
        }
    }

}
