package maze;

import net.kroo.elliot.gifsequencewriter.GifSequenceWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifWriter {
       
    private static Logger logger;
    private MazeView mazeview;
    private BufferedImage imageBuffer;
    private ImageOutputStream output;
    private GifSequenceWriter writer;
    
    public GifWriter(MazeView mazeview) {
        this.mazeview = mazeview;
        logger = Logger.getAnonymousLogger();
        try {
            imageBuffer = new BufferedImage(mazeview.getWidth(), 
                                            mazeview.getHeight(), 
                                            BufferedImage.TYPE_INT_RGB);
            output = new FileImageOutputStream(IO.pickFile("gif", true));
            writer = new GifSequenceWriter(output, 
                                           imageBuffer.getType(), 
                                           mazeview.getFrameDelay(), 
                                           false);
            logger.log(Level.INFO, "GifWriter construction successful");
        } catch (IOException e) {
            logger.log(Level.WARNING, "GifWriter construction failed", e);
        }
    }
        
    public void snapshot() {
        try {
            mazeview.paintAll(imageBuffer.getGraphics());
            writer.writeToSequence(imageBuffer);
            logger.log(Level.INFO, "GifWriter snapshot successful");
        } catch (IOException e) {
            logger.log(Level.WARNING, "GifWriter snapshot failed", e);
        }
    }
    
    public void close() {
        try {
            writer.close();
            output.close();
            logger.log(Level.INFO, "GifWriter finalization successful");
        } catch (IOException e) {
            logger.log(Level.WARNING, "GifWriter finalization failed", e);
        }
    }
}