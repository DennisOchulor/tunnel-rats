package main;

public record Coordinate(int x, int y, int z) {
    public Coordinate shiftX(int shift) { return new Coordinate(x + shift,y,z); }
    public Coordinate shiftY(int shift) { return new Coordinate(x,y + shift,z); }
    public Coordinate shiftZ(int shift) { return new Coordinate(x,y,z + shift); }

    public String asVanillaString() {
        return x + " " + y + " " + z;
    }

    public String asWorldEditString() {
        return x + "," + y + "," + z;
    }
}
