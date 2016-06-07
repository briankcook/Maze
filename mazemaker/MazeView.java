package mazemaker;

import mazemaker.io.GifWriter;
import mazemaker.maze.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class MazeView extends JLabel{
    
    private static final double SPEEDFACTOR = 0.7;
    
    private final transient Maze maze;
    private final transient BufferedImage image;
    private final transient Graphics2D graphics;
    private final int cellSize;
    private final int wallThickness;
    private final HashMap<Direction, Polygon> pointers;
    private final Color highlight;
    
    private Timer timer;
    private int frameDelay;
    private boolean showUnseen;
    private boolean[][] visited;
    private transient GifWriter gifWriter;
    private Point toUpdate;
    private Point selection;
     
    public MazeView(Maze maze, int cellSize, int wallThickness, int frameDelay, boolean showUnseen) {
        super();
        this.maze = maze;
        this.cellSize = cellSize;
        this.wallThickness = wallThickness;
        this.frameDelay = frameDelay;
        this.showUnseen = showUnseen;
        this.image = new BufferedImage(maze.width*cellSize, maze.height*cellSize, BufferedImage.TYPE_INT_RGB);
        graphics = image.createGraphics();
        pointers = new HashMap();
        visited = new boolean[maze.width][maze.height];
        highlight = new Color(0.5f, 0.5f, 1.0f, 0.5f);
        
        // undocumented triangle magic, just go with it
        int[] slm = new int[]{           wallThickness*2, cellSize - wallThickness*2,                 cellSize/2};
        int[] ssl = new int[]{           wallThickness*2,            wallThickness*2, cellSize - wallThickness*2};
        int[] lls = new int[]{cellSize - wallThickness*2, cellSize - wallThickness*2,            wallThickness*2};
        pointers.put(Maze.NORTH, new Polygon(slm, lls, 3));
        pointers.put(Maze.SOUTH, new Polygon(slm, ssl, 3));
        pointers.put(Maze.EAST,  new Polygon(ssl, slm, 3));
        pointers.put(Maze.WEST,  new Polygon(lls, slm, 3));
    }
    
    public void init() {
        addMouseListener(new MouseListener(){
            @Override public void mouseClicked(MouseEvent e){}
            @Override public void mouseEntered(MouseEvent e){}
            @Override public void mouseExited(MouseEvent e){}
            @Override public void mousePressed(MouseEvent e){
                Point origin = e.getPoint();
                handleClick(new Point(origin.x / cellSize, origin.y / cellSize));
            }
            @Override public void mouseReleased(MouseEvent e){}
        });
        
        setIcon(new ImageIcon(image));
        
        reveal();
    }
    
    private void handleClick(Point clicked) {
        if (selection == null) {
            selection = clicked;
        } else {
            if (selection.equals(clicked)) {
                maze.setGoal(selection);
            } else {
                maze.toggleConnection(clicked, selection);
            }
            selection = null;
            cleanUp();
        }
        paintAll();
    }
    
    private void paintAll() {
        for (int i = 0 ; i < maze.width ; i++)
            for (int j = 0 ; j < maze.height ; j++)
                paintCell(new Point(i, j), false);
        
        Point goal = maze.getGoal();
        graphics.setColor(Color.GRAY);
        graphics.fillOval(goal.x * cellSize + wallThickness * 2, 
                          goal.y * cellSize + wallThickness * 2,
                          cellSize - wallThickness * 4, 
                          cellSize - wallThickness * 4);
        
        if (selection != null) {
            graphics.setColor(highlight);
            graphics.fillRect(selection.x* cellSize, selection.y* cellSize, cellSize, cellSize);
        }
        
        repaint();
    }
    
    private void paintCell(Point location){
        paintCell(location, true);
    }
    
    private void paintCell(Point location, boolean repaint){
        int x = cellSize * location.x;
        int y = cellSize * location.y;
        
        graphics.setColor(Color.WHITE);
        graphics.fillRect(x, y, cellSize, cellSize);
        
        graphics.setColor(Color.BLACK);
        // corners
        if (showUnseen || visited[location.x][location.y]) {
            graphics.fillRect(                           x,                            y, wallThickness, wallThickness);
            graphics.fillRect(                           x, y + cellSize - wallThickness, wallThickness, wallThickness);
            graphics.fillRect(x + cellSize - wallThickness,                            y, wallThickness, wallThickness);
            graphics.fillRect(x + cellSize - wallThickness, y + cellSize - wallThickness, wallThickness, wallThickness);
            // walls
            if (!maze.canGo(location.x, location.y, Maze.NORTH))
                graphics.fillRect(x, y, cellSize, wallThickness);
            if (!maze.canGo(location.x, location.y, Maze.SOUTH))
                graphics.fillRect(x, y + cellSize - wallThickness, cellSize, wallThickness);
            if (!maze.canGo(location.x, location.y, Maze.EAST))
                graphics.fillRect(x + cellSize - wallThickness, y, wallThickness, cellSize);
            if (!maze.canGo(location.x, location.y, Maze.WEST))
                graphics.fillRect(x, y, wallThickness, cellSize);
        }
        
        if (repaint)
            repaint();
    }
    
    private void paintActor(MazeActorData actor) {
        int x = cellSize * actor.x;
        int y = cellSize * actor.y;
        if (actor.facing == null) {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(x + wallThickness * 2, 
                              y + wallThickness * 2, 
                              cellSize - wallThickness * 4, 
                              cellSize - wallThickness * 4);
        } else {
            Polygon ref = pointers.get(actor.facing);
            Polygon pointer = new Polygon(ref.xpoints, ref.ypoints, ref.npoints);
            pointer.translate(x, y);
            graphics.setColor(Color.RED);
            graphics.fillPolygon(pointer);
        }
    }
    
    public void runActor(MazeActor actor, boolean animate, boolean showUnseen, boolean resetMaze) {
        this.showUnseen = showUnseen;
        
        visited = new boolean[maze.width][maze.height];
        
        if (resetMaze)
            maze.reset();
        
        paintAll();
        
        actor.init();
        
        if (animate) {
            toUpdate = new Point(0, 0);
            visited[0][0] = true;
            
            timer = new Timer(frameDelay, (ActionEvent e) -> {  
                MazeActorData source = actor.step();
                paintCell(toUpdate);
                if (source == null) {
                    cleanUp();
                    stopRecording();
                } else {
                    visited[source.x][source.y] = true;
                    toUpdate = source;
                    paintCell(source);
                    paintActor(source);
                }
                if (gifWriter != null)
                    gifWriter.snapshot();
            });
            
            timer.start();
        } else {
            while (actor.step() != null)
                if (gifWriter != null)
                    gifWriter.snapshot();
            reveal();
        }
    }
    
    public void pause() {
        if (timer != null)
            timer.stop();
    }
    
    public void resume() {
        if (timer != null)
            timer.start();
    }
    
    public void stop() {
        if (timer != null) {
            pause();
            timer = null;
        }
    }
    
    public void reveal() {
        showUnseen = true;
        paintAll();
    }
    
    public void record() {
        gifWriter = new GifWriter(image, frameDelay);
    }
    
    public void stopRecording() {
        if (gifWriter != null) {
            gifWriter.snapshot();
            gifWriter.close();
            gifWriter = null;
        }
    }
    
    public void speedUp() {
        if (timer != null) {
            timer.setDelay((int)(timer.getDelay() * SPEEDFACTOR));
        }
    }
    
    public void slowDown() {
        if (timer != null) {
            timer.setDelay((int)(timer.getDelay() / SPEEDFACTOR));
        }
    }
    
    public void cleanUp() {
        visited = new boolean[maze.width][maze.height];
        stop();
        reveal();
    }
    
    public int getFrameDelay() {
        return frameDelay;
    }

    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }

    public Maze getMaze() {
        return maze;
    }
}