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
        setCommandBlock(BLUE,blue);
        Coordinate red = blue.shiftX(tunnel.totalSlicesLength() * 2 + tunnel.middle().length() + 7);
        setCommandBlock(RED,red);

        if(ModeCommand.mode() == 2) return;
        int midShift = tunnel.middle().length() / 2;
        Coordinate mid = blue.shiftX(tunnel.totalSlicesLength() + 4).shiftX(midShift);
        Coordinate green = mid.shiftZ(midShift + tunnel.totalSlicesLength() + 4);
        setCommandBlock(GREEN,green);
        Coordinate yellow = mid.shiftZ((midShift + tunnel.totalSlicesLength() + 4) * -1);
        setCommandBlock(YELLOW,yellow);
    }

    private static void setCommandBlock(Coordinate team, Coordinate posToTeleportTo) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setblock " + team.asVanillaString() + " command_block{Command:\"tp @p " + posToTeleportTo.asVanillaString() + "\"} destroy");
    }

}
