package main;

import command.ModeCommand;
import command.StartCommand;
import command.TunnelCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TunnelRats extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("tunnel").setExecutor(new TunnelCommand());
        this.getCommand("mode").setExecutor(new ModeCommand());
        this.getCommand("start").setExecutor(new StartCommand());

        Bukkit.getScheduler().scheduleSyncDelayedTask(this,() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world world"));
    }

}