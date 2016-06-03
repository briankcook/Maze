package maze;

import java.awt.Point;

public abstract class Compass {
    public static final Direction NORTH = new Direction( 0, -1);
    public static final Direction SOUTH = new Direction( 0,  1);
    public static final Direction EAST  = new Direction( 1,  0);
    public static final Direction WEST  = new Direction(-1,  0);
    public static final Direction[] DIRECTIONS = new Direction[]{NORTH, EAST, SOUTH, WEST};
    
    public static Direction turn(Point facing, int amount) {
        int index = 0;
        for (int i = 0 ; i < DIRECTIONS.length ; i++)
            if (facing.equals(DIRECTIONS[i]))
                index = (i+amount) % DIRECTIONS.length;
        return DIRECTIONS[index];
    }
    
    public static Direction turnRight(Direction facing) {
        return turn(facing, 1);
    }
    
    public static Direction turnAround(Direction facing) {
        return turn(facing, 2);
    }
    
    public static Direction turnLeft(Direction facing) {
        return turn(facing, 3);
    }
    
    public static class Direction extends Point {
        public Direction(int x, int y) {
            super(x, y);
        }
    }
}