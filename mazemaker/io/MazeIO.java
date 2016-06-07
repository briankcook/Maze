package mazemaker.io;

import java.awt.Point;
import mazemaker.maze.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface MazeIO {
    
    public static void saveMaze(Maze maze) {
        
        try (FileOutputStream out = new FileOutputStream(IO.pickFile("maze", true))) {
            int width = maze.width;
            int height = maze.width;
            Point goal = maze.getGoal();
            
            out.write(width);
            out.write(height);
            out.write(goal.x);
            out.write(goal.y);
            
            for (byte[] column : maze.getCellData()) 
                for (byte cell : column) 
                    out.write(cell);
        } catch (Exception e) { 
            Logger.getAnonymousLogger().log(Level.WARNING, "Maze save failed", e);
        }
    }
    
    public static Maze openMaze() {
        try (FileInputStream in = new FileInputStream(IO.pickFile("maze", false))) {
            Maze maze;
            
            int width = in.read();
            int height = in.read();
            int goalX = in.read();
            int goalY = in.read();
            
            maze = new Maze(width, height);
            maze.setGoal(new Point(goalX, goalY));
            
            for (int i = 0 ; i < height ; i ++) 
                for (int j = 0 ; j < width ; j++) 
                    maze.setCell(i, j, (byte)in.read());
            
            return maze;
        } catch (Exception e) { 
            Logger.getAnonymousLogger().log(Level.WARNING, "Maze load failed", e);
            return null;
        }
    }
}
