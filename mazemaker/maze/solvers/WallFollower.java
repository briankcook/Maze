package mazemaker.maze.solvers;

import java.awt.Point;
import mazemaker.maze.*;

public class WallFollower extends MazeActor {
    
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
    protected Datum[] step() {
        Point update = new Point(x, y);
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
        return new Datum[]{new Datum(update.x, update.y, maze.getCellData(update.x, update.y)), 
                           new Datum(x, y, Maze.face(maze.getCellData(x, y), facing))};
    }
}