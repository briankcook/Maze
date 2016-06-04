package maze;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazeView extends JPanel{
    
    private final Maze maze;
    private final CellView[][] cells;
    private final int cellSize;
    private final int wallThickness;
    
    private CellView toUpdate;
    private Timer timer;
    private int frameDelay;
    private boolean showUnseen;
     
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
                cells[j][i] = new CellView(this, maze.getCell(j, i), cellSize, wallThickness);
                cells[j][i].setPreferredSize(size);
                add(cells[j][i]);
            }
        }
    }
    
    public void runActor(MazeActor actor) {
        
        maze.reset();
        repaint();
        
        if (actor.animate()) {
            toUpdate = cells[0][0];
            
            timer = new Timer(frameDelay, (ActionEvent e) -> {  
                Cell source = actor.step();
                toUpdate.repaint();
                if (source == null) {
                    timer.stop();
                    repaint();
                } else {
                    toUpdate = cells[source.getX()][source.getY()];
                    toUpdate.repaint();
                }
            });
            
            timer.start();
        } else {
            while (actor.step() != null);
            repaint();
        }
    }
    
    public boolean getShowUnseen() {
        return showUnseen;
    }

    public void setShowUnseen(boolean showUnseen) {
        this.showUnseen = showUnseen;
    }

    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }
}