package mazemaker.maze.solvers;

import java.awt.Point;
import mazemaker.maze.*;

public class WallFollower implements MazeActor {
    
    private final Maze maze;
    private final int preferredDirection;
    private final int alternateDirection;
    
    private Direction facing;
    private boolean moved;
    private int x;
    private int y;

    public WallFollower(Maze maze, boolean rightHanded) {
        this.maze = maze;
        preferredDirection = rightHanded ? Maze.RIGHT : Maze.LEFT;
        alternateDirection = rightHanded ? Maze.LEFT : Maze.RIGHT;
    }
    
    @Override
    public void init() {
        Point start = maze.getStart();
        x = start.x;
        y = start.y;
        facing = Maze.SOUTH;
        moved = true;
    }
    
    @Override
    public Datum[] step() {
        Datum update = new Datum(x, y, null);
        if (maze.isGoal(x, y)) 
            return new Datum[]{};
        if (moved || !maze.canGo(x, y, facing)) {
            int way = moved ? preferredDirection : alternateDirection;
            facing = Maze.turn(facing, way);
            moved = false;
        } else {
            x += facing.x;
            y += facing.y;
            moved = true;
        }
        return new Datum[]{update, new Datum(x, y, facing)};
    }
}