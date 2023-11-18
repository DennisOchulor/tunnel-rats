package main;

import command.ModeCommand;
import org.bukkit.Bukkit;
import tunnel.Tunnel;

public class Teleporters {

    //command blocks that do the teleporting
    private static final Coordinate RED_TP = new Coordinate(14, -61, 9);
    private static final Coordinate BLUE_TP = new Coordinate(2, -61, 9);
    private static final Coordinate GREEN_TP = new Coordinate(8, -61, 15);
    private static final Coordinate YELLOW_TP = new Coordinate(8, -61, 3);
    //command blocks that do the team joining
    private static final Coordinate RED_TEAM = new Coordinate(14, -61, 8);
    private static final Coordinate BLUE_TEAM = new Coordinate(2, -61, 8);
    private static final Coordinate GREEN_TEAM = new Coordinate(8, -61, 14);
    private static final Coordinate YELLOW_TEAM = new Coordinate(8, -61, 2);

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
        setCommandBlock(RED_TEAM, "team join red @p");
        setCommandBlock(BLUE_TEAM, "team join blue @p");
        setCommandBlock(GREEN_TEAM, "team join green @p");
        setCommandBlock(YELLOW_TEAM, "team join yellow @p");

        setCommandBlockTp(BLUE_TP,blue);
        Coordinate red = blue.shiftX(tunnel.totalSlicesLength() * 2 + tunnel.middle().length() + 7);
        setCommandBlockTp(RED_TP,red);

        if(ModeCommand.mode() == 2) return;

        int midShift = tunnel.middle().length() / 2;
        Coordinate mid = blue.shiftX(tunnel.totalSlicesLength() + 4).shiftX(midShift);
        Coordinate green = mid.shiftZ(midShift + tunnel.totalSlicesLength() + 4);
        setCommandBlockTp(GREEN_TP,green);
        Coordinate yellow = mid.shiftZ((midShift + tunnel.totalSlicesLength() + 4) * -1);
        setCommandBlockTp(YELLOW_TP,yellow);
    }

    public static void disableTeleporters() {
        setChainCommandBlock(BLUE_TP, "tellraw @p \\\"Game is already in progress!\\\"");
        setChainCommandBlock(RED_TP, "tellraw @p \\\"Game is already in progress!\\\"");
        setChainCommandBlock(GREEN_TP, "tellraw @p \\\"Game is already in progress!\\\"");
        setChainCommandBlock(YELLOW_TP, "tellraw @p \\\"Game is already in progress!\\\"");

        setCommandBlock(RED_TEAM, "");
        setCommandBlock(BLUE_TEAM, "");
        setCommandBlock(GREEN_TEAM, "");
        setCommandBlock(YELLOW_TEAM, "");
    }

    private static void setCommandBlockTp(Coordinate team, Coordinate posToTeleportTo) {
        setChainCommandBlock(team, "tp @p " + posToTeleportTo.asVanillaString());
    }

    private static void setCommandBlock(Coordinate team, String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setblock " + team.asVanillaString() + " command_block[facing=south]{Command:\"" + command + "\"} destroy");
    }

    private static void setChainCommandBlock(Coordinate team, String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setblock " + team.asVanillaString() + " chain_command_block[facing=south]{auto:1b,Command:\"" + command + "\"} destroy");
    }

}
