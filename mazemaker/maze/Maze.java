package mazemaker.maze;

import java.awt.Point;

public class Maze {
    
    public static final int RIGHT = 1;
    public static final int AROUND = 2;
    public static final int LEFT = 3;
    
    public static final Direction NORTH = new Direction( 0, -1, 0b00000001);
    public static final Direction SOUTH = new Direction( 0,  1, 0b00000010);
    public static final Direction EAST  = new Direction( 1,  0, 0b00000100);
    public static final Direction WEST  = new Direction(-1,  0, 0b00001000);
    
    private static final Direction[] DIRECTIONS = {NORTH, EAST, SOUTH, WEST};
    
    public final int width;
    public final int height;
    
    private byte[][] cellData;
    private Point goal;
    
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        cellData = new byte[width][height];
        goal = new Point(width-1, height-1);
    }

    public static Direction[] getDIRECTIONS() {
        return DIRECTIONS;
    }
    
    public static Direction turn(Direction facing, int way) {
        int index = 0;
        for (int i = 0 ; i < DIRECTIONS.length ; i++)
            if (facing.equals(DIRECTIONS[i]))
                index = (i+way) % DIRECTIONS.length;
        return DIRECTIONS[index];
    }
    
    public void reset() {
        cellData = new byte[width][height];
    }
    
    public byte[][] getCellData() {
        return cellData;
    }
    
    public byte getCellData(int x, int y) {
        return cellData[x][y];
    }
    
    public void setCell(int x, int y, byte data) {
        cellData[x][y] = data;
    }
    
    public void setGoal(Point cell) {
        goal = cell;
    }
    
    public Point getGoal() {
        return goal;
    }
    
    public boolean isGoal(int x, int y) {
        return x == goal.x && y == goal.y;
    }
    
    public boolean canGo(int x, int y, Direction direction) {
        return isValid(x + direction.x, y + direction.y) &&
               (cellData[x][y] & direction.mask) > 0;
    }
    
    public boolean isValid(int x, int y) {
        return x >= 0 && x < width &&
               y >= 0 && y < height;
    }
    
    public Point look(int x, int y, Direction direction) {
        return new Point(x + direction.x, y + direction.y);
    }
    
    public void toggleConnection(Point a, Point b) {
        for (Direction direction : DIRECTIONS) {
            if (look(a.x, a.y, direction).equals(b)) {
                cellData[a.x][a.y] ^= direction.mask;
                cellData[b.x][b.y] ^= turn(direction, AROUND).mask;
            }
        }
    }
}
