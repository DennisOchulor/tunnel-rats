package main;

import command.ModeCommand;
import org.bukkit.Bukkit;
import org.bukkit.block.CommandBlock;
import tunnel.Tunnel;

public class Teleporters {

    private static final Coordinate RED = new Coordinate(14, -60, 8);
    private static final Coordinate BLUE = new Coordinate(2, -60, 8);
    private static final Coordinate GREEN = new Coordinate(8, -60, 14);
    private static final Coordinate YELLOW = new Coordinate(8, -60, 2);

    public static void switchMode() {
        if(ModeCommand.mode() == 2) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock 8 -59 14 air");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock 8 -59 2 air");
        }
        else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock 8 -59 14 light_weighted_pressure_plate");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock 8 -59 2 light_weighted_pressure_plate");
        }
    }

    public static void configureTeleporters(Coordinate blue, Tunnel tunnel) {
        //to be called by TunnelGenerator
        setCommandBlock(BLUE,blue);
        Coordinate red = blue.shiftX(tunnel.totalSlicesLength() * 2 + tunnel.middle().length() + 7); //todo FIX
        setCommandBlock(RED,red);
        if(ModeCommand.mode() == 2) return;
        //todo 4 teams version, also remember 4 teams middle width==length constraint
    }

    private static void setCommandBlock(Coordinate team, Coordinate posToTeleportTo) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"fill " + team.asVanillaString() + " " + team.asVanillaString() + " command_block{Command:\"tp @p " + posToTeleportTo.asVanillaString() + "\"} replace");
    }

}
