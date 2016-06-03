package maze;

import javax.swing.JFrame;

public class MazeWindow extends JFrame{
    
    private final MazeView mazeview;
    
    public MazeWindow(MazeView mazeview) {
        super();
        this.mazeview = mazeview;
    }
    
    public void init() {
        add(mazeview);
        pack();
        setTitle("Maze Viewer");
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public MazeView getMazeView() {
        return mazeview;
    }
}