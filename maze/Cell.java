package maze;

import java.util.HashMap;

public class Cell {
    private boolean visited;
    private boolean goal;
    private boolean onPath;
    private boolean solving;
    private boolean making;
    private Direction facing;
    
    HashMap<Direction,Cell> neighbors;
    
    public Cell() {
        visited = false;
        goal = false;
        onPath = false;
        solving = false;
        making = false;
        facing = null;
        neighbors = new HashMap();
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
}
