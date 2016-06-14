package mazemaker.io;

import mazemaker.maze.*;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface MazeIO {
    
    public static void saveMaze(Maze maze) {
        
        try (FileOutputStream out = new FileOutputStream(IO.pickFile("maze", true))) {
            int width = maze.width;
            int height = maze.height;
            Point goal = maze.getGoal();
            Point start = maze.getStart();
            
            out.write(width);
            out.write(height);
            out.write(goal.x);
            out.write(goal.y);
            out.write(start.x);
            out.write(start.y);
            
            for (byte[] row : maze.getCellData()) 
                for (byte cell : row) 
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
            int startX = in.read();
            int startY = in.read();
            
            maze = new Maze(width, height);
            maze.setGoal(new Point(goalX, goalY));
            maze.setStart(new Point(startX, startY));
            
            for (int i = 0 ; i < width ; i ++) 
                for (int j = 0 ; j < height ; j++) 
                    maze.setCell(i, j, (byte)in.read());
            
            return maze;
        } catch (Exception e) { 
            Logger.getAnonymousLogger().log(Level.WARNING, "Maze load failed", e);
            return null;
        }
    }
}
