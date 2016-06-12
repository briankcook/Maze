package mazemaker.maze;

import java.awt.Point;

public class Datum extends Point{
    public Direction facing;

    public Datum(int x, int y, Direction facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;
    }
}
