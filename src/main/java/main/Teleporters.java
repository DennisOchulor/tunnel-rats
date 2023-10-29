package main;

import command.ModeCommand;
import org.bukkit.Bukkit;
import tunnel.Tunnel;

public class Teleporters {

    private static final Coordinate RED = new Coordinate(14, -61, 8);
    private static final Coordinate BLUE = new Coordinate(2, -61, 8);
    private static final Coordinate GREEN = new Coordinate(8, -61, 14);
    private static final Coordinate YELLOW = new Coordinate(8, -61, 2);

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
        setCommandBlockTp(BLUE,blue);
        Coordinate red = blue.shiftX(tunnel.totalSlicesLength() * 2 + tunnel.middle().length() + 7);
        setCommandBlockTp(RED,red);

        if(ModeCommand.mode() == 2) {
            setCommandBlock(GREEN, "tellraw @p \\\"This team is not included in 2 teams mode.\\\"");
            setCommandBlock(YELLOW, "tellraw @p \\\"This team is not included in 2 teams mode.\\\"");
            return;
        }
        int midShift = tunnel.middle().length() / 2;
        Coordinate mid = blue.shiftX(tunnel.totalSlicesLength() + 4).shiftX(midShift);
        Coordinate green = mid.shiftZ(midShift + tunnel.totalSlicesLength() + 4);
        setCommandBlockTp(GREEN,green);
        Coordinate yellow = mid.shiftZ((midShift + tunnel.totalSlicesLength() + 4) * -1);
        setCommandBlockTp(YELLOW,yellow);
    }

    public static void disableTeleporters() {
        setCommandBlock(BLUE, "tellraw @p \\\"Game is already in progress!\\\"");
        setCommandBlock(RED, "tellraw @p \\\"Game is already in progress!\\\"");
        setCommandBlock(GREEN, "tellraw @p \\\"Game is already in progress!\\\"");
        setCommandBlock(YELLOW, "tellraw @p \\\"Game is already in progress!\\\"");
    }

    private static void setCommandBlockTp(Coordinate team, Coordinate posToTeleportTo) {
        setCommandBlock(team, "tp @p " + posToTeleportTo.asVanillaString());
    }

    private static void setCommandBlock(Coordinate team, String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setblock " + team.asVanillaString() + " command_block{Command:\"" + command + "\"} destroy");
    }

}
