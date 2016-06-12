package mazemaker.io;

import java.awt.image.BufferedImage;
import net.kroo.elliot.gifsequencewriter.GifSequenceWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifWriter {
       
    private static final Logger LOGGER = Logger.getAnonymousLogger();
    private final Canvas canvas;
    private final int frameDelay;
    private WritableImage fximage;
    private ImageOutputStream output;
    private GifSequenceWriter writer;
    
    public GifWriter(Canvas canvas, int frameDelay) {
        this.canvas = canvas;
        this.frameDelay = frameDelay;
    }
    
    public boolean init() {
        try {
            fximage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
            output = new FileImageOutputStream(IO.pickFile("gif", true));
            writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, frameDelay, false);
            LOGGER.log(Level.INFO, "GifWriter construction successful");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "GifWriter construction failed", e);
            failed();
            return false;
        }
    }
        
    public void snapshot() {
        try {
            canvas.snapshot(null, fximage);
            writer.writeToSequence(SwingFXUtils.fromFXImage(fximage, null));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "GifWriter snapshot failed", e);
            failed();
        }
    }
    
    public void close() {
        try {
            writer.close();
            output.close();
            Alert success = new Alert(AlertType.INFORMATION);
            success.setContentText("Your GIF have saved successfully!");
            success.setTitle("Success!");
            success.show();
            LOGGER.log(Level.INFO, "GifWriter finalization successful");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "GifWriter finalization failed", e);
            failed();
        }
    }
    
    private void failed() {
        Alert failure = new Alert(AlertType.ERROR);
        failure.setContentText("Your GIF recording failed!");
        failure.setTitle("Oh no!");
        failure.show();
    }
}