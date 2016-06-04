package maze;

import java.io.Serializable;

public class Direction implements Serializable {
    public final int x;
    public final int y;

    public Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}