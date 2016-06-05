package maze;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.text.NumberFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.text.NumberFormatter;
import pagelayout.CellGrid;
import pagelayout.Column;
import pagelayout.EasyCell;

public class MazeMaker extends JPanel {
    
    private static final Dimension DEFAULT_SCREEN_SIZE = new Dimension(800, 600);
        
    private static final String GENERATOR_PREFIX = "<html>Generator:<br />";
    private static final String BACKSTEP = "Backtrace";
        
    private static final String SOLVER_PREFIX = "<html>Solver:<br />";
    private static final String RIGHTHAND = "Right Hand Rule";
    private static final String LEFTHAND = "Left hand Rule";
    private static final String RANDOMTURNS = "Random Turns";
    
    private final JToolBar toolBar;
    private final JComboBox generatorComboBox;
    private final JComboBox solverComboBox;
    private final JScrollPane content;
    private final JPanel settingsPanel;
    
    private final JFormattedTextField rows;
    private final JFormattedTextField cols;
    private final JFormattedTextField cellsize;
    private final JFormattedTextField wallsize;
    private final JFormattedTextField framedelay;
    
    private final JSlider slider;
    private final JCheckBox makershow;
    private final JCheckBox seeAll;
    
    private MazeView mazeview;

    public MazeMaker() {
        super();
        toolBar = new JToolBar();
        generatorComboBox = new JComboBox();
        solverComboBox = new JComboBox();
        content = new JScrollPane();
        settingsPanel = new JPanel();
        
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
        makershow = new JCheckBox("Show generation");
        seeAll = new JCheckBox("Show unvisited cells");
    }
    
    public void init() {
        initSettingsPanel();
        initToolBar();
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        content.setPreferredSize(DEFAULT_SCREEN_SIZE);
        newMaze();
    }
    
    private void initSettingsPanel() {
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
                                      seeAll);
        
        menu.createLayout(settingsPanel);
    }
    
    private void initToolBar() {        
        toolBar.add(makeToolBarButton("settings.png",
                                      "Toggle Settings Panel",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              buildAndShowSettingsDialog();
                                          }
                                      }));
        
        toolBar.add(makeToolBarButton("new.png",
                                      "New Blank Maze",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              newMaze();
                                          }
                                      }));
        
        toolBar.addSeparator();
        
        toolBar.add(initComboBox(generatorComboBox,
                                 GENERATOR_PREFIX,
                                 BACKSTEP));
        
        toolBar.add(makeToolBarButton("generate.png",
                                      "Generate Maze",
                                      true,
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.getMaze().reset(Maze.HARD_RESET);
                                              mazeview.setShowUnseen(getShowUnseen());
                                              mazeview.runActor(getGenerator(), getShowGeneration());
                                          }
                                      }));
        
        toolBar.addSeparator();
        
        toolBar.add(initComboBox(solverComboBox,
                                 SOLVER_PREFIX,
                                 RIGHTHAND,
                                 LEFTHAND,
                                 RANDOMTURNS));
        
        toolBar.add(makeToolBarButton("solve.png",
                                      "Start Solver",
                                      true,
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.stop();
                                              mazeview.setShowUnseen(getShowUnseen());
                                              mazeview.runActor(getSolver(), true);
                                          }
                                      }));
        
        toolBar.addSeparator();
        
        toolBar.add(makeToolBarButton("pause.png",
                                      "Pause Animation",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.pause();
                                          }
                                      }));
        
        toolBar.add(makeToolBarButton("start.png",
                                      "Resume Animation",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.resume();
                                          }
                                      }));
        
        toolBar.add(makeToolBarButton("stop.png",
                                      "Stop Animation",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.stop();
                                          }
                                      }));
        
        toolBar.add(makeToolBarButton("record.png",
                                      "Record to GIF",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.record();
                                          }
                                      }));
        
        toolBar.addSeparator();
        
        toolBar.add(makeToolBarButton("decrease.png",
                                      "Slow Down Animation",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.slowDown();
                                          }
                                      }));
        
        toolBar.add(makeToolBarButton("increase.png",
                                      "Speed Up Animation",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.speedUp();
                                          }
                                      }));
        
        toolBar.addSeparator();
        
        toolBar.add(makeToolBarButton("clear.png",
                                      "Clear Maze",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              mazeview.cleanUp();
                                          }
                                      }));
    }
    
    private JComboBox initComboBox(JComboBox comboBox,
                                   String prefix,
                                   String... options) {
        for (String option : options)
            comboBox.addItem(prefix + option);
        return comboBox;
    }
    
    private JButton makeToolBarButton(String imageName,
                                      String toolTip,
                                      Action action) {
        return makeToolBarButton(imageName, 
                                 toolTip, 
                                 false,
                                 action);
    }
    
    private JButton makeToolBarButton(String imageName,
                                      String toolTip,
                                      boolean showBorder,
                                      Action action) {
        URL imageURL = MazeMaker.class.getResource("resources/" + imageName);
        JButton button = new JButton(action);
        button.setIcon(new ImageIcon(imageURL));
        button.setToolTipText(toolTip);
        button.setBorderPainted(showBorder);
        button.setFocusPainted(false);
        return button;
    }
        
    private void buildAndShowSettingsDialog() {
        JDialog dialog = new JDialog((JFrame)getTopLevelAncestor(),
                                     "Settings", 
                                     true);
        dialog.setContentPane(settingsPanel);
        dialog.pack();
        dialog.setLocation(content.getLocationOnScreen());
        dialog.setVisible(true);
    }

    private void newMaze() {
        if (mazeview != null)
            mazeview.stop();
        mazeview = new MazeView(new Maze(getMazeWidth(),
                                         getMazeHeight()),
                                getCellSize(),
                                getWallThickness(),
                                getFrameDelay(),
                                getShowUnseen());
        mazeview.init();
        content.setViewportView(wrap(mazeview));
        content.revalidate();
    }

    private JPanel wrap(JComponent component) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new GridBagLayout());
        wrapper.add(component);
        return wrapper;
    }

    private MazeActor getGenerator() {
        switch (getSelectedActor(generatorComboBox)) {
            case BACKSTEP:
                return new BackstepGenerator(mazeview.getMaze(),
                                             getHWeight(),
                                             getVWeight());
            default:
                return null;
        }
    }

    private MazeActor getSolver() {
        switch (getSelectedActor(solverComboBox)) {
            case RIGHTHAND:
                return new WallFollower(mazeview.getMaze(), true);
            case LEFTHAND:
                return new WallFollower(mazeview.getMaze(), false);
            case RANDOMTURNS:
                return new RandomTurns(mazeview.getMaze());
            default:
                return null;
        }
    }
    
    private String getSelectedActor(JComboBox comboBox) {
        String s = (String)comboBox.getSelectedItem();
        return s.substring(s.indexOf("/>")+2);
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
        return slider.getMaximum() + slider.getMinimum() - slider.getValue();
    }

    private boolean getShowUnseen() {
        return seeAll.isSelected();
    }

    private boolean getShowGeneration() {
        return makershow.isSelected();
    }
}