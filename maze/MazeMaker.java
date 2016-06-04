package maze;

import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.text.NumberFormatter;
import pagelayout.CellGrid;
import pagelayout.Column;
import static pagelayout.EasyCell.eol;
import static pagelayout.EasyCell.grid;
import static pagelayout.EasyCell.column;

public class MazeMaker extends JFrame{
    
    private Column menu;
    private CellGrid textfields;
    
    private final JPanel menupanel;
    private final NumberFormatter validator;
    private final JFormattedTextField rows;
    private final JFormattedTextField cols;
    private final JFormattedTextField cellsize;
    private final JFormattedTextField wallsize;
    private final JFormattedTextField framedelay;
    private final JSlider slider;
    private final JCheckBox makershow;
    private final JButton makebutton;
    private final JButton genbutton;
    private final JButton solvebutton;
    private final JCheckBox seeAll;
    
    private MazeWindow mazewindow;
    private MazeSettings settings;

    public MazeMaker() {
        super();
        menupanel = new JPanel();
        validator = new NumberFormatter(NumberFormat.getIntegerInstance());
        rows = new JFormattedTextField(validator);
        cols = new JFormattedTextField(validator);
        cellsize = new JFormattedTextField(validator);
        wallsize = new JFormattedTextField(validator);
        framedelay = new JFormattedTextField(validator);
        slider = new JSlider(1, 5, 3);
        makershow = new JCheckBox("Show generation");
        makebutton = new JButton(new Make("Initialize Maze"));
        genbutton = new JButton(new Generate("Generate Maze"));
        solvebutton = new JButton(new Solve("Solve Maze"));
        seeAll = new JCheckBox("Show unvisited cells while solving");
    }
    
    public void init() {
        validator.setMinimum(0);
        validator.setMaximum(1000);
        validator.setAllowsInvalid(false);
        validator.setValueClass(Integer.class);
        
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setSnapToTicks(true);
        
        rows.setValue(10);
        cols.setValue(10);
        cellsize.setValue(30);
        wallsize.setValue(2);
        framedelay.setValue(100);
        
        makershow.setSelected(true);
        
        textfields = grid(new JLabel("Maze Rows:"),           rows,           eol(), 
                          new JLabel("Maze Columns:"),        cols,           eol(), 
                          new JLabel("Cell size (px):"),      cellsize,       eol(), 
                          new JLabel("Walls (px):"),          wallsize,       eol(), 
                          new JLabel("Frame delay (ms):"),    framedelay);

        menu = column(textfields,  
                      new JLabel("Horizontalness", JLabel.CENTER),  
                      slider,  
                      makershow,  
                      makebutton,
                      genbutton,
                      solvebutton,
                      seeAll);
        
        menu.createLayout(menupanel);
        add(menupanel);
        
        pack();
        setTitle("Maze Maker");
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void refreshSettings(boolean forceSeeAll) {
        settings = new MazeSettings((int)rows.getValue(),
                                    (int)cols.getValue(),
                                    (int)cellsize.getValue(),
                                    (int)wallsize.getValue(),
                                    (int)framedelay.getValue(),
                                    slider.getValue(),
                                    slider.getMaximum() + 1 - slider.getValue(),
                                    forceSeeAll || seeAll.isSelected(),
                                    makershow.isSelected());
    }
    
    
    
    private class Make extends AbstractAction {
        public Make(String name) {
            super(name);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mazewindow != null)
                mazewindow.dispose();
            refreshSettings(true);
            mazewindow = new MazeWindow(new Maze(settings));
            mazewindow.init();
        }
    }
    
    private class Generate extends AbstractAction {
        public Generate(String name) {
            super(name);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mazewindow == null || !mazewindow.isShowing()) 
                return;
            refreshSettings(true);
            mazewindow.getMazeView().runActor(settings, new BackstepGenerator(mazewindow.getMaze()));
        }
    }
    
    private class Solve extends AbstractAction {
        public Solve(String name) {
            super(name);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mazewindow == null || !mazewindow.isShowing()) 
                return;
            refreshSettings(false);
            mazewindow.getMazeView().runActor(settings, new RightHandSolver(mazewindow.getMaze()));
        }
    }
    
    public static void main (String[] args) {
        MazeMaker view = new MazeMaker();
        view.init();
    }
}