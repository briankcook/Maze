package mazemaker.maze;

import java.awt.Point;

public class MazeActorData extends Point{
    public Direction facing;

    public MazeActorData(int x, int y, Direction facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;
    }
}
