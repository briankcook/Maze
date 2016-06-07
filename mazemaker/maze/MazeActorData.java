package mazemaker.maze;

import java.awt.Point;

public class MazeActorData extends Point{
    public final transient Direction facing;
    public final Point[] update;

    public MazeActorData(int x, int y, Direction facing, Point... update) {
        this.x = x;
        this.y = y;
        this.facing = facing;
        this.update = update;
    }
}
