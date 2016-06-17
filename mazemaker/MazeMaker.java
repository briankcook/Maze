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
        playback = new Timeline();
        playback.setCycleCount(1);
        playback.rateProperty().bind(new DoubleBinding(){
            {super.bind(speedSlider.valueProperty());}
 
            @Override
            protected double computeValue() {
                // log scale with ticks at 0.01, 0.1, 1, 10, 100 x
                return Math.pow(10, speedSlider.getValue());
            }
        });
        
        modeButton.setGraphic(pencilImage);
        modeButton.setTooltip(pencilTooltip);
        
        initDialog(help, "Help", parse(IO.readFile("help.text")));
        initDialog(about, "About", parse(IO.readFile("about.text")));
        
        initNumField(rowsField, 1, 20, 500, null);
        initNumField(colsField, 1, 20, 500, null);
        
        initComboBox(genCombo, new String[]{
            BACKSTEP,
            BRANCHINGBS,
            COINFLIP,
            KRUSKAL,
            PRIM,
            BLANK});
        
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
    
    public void openMaze() {
        playback.stop();
        mazeview.setMaze(MazeIO.openMaze());
    }
    
    public void saveMaze() {
        MazeIO.saveMaze(mazeview.getMaze());
    }
    
    public void export() {
        IO.saveToPNG(mazeview);
    }
    
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
    
    public void generate() {
        playback.stop();
        mazeview.setMaze(new Maze(getMazeWidth(), getMazeHeight()));
        runActor(makeActor(getGenerator()));
    }
    
    public void solve() {
        runActor(makeActor(getSolver()));
    }
    
    public void pausePlayback() {
        playback.pause();
    }
    
    public void playPlayback() {
        if (playback.getStatus() == Animation.Status.STOPPED) {
            mazeview.setShowAll(false);
            mazeview.redraw();
        }
        playback.play();
    }
    
    public void stopPlayback() {
        playback.stop();
        mazeview.setShowAll(true);
        mazeview.clear();
        mazeview.redraw();
        if (gifWriter != null) {
            gifWriter.close();
            gifWriter = null;
        }
    }
    
    public void record() {
        stopPlayback();
        gifWriter = new GifWriter(mazeview, (int)(playback.getRate() * FRAMEDELAY));
        if (!gifWriter.init())
            gifWriter = null;
    }
    
    public void help() {
        help.showAndWait();
    }
    
    public void about() {
        about.showAndWait();
    }
    
    /*
    ACTOR LOGIC
    */
    
    private void runActor(MazeActor actor) {
        stopPlayback();
        if (actor == null)
            return;
        actor.init();
        List<Datum[]> steps = actor.run();
        animLabel.setText(animLabel.getText() + " : " + steps.size() + " steps");
        List<KeyFrame> frames = playback.getKeyFrames();
        frames.clear();
        int i = 0;
        for ( /* */ ; i < steps.size() ; i++) {
            Datum[] step = steps.get(i);
            Duration duration = Duration.millis(FRAMEDELAY * i);
            frames.add(new KeyFrame(duration, e -> {
                if (gifWriter != null)
                    gifWriter.snapshot();
                mazeview.draw(step, getSprite());
            }));
        }
        frames.add(new KeyFrame(Duration.millis(FRAMEDELAY * i), e -> mazeview.redraw()));
        stopPlayback();
    }
    
    private MazeActor makeActor(String name) {
        Maze maze = mazeview.getMaze();
        animLabel.setText(name);
        switch (name) {
            case MazeMaker.BACKSTEP:
                return new Backstep(maze, getHBias(), getVBias());
            case MazeMaker.BRANCHINGBS:
                return new BranchingBackstep(maze, getHBias(), getVBias(), 10);
            case MazeMaker.COINFLIP:
                return new RandomBinaryTree(maze, getHBias(), getVBias());
            case MazeMaker.KRUSKAL:
                return new Kruskal(maze);
            case MazeMaker.PRIM:
                return new Prim(maze);
            case MazeMaker.BLANK:
                return null;
            case MazeMaker.RIGHTHAND:
                return new WallFollower(maze, true);
            case MazeMaker.LEFTHAND:
                return new WallFollower(maze, false);
            case MazeMaker.RANDOMTURNS:
                return new RandomTurns(maze);
            default:
                animLabel.setText("error");
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
    
    private int getHBias() {
        return (int)biasSlider.getValue();
    }
    
    private int getVBias() {
        return (int)(biasSlider.getMax() + biasSlider.getMin() - biasSlider.getValue());
    }
    
    private String getGenerator() {
        return (String)genCombo.getValue();
    }
    
    private String getSolver() {
        return (String)solverCombo.getValue();
    }
    
    private String getSprite() {
        return (String)spriteCombo.getValue();
    }
    
}
