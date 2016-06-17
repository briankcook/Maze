package mazemaker.maze;

import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;

public abstract class MazeActor extends Task<List<Datum[]>> {
    
    private final String name;
    
    public MazeActor(String name) {
        this.name = name;
    }
    /*
    performs initialization functions not performed by the constructor
    */
    protected abstract void init();
    
    /*
    returns an array of update data (Datum objects)
    */
    protected abstract Datum[] step();
    
    /*
    returns a List of arrays of update data (Datum objects) in the order
        in which they were updated.  Traverse this list from first-to-last.
    */
    @Override
    public List<Datum[]> call() {
        init();
        List steps = new ArrayList();
        Datum[] step = step();
        while (step.length > 0) {
            if (isCancelled())
                break;
            steps.add(step);
            step = step();
            if (steps.size() % 100 == 0)
                updateMessage(name + " : " + steps.size() + " steps");
        }
        updateMessage(name + " : " + steps.size() + " steps");
        return steps;
    }
    
    public String getName() {
        return name;
    }
}
