package maze;

public class RightHandSolver implements MazeActor {
    
    private Cell location;
    private boolean moved;

    public RightHandSolver(Maze maze) {
        super();
        this.location = maze.getCell(0, 0);
        location.setSolving(true);
        location.setVisited(true);
        location.setFacing(Compass.SOUTH);
        moved = true;
    }
    
    @Override
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