package maze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.HashMap;
import javax.swing.JComponent;

public class CellView extends JComponent {
    
    private final MazeView mazeview;
    private final Cell cell;
    private final int wall;
    private final int pane;
    private final HashMap<Direction,Polygon> pointers;
    
    public CellView(MazeView mazeview, Cell cell, int cellSize, int wallThickness) {
        super();
        this.mazeview = mazeview;
        this.cell = cell;
        pane = cellSize;
        wall = wallThickness;
        pointers = new HashMap();
        
        // undocumented triangle magic, just go with it
        int[] slm = new int[]{     wall*2, pane-wall*2,      pane/2};
        int[] ssl = new int[]{     wall*2,      wall*2, pane-wall*2};
        int[] lls = new int[]{pane-wall*2, pane-wall*2,      wall*2};
        pointers.put(Compass.NORTH, new Polygon(slm, lls, 3));
        pointers.put(Compass.SOUTH, new Polygon(slm, ssl, 3));
        pointers.put( Compass.EAST, new Polygon(ssl, slm, 3));
        pointers.put( Compass.WEST, new Polygon(lls, slm, 3));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cell.isVisited()) {
            g.setColor(Color.pink);
            g.fillRect(0, 0, pane, pane);
        }
        if (cell.isGoal()) {
            g.setColor(Color.GRAY);
            g.fillOval(wall*2, wall*2, pane-wall*4, pane-wall*4);
        }
        if (cell.isOnPath()) {
            g.setColor(Color.BLACK);
            g.fillOval(pane/2-2, pane/2-2, 4, 4);
        }
        if (cell.isMaking()) {
            g.setColor(Color.BLUE);
            g.fillRect(wall*2, wall*2, pane-wall*4, pane-wall*4);
        }
        if (cell.isSolving()) {
            g.setColor(Color.RED);
            g.fillPolygon(pointers.get(cell.getFacing()));
        }
        if (mazeview.getShowUnseen() || cell.isVisited()) {
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