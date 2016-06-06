package mazemaker.maze;

public class Maze {
    
    public static final boolean HARD_RESET = true;
    public static final boolean SOFT_RESET = false;
    
    private final Cell[][] cells;
    private final int width;
    private final int height;
    
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
        
        for (int i = 0 ; i < height ; i++) 
            for (int j = 0 ; j < width ; j++) 
                cells[j][i] = new Cell(j, i);
    }
    
    public void reset(boolean type) {
        for (Cell[] column : cells) {
            for (Cell cell : column) {
                if (type == HARD_RESET)
                    cell.clearNeighbors();
                cell.setVisited(false);
                cell.setMaking(false);
                cell.setSolving(false);
            }
        }
    }
    
    public boolean canGo(Cell cell, Direction direction) {
        int x2 = cell.getX() + direction.x;
        int y2 = cell.getY() + direction.y;
        return x2 >= 0 && x2 < getWidth() &&
               y2 >= 0 && y2 < getHeight();
    }
    
    public Cell join(Cell a, Direction direction) {
        Cell b = look(a, direction);
        Direction reverse = Compass.turn(direction, Compass.AROUND);
        a.putNeighbor(direction, b);
        b.putNeighbor(reverse, a);
        return b;
    }
    
    public void toggle(Cell a, Cell b) {
        Direction direction = null;
        for (Direction d : Compass.getDirections())
            if (canGo(a,d) && look(a, d).equals(b))
                direction = d;
        if (direction != null) {
            if (a.hasNeighbor(b)) {
                Direction reverse = Compass.turn(direction, Compass.AROUND);
                a.removeNeighbor(direction);
                b.removeNeighbor(reverse);
            } else {
                join(a, direction);
            }
        }
    }
    
    public void setGoal(Cell cell) {
        clearGoal();
        cell.setGoal(true);
    }
    
    public void clearGoal() {
        Cell goal = getGoal();
        if (goal != null)
            goal.setGoal(false);
    }
    
    public Cell getGoal() {
        for (Cell[] column : cells) 
            for (Cell cell : column) 
                if (cell.isGoal())
                    return cell;
        return null;
    }
    
    public boolean visited(Cell cell, Direction direction) {
        return look(cell, direction).isVisited();
    }
    
    public Cell look(Cell cell, Direction direction) {
        return cells[cell.getX()+direction.x][cell.getY()+direction.y];
    }
    
    public Cell getCell(int x, int y) {
        return cells[x][y];
    }
    
    public Cell[][] getCells() {
        return cells;
    }
    
    public Cell getLastCell() {
        return getCell(getWidth()-1, getHeight()-1);
    }

    public Cell[][] getMaze() {
        return cells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
