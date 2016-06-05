package mazemaker.mazeactor;

import mazemaker.maze.*;
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
        
        for (Direction direction : currentCell.getDirections())
            if (!currentCell.getNeighbor(direction).equals(previousCell))
                choices.add(direction);
        
        currentCell.setSolving(false);
        if (choices.isEmpty()) {
            currentCell.setFacing(Compass.turn(currentCell.getFacing(), Compass.AROUND));
            previousCell = currentCell;
        } else {
            Direction direction = choices.get(r.nextInt(choices.size()));
            previousCell = currentCell;
            currentCell = currentCell.getNeighbor(direction);
            currentCell.setFacing(direction);
            currentCell.setVisited(true);
        }
        if (!previousCell.equals(currentCell))
            previousCell.setFacing(null);
        currentCell.setSolving(true);
        return currentCell;
    }
}
