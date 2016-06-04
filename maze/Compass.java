package maze;

public abstract class Compass {
    public static final int RIGHT = 1;
    public static final int AROUND = 2;
    public static final int LEFT = 3;
    public static final Direction NORTH = new Direction( 0, -1);
    public static final Direction SOUTH = new Direction( 0,  1);
    public static final Direction EAST  = new Direction( 1,  0);
    public static final Direction WEST  = new Direction(-1,  0);
    public static final Direction[] DIRECTIONS = new Direction[]{NORTH, EAST, SOUTH, WEST};
    
    public static Direction turn(Direction facing, int way) {
        int index = 0;
        for (int i = 0 ; i < DIRECTIONS.length ; i++)
            if (facing.equals(DIRECTIONS[i]))
                index = (i+way) % DIRECTIONS.length;
        return DIRECTIONS[index];
    }
}