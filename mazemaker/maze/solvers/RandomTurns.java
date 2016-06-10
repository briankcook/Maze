package mazemaker.maze.solvers;

import mazemaker.maze.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class RandomTurns implements MazeActor{
    
    private final Maze maze;
    private final ArrayList<Direction> choices;
    private final Random r;
    
    private Direction facing;
    private int x;
    private int y;
    private Point previousCell;
    
    public RandomTurns(Maze maze) {
        this.maze = maze;
        choices = new ArrayList();
        r = new Random();
    }
    
    @Override
    public void init() {
        facing = Maze.SOUTH;
        x = 0;
        y = 0;
        previousCell = new Point(0, 0);
    }
    
    @Override
    public MazeActorData step() {
        if (maze.isGoal(x, y)) 
            return null;
        
        choices.clear();
        
        for (Direction direction : Maze.getDirections())
            if (maze.canGo(x, y, direction) && !previousCell.equals(maze.look(x, y, direction)))
                choices.add(direction);
        
        previousCell = new Point(x, y);
        
        if (choices.isEmpty()) {
            facing = Maze.turn(facing, Maze.AROUND);
        } else {
            facing = choices.get(r.nextInt(choices.size()));
            x += facing.x;
            y += facing.y;
        }
        
        return new MazeActorData(x, y, facing, previousCell);
    }
}
