package maze;

public class Solver{
    private static final boolean RIGHT = false;
    private static final boolean LEFT = true;

    private Cell location;
    private boolean moved;

    public Solver(Cell location) {
        super();
        this.location = location;
        location.solving = true;
        location.facing = Maze.SOUTH;
        moved = true;
    }
    
    public Cell step() {
        if (location.isGoal) 
            return null;
        if (moved) 
            turn(RIGHT);
        else if (!move()) 
            turn(LEFT); 
        return location;
    }

    private void turn(boolean left) {
        if (location.facing == Maze.NORTH)
            location.facing = left ? Maze.WEST : Maze.EAST;
        else if (location.facing == Maze.EAST)
            location.facing = left ? Maze.NORTH : Maze.SOUTH;
        else if (location.facing == Maze.SOUTH)
            location.facing = left ? Maze.EAST : Maze.WEST;
        else if (location.facing == Maze.WEST)
            location.facing = left ? Maze.SOUTH : Maze.NORTH;
        moved = false;
    }

    private boolean move() {
        Cell nextLocation = location.neighbors.get(location.facing);
        if (nextLocation == null)
            return false;
        nextLocation.facing = location.facing;
        location.facing = null;
        location.solving = false;
        location = nextLocation;
        location.solving = true;
        location.visited = true;
        moved = true;
        return true;
    }
}