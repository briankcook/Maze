package mazemaker;

import javafx.scene.paint.Color;

public class Palette {

    private Color cellColor;
    private Color visitedColor;
    private Color wallColor;
    private Color genColor;
    private Color solverColor;
    private Color goalColor;

    public Palette(Color cellColor, Color visitedColor, Color wallColor, Color genColor, Color solverColor, Color goalColor) {
        this.cellColor = cellColor;
        this.visitedColor = visitedColor;
        this.wallColor = wallColor;
        this.genColor = genColor;
        this.solverColor = solverColor;
        this.goalColor = goalColor;
    }

    public Color getCellColor() {
        return cellColor;
    }

    public Color getVisitedColor() {
        return visitedColor;
    }

    public Color getWallColor() {
        return wallColor;
    }

    public Color getGenColor() {
        return genColor;
    }

    public Color getSolverColor() {
        return solverColor;
    }

    public Color getGoalColor() {
        return goalColor;
    }

    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
    }

    public void setVisitedColor(Color visitedColor) {
        this.visitedColor = visitedColor;
    }

    public void setWallColor(Color wallColor) {
        this.wallColor = wallColor;
    }

    public void setGenColor(Color genColor) {
        this.genColor = genColor;
    }

    public void setSolverColor(Color solverColor) {
        this.solverColor = solverColor;
    }

    public void setGoalColor(Color goalColor) {
        this.goalColor = goalColor;
    }
}
