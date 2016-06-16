package mazemaker.maze.solvers;

import java.awt.Point;
import mazemaker.maze.*;
import java.util.ArrayList;
import java.util.Random;

public class RandomTurns implements MazeActor{
    
    private final Maze maze;
    private final ArrayList<Direction> choices;
    private final Random r;
    
    private Direction facing;
    private int x;
    private int y;
    private Datum previousCell;
    
    public RandomTurns(Maze maze) {
        this.maze = maze;
        choices = new ArrayList();
        r = new Random();
    }
    
    @Override
    public void init() {
        Point start = maze.getStart();
        x = start.x;
        y = start.y;
        facing = Maze.SOUTH;
        previousCell = new Datum(x, y, maze.getCellData(x, y));
    }
    
    @Override
    public Datum[] step() {
        if (maze.isGoal(x, y)) 
            return new Datum[]{};
        
        choices.clear();
        
        for (Direction direction : Maze.getDirections())
            if (maze.canGo(x, y, direction) && !previousCell.equals(maze.look(x, y, direction)))
                choices.add(direction);
        
        previousCell = new Datum(x, y, maze.getCellData(x, y));
        
        if (choices.isEmpty()) {
            facing = Maze.turn(facing, Maze.AROUND);
        } else {
            facing = choices.get(r.nextInt(choices.size()));
            x += facing.x;
            y += facing.y;
        }
        
        return new Datum[]{previousCell, new Datum(x, y, maze.face(maze.getCellData(x, y), facing))};
    }
}
