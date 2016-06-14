package mazemaker;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
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
import javafx.util.Duration;
import mazemaker.io.*;
import mazemaker.maze.*;
import mazemaker.maze.generators.*;
import mazemaker.maze.solvers.*;

public class MazeMaker extends Application implements Initializable{
        
    private static final String BACKSTEP = "Backtrace";
    private static final String BRANCHINGBS = "Branching Backtrace";
    private static final String COINFLIP = "Random Binary Tree";
    private static final String KRUSKAL = "Randomized Kruskal's";
    
    private static final String RIGHTHAND = "Right Hand Rule";
    private static final String LEFTHAND = "Left hand Rule";
    private static final String RANDOMTURNS = "Random Turns";
    
    private static final double SPEEDFACTOR = 0.7;
    
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
    private Timeline timeline;
    private GifWriter gifWriter;
    
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
        
        initNumField(rowsField, 1, 20, 500, null);
        initNumField(colsField, 1, 20, 500, null);
        
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
        
        mazeview = new MazeView();
        
        initNumField(cellField, 1, 20, 100, mazeview.cellSize);
        initNumField(wallField, 1,  2,  25, mazeview.wallThickness);
        
        cellColor.setValue(Color.WHITE);
        wallColor.setValue(Color.BLACK);
        goalColor.setValue(Color.GRAY);
        spriteColor.setValue(Color.BLUE);
        visitedColor.setValue(Color.PINK);
        
        mazeview.cellColor.bind(cellColor.valueProperty());
        mazeview.wallColor.bind(wallColor.valueProperty());
        mazeview.goalColor.bind(goalColor.valueProperty());
        mazeview.spriteColor.bind(spriteColor.valueProperty());
        mazeview.visitedColor.bind(visitedColor.valueProperty());
        
        mazeview.showUnvisited.bind(showUnvisitedBox.selectedProperty());
        
        scrollPane.setContent(mazeview);
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
    
    private void initNumField(TextField field, int min, int initial, int max, IntegerProperty tie) {
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
            if (tie != null) 
                tie.set(Integer.parseInt(field.getText()));
        });
        if (tie != null) 
            tie.set(initial);
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
        mazeview.setMaze(new Maze(getMazeWidth(), getMazeHeight()));
    }
    
    public void openMaze() {
        mazeview.setMaze(MazeIO.openMaze());
    }
    
    public void saveMaze() {
        MazeIO.saveMaze(mazeview.getMaze());
    }
    
    public void generate() {
        runActor(makeActor(getGenerator()), getInstant());
    }
    
    public void solve() {
        runActor(makeActor(getSolver()), false);
    }
    
    public void cleanUp() {
        mazeview.clear();
        stopPlayback();
    }
    
    public void pausePlayback() {
        if (timeline != null)
            timeline.stop();
    }
    
    public void playPlayback() {
        if (timeline != null)
            timeline.play();
    }
    
    public void stopPlayback() {
        pausePlayback();
        timeline = null;
        mazeview.playing = false;
        mazeview.redraw();
        if (gifWriter != null) {
            gifWriter.close();
            gifWriter = null;
        }
    }
    
    public void record() {
        cleanUp();
        gifWriter = new GifWriter(mazeview, getFrameDelay());
        if (!gifWriter.init())
            gifWriter = null;
    }
    
    public void speedUp() {
        if (timeline != null)
            timeline.setRate(timeline.getRate() / SPEEDFACTOR);
    }
    
    public void slowDown() {
        if (timeline != null)
            timeline.setRate(timeline.getRate() * SPEEDFACTOR);
    }
    
    public void help() {
        help.showAndWait();
    }
    
    public void about() {
        about.showAndWait();
    }
    
    public void pointer() {
        mazeview.setMode(MazeView.SELECT_MODE);
    }
    
    public void pencil() {
        mazeview.setMode(MazeView.PENCIL_MODE);
    }
    
    public void resize() {
        mazeview.resize();
    }
    
    /*
    ACTOR LOGIC
    */
    
    public void runActor(MazeActor actor, boolean instant) {
        if (gifWriter == null)
            cleanUp();
        mazeview.redraw();
        if (actor == null)
            return;
        actor.init();
        if (instant) {
            while (actor.step() != null)
                if (gifWriter != null)
                    gifWriter.snapshot();
            cleanUp();
        } else {
            mazeview.playing = true;
            mazeview.visited[0][0] = true;
            mazeview.drawCell(0,0);
            timeline = new Timeline(new KeyFrame(Duration.millis(getFrameDelay()),
                ae -> {
                    if (gifWriter != null)
                        gifWriter.snapshot();
                    Datum[] data = actor.step();
                    if (data == null) {
                        stopPlayback();
                    } else {
                        for (Datum datum : data) {
                            mazeview.visited[datum.x][datum.y] = true;
                            mazeview.drawCell(datum.x, datum.y); 
                            if (datum.facing != null)
                                mazeview.drawActor(datum, getSprite());
                        }
                    }
                }
            ));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
        mazeview.redraw();
    }
    
    private MazeActor makeActor(String name) {
        Maze maze = mazeview.getMaze();
        switch (name) {
            case MazeMaker.BACKSTEP:
                maze.reset();
                return new Backstep(maze, getHBias(), getVBias());
            case MazeMaker.BRANCHINGBS:
                maze.reset();
                return new BranchingBackstep(maze, getHBias(), getVBias(), 10);
            case MazeMaker.COINFLIP:
                maze.reset();
                return new RandomBinaryTree(maze, getHBias(), getVBias());
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
    
    /*
    GUI GETTERS
    */
    
    private int getMazeWidth() {
        return Integer.parseInt(colsField.getText());
    }
    
    private int getMazeHeight() {
        return Integer.parseInt(rowsField.getText());
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
    
    private String getSprite() {
        return (String)spriteCombo.getValue();
    }
    
}
