package mazemaker;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import mazemaker.io.IO;
import mazemaker.io.MazeIO;
import mazemaker.maze.Maze;
import mazemaker.maze.MazeActor;
import mazemaker.maze.generators.*;
import mazemaker.maze.solvers.*;

public class MazeMaker extends Application implements Initializable{
        
    public static final String BACKSTEP = "Backtrace";
    public static final String BRANCHINGBS = "Branching Backtrace";
    public static final String COINFLIP = "Random Binary Tree";
    public static final String KRUSKAL = "Randomized Kruskal's";
    
    public static final String RIGHTHAND = "Right Hand Rule";
    public static final String LEFTHAND = "Left hand Rule";
    public static final String RANDOMTURNS = "Random Turns";
    
    private final Alert help = new Alert(AlertType.INFORMATION);
    private final Alert about = new Alert(AlertType.INFORMATION);
    
    @FXML private TextField rowsField;
    @FXML private TextField colsField;
    @FXML private TextField cellField;
    @FXML private TextField wallField;
    @FXML private ComboBox genCombo;
    @FXML private CheckBox instantBox;
    @FXML private Slider biasSlider;
    @FXML private ComboBox solverCombo;
    @FXML private Slider speedSlider;
    @FXML private CheckBox showUnvisitedBox;
    @FXML private ColorPicker cellColor;
    @FXML private ColorPicker visitedColor;
    @FXML private ColorPicker wallColor;
    @FXML private ColorPicker spriteColor;
    @FXML private ColorPicker goalColor;
    @FXML private ComboBox spriteCombo;
    @FXML private ScrollPane scrollPane;
    
    private MazeView mazeview;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MazeMaker.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MazeMaker");
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDialog(help, "Help", parse(IO.readFile("help.text")));
        initDialog(about, "About", parse(IO.readFile("about.text")));
        
        initNumField(rowsField, 1, 20, 500);
        initNumField(colsField, 1, 20, 500);
        initNumField(cellField, 1, 20, 100);
        initNumField(wallField, 1, 2, 25);
        
        initComboBox(genCombo, new String[]{
            BACKSTEP,
            BRANCHINGBS,
            COINFLIP,
            KRUSKAL});
        
        initComboBox(solverCombo, new String[]{
            RIGHTHAND,
            LEFTHAND,
            RANDOMTURNS});
        
        initComboBox(spriteCombo, MazeView.getSpriteTypes());
        
        cellColor.setValue(Color.WHITE);
        visitedColor.setValue(Color.PINK);
        wallColor.setValue(Color.BLACK);
        spriteColor.setValue(Color.BLUE);
        goalColor.setValue(Color.GRAY);
        
        newMaze();
    }
    
    private TextFlow parse(List<String> lines) {
        Text[] parsedSegments = new Text[lines.size()];
        for (int i = 0 ; i < lines.size() ; i++) {
            parsedSegments[i] = new Text(lines.get(i).substring(1) + "\n");
            switch (lines.get(i).charAt(0)) {
                case '#':
                    parsedSegments[i].setStyle("-fx-font-size: 20px;");
                    break;
                case '$':
                    parsedSegments[i].setStyle("-fx-font-size: 16px;");
                    break;
                default:
                    parsedSegments[i].setStyle("-fx-font-size: 12px;");
                    break;
            }
        }
        return new TextFlow(parsedSegments);       
    }
    
    private void initDialog(Alert dialog, String title, TextFlow content) {
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.getDialogPane().setStyle("-fx-max-width: 500px;");
        dialog.getDialogPane().setContent(content);
    }
    
    private void initNumField(TextField field, int min, int initial, int max) {
        field.setText(Integer.toString(initial));
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newVal = Integer.parseInt(newValue);
                if (!(min <= newVal))
                    field.setText(Integer.toString(min));
                else if (!(newVal <= max))
                    field.setText(Integer.toString(max));
            } catch (NumberFormatException e) {
                field.setText(oldValue);
            }
        });
    }
    
    private void initComboBox(ComboBox comboBox, String... values) {
        comboBox.setItems(FXCollections.observableArrayList(values));
        comboBox.setValue(comboBox.getItems().get(0));
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    /*
    GUI ACTIONS
    */
    
    public void newMaze() {
        setMaze(new Maze(getMazeWidth(), getMazeHeight()));
    }
    
    public void openMaze() {
        setMaze(MazeIO.openMaze());
    }
    
    public void saveMaze() {
        MazeIO.saveMaze(mazeview.getMaze());
    }
    
    public void generate() {
        mazeview.runActor(makeActor(getGenerator(), getHBias(), getVBias()),
                          getSprite(), getFrameDelay(), getInstant(), getShowUnvisited());
    }
    
    public void solve() {
        mazeview.runActor(makeActor(getSolver(), getHBias(), getVBias()),
                          getSprite(), getFrameDelay(), false, getShowUnvisited());
    }
    
    public void cleanUp() {
        mazeview.cleanUp();
    }
    
    public void pausePlayback() {
        mazeview.pause();
    }
    
    public void playPlayback() {
        mazeview.play();
    }
    
    public void stopPlayback() {
        mazeview.stop();
    }
    
    public void record() {
        mazeview.record(getFrameDelay());
    }
    
    public void speedUp() {
        mazeview.speedUp();
    }
    
    public void slowDown() {
        mazeview.slowDown();
    }
    
    public void help() {
        help.showAndWait();
    }
    
    public void about() {
        about.showAndWait();
    }
    
    public void pointer() {
        mazeview.pointer();
    }
    
    public void pencil() {
        mazeview.pencil();
    }
    
    /*
    COLOR HANDLING
    */
    
    public void setCellColor() {
        mazeview.setCellColor(cellColor.getValue());
    }
    
    public void setVisitedColor() {
        mazeview.setVisitedColor(visitedColor.getValue());
    }
    
    public void setWallColor() {
        mazeview.setWallColor(wallColor.getValue());
    }
    
    public void setSpriteColor() {
        mazeview.setSpriteColor(spriteColor.getValue());
    }
    
    public void setGoalColor() {
        mazeview.setGoalColor(goalColor.getValue());
    }
    
    /*
    HELPERS, GUI READERS
    */
    
    private MazeActor makeActor(String name, int hBias, int vBias) {
        Maze maze = mazeview.getMaze();
        switch (name) {
            case MazeMaker.BACKSTEP:
                maze.reset();
                return new Backstep(maze, hBias, vBias);
            case MazeMaker.BRANCHINGBS:
                maze.reset();
                return new BranchingBackstep(maze, hBias, vBias, 10);
            case MazeMaker.COINFLIP:
                maze.reset();
                return new RandomBinaryTree(maze, hBias, vBias);
            case MazeMaker.KRUSKAL:
                maze.reset();
                return new Kruskal(maze);
            case MazeMaker.RIGHTHAND:
                return new WallFollower(maze, true);
            case MazeMaker.LEFTHAND:
                return new WallFollower(maze, false);
            case MazeMaker.RANDOMTURNS:
                return new RandomTurns(maze);
            default:
                return null;
        }
    }
    
    private void setMaze(Maze maze) {
        mazeview = new MazeView(maze, getCellSize(), getWallThickness());
        setCellColor();
        setVisitedColor();
        setWallColor();
        setSpriteColor();
        setGoalColor();
        scrollPane.setContent(mazeview);
    }
    
    private int getMazeWidth() {
        return Integer.parseInt(colsField.getText());
    }
    
    private int getMazeHeight() {
        return Integer.parseInt(rowsField.getText());
    }
    
    private int getCellSize() {
        return Integer.parseInt(cellField.getText());
    }
    
    private int getWallThickness() {
        return Integer.parseInt(wallField.getText());
    }
    
    private int getFrameDelay() {
        return (int)Math.pow(10, speedSlider.getMax() - speedSlider.getValue());
    }
    
    private int getHBias() {
        return (int)biasSlider.getValue();
    }
    
    private int getVBias() {
        return (int)(biasSlider.getMax() + biasSlider.getMin() - biasSlider.getValue());
    }
    
    private String getGenerator() {
        return (String)genCombo.getValue();
    }
    
    private boolean getInstant() {
        return instantBox.isSelected();
    }
    
    private String getSolver() {
        return (String)solverCombo.getValue();
    }
    
    private boolean getShowUnvisited() {
        return showUnvisitedBox.isSelected();
    }
    
    private String getSprite() {
        return (String)spriteCombo.getValue();
    }
    
}
