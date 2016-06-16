package mazemaker.maze;

import java.awt.Point;

public class Datum extends Point{
    public byte cellData;

    public Datum(int x, int y, byte cellData) {
        this.x = x;
        this.y = y;
        this.cellData = cellData;
    }
}
