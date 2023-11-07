package main;

import command.StartCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class TeamManager {

    private static final Team RED;
    private static final Team BLUE;
    private static final Team GREEN;
    private static final Team YELLOW;
    private static final Team DEAD_RED;
    private static final Team DEAD_BLUE;
    private static final Team DEAD_GREEN;
    private static final Team DEAD_YELLOW;

    static {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team red = board.getTeam("red");
        if(red == null) red = board.registerNewTeam("red");
        red.setColor(ChatColor.RED);
        RED = red;

        Team blue = board.getTeam("blue");
        if(blue == null) blue = board.registerNewTeam("blue");
        blue.setColor(ChatColor.BLUE);
        BLUE = blue;

        Team green = board.getTeam("green");
        if(green == null) green = board.registerNewTeam("green");
        green.setColor(ChatColor.GREEN);
        GREEN = green;

        Team yellow = board.getTeam("yellow");
        if(yellow == null) yellow = board.registerNewTeam("yellow");
        yellow.setColor(ChatColor.YELLOW);
        YELLOW = yellow;

        Team deadRed = board.getTeam("deadRed");
        if(deadRed == null) deadRed = board.registerNewTeam("deadRed");
        deadRed.setColor(ChatColor.RED);
        deadRed.setPrefix("[DEAD] ");
        DEAD_RED = deadRed;

        Team deadBlue = board.getTeam("deadBlue");
        if(deadBlue == null) deadBlue = board.registerNewTeam("deadBlue");
        deadBlue.setColor(ChatColor.BLUE);
        deadBlue.setPrefix("[DEAD] ");
        DEAD_BLUE = deadBlue;

        Team deadGreen = board.getTeam("deadGreen");
        if(deadGreen == null) deadGreen = board.registerNewTeam("deadGreen");
        deadGreen.setColor(ChatColor.GREEN);
        deadGreen.setPrefix("[DEAD] ");
        DEAD_GREEN = deadGreen;

        Team deadYellow = board.getTeam("deadYellow");
        if(deadYellow == null) deadYellow = board.registerNewTeam("deadYellow");
        deadYellow.setColor(ChatColor.YELLOW);
        deadYellow.setPrefix("[DEAD] ");
        DEAD_YELLOW = deadYellow;
    }

    public static void init() {} //run static initializer

    public static Set<String> getAllAlivePlayers() {
        HashSet<String> players = new HashSet<>();
        players.addAll(RED.getEntries());
        players.addAll(BLUE.getEntries());
        players.addAll(GREEN.getEntries());
        players.addAll(YELLOW.getEntries());
        return players;
    }

    /**
     * Team elimination is only announced if the player was in a team and the {@link Team#getSize()} == 0
     * @param name Name of player
     * @return true if the player's team was changed.
     */
    public static boolean joinDeadTeamAndAnnounceTeamElim(String name) {
        if(RED.hasEntry(name)) {
            DEAD_RED.addEntry(name);
            if(RED.getSize() == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Red Team was eliminated!\",\"color\":\"red\",\"bold\":true}");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.lightning_bolt.thunder weather @a ~ ~ ~ 2");
            }
            return true;
        }
        if(BLUE.hasEntry(name)) {
            DEAD_BLUE.addEntry(name);
            if(BLUE.getSize() == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Blue Team was eliminated!\",\"color\":\"blue\",\"bold\":true}");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.lightning_bolt.thunder weather @a ~ ~ ~ 2");
            }
            return true;
        }
        if(GREEN.hasEntry(name)) {
            DEAD_GREEN.addEntry(name);
            if(GREEN.getSize() == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Green Team was eliminated!\",\"color\":\"green\",\"bold\":true}");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.lightning_bolt.thunder weather @a ~ ~ ~ 2");
            }
            return true;
        }
        if(YELLOW.hasEntry(name)) {
            DEAD_YELLOW.addEntry(name);
            if(YELLOW.getSize() == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Yellow Team was eliminated!\",\"color\":\"yellow\",\"bold\":true}");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.lightning_bolt.thunder weather @a ~ ~ ~ 2");
            }
            return true;
        }
        return false;
    }

    public static void emptyAllTeams() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty red");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty blue");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty green");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty yellow");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty deadRed");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty deadBlue");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty deadGreen");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team empty deadYellow");
    }

    /**
     *
     * @return true if there is a winner.
     */
    public static boolean declareWinnerIfExists() {
        boolean redEmpty = RED.getSize() == 0;
        boolean blueEmpty = BLUE.getSize() == 0;
        boolean greenEmpty = GREEN.getSize() == 0;
        boolean yellowEmpty = YELLOW.getSize() == 0;

        if(blueEmpty && greenEmpty && yellowEmpty) { // RED WINS!
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\"Red Team wins!\",\"color\":\"red\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Red Team wins!\",\"color\":\"red\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.player.levelup player @a ~ ~ ~ 2");
            StartCommand.running = false;
            return true;
        }
        else if(redEmpty && greenEmpty && yellowEmpty) { // BLUE WINS!
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\"Blue Team wins!\",\"color\":\"blue\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Blue Team wins!\",\"color\":\"blue\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.player.levelup player @a ~ ~ ~ 2");
            StartCommand.running = false;
            return true;
        }
        else if(blueEmpty && redEmpty && yellowEmpty) { // GREEN WINS!
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\"Green Team wins!\",\"color\":\"green\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Green Team wins!\",\"color\":\"green\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.player.levelup player @a ~ ~ ~ 2");
            StartCommand.running = false;
            return true;
        }
        else if(blueEmpty && greenEmpty && redEmpty) { // YELLOW WINS!
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {\"text\":\"Yellow Team wins!\",\"color\":\"yellow\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a {\"text\":\"Yellow Team wins!\",\"color\":\"yellow\",\"bold\":true}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at @a run playsound entity.player.levelup player @a ~ ~ ~ 2");
            StartCommand.running = false;
            return true;
        }
        else return false;
    }
}
