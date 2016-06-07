package mazemaker;

import mazemaker.io.*;
import mazemaker.maze.*;
import mazemaker.maze.solvers.*;
import mazemaker.maze.generators.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.text.NumberFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.NumberFormatter;

public class MazeMaker extends JPanel {
    
    private static final Dimension DEFAULT_SCREEN_SIZE = new Dimension(800, 600);
    private static final Insets INSETS = new Insets(3, 3, 3, 3);
        
    private static final String BACKSTEP = "Backtrace";
    
    private static final String RIGHTHAND = "Right Hand Rule";
    private static final String LEFTHAND = "Left hand Rule";
    private static final String RANDOMTURNS = "Random Turns";
    
    private final JMenuBar menubar;
    private final JToolBar toolBar;
    private final JScrollPane sidebar;
    private final JPanel sidebarpanel;
    private final JComboBox generatorComboBox;
    private final JComboBox solverComboBox;
    private final JPanel settingsPanel;
    private final JScrollPane content;
    
    private final JLabel about;
    private final JLabel help;
    
    private final JFormattedTextField rows;
    private final JFormattedTextField cols;
    private final JFormattedTextField cellsize;
    private final JFormattedTextField wallsize;
    private final JFormattedTextField framedelay;
    
    private final JSlider slider;
    private final JCheckBox makershow;
    private final JCheckBox seeAll;

    private final Action _new = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newMaze();
            }
        };

    private final Action _save = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MazeIO.saveMaze(mazeview.getMaze());
            }
        };

    private final Action _open = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMaze(MazeIO.openMaze());
            }
        };

    private final Action _generate = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.runActor(getGenerator(), getShowGeneration(), getShowUnseen(), true);
            }
        };

    private final Action _solve = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.runActor(getSolver(), true, getShowUnseen(), false);
            }
        };

    private final Action _pause = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.pause();
            }
        };

    private final Action _play = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.resume();
            }
        };

    private final Action _stop = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.stop();
            }
        };

    private final Action _record = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.record();
            }
        };

    private final Action _slower = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.slowDown();
            }
        };

    private final Action _faster = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.speedUp();
            }
        };

    private final Action _clear = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeview.cleanUp();
            }
        };

    private final Action _about = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, about);
            }
        };

    private final Action _help = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, help);
            }
        };
    
    private transient MazeView mazeview;

    public MazeMaker(JMenuBar menuBar) {
        super();
        this.menubar = menuBar;
        toolBar = new JToolBar();
        sidebar = new JScrollPane();
        sidebarpanel = new JPanel(new BorderLayout());
        generatorComboBox = new JComboBox(new String[]{BACKSTEP});
        solverComboBox = new JComboBox(new String[]{RIGHTHAND, LEFTHAND, RANDOMTURNS});
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
        
        about = new JLabel("<html>MazeMaker v0.3 by briankcook<br />"
                         + "<a href='https://github.com/briankcook/Maze'>"
                         + "https://github.com/briankcook/Maze </a> <br />"
                         + "<br />"
                         + "Icons courtesy Tango Desktop Project <br />"
                         + "<a href='http://tango.freedesktop.org/'>"
                         + "http://tango.freedesktop.org/</a>");
        help = new JLabel();
    }
    
    public void init() {
        initMenu();
        initSidebar();
        initToolBar();
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        content.setPreferredSize(DEFAULT_SCREEN_SIZE);
        newMaze();
    }
    
    private void initMenu() {
        JMenu filemenu = new JMenu("File");
        JMenu mazemenu = new JMenu("Maze");
        JMenu playmenu = new JMenu("Playback");
        JMenu helpmenu = new JMenu("Help");
        
        menubar.add(filemenu);
        menubar.add(mazemenu);
        menubar.add(playmenu);
        menubar.add(helpmenu);
        
        addMenuItem(filemenu, "New",      KeyEvent.VK_N,      _new);
        addMenuItem(filemenu, "Open",     KeyEvent.VK_O,      _open);
        addMenuItem(filemenu, "Save",     KeyEvent.VK_S,      _save);
             
        addMenuItem(mazemenu, "Generate", KeyEvent.VK_G,      _generate);
        addMenuItem(mazemenu, "Solve",    KeyEvent.VK_H,      _solve);
        addMenuItem(mazemenu, "Clean Up", KeyEvent.VK_C,      _clear);
             
        addMenuItem(playmenu, "Pause",    KeyEvent.VK_P,      _pause);
        addMenuItem(playmenu, "Play",     KeyEvent.VK_Q,      _play);
        addMenuItem(playmenu, "Stop",     KeyEvent.VK_X,      _stop);
        addMenuItem(playmenu, "Record",   KeyEvent.VK_R,      _record);
        addMenuItem(playmenu, "Slower",   KeyEvent.VK_MINUS,  _slower);
        addMenuItem(playmenu, "Faster",   KeyEvent.VK_EQUALS, _faster);
        
        addMenuItem(helpmenu, "Help",     0,                  _help);
        addMenuItem(helpmenu, "About",    0,                  _about);
    }
    
    private void addMenuItem(JMenu menu, String label, int key, Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setText(label);
        if (key != 0)
            item.setAccelerator(KeyStroke.getKeyStroke(key, KeyEvent.CTRL_DOWN_MASK));
        menu.add(item);
    }
    
    private void initSidebar() {
        
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
        addSettingsItem(new JLabel("Generation Algorithm", JLabel.CENTER), 
                        2, 7, 0);
        addSettingsItem(generatorComboBox, 
                        2, 8, 0);
        addSettingsItem(new JLabel("Solving Algorithm", JLabel.CENTER), 
                        2, 9, 0);
        addSettingsItem(solverComboBox, 
                        2, 10, 0);
        addSettingsItem(makershow, 
                        2, 11, 0);
        addSettingsItem(seeAll, 
                        2, 12, 0);
        
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        sidebarpanel.add(settingsPanel, BorderLayout.NORTH);
        sidebarpanel.add(new JPanel(), BorderLayout.CENTER);
        sidebar.setViewportView(sidebarpanel);
        sidebar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
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
        
        addToolBarButton(toolBar, "new.png",  "New Blank Maze", _new);
        addToolBarButton(toolBar, "save.png", "Save Maze",      _save);
        addToolBarButton(toolBar, "open.png", "Open Maze",      _open);
        
        toolBar.addSeparator();
        
        addToolBarButton(toolBar, "generate.png", "Generate Maze", _generate);
        
        addToolBarButton(toolBar, "solve.png", "Start Solver", _solve);
        
        toolBar.addSeparator();
        
        addToolBarButton(toolBar, "pause.png",  "Pause Animation",  _pause);
        addToolBarButton(toolBar, "start.png",  "Resume Animation", _play);
        addToolBarButton(toolBar, "stop.png",   "Stop Animation",   _stop);
        addToolBarButton(toolBar, "record.png", "Record to GIF",    _record);
        
        toolBar.addSeparator();
        
        addToolBarButton(toolBar, "decrease.png", "Slow Down Animation", _slower);
        addToolBarButton(toolBar, "increase.png", "Speed Up Animation",  _faster);
        
        toolBar.addSeparator();
        
        addToolBarButton(toolBar, "clear.png", "Clear Maze", _clear);
    }
    
    private void addToolBarButton(Container container, String imageName, String toolTip, Action action) {
        URL imageURL = MazeMaker.class.getResource("resources/" + imageName);
        JButton button = new JButton(action);
        button.setIcon(new ImageIcon(imageURL));
        button.setToolTipText(toolTip);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        container.add(button);
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
        switch ((String)generatorComboBox.getSelectedItem()) {
            case BACKSTEP:
                return new BackstepGenerator(mazeview.getMaze(),
                                             getHWeight(),
                                             getVWeight());
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