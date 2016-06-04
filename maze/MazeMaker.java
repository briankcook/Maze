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
import pagelayout.EasyCell;

public class MazeMaker extends JPanel {
    
    private final JFormattedTextField rows;
    private final JFormattedTextField cols;
    private final JFormattedTextField cellsize;
    private final JFormattedTextField wallsize;
    private final JFormattedTextField framedelay;
    
    private final JSlider slider;
    private final JButton makebutton;
    private final JButton genbutton;
    private final JButton solvebutton;
    private final JCheckBox makershow;
    private final JCheckBox seeAll;
    
    private JFrame mazewindow;
    private MazeView mazeview;

    public MazeMaker() {
        super();
        NumberFormatter validator = new NumberFormatter(NumberFormat.getIntegerInstance());
        validator.setMinimum(0);
        validator.setMaximum(1000);
        validator.setAllowsInvalid(false);
        validator.setValueClass(Integer.class);
        rows = new JFormattedTextField(validator);
        cols = new JFormattedTextField(validator);
        cellsize = new JFormattedTextField(validator);
        wallsize = new JFormattedTextField(validator);
        framedelay = new JFormattedTextField(validator);
        slider = new JSlider(1, 5, 3);
        makebutton = new JButton(new MazeAction("Initialize Maze", MazeAction.ACTION_INITIALIZE));
        genbutton = new JButton(new MazeAction("Generate Maze", MazeAction.ACTION_GENERATE));
        solvebutton = new JButton(new MazeAction("Solve Maze", MazeAction.ACTION_SOLVE));
        makershow = new JCheckBox("Show generation");
        seeAll = new JCheckBox("Show unvisited cells");
    }
    
    public void init() {
        
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setSnapToTicks(true);
        
        rows.setValue(10);
        cols.setValue(10);
        cellsize.setValue(30);
        wallsize.setValue(2);
        framedelay.setValue(100);
        
        makershow.setSelected(true);
        seeAll.setSelected(true);
        
        CellGrid textfields = EasyCell.grid(new JLabel("Maze Rows:"),           rows,           EasyCell.eol(), 
                                            new JLabel("Maze Columns:"),        cols,           EasyCell.eol(), 
                                            new JLabel("Cell size (px):"),      cellsize,       EasyCell.eol(), 
                                            new JLabel("Walls (px):"),          wallsize,       EasyCell.eol(), 
                                            new JLabel("Frame delay (ms):"),    framedelay);

        Column menu = EasyCell.column(textfields,  
                                      new JLabel("Horizontalness"),  
                                      slider,  
                                      makershow,  
                                      makebutton,
                                      genbutton,
                                      solvebutton,
                                      seeAll);
        
        menu.createLayout(this);
    }
    
    private class MazeAction extends AbstractAction {
    
        public static final int ACTION_INITIALIZE = 1;
        public static final int ACTION_GENERATE = 2;
        public static final int ACTION_SOLVE = 3;
        
        private final int action;
        
        public MazeAction(String name, int action) {
            super(name);
            this.action = action;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (action == ACTION_INITIALIZE) {
                if (mazewindow != null)
                    mazewindow.dispose();
                
                mazewindow = new JFrame();
                mazeview = new MazeView(new Maze(getMazeWidth(), getMazeHeight()),
                                        getCellSize(),
                                        getWallThickness(),
                                        getFrameDelay(),
                                        getShowUnseen());
                mazeview.init();
                mazewindow.add(mazeview);
                mazewindow.pack();
                mazewindow.setTitle("Maze Viewer");
                mazewindow.setVisible(true);
                mazewindow.setLocationRelativeTo(null);
                mazewindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            } else {
                if (mazewindow == null || !mazewindow.isShowing()) 
                    return;
                
                MazeActor actor = null;
                if (action == ACTION_GENERATE) {
                    actor = new BackstepGenerator(mazeview.getMaze(), 
                                                  getHWeight(), 
                                                  getVWeight(),
                                                  getShowGeneration());
                } else if (action == ACTION_SOLVE) {
                    actor = new RightHandSolver(mazeview.getMaze());
                }
                mazeview.setShowUnseen(getShowUnseen());
                mazeview.setFrameDelay(getFrameDelay());
                mazeview.runActor(actor);
            }
        }
    
        private int getMazeHeight() {
            return (int)rows.getValue();
        }

        private int getMazeWidth() {
            return (int)cols.getValue();
        }

        private int getCellSize() {
            return (int)cellsize.getValue();
        }

        private int getWallThickness() {
            return (int)wallsize.getValue();
        }

        private int getFrameDelay() {
            return (int)framedelay.getValue();
        }

        private int getHWeight() {
            return slider.getValue();
        }

        private int getVWeight() {
            return slider.getMaximum() + 1 - slider.getValue();
        }

        private boolean getShowUnseen() {
            return seeAll.isSelected();
        }

        private boolean getShowGeneration() {
            return makershow.isSelected();
        }
    }
}