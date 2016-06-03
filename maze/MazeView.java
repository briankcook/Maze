package maze;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazeView extends JPanel{
    private final Cell[][] maze;
    private final HashMap<Cell, CellView> map;
    public MazeSettings settings;
    private CellView toUpdate;
    private Timer timer;
     
    public MazeView(Cell[][] maze, MazeSettings settings) {
        super();
        this.maze = maze;
        this.settings = settings;
        map = new HashMap();
    }
    
    public void init(Maker maker) {
        setLayout(new GridLayout(settings.getMazeHeight(), settings.getMazeWidth()));
        Dimension size = new Dimension(settings.getCellSize(), settings.getCellSize());
        
        for (int i = 0 ; i < settings.getMazeHeight() ; i++) {
            for (int j = 0 ; j < settings.getMazeWidth() ; j++) {
                CellView view = new CellView(maze[j][i], settings);
                view.setPreferredSize(size);
                map.put(maze[j][i], view);
                add(view);
            }
        }
        
        toUpdate = map.get(maze[0][0]);
        
        timer = new Timer(settings.getFrameDelay(), (ActionEvent e) -> {  
            update(maker.step());
        });
    }
    
    public Cell getStartCell() {
        return maze[0][0];
    }
    
    public void start() {
        timer.start();
    }
    
    public void solveMode(MazeSettings settings, Solver solver) {
        this.settings.setFrameDelay(settings.getFrameDelay());
        this.settings.setShowUnseen(settings.showUnseen());
        
        map.keySet().stream().forEach((cell) -> {
            cell.visited = false;
        });
        
        map.values().stream().forEach((view) -> {
            view.setGenMode(false);
        });
        
        maze[0][0].visited=true;
        repaint();
        
        toUpdate = map.get(maze[0][0]);
        
        timer = new Timer(settings.getFrameDelay(), (ActionEvent e) -> {  
            update(solver.step());
        });
        
        timer.start();
    }
    
    private void update(Cell source) {
        toUpdate.repaint();
        toUpdate = map.get(source);
        if (source == null) 
            timer.stop();
        else
            toUpdate.repaint();
    }
}