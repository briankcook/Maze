package maze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.HashMap;
import javax.swing.JPanel;
import maze.Compass.Direction;

public class CellView extends JPanel {
    
    private final Cell cell;
    private final int wall;
    private final int pane;
    private boolean genMode = true;
    private final MazeSettings settings;
    private final HashMap<Direction,Polygon> pointers;
    
    public CellView(Cell cell, MazeSettings settings) {
        super();
        this.cell = cell;
        this.settings = settings;
        pane = settings.getCellSize();
        wall = settings.getWallThickness();
        pointers = new HashMap();
        
        // undocumented triangle magic, just go with it
        int[] SLM = new int[]{     wall*2, pane-wall*2,      pane/2},
              SSL = new int[]{     wall*2,      wall*2, pane-wall*2},
              LLS = new int[]{pane-wall*2, pane-wall*2,      wall*2};
        pointers.put(Compass.NORTH, new Polygon(SLM, LLS, 3));
        pointers.put(Compass.SOUTH, new Polygon(SLM, SSL, 3));
        pointers.put( Compass.EAST, new Polygon(SSL, SLM, 3));
        pointers.put( Compass.WEST, new Polygon(LLS, SLM, 3));
    }
    
    public void setGenMode(boolean mode) {
        genMode = mode;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cell.visited) {
            g.setColor(Color.pink);
            g.fillRect(0, 0, pane, pane);
        }
        if (cell.isGoal) {
            g.setColor(Color.GRAY);
            g.fillOval(wall*2, wall*2, pane-wall*4, pane-wall*4);
        }
        if (cell.onPath) {
            g.setColor(Color.BLACK);
            g.fillOval(pane/2-2, pane/2-2, 4, 4);
        }
        if (cell.making) {
            g.setColor(Color.BLUE);
            g.fillRect(wall*2, wall*2, pane-wall*4, pane-wall*4);
        }
        if (cell.solving) {
            g.setColor(Color.RED);
            g.fillPolygon(pointers.get(cell.facing));
        }
        if (genMode || settings.showUnseen() || cell.visited) {
            g.setColor(Color.BLACK);
            
            // fill corners
            g.fillRect(        0,         0, wall, wall);
            g.fillRect(        0, pane-wall, wall, wall);
            g.fillRect(pane-wall,         0, wall, wall);
            g.fillRect(pane-wall, pane-wall, wall, wall);
            
            // draw walls
            if (cell.neighbors.get(Compass.NORTH) == null)
                g.fillRect(        0,        0, pane, wall);
            if (cell.neighbors.get(Compass.SOUTH) == null)
                g.fillRect(        0,pane-wall, pane, wall);
            if (cell.neighbors.get(Compass.EAST) == null)
                g.fillRect(pane-wall,        0, wall, pane);
            if (cell.neighbors.get(Compass.WEST) == null)
                g.fillRect(        0,        0, wall, pane);
        }
    }
}