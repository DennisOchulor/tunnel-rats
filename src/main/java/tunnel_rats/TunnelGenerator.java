package tunnel_rats;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class TunnelGenerator {//todo take into account .schem length and double total slice length
    private record Coordinate(int x, int y, int z) {
        Coordinate shiftX(int shift) { return new Coordinate(x + shift,y,z); }
        Coordinate shiftY(int shift) { return new Coordinate(x,y + shift,z); }
        Coordinate shiftZ(int shift) { return new Coordinate(x,y,z + shift); }
    }

    private final Tunnel tunnel;
    private final CommandSender sender;
    private final int totalTunnelLength;
    private Coordinate refPos;

    private static final Coordinate initialPos = new Coordinate(31, -56, 8);
    private static volatile boolean generating = false;

    private TunnelGenerator(Tunnel tunnel, CommandSender sender) {
        this.tunnel = tunnel;
        this.refPos = initialPos.shiftZ(tunnel.width() % 2 == 0 ? tunnel.width() / 2 : tunnel.width() / 2 + 1);
        this.sender = sender;
        this.totalTunnelLength = 9 /* space for 2 bedrooms */ + (tunnel.totalSlicesLength() * 2) + tunnel.middle().length();
    }

    public static void generate(Tunnel tunnel, CommandSender sender) {
        if(generating) throw new IllegalStateException("There is a tunnel in generation at the moment!");
        generating = true;
        //todo BLANK THE WORLD
        new TunnelGenerator(tunnel,sender).start();

    }

    private void start() {
        //the base
        Coordinate frontLeftBase = refPos.shiftZ((tunnel.width() - 2) * -1);
        Coordinate backRightBase = refPos.shiftZ(-1).shiftX(totalTunnelLength);
        setBedrock(frontLeftBase,backRightBase);

        //two bottom lines todo make runnable
        Coordinate frontLeftBottomLine = frontLeftBase.shiftY(1).shiftZ(-1);
        Coordinate backLeftBottomLine = frontLeftBottomLine.shiftX(totalTunnelLength);
        setBedrock(frontLeftBottomLine,backLeftBottomLine);
        Coordinate frontRightBottomLine = frontLeftBottomLine.shiftZ(tunnel.width() - 1);
        Coordinate backRightBottomLine = backLeftBottomLine.shiftZ(tunnel.width() - 1);
        setBedrock(frontRightBottomLine,backRightBottomLine);

        //two sides
        Coordinate frontLeftSide = frontLeftBottomLine.shiftZ(-1).shiftY(1);
        Coordinate backLeftSide = frontLeftSide.shiftY(tunnel.height() - 3).shiftX(totalTunnelLength);
        setBedrock(frontLeftSide,backLeftSide);
        Coordinate frontRightSide = frontRightBottomLine.shiftZ(1).shiftY(1);
        Coordinate backRightSide = frontRightSide.shiftY(tunnel.height() - 3).shiftX(totalTunnelLength);
        setBedrock(frontRightSide,backRightSide);

        //two upper lines todo make runnable
        Coordinate backLeftUpperLine = backLeftSide.shiftY(1).shiftZ(1);
        Coordinate frontLeftUpperLine = backLeftUpperLine.shiftX(totalTunnelLength * -1);
        setBedrock(backLeftUpperLine,frontLeftUpperLine);
        Coordinate backRightUpperLine = backRightSide.shiftY(1).shiftZ(-1);
        Coordinate frontRightUpperLine = backRightUpperLine.shiftX(totalTunnelLength * -1);
        setBedrock(backRightUpperLine,frontRightUpperLine);

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
        setblock(chest,"chest[facing=west]");
        setblock(bed,"red_bed[facing=east,part=head]");
        setblock(bed.shiftX(-1),"red_bed[facing=east,part=foot]");
        setblock(torch,"wall_torch[facing=west]");

        //the second bedroom
        Coordinate torch1 = torch.shiftX(totalTunnelLength - 8);
        Coordinate chest1 = torch1.shiftY(-1);
        Coordinate bed1 = chest1.shiftX(1);
        setblock(chest1,"chest[facing=east]");
        setblock(bed1,"red_bed[facing=west,part=head]");
        setblock(bed1.shiftX(1),"red_bed[facing=west,part=foot]");
        setblock(torch1,"wall_torch[facing=east]");
    }

    private void setBedrock(Coordinate c0, Coordinate c1) {
        worldedit("pos1 " + c0.x() + "," + c0.y() + "," + c0.z());
        worldedit("pos2 " + c1.x() + "," + c1.y() + "," + c1.z());
        worldedit("set bedrock");
    }

    private void setblock(Coordinate c, String block) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setblock " + c.x() + " " + c.y() + " " + c.z() + " " + block);
    }

    private void worldedit(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/" + command);
    }

}
