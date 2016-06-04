package maze;

public class RightHandSolver implements MazeActor {
    
    private Cell currentCell;
    private boolean moved;

    public RightHandSolver(Maze maze) {
        super();
        currentCell = maze.getCell(0, 0);
        currentCell.setSolving(true);
        currentCell.setVisited(true);
        currentCell.setFacing(Compass.SOUTH);
        moved = true;
    }
    
    @Override
    public Cell step() {
        if (currentCell.isGoal()) 
            return null;
        Cell nextLocation = currentCell.neighbors.get(currentCell.getFacing());
        if (moved || nextLocation == null) {
            int way = moved ? Compass.RIGHT : Compass.LEFT;
            currentCell.setFacing(Compass.turn(currentCell.getFacing(), way));
            moved = false;
        } else {
            nextLocation.setFacing(currentCell.getFacing());
            nextLocation.setSolving(true);
            nextLocation.setVisited(true);
            currentCell.setFacing(null);
            currentCell.setSolving(false);
            currentCell = nextLocation;
            moved = true;
        }
        return currentCell;
    }
    
    @Override
    public boolean animate() {
        return true;
    }
}