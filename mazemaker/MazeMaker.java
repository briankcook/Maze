package mazemaker;

import mazemaker.io.*;
import mazemaker.maze.*;
import mazemaker.maze.solvers.*;
import mazemaker.maze.generators.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

public class MazeMaker extends JPanel {
    
    private static final Dimension DEFAULT_SCREEN_SIZE = new Dimension(800, 600);
    private static final Insets INSETS = new Insets(3, 3, 3, 3);
        
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
    
    private transient MazeView mazeview;

    public MazeMaker() {
        super();
        toolBar = new JToolBar();
        generatorComboBox = new JComboBox();
        solverComboBox = new JComboBox();
        content = new JScrollPane();
        settingsPanel = new JPanel(new GridBagLayout());
        
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
        
        addSettingsItem(new JLabel("Maze Rows:"),
                        1, 0, 0);
        addSettingsItem(rows,
                        1, 0, 1);
        addSettingsItem(new JLabel("Maze Columns:"),
                        1, 1, 0);
        addSettingsItem(cols,
                        1, 1, 1);
        addSettingsItem(new JLabel("Cell size (px):"),
                        1, 2, 0);
        addSettingsItem(cellsize,
                        1, 2, 1);
        addSettingsItem(new JLabel("Walls (px):"),
                        1, 3, 0);
        addSettingsItem(wallsize,
                        1, 3, 1);
        addSettingsItem(new JLabel("Frame delay (ms):"), 
                        1, 4, 0);
        addSettingsItem(framedelay, 
                        1, 4, 1);
        addSettingsItem(new JLabel("Horizontalness", JLabel.CENTER), 
                        2, 5, 0);
        addSettingsItem(slider, 
                        2, 6, 0);
        addSettingsItem(makershow, 
                        2, 7, 0);
        addSettingsItem(seeAll, 
                        2, 8, 0);
    }
    
    private void addSettingsItem(JComponent component, int span, int row, int col) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = span;
        constraints.gridx = col;
        constraints.gridy = row;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = INSETS;
        settingsPanel.add(component, constraints);
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
        
        toolBar.add(makeToolBarButton("save.png",
                                      "Save Maze",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              MazeIO.saveMaze(mazeview.getMaze());
                                          }
                                      }));
        
        toolBar.add(makeToolBarButton("open.png",
                                      "Open Maze",
                                      new AbstractAction(){
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              setMaze(MazeIO.openMaze());
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
                                              mazeview.runActor(getGenerator(), getShowGeneration(), getShowUnseen(), true);
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
                                              mazeview.runActor(getSolver(), true, getShowUnseen(), false);
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
                                              mazeview.stopRecording();
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
        setMaze(new Maze(getMazeWidth(), getMazeHeight()));
    }

    private void setMaze(Maze maze) {
        if (mazeview != null)
            mazeview.stop();
        mazeview = new MazeView(maze,
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