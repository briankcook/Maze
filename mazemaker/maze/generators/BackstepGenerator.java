package mazemaker.maze.generators;

import mazemaker.maze.*;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

public class BackstepGenerator implements MazeActor {
    
    private final Maze maze;
    private final int hWeight;
    private final int vWeight;
    private final Random r;
    private final Deque<Point> history;
    private final ArrayList<Point> choices;
    private final boolean[][] visited;
    
    private int x;
    private int y;
    
    public BackstepGenerator(Maze maze, int hWeight, int vWeight) {
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
        x = 0;
        y = 0;
        visited[x][y] = true;
        history.push(new Point(x, y));
    }
    
    @Override
    public MazeActorData step() {
        Point update = new Point(x, y);
        choices.clear();
        
        for (Direction direction : Maze.getDIRECTIONS())
            if (maze.isValid(x + direction.x, y + direction.y) && 
                    !visited[x + direction.x][y + direction.y])
                add(direction);
        
        if (choices.isEmpty()) {
            if (history.isEmpty())
                return null;
            else 
                moveTo(history.pop());
        } else {
            history.push(new Point(x, y));
            moveTo(pickDirection());
            maze.toggleConnection(new Point(x,y), history.peek());
            visited[x][y] = true;
        }
        return new MazeActorData(x, y, null, update);
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