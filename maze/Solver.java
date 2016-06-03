package maze;

public class Solver{
    
    private Cell location;
    private boolean moved;

    public Solver(Cell location) {
        super();
        this.location = location;
        location.setSolving(true);
        location.setFacing(Compass.SOUTH);
        moved = true;
    }
    
    public Cell step() {
        if (location.isGoal()) 
            return null;
        Cell nextLocation = location.neighbors.get(location.getFacing());
        if (moved || nextLocation == null) {
            int way = moved ? Compass.RIGHT : Compass.LEFT;
            location.setFacing(Compass.turn(location.getFacing(), way));
            moved = false;
        } else {
            nextLocation.setFacing(location.getFacing());
            nextLocation.setSolving(true);
            nextLocation.setVisited(true);
            location.setFacing(null);
            location.setSolving(false);
            location = nextLocation;
            moved = true;
        }
        return location;
    }
}