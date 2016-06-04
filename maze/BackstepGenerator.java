package maze;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class BackstepGenerator implements MazeActor {
    
    private final Maze maze;
    private final int hWeight;
    private final int vWeight;
    private final boolean animate;
    private final Random r;
    private final Stack<Cell> history;
    private final ArrayList<Direction> choices;
    
    private Cell currentCell;
    
    public BackstepGenerator(Maze maze, int hWeight, int vWeight, boolean animate) {
        this.maze = maze;
        this.hWeight = hWeight;
        this.vWeight = vWeight;
        this.animate = animate;
        r = new Random();
        history = new Stack();
        choices = new ArrayList();
        currentCell = maze.getCell(0, 0);
        currentCell.setMaking(true);
        currentCell.setVisited(true);
        history.push(currentCell);
        maze.getLastCell().setGoal(true);
    }
    
    @Override
    public Cell step() {
        choices.clear();

        currentCell.setMaking(false);
        
        for (Direction direction : Compass.DIRECTIONS)
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
            Direction direction = choices.get(r.nextInt(choices.size()));
            Cell nextCell = maze.look(currentCell, direction);

            currentCell.neighbors.put(direction, nextCell);
            nextCell.neighbors.put(Compass.turn(direction, Compass.AROUND), currentCell);

            history.push(currentCell);
            currentCell = nextCell;
            currentCell.setMaking(true);
            currentCell.setVisited(true);
        }
        return currentCell;
    }
    
    @Override
    public boolean animate() {
        return animate;
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