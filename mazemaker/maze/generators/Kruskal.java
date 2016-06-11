package mazemaker.maze.generators;

import java.util.ArrayList;
import java.util.Collections;
import mazemaker.maze.*;

public class Kruskal implements MazeActor{
    
    private final Maze maze;
    
    private int[][] setTags;
    private ArrayList<Edge> edges;
    private MazeActorData prev1;
    private MazeActorData prev2;
    
    public Kruskal(Maze maze) {
        this.maze = maze;
    }
    
    @Override
    public void init() {
        setTags = new int[maze.width][maze.height];
        edges = new ArrayList();
        for (int i = 0 ; i < maze.width ; i++) {
            for (int j = 0 ; j < maze.width ; j++) {
                setTags[i][j] = j + i * maze.width;
                if (i + 1 < maze.width)
                    edges.add(new Edge(i, j, i+1, j));
                if (j + 1 < maze.width)
                    edges.add(new Edge(i, j, i, j+1));
            }
        }
        Collections.shuffle(edges);
        prev1 = new MazeActorData(0, 0, null);
        prev2 = new MazeActorData(0, 0, null);
    }
    
    @Override
    public MazeActorData[] step() {
        if (edges.isEmpty())
            return null;
        Edge edge = edges.remove(0);
        
        MazeActorData update1 = prev1;
        MazeActorData update2 = prev2;
        
        update1.facing = null;
        update2.facing = null;
        
        prev1 = new MazeActorData(edge.x1, edge.y1, null);
        prev2 = new MazeActorData(edge.x2, edge.y2, null);
        
        int set1 = setTags[edge.x1][edge.y1];
        int set2 = setTags[edge.x2][edge.y2];
        
        if (set1 != set2) {
            maze.toggleConnection(prev1, prev2);
            for (int i = 0 ; i < maze.width ; i++) 
                for (int j = 0 ; j < maze.width ; j++) 
                    if (setTags[i][j] == set2)
                        setTags[i][j] = set1;
        }
        
        Direction facing = new Direction(edge.x2-edge.x1, edge.y2-edge.y1, 0);
        for (Direction direction : Maze.getDirections()) {
            if (facing.x == direction.x && facing.y == direction.y) {
                prev1.facing = direction;
                prev2.facing = Maze.turn(direction, Maze.AROUND);
            }
        }
        
        return new MazeActorData[]{prev1, prev2, update1, update2};
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