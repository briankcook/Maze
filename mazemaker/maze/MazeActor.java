package mazemaker.maze;

import java.util.ArrayList;
import java.util.List;

public abstract class MazeActor {
    /*
    performs initialization functions not performed by the constructor
    */
    public abstract void init();
    
    /*
    returns an array of update data (Datum objects)
    */
    protected abstract Datum[] step();
    
    /*
    returns a List of arrays of update data (Datum objects) in the order
        in which they were updated.  Traverse this list from first-to-last.
    */
    public List<Datum[]> run() {
        List steps = new ArrayList();
        Datum[] step = step();
        while (step.length > 0) {
            steps.add(step);
            step = step();
        }
        return steps;
    }
}
