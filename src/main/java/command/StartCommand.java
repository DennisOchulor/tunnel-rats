package command;

import main.TeamManager;
import main.Teleporters;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class StartCommand implements CommandExecutor {

    public static volatile boolean running = false;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command,
                             String s, String[] strings) {
        running = true;
        Teleporters.disableTeleporters();
        Set<String> players = TeamManager.getAllPlayers();
        title("Remember to set your spawn!");
        playNoteblock(0.5);

        players.forEach(p -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"effect clear " + p);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"effect give " + p + " saturation 3 2 true");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"xp set " + p + " 0 points");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"xp set " + p + " 0 levels");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"advancement revoke " + p + " everything");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"gamemode survival " + p);
        });

        Plugin plugin = Bukkit.getPluginManager().getPlugin("tunnel-rats-plugin");
        Bukkit.getScheduler().runTaskLater(plugin,() -> { title("5"); playNoteblock(1); },100);
        Bukkit.getScheduler().runTaskLater(plugin,() -> { title("4"); playNoteblock(1); },120);
        Bukkit.getScheduler().runTaskLater(plugin,() -> { title("3"); playNoteblock(1); },140);
        Bukkit.getScheduler().runTaskLater(plugin,() -> { title("2"); playNoteblock(1); },160);
        Bukkit.getScheduler().runTaskLater(plugin,() -> { title("1"); playNoteblock(1); },180);
        Bukkit.getScheduler().runTaskLater(plugin,() -> { title("GO!!!"); playNoteblock(2); },200);

        return true;
    }

    private static void title(String text) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"title @a title \"" + text + "\"");
    }

    private static void playNoteblock(double pitch) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"execute at @a run playsound block.note_block.harp block @a ~ ~ ~ 2 " + pitch);
    }

}
