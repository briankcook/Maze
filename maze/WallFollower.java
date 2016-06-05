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
        Direction direction = currentCell.getFacing();
        Cell nextLocation = currentCell.neighbors.get(direction);
        if (moved || nextLocation == null) {
            int way = moved ? preferredDirection : alternateDirection;
            currentCell.setFacing(Compass.turn(direction, way));
            moved = false;
        } else {
            nextLocation.setFacing(direction);
            nextLocation.setSolving(true);
            nextLocation.setVisited(true);
            currentCell.setFacing(null);
            currentCell.setSolving(false);
            currentCell = nextLocation;
            moved = true;
        }
        return currentCell;
    }
}