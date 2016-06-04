package maze;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazeView extends JPanel{
    
    private final Maze maze;
    private final CellView[][] cells;
    private final MazeSettings settings;
    
    private CellView toUpdate;
    private Timer timer;
     
    public MazeView(Maze maze) {
        super();
        this.maze = maze;
        this.settings = maze.getSettings();
        cells = new CellView[settings.getMazeWidth()][settings.getMazeHeight()];
    }
    
    public void init() {
        setLayout(new GridLayout(settings.getMazeHeight(), settings.getMazeWidth()));
        Dimension size = new Dimension(settings.getCellSize(), settings.getCellSize());
        
        for (int i = 0 ; i < settings.getMazeHeight() ; i++) {
            for (int j = 0 ; j < settings.getMazeWidth() ; j++) {
                cells[j][i] = new CellView(maze.getCell(j, i), settings);
                cells[j][i].setPreferredSize(size);
                add(cells[j][i]);
            }
        }
    }
    
    public void runActor(MazeSettings settings, MazeActor actor) {
        this.settings.setFrameDelay(settings.getFrameDelay());
        this.settings.setShowUnseen(true);
        
        maze.reset();
        repaint();
        
        toUpdate = cells[0][0];
        
        timer = new Timer(settings.getFrameDelay(), (ActionEvent e) -> {  
            Cell source = actor.step();
            toUpdate.repaint();
            if (source == null) {
                timer.stop();
                settings.setShowUnseen(true);
                repaint();
            } else {
                toUpdate = cells[source.getX()][source.getY()];
                toUpdate.repaint();
            }
        });
        
        timer.start();
    }
}