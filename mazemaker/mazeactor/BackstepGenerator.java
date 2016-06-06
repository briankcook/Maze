package mazemaker.mazeactor;

import mazemaker.maze.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

public class BackstepGenerator implements MazeActor {
    
    private final Maze maze;
    private final int hWeight;
    private final int vWeight;
    private final Random r;
    private final Deque<Cell> history;
    private final ArrayList<Direction> choices;
    
    private Cell currentCell;
    
    public BackstepGenerator(Maze maze, int hWeight, int vWeight) {
        this.maze = maze;
        this.hWeight = hWeight;
        this.vWeight = vWeight;
        r = new Random();
        history = new ArrayDeque();
        choices = new ArrayList();
    }
    
    @Override
    public void init() {
        currentCell = maze.getCell(0, 0);
        currentCell.setMaking(true);
        currentCell.setVisited(true);
        history.push(currentCell);
        maze.clearGoal();
        maze.getLastCell().setGoal(true);
    }
    
    @Override
    public Cell step() {
        choices.clear();
        
        currentCell.setMaking(false);
        
        for (Direction direction : Compass.getDirections())
            if (maze.canGo(currentCell, direction) && !maze.visited(currentCell, direction))
                add(direction);
        
        if (choices.isEmpty()) {
            if (history.isEmpty()){
                return null;
            } else {
                currentCell = history.pop();
                currentCell.setMaking(true);
            }
        } else {
            history.push(currentCell);
            currentCell = maze.join(currentCell, pickDirection());
            currentCell.setMaking(true);
            currentCell.setVisited(true);
        }
        return currentCell;
    }
    
    private Direction pickDirection() {
        return choices.get(r.nextInt(choices.size()));
    }
    
    private void add(Direction direction) {
        int numTimes;
        if (direction.equals(Compass.NORTH) || direction.equals(Compass.SOUTH))
            numTimes = vWeight;
        else
            numTimes = hWeight;
        for (int  i = 0 ; i < numTimes ; i++)
            choices.add(direction);
    }
}