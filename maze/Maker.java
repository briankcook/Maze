package maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Maker{
    
    private final MazeSettings settings;
    private final Random r;
    private final Stack<Point> history;
    private final ArrayList<Direction> choices;
    private final Cell[][] maze;
    
    private int x;
    private int y;
    
    public Maker(MazeSettings settings) {
        this.settings = settings;
        r = new Random();
        history = new Stack();
        choices = new ArrayList();
        maze = new Cell[settings.getMazeWidth()][settings.getMazeHeight()];
    }
    
    public Cell[][] getMaze() {
        return maze;
    }
    
    public void init() {
        for (int i = 0 ; i < settings.getMazeHeight() ; i++) 
            for (int j = 0 ; j < settings.getMazeWidth() ; j++) 
                maze[j][i] = new Cell();
        x = 0;
        y = 0;
        currentCell().setVisited(true);
        currentCell().setMaking(true);
        history.push(new Point(x,y));
        maze[settings.getMazeWidth()-1][settings.getMazeHeight()-1].setGoal(true);
    }
    
    public Cell step() {
        choices.clear();
        
        for (Direction direction : Compass.DIRECTIONS)
            if (canGo(direction) && haventGone(direction))
                add(direction);

        currentCell().setMaking(false);
        
        if (choices.isEmpty()) {
            if (history.isEmpty()){
                return null;
            } else {
                Point p = history.pop();
                x = p.x;
                y = p.y;
                currentCell().setMaking(true);
            }
        } else {
            Direction direction = choices.get(r.nextInt(choices.size()));

            Cell nextCell = look(direction);

            currentCell().neighbors.put(direction, nextCell);
            nextCell.neighbors.put(Compass.turn(direction, Compass.AROUND), currentCell());

            nextCell.setMaking(true);
            nextCell.setVisited(true);

            history.push(new Point(x,y));

            x += direction.x;
            y += direction.y;
        }
        return currentCell();
    }
    
    private void add(Direction direction) {
        int numTimes;
        if (direction.equals(Compass.NORTH) || direction.equals(Compass.SOUTH))
            numTimes = settings.getVweight();
        else
            numTimes = settings.getHweight();
        for (int  i = 0 ; i < numTimes ; i++)
            choices.add(direction);
    }
    
    private boolean canGo(Direction direction) {
        int x2 = x + direction.x,
            y2 = y + direction.y;
        return (x2 >= 0 && x2 < settings.getMazeWidth() &&
                y2 >= 0 && y2 < settings.getMazeHeight());
    }
    
    private Cell currentCell() {
        return maze[x][y];
    }
    
    private Cell look(Direction direction) {
        return maze[x+direction.x][y+direction.y];
    }
    
    private boolean haventGone(Direction direction) {
        return !(look(direction).isVisited());
    }
}