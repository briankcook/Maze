package maze;

import java.awt.Point;
import java.util.HashMap;

public class Cell {
    boolean visited;
    boolean isGoal;
    boolean onPath;
    boolean solving;
    boolean making;
    Point facing;
    HashMap<Point,Cell> neighbors;
    
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
