package maze;

import javax.swing.JFrame;

public class Main {
    
    public static void main (String[] args) {
        MazeMaker mazemaker = new MazeMaker();
        mazemaker.init();
        JFrame view = new JFrame();
        view.add(mazemaker);
        view.pack();
        view.setTitle("Maze Maker");
        view.setVisible(true);
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
