package maze;

import javax.swing.JFrame;

public class MazeWindow extends JFrame{
    
    private final Maze maze;
    private final MazeView mazeview;
    
    public MazeWindow(Maze maze, int cellSize, int wallThickness, int frameDelay, boolean showUnseen) {
        super();
        this.maze = maze;
        mazeview = new MazeView(maze, cellSize, wallThickness, frameDelay, showUnseen);
    }
    
    public void init() {
        mazeview.init();
        add(mazeview);
        pack();
        setTitle("Maze Viewer");
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public Maze getMaze() {
        return maze;
    }
    
    public MazeView getMazeView() {
        return mazeview;
    }
}