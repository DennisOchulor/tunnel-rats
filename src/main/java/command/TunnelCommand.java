package command;

import main.FileManager;
import main.TunnelGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.error.YAMLException;
import tunnel.Tunnel;

import java.util.List;

public class TunnelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args)
    {
        if(args.length == 0) {
            List<String> tunnels = FileManager.listTunnels();
            sender.sendMessage(Component.text().content("List of Tunnels: (click on the name to generate it)").style(Style.style(TextDecoration.UNDERLINED)).build());
            tunnels.forEach(t -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"tellraw " + sender.getName() + // STRING TEMPLATES WOULD HELP BUT UGH PREVIEW
                        " {\"text\":\"" + t + "\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tunnel " + t + "\"}}");
            });
            return true;
        }

        try {
            Tunnel tunnel = Tunnel.getTunnel(args[0]);
            if(tunnel == null) sender.sendMessage("The tunnel '" + args[0] + "' does not exists!");
            else {
                sender.sendMessage("Generating tunnel...one moment!");
                TunnelGenerator.generate(tunnel, sender);
                sender.sendMessage("Successfully generated tunnel '" + args[0] + "'!");
            }
        }
        catch (IllegalArgumentException | YAMLException e) {
            sender.sendMessage(Component.text().content("[Validation Error] " + e.getMessage()).color(TextColor.color(255,0,0)).build());
        }
        catch (IllegalStateException e) {
            sender.sendMessage(Component.text().content(e.getMessage()).color(TextColor.color(255,0,0)).build());
        }
        return true;
    }
}
