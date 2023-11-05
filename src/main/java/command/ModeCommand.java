package command;

import main.Teleporters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.prefs.Preferences;

public class ModeCommand implements CommandExecutor {

    private static final Preferences pref = Preferences.userNodeForPackage(ModeCommand.class);
    private static volatile int mode = pref.getInt("mode",2);

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args)
    {
        if(mode == 2) {
            mode = 4;
            pref.putInt("mode",4);
        }
        else {
            mode = 2;
            pref.putInt("mode",2);
        }
        Teleporters.switchMode();
        sender.sendMessage("Switched to " + mode + " teams mode.");
        return true;
    }

    public static int mode() {
        return mode;
    }
}
