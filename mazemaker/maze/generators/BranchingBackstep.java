package mazemaker.maze.generators;

import mazemaker.maze.*;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

public class BranchingBackstep implements MazeActor {
    
    private final Maze maze;
    private final int hWeight;
    private final int vWeight;
    private final int branchFactor;
    private final Random r;
    private final boolean[][] visited;
    private final ArrayList<Point> choices;
    private final ArrayList<State> branches;
    
    private int x;
    private int y;
    private int steps;
    private Deque<Point> history;
    
    public BranchingBackstep(Maze maze, int hWeight, int vWeight, int branchFactor) {
        this.maze = maze;
        this.hWeight = hWeight;
        this.vWeight = vWeight;
        this.branchFactor = branchFactor;
        r = new Random();
        choices = new ArrayList();
        visited = new boolean[maze.width][maze.height];
        branches = new ArrayList();
    }
    
    @Override
    public void init() {
        x = 0;
        y = 0;
        visited[x][y] = true;
        makeBranch();
    }
    
    @Override
    public Datum[] step() {
        if (!nextBranch())
            return null;
        if (steps == branchFactor) 
            makeBranch();
        Datum[] data = subStep();
        return data;
    }
    
    private Datum[] subStep() {
        Datum update = new Datum(x, y, null);
        choices.clear();
        
        for (Direction direction : Maze.getDirections())
            if (maze.isValid(x + direction.x, y + direction.y) && 
                    !visited[x + direction.x][y + direction.y])
                add(direction);
        
        if (choices.isEmpty()) {
            if (history.isEmpty())
                return new Datum[]{update};
            else 
                moveTo(history.pop());
        } else {
            history.push(new Point(x, y));
            moveTo(pickDirection());
            maze.toggleConnection(new Point(x,y), history.peek());
            visited[x][y] = true;
            steps++;
        }
        branches.add(new State(x, y, steps, history));
        return new Datum[]{update, new Datum(x, y, Maze.NORTH)};
    }
    
    private void makeBranch() {
        steps = 0;
        Deque<Point> newHistory = new ArrayDeque();
        newHistory.add(new Point(x, y));
        branches.add(new State(x, y, 0, newHistory));
    }
    
    private boolean nextBranch() {
        if (branches.isEmpty())
            return false;
        State state = branches.remove(0);
        x = state.x;
        y = state.y;
        steps = state.steps;
        history = state.history;
        return true;
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
    
    class State {
        int x;
        int y;
        int steps;
        Deque<Point> history;

        public State(int x, int y, int steps, Deque<Point> history) {
            this.x = x;
            this.y = y;
            this.steps = steps;
            this.history = history;
        }
    }
}