package maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Maker{
    
    Cell[][] maze;
    private int x;
    private int y;
    private final MazeSettings settings;
    private final Random r;
    private final Stack<Point> history;
    private final ArrayList<Point> directions;
    
    public Maker(MazeSettings settings) {
        this.settings = settings;
        r = new Random();
        history = new Stack();
        directions = new ArrayList();
    }
    
    public void init() {
        maze = new Cell[settings.getMazeWidth()][settings.getMazeHeight()];
        for (int i = 0 ; i < settings.getMazeHeight() ; i++) 
            for (int j = 0 ; j < settings.getMazeWidth() ; j++) 
                maze[j][i] = new Cell();
        x = 0;
        y = 0;
        maze[x][y].visited = true;
        maze[x][y].making = true;
        history.push(new Point(x,y));
        maze[settings.getMazeWidth()-1][settings.getMazeHeight()-1].isGoal = true;
    }
    
    public Cell step() {
        directions.clear();

        if (x > 0 && maze[x-1][y].visited==false)
            for (int i = 0 ; i < settings.getHweight() ; i++)
                directions.add(Maze.WEST);

        if (x < settings.getMazeWidth()-1 && maze[x+1][y].visited==false)
            for (int i = 0 ; i < settings.getHweight() ; i++)
                directions.add(Maze.EAST);

        if (y > 0 && maze[x][y-1].visited==false)
            for (int i = 0 ; i < settings.getVweight() ; i++)
                directions.add(Maze.NORTH);

        if (y < settings.getMazeHeight()-1 && maze[x][y+1].visited==false)
            for (int i = 0 ; i < settings.getVweight() ; i++)
                directions.add(Maze.SOUTH);

        if (directions.isEmpty()) {
            if (history.isEmpty()){
                maze[x][y].making = false;
                return null;
            } else {
                maze[x][y].making = false;
                Point p = history.pop();
                x = p.x;
                y = p.y;
                maze[x][y].making = true;
            }
        } else {
            Point direction = directions.get(r.nextInt(directions.size()));

            Cell current = maze[x][y];
            Cell next = maze[x+direction.x][y+direction.y];

            current.neighbors.put(direction, next);
            next.neighbors.put(Maze.reverse(direction), current);

            current.making = false;
            next.making = true;

            history.push(new Point(x,y));

            x += direction.x;
            y += direction.y;

            maze[x][y].visited = true;

        }
        return maze[x][y];
    }
}