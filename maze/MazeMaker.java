package maze;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.net.URL;
import java.text.NumberFormat;
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
        
    private static final String BACKSTEP = "backstep";
    private static final String RIGHTHAND = "righthand";
    private static final String LEFTHAND = "lefthand";
    private static final String RANDOMTURNS = "random";
    
    private final JToolBar toolBar;
    private final ToolBarListener toolBarListener;
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
        toolBarListener = new ToolBarListener();
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
        content.setPreferredSize(new Dimension(800, 600));
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
                                      ToolBarListener.SETTINGS));
        
        toolBar.add(makeToolBarButton("clear.png",
                                      "Clear Maze",
                                      ToolBarListener.CLEAR));
        
        toolBar.addSeparator();
        
        toolBar.add(generatorComboBox);
        
        toolBar.add(makeToolBarButton("generate.png",
                                      "Generate Maze",
                                      ToolBarListener.GENERATE));
        
        toolBar.addSeparator();
        
        toolBar.add(solverComboBox);
        
        toolBar.add(makeToolBarButton("solve.png",
                                      "Start Solver",
                                      ToolBarListener.SOLVE));
        
        toolBar.addSeparator();
        
        toolBar.add(makeToolBarButton("pause.png",
                                      "Pause Animation",
                                      ToolBarListener.PAUSE));
        
        toolBar.add(makeToolBarButton("start.png",
                                      "Resume Animation",
                                      ToolBarListener.PLAY));
        
        toolBar.add(makeToolBarButton("stop.png",
                                      "Stop",
                                      ToolBarListener.STOP));
        
        toolBar.add(makeToolBarButton("record.png",
                                      "Record to GIF",
                                      ToolBarListener.RECORD));
        
        toolBar.addSeparator();
        
        toolBar.add(makeToolBarButton("decrease.png",
                                      "Slow Down Animation",
                                      ToolBarListener.SLOWER));
        
        toolBar.add(makeToolBarButton("increase.png",
                                      "Speed Up Animation",
                                      ToolBarListener.FASTER));
        
        generatorComboBox.addItem(BACKSTEP);
        solverComboBox.addItem(RIGHTHAND);
        solverComboBox.addItem(LEFTHAND);
        solverComboBox.addItem(RANDOMTURNS);
    }
    
    private JButton makeToolBarButton(String imageName,
                                      String toolTip,
                                      String actionCommand) {
        URL imageURL = MazeMaker.class.getResource("resources/" + imageName);
        JButton button = new JButton(new ImageIcon(imageURL));
        button.setBorderPainted(false);
        button.setToolTipText(toolTip);
        button.addActionListener(toolBarListener);
        button.setActionCommand(actionCommand);
        button.setFocusPainted(false);
        return button;
    }
        
    private void buildAndShowSettingsDialog() {
        JDialog dialog = new JDialog((JFrame)this.getTopLevelAncestor(),
                                     "Settings", 
                                     true);
        dialog.setContentPane(settingsPanel);
        dialog.pack();
        dialog.setLocation(content.getLocationOnScreen());
        dialog.setVisible(true);
    }
    
    private class ToolBarListener implements ActionListener, Serializable {
        private static final String SETTINGS = "settings";
        private static final String CLEAR = "clear";
        private static final String GENERATE = "generate";
        private static final String SOLVE = "solve";
        private static final String PAUSE = "pause";
        private static final String PLAY = "play";
        private static final String STOP = "stop";
        private static final String RECORD = "record";
        private static final String FASTER = "faster";
        private static final String SLOWER = "slower";
        
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case SETTINGS: 
                    buildAndShowSettingsDialog();
                    break;
                case CLEAR: 
                    mazeview.getMaze().reset();
                    break;
                case GENERATE: 
                    newMaze();
                    break;
                case SOLVE: 
                    mazeview.runActor(getSolver());
                    break;
                case PAUSE: 
                    break;
                case PLAY: 
                    break;
                case STOP: 
                    break;
                case RECORD: 
                    break;
                case FASTER: 
                    break;
                case SLOWER: 
                    break;
                default:
                    break;
            }
        }
    
        private void newMaze() {
            mazeview = new MazeView(new Maze(getMazeWidth(),
                                             getMazeHeight()),
                                    getCellSize(),
                                    getWallThickness(),
                                    getFrameDelay(),
                                    getShowUnseen());
            mazeview.init();
            content.setViewportView(wrap(mazeview));
            content.revalidate();
            mazeview.runActor(getGenerator());
        }
    
        private JPanel wrap(JComponent component) {
            JPanel wrapper = new JPanel();
            wrapper.setLayout(new GridBagLayout());
            wrapper.add(component);
            return wrapper;
        }
        
        private MazeActor getGenerator() {
            switch ((String)generatorComboBox.getSelectedItem()) {
                case BACKSTEP:
                    return new BackstepGenerator(mazeview.getMaze(),
                                                 getHWeight(),
                                                 getVWeight(),
                                                 getShowGeneration());
                default:
                    return null;
            }
        }
        
        private MazeActor getSolver() {
            switch ((String)solverComboBox.getSelectedItem()) {
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
}