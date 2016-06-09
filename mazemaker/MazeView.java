package mazemaker;

import java.awt.Point;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import mazemaker.maze.*;
import mazemaker.maze.generators.*;
import mazemaker.maze.solvers.*;

public class MazeView extends Canvas{
    
    private static final double SPEEDFACTOR = 0.7;
    
    private final Maze maze;
    private final Palette palette;
    
    private int cellSize;
    private int wallThickness;
    private boolean showUnvisited;
    private boolean[][] visited;
    private Timeline timeline;
    
    public MazeView(Maze maze, Palette palette, int cellSize, int wallThickness) {
        super(maze.width  * cellSize + wallThickness * 2, 
              maze.height * cellSize + wallThickness * 2);
        this.maze = maze;
        this.palette = palette;
        this.cellSize = cellSize;
        this.wallThickness = wallThickness;
        visited = new boolean[maze.width][maze.height];
        showUnvisited = true;
        redraw();
    }
    
    /*
    PAINTING
    */
    
    public void redraw() {
        for (int x = 0 ; x < maze.height ; x++)
            for (int y = 0 ; y < maze.width ; y++)
                drawCell(x, y);
        Point goal = maze.getGoal();
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(palette.getGoalColor());
        gc.fillOval(goal.x * cellSize + wallThickness * 2, 
                    goal.y * cellSize + wallThickness * 2, 
                    cellSize - wallThickness * 2,
                    cellSize - wallThickness * 2);
    }
    
    private void drawCell(int x, int y) {
        int gx = x * cellSize + wallThickness;
        int gy = y * cellSize + wallThickness;
        GraphicsContext gc = getGraphicsContext2D();
        
        if (visited[x][y])
            gc.setFill(palette.getVisitedColor());
        else
            gc.setFill(palette.getCellColor());
        gc.fillRect(gx, gy, cellSize, cellSize);
        
        gc.setStroke(palette.getWallColor());
        gc.setLineWidth(wallThickness);
        if (showUnvisited || visited[x][y]) {
            if (!maze.canGo(x, y, Maze.NORTH))
                gc.strokeLine(         gx,          gy, gx+cellSize,          gy);
            if (!maze.canGo(x, y, Maze.WEST))
                gc.strokeLine(         gx,          gy,          gx, gy+cellSize);
            if (!maze.canGo(x, y, Maze.SOUTH))
                gc.strokeLine(         gx, gy+cellSize, gx+cellSize, gy+cellSize);
            if (!maze.canGo(x, y, Maze.EAST))
                gc.strokeLine(gx+cellSize,          gy, gx+cellSize, gy+cellSize);
        }
    }
    
    private void drawActor(MazeActorData data) {
        int gx = data.x * cellSize + wallThickness;
        int gy = data.y * cellSize + wallThickness;
        GraphicsContext gc = getGraphicsContext2D();
        if (data.facing == null)
            gc.setFill(palette.getGenColor());
        else
            gc.setFill(palette.getSolverColor());
        gc.fillOval(gx, gy, cellSize, cellSize);
    }
    
    /*
    ACTIONS
    */
    
    public void runActor(String name, int hBias, int vBias, int frameDelay, boolean instant, boolean showUnvisited) {
        MazeActor actor = makeActor(name, hBias, vBias);
        cleanUp();
        this.showUnvisited = showUnvisited;
        redraw();
        if (actor == null)
            return;
        actor.init();
        if (instant) {
            while (actor.step() != null);
                /*if (gifWriter != null)
                    gifWriter.snapshot();*/
            cleanUp();
        } else {
            visited[0][0] = true;
            timeline = new Timeline(new KeyFrame(Duration.millis(frameDelay),
                ae -> {
                    MazeActorData source = actor.step();
                    if (source == null) {
                        stop();
                    } else {
                        visited[source.x][source.y] = true;
                        for (Point p : source.update)
                            drawCell(p.x, p.y); 
                        drawCell(source.x, source.y);
                        drawActor(source);
                    }
                }
            ));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }
    
    public void cleanUp() {
        visited = new boolean[maze.width][maze.height];
        stop();
    }
    
    public void pause() {
        if (timeline != null)
            timeline.stop();
    }
    
    public void play() {
        if (timeline != null)
            timeline.play();
    }
    
    public void stop() {
        showUnvisited = true;
        pause();
        timeline = null;
        redraw();
    }
    
    public void record() {
        
    }
    
    public void speedUp() {
        if (timeline != null)
            timeline.setRate(timeline.getRate() / SPEEDFACTOR);
    }
    
    public void slowDown() {
        if (timeline != null)
            timeline.setRate(timeline.getRate() * SPEEDFACTOR);
    }
    
    /*
    SETTERS, GETTERS
    */
    
    public void setSize(int cellSize, int wallThickness) {
        this.cellSize = cellSize;
        this.wallThickness = wallThickness;
        setWidth(maze.width  * cellSize + wallThickness * 2);
        setHeight(maze.height  * cellSize + wallThickness * 2);
        redraw();
    }
    
    public Maze getMaze() {
        return maze;
    }

    public Palette getPalette() {
        return palette;
    }
    
    /*
    HELPERS
    */
    
    private MazeActor makeActor(String name, int hBias, int vBias) {
        switch (name) {
            case MazeMaker.BACKSTEP:
                maze.reset();
                return new BackstepGenerator(maze, hBias, vBias);
            case MazeMaker.COINFLIP:
                maze.reset();
                return new RandomBinaryTreeGenerator(maze, hBias, vBias);
            case MazeMaker.RIGHTHAND:
                return new WallFollower(maze, true);
            case MazeMaker.LEFTHAND:
                return new WallFollower(maze, false);
            case MazeMaker.RANDOMTURNS:
                return new RandomTurns(maze);
            default:
                return null;
        }
    }
}