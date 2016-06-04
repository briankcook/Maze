package maze;

public class MazeSettings {
    
    private final int mazeHeight;
    private final int mazeWidth;
    private final int cellSize;
    private final int wallThickness;
    private final int hweight;
    private final int vweight;
    private final boolean showGeneration;
    
    private int frameDelay;
    private boolean showUnseen;
    
    /**
     * All-or-nothing constructor for MazeSettings objects
     * @param mazeHeight        The number of rows in the maze.
     * @param mazeWidth         The number of columns in the maze.
     * @param cellSize          The width in pixels of the rows and columns.
     * @param wallThickness     The width in pixels of the maze walls.
     * @param frameDelay        The delay in ms between drawing frames during generation and solving.
     * @param hweight           The weight given to horizontal movement choices.
     * @param vweight           The weight given to vertical movement choices.
     * @param showUnseen        If true, cells which have not been visited will be rendered.
     * @param showGeneration    If true, Maze Viewer will show maze generation animation.
     */
    public MazeSettings(int mazeHeight,
                        int mazeWidth,
                        int cellSize,
                        int wallThickness,
                        int frameDelay,
                        int hweight,
                        int vweight,
                        boolean showUnseen,
                        boolean showGeneration) {
        this.mazeHeight = mazeHeight;
        this.mazeWidth = mazeWidth;
        this.cellSize = cellSize;
        this.wallThickness = wallThickness;
        this.frameDelay = frameDelay;
        this.hweight = hweight;
        this.vweight = vweight;
        this.showUnseen = showUnseen;
        this.showGeneration = showGeneration;
    }

    /*
    * @return Returns the number of rows in the maze.
    */
    public int getMazeHeight() {
        return mazeHeight;
    }

    /*
    * @return Returns the number of columns in the maze.
    */
    public int getMazeWidth() {
        return mazeWidth;
    }

    /*
    * @return Returns the width in pixels of the rows and columns of the maze.
    */
    public int getCellSize() {
        return cellSize;
    }

    /*
    * @return Returns the width in pixels of the maze walls.
    */
    public int getWallThickness() {
        return wallThickness;
    }

    /*
    * @return Returns the frame delay in ms between animation frames.
    */
    public int getFrameDelay() {
        return frameDelay;
    }

    /*
    * @return Returns the weight given to horizontal movement choices.
    */
    public int getHweight() {
        return hweight;
    }

    /*
    * @return Returns the weight given to vertical movement choices.
    */
    public int getVweight() {
        return vweight;
    }

    /*
    * @return Returns true if unvisited cells should be rendered.
    */
    public boolean showUnseen() {
        return showUnseen;
    }

    /*
    * @return Returns true if the maze generation animation should be shown.
    */
    public boolean showGeneration() {
        return showGeneration;
    }
    
    /*
    * @return Sets the frame delay in ms between animation frames.
    */
    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }

    /*
    * @return Sets whether unvisted cells should be rendered.
    */
    public void setShowUnseen(boolean showUnseen) {
        this.showUnseen = showUnseen;
    }
} 