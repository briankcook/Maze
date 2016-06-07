package mazemaker;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

public class Main {
    
    private Main(){}
    
    public static void main (String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame view = new JFrame("Maze Maker");
            JMenuBar menubar = new JMenuBar();
            MazeMaker mazemaker = new MazeMaker(menubar);
            mazemaker.init();
            view.setJMenuBar(menubar);
            view.add(mazemaker);
            view.pack();
            view.setVisible(true);
            view.setLocationRelativeTo(null);
            view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            view.setMinimumSize(new Dimension(400, 300));
        });
    }
}
