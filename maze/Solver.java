package maze;

public class Solver{
    
    private Cell location;
    private boolean moved;

    public Solver(Cell location) {
        super();
        this.location = location;
        location.solving = true;
        location.facing = Compass.SOUTH;
        moved = true;
    }
    
    public Cell step() {
        if (location.isGoal) 
            return null;
        if (moved) {
            location.facing = Compass.turnRight(location.facing);
            moved = false;
        }else if (!move()) {
            location.facing = Compass.turnLeft(location.facing);
            moved = false;
        }
        return location;
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