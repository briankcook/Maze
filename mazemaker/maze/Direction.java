package mazemaker.maze;

public class Direction {
    public final int x;
    public final int y;
    public final byte mask;
    public final byte facing;
    
    public Direction(int x, int y, byte mask, byte facing) {
        this.x = x;
        this.y = y;
        this.mask = mask;
        this.facing = facing;
    }
}
