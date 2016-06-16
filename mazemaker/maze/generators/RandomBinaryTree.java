package mazemaker.maze.generators;

import java.awt.Point;
import java.util.Random;
import mazemaker.maze.*;

public class RandomBinaryTree extends MazeActor{
    
    private final Maze maze;
    private final int hWeight;
    private final int vWeight;
    private final Random r;
    
    private int x;
    private int y;
    
    public RandomBinaryTree(Maze maze, int hWeight, int vWeight) {
        this.maze = maze;
        this.hWeight = hWeight;
        this.vWeight = vWeight;
        r = new Random();
    }

    
    @Override
    public void init() {
        x = 0;
        y = 0;
    }
    
    @Override
    protected Datum[] step() {
        Point current = new Point(x, y);
        Point update = new Point(x, y);
        if (maze.isValid(x-1, y-1))
            if (r.nextInt(hWeight+vWeight) + 1 > vWeight)
                update.x -= 1; 
            else
                update.y -= 1; 
        else if (maze.isValid(x-1, y))
                update.x -= 1; 
        else if (maze.isValid(x, y-1))
                update.y -= 1; 
        maze.toggleConnection(current, update);
        if (++x == maze.width) {
            x = 0;
            y++;
        }
        if (y == maze.height)
            return new Datum[]{};
        return new Datum[]{maze.datum(current.x, current.y, null),
                           maze.datum(update.x, update.y, null)};
    }
}
