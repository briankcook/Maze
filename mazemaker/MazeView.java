package mazemaker;

import java.awt.Point;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import mazemaker.io.GifWriter;
import mazemaker.maze.*;

public class MazeView extends Canvas{
    
    public static final String CIRCLE = "Circle";
    public static final String SQUARE = "Square";
    public static final String TRIANGLE = "Triangle";
    public static final String POINTER = "Pointer";
    
    public static final int SELECT_MODE = 0;
    public static final int PENCIL_MODE = 1;
    
    private static final double[][] TRIANGLESPRITE = new double[][]
        {{0.5, 0.0, 1.0},
         {0.0, 1.0, 1.0}};
    
    private static final double[][] POINTERSPRITE = new double[][]
        {{0.5, 0.2, 0.5, 0.8},
         {0.0, 1.0, 0.7, 1.0}};
    
    private static final Color SELECTIONCOLOR = new Color(0.5, 0.5, 1.0, 0.5);
    
    private static final double SPEEDFACTOR = 0.7;
    
    private static final ImageCursor PENCIL_CURSOR = new ImageCursor(new Image(
            MazeView.class.getResourceAsStream("resources/pencil.png")), 0, 32);
    
    private final Maze maze;
    
    private int cellSize;
    private int wallThickness;
    private boolean showUnvisited;
    private boolean[][] visited;
    private Timeline timeline;
    private Point selection;
    private int editMode;

    private Color cellColor;
    private Color visitedColor;
    private Color wallColor;
    private Color spriteColor;
    private Color goalColor;
    
    private GifWriter gifWriter;
    
    public MazeView(Maze maze, int cellSize, int wallThickness) {
        super(maze.width  * cellSize + wallThickness * 2, 
              maze.height * cellSize + wallThickness * 2);
        this.maze = maze;
        this.cellSize = cellSize;
        this.wallThickness = wallThickness;
        visited = new boolean[maze.width][maze.height];
        showUnvisited = true;
        selection = null;
        editMode = SELECT_MODE;
        redraw();
        setOnMousePressed(this::handlePress);
        setOnMouseReleased(this::handleRelease);
        setOnMouseDragged(this::handleDrag);
        setOnMouseEntered(this::mouseIn);
        setOnMouseExited(this::mouseOut);
    }
    
    private void handlePress(MouseEvent e) {
        Point clicked = getCell(e);
        if (selection == null) {
            selection = clicked;
            if (editMode == PENCIL_MODE)
                visited[clicked.x][clicked.y] = true;
        } else if (editMode == SELECT_MODE) {
            if (selection.equals(clicked)) 
                maze.setGoal(clicked);
            else 
                maze.toggleConnection(clicked, selection);
            selection = null;
        }
        redraw();
    }
    
    private void handleRelease(MouseEvent e) {
        if (editMode != SELECT_MODE)
            selection = null;
        redraw();
    }
    
    private void handleDrag(MouseEvent e) {
        if (editMode == PENCIL_MODE) {
            Point current = getCell(e);
            if (!selection.equals(current)) {
                maze.toggleConnection(current, selection);
                visited[current.x][current.y] = true;
                drawCell(current.x, current.y);
                drawCell(selection.x, selection.y);
                selection = current;
            }
        }
    }
    
    private void mouseIn(MouseEvent e) {
        if (editMode == PENCIL_MODE)
            setCursor(PENCIL_CURSOR);
        else
            setCursor(Cursor.HAND);
    }
    
    private void mouseOut(MouseEvent e) {
        setCursor(Cursor.DEFAULT);
    }
    
    private Point getCell(MouseEvent e) {
        return new Point(Math.min((int)((e.getX() - wallThickness) / cellSize), maze.width - 1),
                         Math.min((int)((e.getY() - wallThickness) / cellSize), maze.height - 1));
    }
    
    /*
    PAINTING
    */
    
    private void redraw() {
        for (int x = 0 ; x < maze.width ; x++)
            for (int y = 0 ; y < maze.height ; y++)
                drawCell(x, y);
        Point goal = maze.getGoal();
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(goalColor);
        gc.fillOval(goal.x * cellSize + wallThickness * 2, 
                    goal.y * cellSize + wallThickness * 2, 
                    cellSize - wallThickness * 2,
                    cellSize - wallThickness * 2);
        if (selection != null) {
            gc.setFill(SELECTIONCOLOR);
            gc.fillRect(selection.x * cellSize + wallThickness,
                        selection.y * cellSize + wallThickness,
                        cellSize,
                        cellSize);
        }
    }
    
    private void drawCell(int x, int y) {
        int gx = x * cellSize + wallThickness;
        int gy = y * cellSize + wallThickness;
        GraphicsContext gc = getGraphicsContext2D();
        
        if (visited[x][y])
            gc.setFill(visitedColor);
        else
            gc.setFill(cellColor);
        gc.fillRect(gx, gy, cellSize, cellSize);
        
        gc.setStroke(wallColor);
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
    
    private void drawActor(Datum datum, String spriteName) {
        int gx = datum.x * cellSize + wallThickness * 2;
        int gy = datum.y * cellSize + wallThickness * 2;
        int scale = cellSize - wallThickness * 2;
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(spriteColor);
        switch (spriteName) {
            case CIRCLE:
                gc.fillOval(gx, gy, scale, scale);
                break;
            case TRIANGLE:
                drawSprite(TRIANGLESPRITE, gx, gy, scale, datum.facing);
                break;
            case POINTER:
                drawSprite(POINTERSPRITE, gx, gy, scale, datum.facing);
                break;
            case SQUARE:
            default:
                gc.fillRect(gx, gy, scale, scale);
                break;
        }
    }
    
    private void drawSprite(double[][] sprite, int x, int y, int scale, Direction facing) {
        GraphicsContext gc = getGraphicsContext2D();
        double[] xs = sprite[0].clone();
        double[] ys = sprite[1].clone();
        
        // flip
        if (facing.equals(Maze.SOUTH) || facing.equals(Maze.EAST))
            for (int i = 0 ; i < xs.length ; i++)
                ys[i] = 1.0 - ys[i];
        
        // rotate
        if (facing.equals(Maze.EAST) || facing.equals(Maze.WEST)) {
            double[] temp = xs;
            xs = ys;
            ys = temp;
        }
        
        // scale and move
        for (int i = 0 ; i < xs.length ; i++) {
            xs[i] = xs[i] * scale + x;
            ys[i] = ys[i] * scale + y;
        }
        
        gc.fillPolygon(xs, ys, xs.length);
    }
    
    /*
    ACTIONS
    */
    
    public void runActor(MazeActor actor, String sprite, int frameDelay, boolean instant, boolean showUnvisited) {
        if (gifWriter == null)
            cleanUp();
        this.showUnvisited = showUnvisited;
        redraw();
        if (actor == null)
            return;
        actor.init();
        if (instant) {
            while (actor.step() != null)
                if (gifWriter != null)
                    gifWriter.snapshot();
            cleanUp();
        } else {
            visited[0][0] = true;
            drawCell(0,0);
            timeline = new Timeline(new KeyFrame(Duration.millis(frameDelay),
                ae -> {
                    if (gifWriter != null)
                        gifWriter.snapshot();
                    Datum[] data = actor.step();
                    if (data == null) {
                        stop();
                    } else {
                        for (Datum datum : data) {
                            visited[datum.x][datum.y] = true;
                            drawCell(datum.x, datum.y); 
                            if (datum.facing != null)
                                drawActor(datum, sprite);
                        }
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
        if (gifWriter != null) {
            gifWriter.close();
            gifWriter = null;
        }
    }
    
    public void record(int frameDelay) {
        cleanUp();
        gifWriter = new GifWriter(this, frameDelay);
        if (!gifWriter.init())
            gifWriter = null;
    }
    
    public void speedUp() {
        if (timeline != null)
            timeline.setRate(timeline.getRate() / SPEEDFACTOR);
    }
    
    public void slowDown() {
        if (timeline != null)
            timeline.setRate(timeline.getRate() * SPEEDFACTOR);
    }
    
    public void pointer() {
        editMode = SELECT_MODE;
    }
    
    public void pencil() {
        editMode = PENCIL_MODE;
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

    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
        redraw();
    }

    public void setVisitedColor(Color visitedColor) {
        this.visitedColor = visitedColor;
        redraw();
    }

    public void setWallColor(Color wallColor) {
        this.wallColor = wallColor;
        redraw();
    }

    public void setSpriteColor(Color spriteColor) {
        this.spriteColor = spriteColor;
        redraw();
    }

    public void setGoalColor(Color goalColor) {
        this.goalColor = goalColor;
        redraw();
    }
    
    public static String[] getSpriteTypes() {
        return new String[] {CIRCLE, SQUARE, TRIANGLE, POINTER};
    }
}