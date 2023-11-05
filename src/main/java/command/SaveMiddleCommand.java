package command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import main.Coordinate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SaveMiddleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command,
                             String s, String[] strings) {
        if(strings.length != 1) {
            commandSender.sendMessage("Incorrect usage!");
            return false;
        }
        try {
            LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(commandSender));
            BlockVector3 vec = session.getSelection().getMinimumPoint();
            Coordinate copyPos = new Coordinate(vec.getBlockX(),vec.getBlockY(),vec.getBlockZ()).shiftZ(session.getSelection().getLength());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"tp " + commandSender.getName() + " " + copyPos.asVanillaString());
            Bukkit.dispatchCommand(commandSender,"/copy -e");
            Bukkit.dispatchCommand(commandSender,"/schem save " + strings[0] + " -f");
            commandSender.sendMessage("Successfully saved middle '" + strings[0] + "'!");
            return true;
        }
        catch (IncompleteRegionException e) {
            commandSender.sendMessage("Please select a region using WorldEdit's wand (//wand) first!");
            return true;
        }
    }
}
