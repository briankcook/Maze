package mazemaker.maze;

public class Direction {
    public final int x;
    public final int y;
    public final int mask;
    
    public Direction(int x, int y, int mask) {
        this.x = x;
        this.y = y;
        this.mask = mask;
    }
}
