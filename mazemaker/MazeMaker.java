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

public class MazeMaker extends Application implements Initializable{
        
    public static final String BACKSTEP = "Backtrace";
    public static final String COINFLIP = "Random Binary Tree";
    
    public static final String RIGHTHAND = "Right Hand Rule";
    public static final String LEFTHAND = "Left hand Rule";
    public static final String RANDOMTURNS = "Random Turns";
    
    private final Alert help = new Alert(AlertType.INFORMATION);
    private final Alert about = new Alert(AlertType.INFORMATION);
    
    @FXML private TextField rowsField;
    @FXML private TextField colsField;
    @FXML private TextField cellField;
    @FXML private TextField wallField;
    @FXML private TextField rateField;
    @FXML private ComboBox genCombo;
    @FXML private CheckBox instantBox;
    @FXML private Slider slider;
    @FXML private ComboBox solverCombo;
    @FXML private CheckBox showUnvisitedBox;
    @FXML private ColorPicker cellColor;
    @FXML private ColorPicker visitedColor;
    @FXML private ColorPicker wallColor;
    @FXML private ColorPicker genColor;
    @FXML private ColorPicker solverColor;
    @FXML private ColorPicker goalColor;
    @FXML private ScrollPane scrollPane;
    
    private MazeView mazeview;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MazeMaker.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("resources/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDialog(help, "Help", parse(IO.readFile("help.text")));
        initDialog(about, "About", parse(IO.readFile("about.text")));
        
        initNumField(rowsField, 1, 20, 500);
        initNumField(colsField, 1, 20, 500);
        initNumField(cellField, 1, 20, 100);
        initNumField(wallField, 1, 1, 25);
        initNumField(rateField, 1, 100, 10000);
        
        initComboBox(genCombo, new String[]{
            BACKSTEP,
            COINFLIP});
        
        initComboBox(solverCombo, new String[]{
            RIGHTHAND,
            LEFTHAND,
            RANDOMTURNS});
        
        cellColor.setValue(Color.WHITE);
        visitedColor.setValue(Color.PINK);
        wallColor.setValue(Color.BLACK);
        genColor.setValue(Color.BLUE);
        solverColor.setValue(Color.RED);
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
                case '%':
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
            boolean ok = true;
            try {
                int value = Integer.parseInt(newValue);
                if (!(min <= value && value <= max))
                    ok = false;
            } catch (NumberFormatException e) {
                ok = false;
            }
            if (!ok)
                field.setText(oldValue);
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
        mazeview.runActor((String)genCombo.getValue(), getHBias(), getVBias(), getFrameDelay(), getInstant(), getShowUnvisited());
    }
    
    public void solve() {
        mazeview.runActor((String)solverCombo.getValue(), getHBias(), getVBias(), getFrameDelay(), false, getShowUnvisited());
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
        mazeview.record();
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
    
    /*
    COLOR HANDLING
    */
    
    public void setCellColor() {
        mazeview.getPalette().setCellColor(cellColor.getValue());
        mazeview.redraw();
    }
    
    public void setVisitedColor() {
        mazeview.getPalette().setVisitedColor(visitedColor.getValue());
        mazeview.redraw();
    }
    
    public void setWallColor() {
        mazeview.getPalette().setWallColor(wallColor.getValue());
        mazeview.redraw();
    }
    
    public void setGenColor() {
        mazeview.getPalette().setGenColor(genColor.getValue());
        mazeview.redraw();
    }
    
    public void setSolverColor() {
        mazeview.getPalette().setSolverColor(solverColor.getValue());
        mazeview.redraw();
    }
    
    public void setGoalColor() {
        mazeview.getPalette().setGoalColor(goalColor.getValue());
        mazeview.redraw();
    }
    
    private Palette getPalette() {
        return new Palette(cellColor.getValue(),
                           visitedColor.getValue(),
                           wallColor.getValue(),
                           genColor.getValue(),
                           solverColor.getValue(),
                           goalColor.getValue());
    }
    
    /*
    HELPERS, GUI READERS
    */
    
    private void setMaze(Maze maze) {
        mazeview = new MazeView(maze, getPalette(), getCellSize(), getWallThickness());
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
        return Integer.parseInt(rateField.getText());
    }
    
    private int getHBias() {
        return (int)slider.getValue();
    }
    
    private int getVBias() {
        return (int)(slider.getMax() + slider.getMin() - slider.getValue());
    }
    
    private boolean getInstant() {
        return instantBox.isSelected();
    }
    
    private boolean getShowUnvisited() {
        return showUnvisitedBox.isSelected();
    }
    
}
