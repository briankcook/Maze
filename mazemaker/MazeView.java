package mazemaker;

import mazemaker.io.GifWriter;
import mazemaker.mazeactor.MazeActor;
import mazemaker.maze.Cell;
import mazemaker.maze.Maze;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazeView extends JPanel{
    
    private static final double SPEEDFACTOR = 0.7;
    
    private final transient Maze maze;
    private final CellView[][] cells;
    private final int cellSize;
    private final int wallThickness;
    
    private CellView toUpdate;
    private Timer timer;
    private int frameDelay;
    private boolean showUnseen;
    private transient GifWriter gifWriter;
    private CellView selectedCell;
     
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
        addMouseListener(new MouseListener(){
            @Override public void mouseClicked(MouseEvent e){}
            @Override public void mouseEntered(MouseEvent e){}
            @Override public void mouseExited(MouseEvent e){}
            @Override public void mousePressed(MouseEvent e){
                handleClick(e);
            }
            @Override public void mouseReleased(MouseEvent e){}
        });
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK, wallThickness));
        setLayout(new GridLayout(maze.getHeight(), maze.getWidth()));
        Dimension size = new Dimension(cellSize, cellSize);
        
        for (int i = 0 ; i < maze.getHeight() ; i++) {
            for (int j = 0 ; j < maze.getWidth() ; j++) {
                cells[j][i] = new CellView(this, getMaze().getCell(j, i), cellSize, wallThickness);
                cells[j][i].setPreferredSize(size);
                add(cells[j][i]);
            }
        }
        
        reveal();
    }
    
    private void handleClick(MouseEvent e) {
        CellView cell = (CellView)getComponentAt(e.getPoint());
        if (selectedCell == null) {
            selectedCell = cell;
            selectedCell.setSelected(true);
            selectedCell.repaint();
        } else {
            if (cell.equals(selectedCell)) {
                maze.setGoal(selectedCell.getCell());
            } else {
                maze.toggle(cell.getCell(), selectedCell.getCell());
            }
            selectedCell.setSelected(false);
            selectedCell = null;
            cleanUp();
        }
    }
    
    public void runActor(MazeActor actor, boolean animate) {
        
        cleanUp();
        
        actor.init();
        
        if (animate) {
            toUpdate = cells[0][0];
            
            timer = new Timer(frameDelay, (ActionEvent e) -> {  
                Cell source = actor.step();
                toUpdate.repaint();
                if (source == null) {
                    stop();
                    reveal();
                    stopRecording();
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
            reveal();
        }
    }
    
    public void reveal() {
        setShowUnseen(true);
        repaint();
    }
    
    public void cleanUp() {
        stop();
        getMaze().reset(Maze.SOFT_RESET);
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
    
    public void stop() {
        if (timer != null) {
            pause();
            reveal();
            timer = null;
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
    
    public void stopRecording() {
        if (gifWriter != null) {
            gifWriter.snapshot();
            gifWriter.close();
            gifWriter = null;
        }
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