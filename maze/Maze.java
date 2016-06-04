package maze;

public class Maze {
    private final Cell[][] maze;
    private final MazeSettings settings;
    
    public Maze(MazeSettings settings) {
        this.settings = settings;
        maze = new Cell[settings.getMazeWidth()][settings.getMazeHeight()];
        
        for (int i = 0 ; i < settings.getMazeHeight() ; i++) 
            for (int j = 0 ; j < settings.getMazeWidth() ; j++) 
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
        return (x2 >= 0 && x2 < settings.getMazeWidth() &&
                y2 >= 0 && y2 < settings.getMazeHeight());
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
        return getCell(settings.getMazeWidth()-1, settings.getMazeHeight()-1);
    }

    public Cell[][] getMaze() {
        return maze;
    }

    public MazeSettings getSettings() {
        return settings;
    }
}
