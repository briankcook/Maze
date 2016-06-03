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
    private final ArrayList<Point> choices;
    
    public Maker(MazeSettings settings) {
        this.settings = settings;
        r = new Random();
        history = new Stack();
        choices = new ArrayList();
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
        choices.clear();
        
        for (Point direction : Maze.DIRECTIONS)
            if (canGo(direction) && maze[x+direction.x][y+direction.y].visited == false)
                add(direction);

        if (choices.isEmpty()) {
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
            Point direction = choices.get(r.nextInt(choices.size()));

            Cell current = maze[x][y];
            Cell next = maze[x+direction.x][y+direction.y];

            current.neighbors.put(direction, next);
            next.neighbors.put(Maze.turnAround(direction), current);

            current.making = false;
            next.making = true;

            history.push(new Point(x,y));

            x += direction.x;
            y += direction.y;

            maze[x][y].visited = true;

        }
        return maze[x][y];
    }
    
    private void add(Point direction) {
        int numTimes;
        if (direction.equals(Maze.NORTH) || direction.equals(Maze.SOUTH))
            numTimes = settings.getVweight();
        else
            numTimes = settings.getHweight();
        for (int  i = 0 ; i < numTimes ; i++)
            choices.add(direction);
    }
    
    private boolean canGo(Point direction) {
        int x2 = x + direction.x,
            y2 = y + direction.y;
        return (x2 >= 0 && x2 < settings.getMazeWidth() &&
                y2 >= 0 && y2 < settings.getMazeHeight());
    }
}