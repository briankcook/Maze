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

    public int getMazeHeight() {
        return mazeHeight;
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getWallThickness() {
        return wallThickness;
    }

    public int getFrameDelay() {
        return frameDelay;
    }

    public int getHweight() {
        return hweight;
    }

    public int getVweight() {
        return vweight;
    }

    public boolean showUnseen() {
        return showUnseen;
    }

    public boolean showGeneration() {
        return showGeneration;
    }
    
    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }

    public void setShowUnseen(boolean showUnseen) {
        this.showUnseen = showUnseen;
    }
} 