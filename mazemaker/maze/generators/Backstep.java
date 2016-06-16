package mazemaker.maze.generators;

import mazemaker.maze.*;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

public class Backstep extends MazeActor {
    
    private final Maze maze;
    private final int hWeight;
    private final int vWeight;
    private final Random r;
    private final Deque<Point> history;
    private final ArrayList<Point> choices;
    private final boolean[][] visited;
    
    private int x;
    private int y;
    
    public Backstep(Maze maze, int hWeight, int vWeight) {
        this.maze = maze;
        this.hWeight = hWeight;
        this.vWeight = vWeight;
        r = new Random();
        history = new ArrayDeque();
        choices = new ArrayList();
        visited = new boolean[maze.width][maze.height];
    }
    
    @Override
    public void init() {
        Point start = maze.getStart();
        x = start.x;
        y = start.y;
        visited[x][y] = true;
        history.push(new Point(x, y));
    }
    
    @Override
    protected Datum[] step() {
        Point update = new Point(x, y);
        choices.clear();
        
        for (Direction direction : Maze.getDirections())
            if (maze.isValid(x + direction.x, y + direction.y) && 
                    !visited[x + direction.x][y + direction.y])
                add(direction);
        
        if (choices.isEmpty()) {
            if (history.isEmpty())
                return new Datum[]{};
            else 
                moveTo(history.pop());
        } else {
            history.push(new Point(x, y));
            moveTo(pickDirection());
            maze.toggleConnection(new Point(x,y), history.peek());
            visited[x][y] = true;
        }
        return new Datum[]{new Datum(update.x, update.y, maze.getCellData(update.x, update.y)), 
                           new Datum(x, y, Maze.face(maze.getCellData(x, y), Maze.NORTH))};
    }
    
    private void moveTo(Point cell) {
        x = cell.x;
        y = cell.y;
    }
    
    private Point pickDirection() {
        return choices.get(r.nextInt(choices.size()));
    }
    
    private void add(Direction direction) {
        int numTimes;
        Point cell = maze.look(x, y, direction);
        if (direction.equals(Maze.NORTH) || direction.equals(Maze.SOUTH))
            numTimes = vWeight;
        else
            numTimes = hWeight;
        for (int  i = 0 ; i < numTimes ; i++)
            choices.add(cell);
    }
}