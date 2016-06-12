package mazemaker.io;

import java.awt.image.BufferedImage;
import net.kroo.elliot.gifsequencewriter.GifSequenceWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;

public class GifWriter {
       
    private static final Logger LOGGER = Logger.getAnonymousLogger();
    private Canvas canvas;
    private WritableImage fximage;
    private ImageOutputStream output;
    private GifSequenceWriter writer;
    
    public GifWriter(Canvas canvas, int frameDelay) {
        this.canvas = canvas;
        try {
            fximage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
            output = new FileImageOutputStream(IO.pickFile("gif", true));
            writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, frameDelay, false);
            LOGGER.log(Level.INFO, "GifWriter construction successful");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "GifWriter construction failed", e);
            failed();
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
            JOptionPane.showMessageDialog(null, "GIF saved successfully!");
            LOGGER.log(Level.INFO, "GifWriter finalization successful");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "GifWriter finalization failed", e);
            failed();
        }
    }
    
    private void failed() {
        JOptionPane.showMessageDialog(null, "Something went wrong while recording your GIF.", "Oh no!", JOptionPane.ERROR_MESSAGE);
    }
}