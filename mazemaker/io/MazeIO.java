package mazemaker.io;

import mazemaker.maze.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MazeIO {
       
    private static final Logger LOGGER = Logger.getAnonymousLogger();
    
    private static final int NORTH = 0b1000;
    private static final int EAST  = 0b0100;
    private static final int SOUTH = 0b0010;
    private static final int WEST  = 0b0001;
    
    private MazeIO(){}
    
    public static void saveMaze(Maze maze) {
        
        try (FileOutputStream out = new FileOutputStream(IO.pickFile("maze", true))) {
            int width = maze.getWidth();
            int height = maze.getHeight();
            out.write(width);
            out.write(height);
            for (Cell[] column : maze.getCells()) {
                for (Cell cell : column) {
                    byte b = 0;
                    b |= cell.getNeighbor(Compass.NORTH) != null ? NORTH : 0;
                    b |= cell.getNeighbor(Compass.EAST)  != null ? EAST  : 0;
                    b |= cell.getNeighbor(Compass.SOUTH) != null ? SOUTH : 0;
                    b |= cell.getNeighbor(Compass.WEST)  != null ? WEST  : 0;
                    out.write(b);
                }
            }
        } catch (Exception e) { 
            LOGGER.log(Level.WARNING, "Maze save failed", e);
        }
    }
    
    public static Maze openMaze() {
        Maze maze;
        try (FileInputStream in = new FileInputStream(IO.pickFile("maze", false))) {
            int width = in.read();
            int height = in.read();
            maze = new Maze(width, height);
            maze.getCell(width-1, height-1).setGoal(true);
            for (Cell[] column : maze.getCells()) {
                for (Cell cell : column) {
                    int b = in.read();
                    if ((b & NORTH) > 0)
                        maze.join(cell, Compass.NORTH);
                    if ((b & EAST) > 0)
                        maze.join(cell, Compass.EAST);
                    if ((b & SOUTH) > 0)
                        maze.join(cell, Compass.SOUTH);
                    if ((b & WEST) > 0)
                        maze.join(cell, Compass.WEST);
                }
            }
        } catch (Exception e) { 
            LOGGER.log(Level.WARNING, "Maze load failed", e);
            return null;
        }
        return maze;
    }
}
