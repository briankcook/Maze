package mazemaker.mazeactor;

import java.awt.Point;
import mazemaker.maze.Direction;

public class MazeActorData extends Point{
    public final transient Direction facing;

    public MazeActorData(int x, int y, Direction facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;
    }
}
