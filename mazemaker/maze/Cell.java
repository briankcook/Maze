package mazemaker.maze;

import java.util.HashMap;
import java.util.Set;

public class Cell {
    
    private final int x;
    private final int y;
    
    private boolean visited;
    private boolean goal;
    private boolean onPath;
    private boolean solving;
    private boolean making;
    private Direction facing;
    
    private HashMap<Direction,Cell> neighbors;
    
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        visited = false;
        goal = false;
        onPath = false;
        solving = false;
        making = false;
        facing = null;
        neighbors = new HashMap();
    }
    
    public Cell look(Direction direction) {
        return neighbors.get(direction);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean isGoal() {
        return goal;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public boolean isSolving() {
        return solving;
    }

    public boolean isMaking() {
        return making;
    }

    public Direction getFacing() {
        return facing;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setGoal(boolean isGoal) {
        this.goal = isGoal;
    }

    public void setOnPath(boolean onPath) {
        this.onPath = onPath;
    }

    public void setSolving(boolean solving) {
        this.solving = solving;
    }

    public void setMaking(boolean making) {
        this.making = making;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public void clearNeighbors() {
        neighbors.clear();
    }

    public Cell getNeighbor(Direction direction) {
        return neighbors.get(direction);
    }

    public void putNeighbor(Direction direction, Cell cell) {
        neighbors.put(direction, cell);
    }
    
    public Set<Direction> getDirections() {
        return neighbors.keySet();
    }
}
