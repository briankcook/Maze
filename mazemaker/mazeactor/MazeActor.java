package mazemaker.mazeactor;

import mazemaker.maze.Cell;

public interface MazeActor {
    public void init();
    public Cell step();
}
