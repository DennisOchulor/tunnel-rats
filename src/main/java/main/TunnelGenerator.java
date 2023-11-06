package main;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import command.ModeCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import tunnel.Slice;
import tunnel.Tunnel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class TunnelGenerator {

    private final Tunnel tunnel;
    private final CommandSender sender;
    private final int totalTunnelLength;
    private final List<Runnable> runLater = new ArrayList<>(); //for the 4 lines
    private Coordinate refPos;

    private static final Coordinate initialPos = new Coordinate(31, -56, 8);
    private static volatile boolean generating = false;
    static final Preferences pref = Preferences.userNodeForPackage(TunnelGenerator.class);

    private TunnelGenerator(Tunnel tunnel, CommandSender sender) {
        this.tunnel = tunnel;
        this.refPos = initialPos.shiftZ(tunnel.width() % 2 == 0 ? tunnel.width() / 2 : tunnel.width() / 2 + 1);
        this.sender = sender;
        this.totalTunnelLength = 9 /* space for 2 bedrooms */ + (tunnel.totalSlicesLength() * 2) + tunnel.middle().length();
    }

    public static void generate(Tunnel tunnel, CommandSender sender) {
        if(generating) throw new IllegalStateException("There is a tunnel in generation at the moment!");
        generating = true;
        TunnelGenerator generator = new TunnelGenerator(tunnel,sender);

        //BLANK THE WORLD
        String blankWorld = pref.get("blankworld","");
        for(String command : blankWorld.split("\n")) {
            worldedit(command);
        }

        //SET NEW BLANKWORLD  +/-10 is as a safety buffer
        Coordinate pos1 = generator.refPos.shiftZ(-1 * generator.totalTunnelLength / 2 - 10).shiftX(-2);
        Coordinate pos2 = generator.refPos.shiftZ(generator.totalTunnelLength / 2 + 10).shiftX(generator.totalTunnelLength + 10).shiftY(tunnel.height() + 10);
        pref.put("blankworld","pos1 " + pos1.asWorldEditString() + "\n" + "pos2 " + pos2.asWorldEditString() + "\n" + "set air");
        generator.start();
    }

    private void start() {
        phase1();
        phase2();
        phase3();
        if(ModeCommand.mode() == 4) phase4();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=item]"); //to delete dropped beds
        generating = false;
    }

    private void phase1() { //THE BEDROCK TUNNEL ITSELF AND THE BEDROOMS
        //the base
        Coordinate frontLeftBase = refPos.shiftZ((tunnel.width() - 2) * -1);
        Coordinate backRightBase = refPos.shiftZ(-1).shiftX(totalTunnelLength);
        setBedrock(frontLeftBase,backRightBase);

        //two bottom lines
        Coordinate frontLeftBottomLine = frontLeftBase.shiftY(1).shiftZ(-1);
        Coordinate backLeftBottomLine = frontLeftBottomLine.shiftX(totalTunnelLength);
        runLater.add(() -> setBedrock(frontLeftBottomLine,backLeftBottomLine));
        Coordinate frontRightBottomLine = frontLeftBottomLine.shiftZ(tunnel.width() - 1);
        Coordinate backRightBottomLine = backLeftBottomLine.shiftZ(tunnel.width() - 1);
        runLater.add(() -> setBedrock(frontRightBottomLine,backRightBottomLine));

        //two sides
        Coordinate frontLeftSide = frontLeftBottomLine.shiftZ(-1).shiftY(1);
        Coordinate backLeftSide = frontLeftSide.shiftY(tunnel.height() - 3).shiftX(totalTunnelLength);
        setBedrock(frontLeftSide,backLeftSide);
        Coordinate frontRightSide = frontRightBottomLine.shiftZ(1).shiftY(1);
        Coordinate backRightSide = frontRightSide.shiftY(tunnel.height() - 3).shiftX(totalTunnelLength);
        setBedrock(frontRightSide,backRightSide);

        //two upper lines
        Coordinate backLeftUpperLine = backLeftSide.shiftY(1).shiftZ(1);
        Coordinate frontLeftUpperLine = backLeftUpperLine.shiftX(totalTunnelLength * -1);
        runLater.add(() -> setBedrock(backLeftUpperLine,frontLeftUpperLine));
        Coordinate backRightUpperLine = backRightSide.shiftY(1).shiftZ(-1);
        Coordinate frontRightUpperLine = backRightUpperLine.shiftX(totalTunnelLength * -1);
        runLater.add(() -> setBedrock(backRightUpperLine,frontRightUpperLine));

        //the roof
        Coordinate frontLeftRoof = frontLeftUpperLine.shiftY(1).shiftZ(1);
        Coordinate backRightRoof = backRightUpperLine.shiftY(1).shiftZ(-1);
        setBedrock(frontLeftRoof,backRightRoof);

        //ends of the tunnel
        Coordinate frontLeftEnd = frontLeftBottomLine.shiftZ(1).shiftX(-1);
        setBedrock(frontLeftEnd,frontLeftEnd.shiftZ(tunnel.width() - 3));
        setBedrock(frontLeftEnd.shiftY(1).shiftZ(-1), frontLeftEnd.shiftZ(tunnel.width() - 2).shiftY(tunnel.height() - 2));
        setBedrock(frontLeftEnd.shiftY(tunnel.height() - 1), frontLeftEnd.shiftZ(tunnel.width() - 3));
        Coordinate backLeftEnd = backLeftBottomLine.shiftZ(1).shiftX(1);
        setBedrock(backLeftEnd,backLeftEnd.shiftZ(tunnel.width() - 3));
        setBedrock(backLeftEnd.shiftY(1).shiftZ(-1), backLeftEnd.shiftZ(tunnel.width() - 2).shiftY(tunnel.height() - 2));
        setBedrock(backLeftEnd.shiftY(tunnel.height() - 1), backLeftEnd.shiftZ(tunnel.width() - 3));

        //the first bedroom
        Coordinate chest = refPos.shiftY(1).shiftX(4).shiftZ(tunnel.width() / 2 * -1);
        Coordinate bed = chest.shiftX(-1);
        Coordinate torch = chest.shiftY(1);
        setblock(chest,"chest[facing=west]{Items:[{Count:15b,Slot:13b,id:\"minecraft:bread\"}]}");
        setblock(bed,"red_bed[facing=east,part=head]");
        setblock(bed.shiftX(-1),"red_bed[facing=east,part=foot]");
        setblock(torch,"wall_torch[facing=west]");

        //the second bedroom
        Coordinate torch1 = torch.shiftX(totalTunnelLength - 8);
        Coordinate chest1 = torch1.shiftY(-1);
        Coordinate bed1 = chest1.shiftX(1);
        setblock(chest1,"chest[facing=east]{Items:[{Count:15b,Slot:13b,id:\"minecraft:bread\"}]}");
        setblock(bed1,"red_bed[facing=west,part=head]");
        setblock(bed1.shiftX(1),"red_bed[facing=west,part=foot]");
        setblock(torch1,"wall_torch[facing=east]");

        //Configure teleporters
        Teleporters.configureTeleporters(bed.shiftX(-2), tunnel);
    }

    private void phase2() { //THE SLICES AND MIDDLE
        //First half of slices
        refPos = refPos.shiftX(5).shiftY(1);
        Coordinate pos2 = null;
        for(Slice s : tunnel.slices()) {
            pos2 = refPos.shiftZ((tunnel.width() - 1) * -1).shiftY(tunnel.height() - 1).shiftX(s.length() - 1);
            setRandom(refPos,pos2,s.composition());
            if(s.nbtReplaces() != null) { //worldedit can't read NBT data directly in commands, so use vanilla /fill if NBT data exists in the composition
                for(Slice.NbtReplace nbtReplace : s.nbtReplaces()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill " + refPos.asVanillaString() + " " + pos2.asVanillaString() + " " + nbtReplace.block() + " replace " + nbtReplace.filter());
                }
            }
            refPos = refPos.shiftX(s.length());
        }

        //second half of slices copied and mirrored from the first half
        Coordinate frontBottomRight = refPos.shiftX(tunnel.totalSlicesLength() * -1);
        worldedit("pos1 " + frontBottomRight.asWorldEditString());
        worldedit("pos2 " + pos2.asWorldEditString());
        worldedit("copy -e");
        worldedit("rotate 180");
        Coordinate backBottomLeft = refPos.shiftX(tunnel.middle().length() + tunnel.totalSlicesLength() - 1).shiftZ(-1 * tunnel.width() + 1);
        worldedit("pos1 " + backBottomLeft.asWorldEditString());
        worldedit("paste -e");

        //the middle
        if(ModeCommand.mode() == 4) return; //middle will be generated later for reasons
        Coordinate middleCoords = refPos.shiftZ(1);
        try(EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")));
            ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(tunnel.middle().file())))
        {
            Clipboard clipboard = reader.read();
            Operation op = new ClipboardHolder(clipboard).createPaste(session).to(BlockVector3.at(middleCoords.x(), middleCoords.y(), middleCoords.z())).build();
            Operations.complete(op);
        }
        catch (IOException | WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    private void phase3() { //THE FOUR LINES OF THE TUNNEL AND SOME CLEANUP
        runLater.forEach(Runnable::run);
    }

    private void phase4() { //4 TEAMS TUNNEL GENERATION
        Coordinate middleCoords = refPos.shiftZ(1);

        refPos = refPos.shiftX((tunnel.totalSlicesLength() + 6) * -1).shiftZ(1).shiftY(-1); //now at FULL frontBottomRight
        worldedit("pos1 " + refPos.asWorldEditString());
        Coordinate frontOfMidUpperLeft = refPos.shiftX(tunnel.totalSlicesLength() + 5).shiftZ((tunnel.width() + 1) * -1).shiftY((tunnel.height() + 1));
        worldedit("pos2 " + frontOfMidUpperLeft.asWorldEditString());
        worldedit("copy -e");
        worldedit("rotate 90");
        Coordinate pasteLocation = frontOfMidUpperLeft.shiftY((tunnel.height() + 1) * -1).shiftZ((tunnel.totalSlicesLength() + 5) * -1);
        worldedit("pos1 " + pasteLocation.asWorldEditString());
        worldedit("paste -e");
        worldedit("rotate -180");
        pasteLocation = pasteLocation.shiftZ(totalTunnelLength + 2).shiftX(tunnel.middle().length() + 1);
        worldedit("pos1 " + pasteLocation.asWorldEditString());
        worldedit("paste -e");

        //GENERATE THE MIDDLE NOW FOR 4 TEAMS
        try(EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")));
            ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(tunnel.middle().file())))
        {
            Clipboard clipboard = reader.read();
            Operation op = new ClipboardHolder(clipboard).createPaste(session).to(BlockVector3.at(middleCoords.x(), middleCoords.y(), middleCoords.z())).build();
            Operations.complete(op);
        }
        catch (IOException | WorldEditException e) {
            throw new RuntimeException(e);
        }

        //FINALLY FILL IN THE GAPS IN THE MIDDLE
        Coordinate lowerRight = middleCoords.shiftZ(-1).shiftY(-1);
        Coordinate lowerLeft = lowerRight.shiftX(tunnel.middle().length() - 1).shiftZ((tunnel.middle().width() - 1) * -1);
        setBedrock(lowerLeft,lowerRight);
        Coordinate upperRight = lowerRight.shiftY(tunnel.middle().height() + 1);
        Coordinate upperLeft = lowerLeft.shiftY(tunnel.middle().height() + 1);
        setBedrock(upperRight,upperLeft);
    }

    private static void setBedrock(Coordinate c0, Coordinate c1) {
        worldedit("pos1 " + c0.asWorldEditString());
        worldedit("pos2 " + c1.asWorldEditString());
        worldedit("set bedrock");
    }

    private static void setblock(Coordinate c, String block) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setblock " + c.asVanillaString() + " " + block);
    }

    private static void setRandom(Coordinate c0, Coordinate c1, String blocks) {
        worldedit("pos1 " + c0.asWorldEditString());
        worldedit("pos2 " + c1.asWorldEditString());
        worldedit("set " + blocks);
    }

    private static void worldedit(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/" + command);
    }

}
