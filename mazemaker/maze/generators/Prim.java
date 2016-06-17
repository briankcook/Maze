package mazemaker.maze.generators;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import mazemaker.maze.*;

public class Prim extends MazeActor {

    private final Maze maze;
    private final ArrayList<Edge> edges;
    private final Random r;
    private final boolean[][] visited;
    
    public Prim(String name, Maze maze) {
        super(name);
        this.maze = maze;
        visited = new boolean[maze.width][maze.height];
        r = new Random();
        edges = new ArrayList();
    }
    
    @Override
    public void init() {
        Point start = maze.getStart();
        for (Direction direction : Maze.getDirections())
            if (maze.isValid(start.x + direction.x, start.y + direction.y))
                edges.add(new Edge(start, maze.look(start.x, start.y, direction)));
        visited[start.x][start.y] = true;
    }
    
    @Override
    protected Datum[] step() {
        Edge edge;
        do {
            if (edges.isEmpty())
                return new Datum[]{};
            edge = edges.remove(r.nextInt(edges.size()));
        } while (visited[edge.b.x][edge.b.y]);
        maze.toggleConnection(edge.a, edge.b);
        
        for (Direction direction : Maze.getDirections())
            if (maze.isValid(edge.b.x + direction.x, edge.b.y + direction.y))
                edges.add(new Edge(edge.b, new Point(edge.b.x + direction.x, edge.b.y + direction.y)));
        
        visited[edge.b.x][edge.b.y] = true;
        
        return new Datum[] {maze.datum(edge.a.x, edge.a.y, null), 
                            maze.datum(edge.b.x, edge.b.y, null)};
    }

    private class Edge {
        
        Point a;
        Point b;

        public Edge(Point a, Point b) {
            this.a = a;
            this.b = b;
        }
        
    }

}