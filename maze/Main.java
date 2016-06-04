package maze;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    
    private Main(){}
    
    public static void main (String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame view = new JFrame("Maze Maker");
            MazeMaker mazemaker = new MazeMaker();
            mazemaker.init();
            view.add(mazemaker);
            view.pack();
            view.setVisible(true);
            view.setLocationRelativeTo(null);
            view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
