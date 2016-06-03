package maze;

import java.util.HashMap;
import maze.Compass.Direction;

public class Cell {
    boolean visited;
    boolean isGoal;
    boolean onPath;
    boolean solving;
    boolean making;
    Direction facing;
    HashMap<Direction,Cell> neighbors;
    
    public Cell() {
        visited = false;
        isGoal = false;
        onPath = false;
        solving = false;
        making = false;
        facing = null;
        neighbors = new HashMap();
    }
}
