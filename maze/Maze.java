package maze;

public class Maze {
    
    private final Cell[][] maze;
    private final int width;
    private final int height;
    
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new Cell[width][height];
        
        for (int i = 0 ; i < height ; i++) 
            for (int j = 0 ; j < width ; j++) 
                maze[j][i] = new Cell(j, i);
    }
    
    public void reset() {
        for (Cell column[] : maze)
            for (Cell cell : column)
                cell.setVisited(false);
        maze[0][0].setVisited(true);
    }
    
    public boolean canGo(Cell cell, Direction direction) {
        int x2 = cell.getX() + direction.x,
            y2 = cell.getY() + direction.y;
        return (x2 >= 0 && x2 < getWidth() &&
                y2 >= 0 && y2 < getHeight());
    }
    
    public boolean visited(Cell cell, Direction direction) {
        return look(cell, direction).isVisited();
    }
    
    public Cell look(Cell cell, Direction direction) {
        return maze[cell.getX()+direction.x][cell.getY()+direction.y];
    }
    
    public Cell getCell(int x, int y) {
        return maze[x][y];
    }
    
    public Cell getLastCell() {
        return getCell(getWidth()-1, getHeight()-1);
    }

    public Cell[][] getMaze() {
        return maze;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
