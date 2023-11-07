package event;

import command.StartCommand;
import main.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnPlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!StartCommand.running) return;
        Player player = event.getEntity();
        if(player.getBedSpawnLocation() != null) return; // they are still in the game.
        String name = player.getName();
        if(TeamManager.joinDeadTeamAndAnnounceTeamElim(name)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a \"" + player.getDisplayName() + " has been eliminated!\"");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode spectator " + name);
            TeamManager.declareWinnerIfExists();
        }
    }

}
