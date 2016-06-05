package maze;

import java.util.ArrayList;
import java.util.Random;

public class RandomTurns implements MazeActor{
    
    private final ArrayList<Direction> choices;
    private final Random r;
    private Cell currentCell;
    private Cell previousCell;
    
    public RandomTurns(Maze maze) {
        choices = new ArrayList();
        r = new Random();
        previousCell = maze.getCell(0, 0);
    }
    
    @Override
    public void init() {
        currentCell = previousCell;
        currentCell.setSolving(true);
        currentCell.setVisited(true);
        currentCell.setFacing(Compass.SOUTH);
    }
    
    @Override
    public Cell step() {
        if (currentCell.isGoal()) 
            return null;
        
        choices.clear();
        
        for (Direction direction : currentCell.neighbors.keySet())
            if (!currentCell.neighbors.get(direction).equals(previousCell))
                choices.add(direction);
        
        currentCell.setSolving(false);
        if (choices.isEmpty()) {
            previousCell.setFacing(Compass.turn(currentCell.getFacing(), Compass.AROUND));
            Cell swap = currentCell;
            currentCell = previousCell;
            previousCell = swap;
        } else {
            Direction direction = choices.get(r.nextInt(choices.size()));
            previousCell = currentCell;
            currentCell = currentCell.neighbors.get(direction);
            currentCell.setFacing(direction);
            currentCell.setVisited(true);
        }
        previousCell.setFacing(null);
        currentCell.setSolving(true);
        return currentCell;
    }
    
    @Override
    public boolean animate() {
        return true;
    }
}
