package mazemaker.maze.generators;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import mazemaker.maze.*;

public class Kruskal extends MazeActor{
    
    private final Maze maze;
    
    private int[][] setTags;
    private ArrayList<Edge> edges;
    private int sets;
    
    public Kruskal(Maze maze) {
        this.maze = maze;
    }
    
    @Override
    public void init() {
        setTags = new int[maze.width][maze.height];
        edges = new ArrayList();
        for (int i = 0 ; i < maze.width ; i++) {
            for (int j = 0 ; j < maze.height ; j++) {
                setTags[i][j] = j + i * maze.width;
                if (i + 1 < maze.width)
                    edges.add(new Edge(i, j, i+1, j));
                if (j + 1 < maze.height)
                    edges.add(new Edge(i, j, i, j+1));
            }
        }
        sets = maze.width * maze.height;
        Collections.shuffle(edges);
    }
    
    @Override
    protected Datum[] step() {
        if (sets == 1)
            return new Datum[]{};
        Edge edge;
        int set1;
        int set2;
        
        do {
            edge = edges.remove(0);
            set1 = setTags[edge.x1][edge.y1];
            set2 = setTags[edge.x2][edge.y2];
        } while (set1 == set2);
        
        Point a = new Point(edge.x1, edge.y1);
        Point b = new Point(edge.x2, edge.y2);
        
        sets--;
        maze.toggleConnection(a, b);
        for (int i = 0 ; i < maze.width ; i++) 
            for (int j = 0 ; j < maze.height ; j++) 
                if (setTags[i][j] == set2)
                    setTags[i][j] = set1;
        
        return new Datum[]{maze.datum(a.x, a.y,null), 
                           maze.datum(b.x, b.y,null)};
    }

    private class Edge {
        
        int x1;
        int y1;
        int x2;
        int y2;

        public Edge(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
    }

}