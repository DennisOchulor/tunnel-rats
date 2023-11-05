package command;

import main.FileManager;
import main.TunnelGenerator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tunnel.Tunnel;

import java.util.List;

public class TunnelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args)
    {
        Player player = (Player) sender;

        if(args.length == 0) {
            List<String> tunnels = FileManager.listTunnels();
            TextComponent header = new TextComponent("List of Tunnels: (click on the name to generate it)");
            header.setUnderlined(true);
            player.spigot().sendMessage(header);
            tunnels.forEach(t -> {
                TextComponent line = new TextComponent(t);
                line.setColor(ChatColor.GOLD);
                line.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/tunnel " + t));
                player.spigot().sendMessage(line);
            });
            return true;
        }

        try {
            Tunnel tunnel = Tunnel.getTunnel(args[0], player);
            if(tunnel == null) sender.sendMessage("The tunnel '" + args[0] + "' does not exists!");
            else {
                sender.sendMessage("Generating tunnel...one moment!");
                TunnelGenerator.generate(tunnel, sender);
                sender.sendMessage("Successfully generated tunnel '" + args[0] + "'!");
            }
        }
        catch (IllegalArgumentException e) {
            TextComponent errorMsg = new TextComponent("[Validation Error] " + e.getMessage());
            errorMsg.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMsg);
        }
        catch (IllegalStateException e) {
            TextComponent errorMsg = new TextComponent(e.getMessage());
            errorMsg.setColor(ChatColor.RED);
            player.spigot().sendMessage(errorMsg);
        }
        return true;
    }
}
