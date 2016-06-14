package mazemaker;

import java.awt.Point;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    
    private static final ImageCursor PENCIL_CURSOR = new ImageCursor(new Image(
            MazeView.class.getResourceAsStream("resources/pencil.png")), 0, 32);
    
    private Maze maze;
    private Point selection;
    private int editMode;

    final BooleanProperty showUnvisited;
    final IntegerProperty cellSize;
    final IntegerProperty wallThickness;
    final ObjectProperty<Paint> cellColor;
    final ObjectProperty<Paint> wallColor;
    final ObjectProperty<Paint> goalColor;
    final ObjectProperty<Paint> spriteColor;
    final ObjectProperty<Paint> visitedColor;
    
    public boolean showAll;
    public boolean[][] visited;
    
    
    public MazeView() {
        super();
        showAll = true;
        selection = null;
        editMode = SELECT_MODE;
        
        showUnvisited = new SimpleBooleanProperty();
        
        ChangeListener resize = (a, b, c) -> resize();
        
        cellSize = new SimpleIntegerProperty();
        wallThickness = new SimpleIntegerProperty();
        
        cellSize.addListener(resize);
        wallThickness.addListener(resize);
        
        ChangeListener redraw = (a, b, c) -> redraw();
        
        cellColor = new SimpleObjectProperty();
        wallColor = new SimpleObjectProperty();
        goalColor = new SimpleObjectProperty();
        spriteColor = new SimpleObjectProperty();
        visitedColor = new SimpleObjectProperty();
        
        cellColor.addListener(redraw);
        wallColor.addListener(redraw);
        goalColor.addListener(redraw);
        spriteColor.addListener(redraw);
        visitedColor.addListener(redraw);
        
        setOnMousePressed(this::handlePress);
        setOnMouseReleased(this::handleRelease);
        setOnMouseDragged(this::handleDrag);
        setOnMouseEntered(this::mouseIn);
        setOnMouseExited(this::mouseOut);
    }
    
    /*
    MOUSE EVENTS
    */
    
    private void handlePress(MouseEvent e) {
        selection = getCell(e);
        if (editMode == PENCIL_MODE || e.isPrimaryButtonDown())
            visited[selection.x][selection.y] = true;
        else if (e.isSecondaryButtonDown())
            visited[selection.x][selection.y] = false;
        redraw();
    }
    
    private void handleRelease(MouseEvent e) {
        selection = null;
        redraw();
    }
    
    private void handleDrag(MouseEvent e) {
        Point current = getCell(e);
        if (!selection.equals(current)) {
            if (editMode == PENCIL_MODE) {
                maze.toggleConnection(current, selection);
                visited[current.x][current.y] = true;
            } else if (editMode == SELECT_MODE) {
                if (maze.isGoal(selection.x, selection.y))
                    maze.setGoal(current);
                else if (maze.isStart(selection.x, selection.y))
                    maze.setStart(current);
                else if (e.isPrimaryButtonDown())
                    visited[current.x][current.y] = true;
                else if (e.isSecondaryButtonDown())
                    visited[current.x][current.y] = false;
            }
            drawCell(current.x, current.y);
            drawCell(selection.x, selection.y);
            drawStart();
            drawGoal();
            selection = current;
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
        int cell = cellSize.get();
        int wall = wallThickness.get();
        return new Point(Math.min((int)((e.getX() - wall) / cell), maze.width - 1),
                         Math.min((int)((e.getY() - wall) / cell), maze.height - 1));
    }
    
    /*
    PAINTING
    */
    
    public void redraw() {
        if (maze == null)
            return;
        for (int x = 0 ; x < maze.width ; x++)
            for (int y = 0 ; y < maze.height ; y++)
                drawCell(x, y);
        drawStart();
        drawGoal();
    }
    
    public void drawCell(int x, int y) {
        int cell = cellSize.get();
        int wall = wallThickness.get();
        int gx = x * cell + wall;
        int gy = y * cell + wall;
        GraphicsContext gc = getGraphicsContext2D();
        
        if (visited[x][y])
            gc.setFill(visitedColor.get());
        else
            gc.setFill(cellColor.get());
        gc.fillRect(gx, gy, cell, cell);
        
        gc.setStroke(wallColor.get());
        gc.setLineWidth(wall);
        double mod = (wall % 2 == 1) ? -0.5 : 0;
        if (showAll || showUnvisited.get() || visited[x][y]) {
            if (!maze.canGo(x, y, Maze.NORTH))
                gc.strokeLine(           gx + mod,            gy + mod, gx + cell + mod,            gy + mod);
            if (!maze.canGo(x, y, Maze.WEST))
                gc.strokeLine(           gx + mod,            gy + mod,            gx + mod, gy + cell + mod);
            if (!maze.canGo(x, y, Maze.SOUTH))
                gc.strokeLine(           gx + mod, gy + cell + mod, gx + cell + mod, gy + cell + mod);
            if (!maze.canGo(x, y, Maze.EAST))
                gc.strokeLine(gx + cell + mod,            gy + mod, gx + cell + mod, gy + cell + mod);
        }
    }
    
    public void drawActor(Datum datum, String spriteName) {
        int cell = cellSize.get();
        int wall = wallThickness.get();
        int gx = datum.x * cell + wall * 2;
        int gy = datum.y * cell + wall * 2;
        int scale = cell - wall * 2;
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(spriteColor.get());
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
    
    public void drawSprite(double[][] sprite, int x, int y, int scale, Direction facing) {
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
    
    public void drawGoal() {
        int cell = cellSize.get();
        int wall = wallThickness.get();
        GraphicsContext gc = getGraphicsContext2D();
        Point goal = maze.getGoal();
        gc.setFill(goalColor.get());
        gc.fillOval(goal.x * cell + wall * 2, 
                    goal.y * cell + wall * 2, 
                    cell - wall * 2,
                    cell - wall * 2);
    }
    
    public void drawStart() {
        int cell = cellSize.get();
        int wall = wallThickness.get();
        GraphicsContext gc = getGraphicsContext2D();
        Point start = maze.getStart();
        gc.setFill(spriteColor.get());
        gc.fillOval(start.x * cell + wall * 2, 
                    start.y * cell + wall * 2, 
                    cell - wall * 2,
                    cell - wall * 2);
    }
    
    /*
    ACTIONS, SETTERS, GETTERS
    */
    
    public void clear() {
        visited = new boolean[maze.width][maze.height];
    }
    
    public void resize() {
        if (maze == null)
            return;
        int cell = cellSize.get();
        int wall = wallThickness.get();
        setWidth(maze.width  * cell + wall * 2);
        setHeight(maze.height  * cell + wall * 2);
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        redraw();
    }
    
    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        visited = new boolean[maze.width][maze.height];
        resize();
    }
    
    public void setMode(int mode) {
        editMode = mode;
    }
    
    public static String[] getSpriteTypes() {
        return new String[] {CIRCLE, SQUARE, TRIANGLE, POINTER};
    }
}