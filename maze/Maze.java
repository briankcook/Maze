package maze;

import java.awt.Point;
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
import pagelayout.Row;
import static pagelayout.EasyCell.eol;
import static pagelayout.EasyCell.grid;
import static pagelayout.EasyCell.row;
import static pagelayout.EasyCell.column;

public class Maze extends JFrame{
    public static final Point NORTH = new Point( 0, -1);
    public static final Point SOUTH = new Point( 0,  1);
    public static final Point EAST  = new Point( 1,  0);
    public static final Point WEST  = new Point(-1,  0);
    public static final Point[] DIRECTIONS = new Point[]{NORTH, EAST, SOUTH, WEST};
    
    public static final Point turn(Point facing, int amount) {
        int index = 0;
        for (int i = 0 ; i < DIRECTIONS.length ; i++)
            if (facing.equals(DIRECTIONS[i]))
                index = (i+amount) % DIRECTIONS.length;
        return DIRECTIONS[index];
    }
    
    public static final Point turnRight(Point facing) {
        return turn(facing, 1);
    }
    
    public static final Point turnAround(Point direction) {
        return turn(direction, 2);
    }
    
    public static final Point turnLeft(Point facing) {
        return turn(facing, 3);
    }
    
    private Column menu;
    private CellGrid textfields;
    private Row buttons;
    
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
    private final JButton solvebutton;
    private final JCheckBox solveaware;
    
    private MazeWindow mazewindow;

    public Maze() {
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
        makebutton = new JButton(new Generate("Generate Maze"));
        solvebutton = new JButton(new Solve("Solve Maze"));
        solveaware = new JCheckBox("Show unvisited cells while solving");
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
        
        buttons = row(makebutton, solvebutton);

        menu = column(textfields,  
                      new JLabel("Horizontalness", JLabel.CENTER),  
                      slider,  
                      makershow,  
                      buttons,
                      solveaware);
        
        menu.createLayout(menupanel);
        add(menupanel);
        
        pack();
        setTitle("Maze Maker");
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    MazeSettings getSettings() {
        MazeSettings settings;
        settings = new MazeSettings((int)rows.getValue(),
                                    (int)cols.getValue(),
                                    (int)cellsize.getValue(),
                                    (int)wallsize.getValue(),
                                    (int)framedelay.getValue(),
                                    slider.getValue(),
                                    slider.getMaximum() + 1 - slider.getValue(),
                                    solveaware.isSelected(),
                                    makershow.isSelected());
        return settings;
    }
    
    class Generate extends AbstractAction {
        public Generate(String name) {
            super(name);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mazewindow != null)
                mazewindow.dispose();
            
            MazeSettings settings = getSettings();
            
            if (settings == null) 
                return;
            
            Maker maker = new Maker(settings);
            maker.init();

            MazeView mazeview = new MazeView(maker.maze, settings);
            mazeview.init(maker);

            mazewindow = new MazeWindow(mazeview);
            mazewindow.init();
            
            mazeview.start();
        }
    }
    
    class Solve extends AbstractAction {
        public Solve(String name) {
            super(name);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mazewindow == null || !mazewindow.isShowing()) 
                return;
            
            MazeSettings settings = getSettings();

            if (settings == null) 
                return;

            Solver solver = new Solver(mazewindow.getMazeView().getStartCell());

            mazewindow.getMazeView().solveMode(settings, solver);
        }
    }
    
    public static void main (String[] args) {
        Maze view = new Maze();
        view.init();
    }
}