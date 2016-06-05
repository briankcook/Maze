package maze;

public class WallFollower implements MazeActor {
    
    private final int preferredDirection;
    private final int alternateDirection;
    private Cell currentCell;
    private boolean moved;

    public WallFollower(Maze maze, boolean rightHanded) {
        preferredDirection = rightHanded ? Compass.RIGHT : Compass.LEFT;
        alternateDirection = rightHanded ? Compass.LEFT : Compass.RIGHT;
        currentCell = maze.getCell(0, 0);
    }
    
    @Override
    public void init() {
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
            int way = moved ? preferredDirection : alternateDirection;
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