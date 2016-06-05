package maze;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazeView extends JPanel{
    
    private static final double SPEEDFACTOR = 0.7;
    
    private final Maze maze;
    private final CellView[][] cells;
    private final int cellSize;
    private final int wallThickness;
    
    private CellView toUpdate;
    private Timer timer;
    private int frameDelay;
    private boolean showUnseen;
    private transient GifWriter gifWriter;
     
    public MazeView(Maze maze, int cellSize, int wallThickness, int frameDelay, boolean showUnseen) {
        super();
        this.maze = maze;
        this.cellSize = cellSize;
        this.wallThickness = wallThickness;
        this.frameDelay = frameDelay;
        this.showUnseen = showUnseen;
        cells = new CellView[maze.getWidth()][maze.getHeight()];
    }
    
    public void init() {
        setLayout(new GridLayout(maze.getHeight(), maze.getWidth()));
        Dimension size = new Dimension(cellSize, cellSize);
        
        for (int i = 0 ; i < maze.getHeight() ; i++) {
            for (int j = 0 ; j < maze.getWidth() ; j++) {
                cells[j][i] = new CellView(this, getMaze().getCell(j, i), cellSize, wallThickness);
                cells[j][i].setPreferredSize(size);
                add(cells[j][i]);
            }
        }
    }
    
    public void runActor(MazeActor actor) {
        
        cleanUp();
        
        actor.init();
        
        if (actor.animate()) {
            toUpdate = cells[0][0];
            
            timer = new Timer(frameDelay, (ActionEvent e) -> {  
                Cell source = actor.step();
                toUpdate.repaint();
                if (source == null) {
                    stop(false);
                } else {
                    toUpdate = cells[source.getX()][source.getY()];
                    toUpdate.repaint();
                }
                if (gifWriter != null)
                    gifWriter.snapshot();
            });
            
            timer.start();
        } else {
            while (actor.step() != null)
                if (gifWriter != null)
                    gifWriter.snapshot();
            repaint();
        }
    }
    
    public void cleanUp() {
        stop(true);
        getMaze().reset(false);
        repaint();
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
    
    public void stop(boolean hard) {
        if (timer != null) {
            pause();
            timer = null;
            if (hard)
                maze.reset(false);
            setShowUnseen(true);
            repaint();
        }
        if (!hard && gifWriter != null) {
            gifWriter.close();
            gifWriter = null;
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
    
    public void record() {
        gifWriter = new GifWriter(this);
    }
    
    public boolean getShowUnseen() {
        return showUnseen;
    }
    
    public int getFrameDelay() {
        return frameDelay;
    }

    public void setShowUnseen(boolean showUnseen) {
        this.showUnseen = showUnseen;
    }

    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }

    public Maze getMaze() {
        return maze;
    }
}