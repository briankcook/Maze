package mazemaker.maze;

import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;

public abstract class MazeTask extends Task<List<Datum[]>> {
    
    private final String name;
    
    public MazeTask(String name) {
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
                updateSteps(steps.size());
        }
        if (isCancelled())
            updateMessage("cancelled");
        else
            updateSteps(steps.size());
        return steps;
    }
    
    public String getName() {
        return name;
    }
    
    private void updateSteps(int steps) {
        updateMessage(name + " : " + steps + " steps");
    }
}
