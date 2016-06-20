package mazemaker;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    
    private static final int FRAMEDELAY = 100;
        
    private static final String BACKSTEP = "Backtrace";
    private static final String BRANCHINGBS = "Branching Backtrace";
    private static final String COINFLIP = "Random Binary Tree";
    private static final String KRUSKAL = "Randomized Kruskal's";
    private static final String PRIM = "Randomized Prim's";
    private static final String BLANK = "Blank Maze";
    
    private static final String RIGHTHAND = "Right Hand Rule";
    private static final String LEFTHAND = "Left hand Rule";
    private static final String RANDOMTURNS = "Random Turns";
    
    private final Alert help = new Alert(AlertType.INFORMATION);
    private final Alert about = new Alert(AlertType.INFORMATION);
    
    private final ImageView pencilImage = new ImageView(
            new Image(getClass().getResourceAsStream("resources/pencil.png"))); 
    private final ImageView cursorImage = new ImageView(
            new Image(getClass().getResourceAsStream("resources/pointer.png"))); 
    
    private final Tooltip pencilTooltip = new Tooltip("Switch to Draw Mode)"); 
    private final Tooltip cursorTooltip = new Tooltip("Switch to Cursor Mode"); 
    
    @FXML private Button modeButton;
    @FXML private Label animLabel;
    @FXML private CheckBox showUnvisitedBox;
    @FXML private Slider speedSlider;
    
    @FXML private TextField rowsField;
    @FXML private TextField colsField;
    @FXML private ComboBox genCombo;
    @FXML private Slider biasSlider;
    
    @FXML private ComboBox solverCombo;
    
    @FXML private TextField cellField;
    @FXML private TextField wallField;
    @FXML private ColorPicker cellColor;
    @FXML private ColorPicker visitedColor;
    @FXML private ColorPicker wallColor;
    @FXML private ColorPicker spriteColor;
    @FXML private ColorPicker goalColor;
    @FXML private ComboBox spriteCombo;
    
    @FXML private ScrollPane scrollPane;
    
    private MazeView mazeview;
    private Timeline playback;
    private GifWriter gifWriter;
    private Task currentTask;
    private List<Datum[]> steps;
    private int step;
    
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
        playback = new Timeline(new KeyFrame(new Duration(FRAMEDELAY), f -> {
            if (step == steps.size()) {
                stopAll();
            } else {
                if (gifWriter != null)
                    gifWriter.snapshot();
                mazeview.draw(steps.get(step++), (String)spriteCombo.getValue());
            }
        }));
        playback.rateProperty().bind(new DoubleBinding(){
            {super.bind(speedSlider.valueProperty());}
 
            @Override
            protected double computeValue() {
                // log scale with ticks at 0.01, 0.1, 1, 10, 100 x
                return Math.pow(10, speedSlider.getValue());
            }
        });
        playback.setCycleCount(Animation.INDEFINITE);
        
        modeButton.setGraphic(pencilImage);
        modeButton.setTooltip(pencilTooltip);
        
        initDialog(help, "Help", parse(IO.readFile("help.text")));
        initDialog(about, "About", parse(IO.readFile("about.text")));
        
        initNumField(rowsField, 1, 20, 500, null);
        initNumField(colsField, 1, 20, 500, null);
        
        initComboBox(genCombo, 
            BACKSTEP,
            BRANCHINGBS,
            COINFLIP,
            KRUSKAL,
            PRIM,
            BLANK);
        
        initComboBox(solverCombo, 
            RIGHTHAND,
            LEFTHAND,
            RANDOMTURNS);
        
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
                    parsedSegments[i].setStyle("-fx-font-size: 12px; -fx-line-spacing: 4px;");
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
    
    /*
    Stop all running actions, read maze from file, set as current maze
    */
    public void openMaze() {
        stopAll();
        mazeview.setMaze(MazeIO.openMaze());
    }
    
    /*
    Save current maze to file
    */
    public void saveMaze() {
        MazeIO.saveMaze(mazeview.getMaze());
    }
    
    /*
    Save snapshot of maze to PNG file
    */
    public void export() {
        IO.saveToPNG(mazeview);
    }
    
    /*
    Toggle between cursor and pencil mode for maze editing.
    */
    public void mode() {
        if (mazeview.getMode() == MazeView.SELECT_MODE) {
            mazeview.setMode(MazeView.PENCIL_MODE);
            modeButton.setGraphic(cursorImage);
            modeButton.setTooltip(cursorTooltip);
        } else {
            mazeview.setMode(MazeView.SELECT_MODE);
            modeButton.setGraphic(pencilImage);
            modeButton.setTooltip(pencilTooltip);
        }
    }
    
    /*
    create new blank maze, set as current maze, run generator
    */
    public void generate() {
        int width = Integer.parseInt(colsField.getText());
        int height = Integer.parseInt(rowsField.getText());
        
        Maze maze = new Maze(width, height);
        
        mazeview.setMaze(maze);
        
        int hBias = (int)biasSlider.getValue();
        int vBias = (int)(biasSlider.getMax() + biasSlider.getMin() - biasSlider.getValue());
        
        String name = (String)genCombo.getValue();
        
        switch (name) {
            case MazeMaker.BACKSTEP:
                runMazeTask(new Backstep(name, maze, hBias, vBias));
                break;
            case MazeMaker.BRANCHINGBS:
                runMazeTask(new BranchingBackstep(name, maze, hBias, vBias, 10));
                break;
            case MazeMaker.COINFLIP:
                runMazeTask(new RandomBinaryTree(name, maze, hBias, vBias));
                break;
            case MazeMaker.KRUSKAL:
                runMazeTask(new Kruskal(name, maze));
                break;
            case MazeMaker.PRIM:
                runMazeTask(new Prim(name, maze));
                break;
            case MazeMaker.BLANK:
            default:
                break;
        }
    }
    
    /*
    run solver
    */
    public void solve() {
        Maze maze = mazeview.getMaze();
        
        String name = (String)solverCombo.getValue();
        
        switch (name) {
            case MazeMaker.RIGHTHAND:
                runMazeTask(new WallFollower(name, maze, true));
                break;
            case MazeMaker.LEFTHAND:
                runMazeTask(new WallFollower(name, maze, false));
                break;
            case MazeMaker.RANDOMTURNS:
                runMazeTask(new RandomTurns(name, maze));
                break;
            default:
                break;
        }
    }
    
    /*
    Pause playback only
    */
    public void pausePlayback() {
        playback.stop();
    }
    
    /*
    If stopped, refresh view.  Resume playback from current position.
    */
    public void playPlayback() {
        if (step == 0) {
            mazeview.setShowAll(false);
            mazeview.redraw();
        }
        if (steps != null)
            playback.play();
    }
    
    /*
    Stop all current actions.
    */
    public void stopAll() {
        if (currentTask != null)
            currentTask.cancel();
        playback.stop();
        step = 0;
        mazeview.setShowAll(true);
        mazeview.clear();
        mazeview.redraw();
        if (gifWriter != null) {
            gifWriter.close();
            gifWriter = null;
        }
    }
    
    /*
    Stop all current actions, create gif recorder
    */
    public void record() {
        stopAll();
        gifWriter = new GifWriter(mazeview, (int)(playback.getRate() * FRAMEDELAY));
        if (!gifWriter.init())
            gifWriter = null;
    }
    
    /*
    Show help dialog
    */
    public void help() {
        help.showAndWait();
    }
    
    /*
    Show about dialog
    */
    public void about() {
        about.showAndWait();
    }
    
    /*
    shared logic between generate() and solve()
    */
    private void runMazeTask(MazeTask task) {
        stopAll();
        
        animLabel.textProperty().bind(task.messageProperty());
        
        task.setOnSucceeded(e -> {
            steps = task.getValue();
            step = 0;
            mazeview.redraw();
            currentTask = null;
        });
        
        currentTask = task;
        new Thread(currentTask).start();
    }
}
