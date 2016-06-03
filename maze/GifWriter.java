package maze;

import GifSequencer.GifSequenceWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifWriter {
       
    private MazeView mazeview;
    BufferedImage imageBuffer;
    ImageOutputStream output;
    GifSequenceWriter writer;
    
    public GifWriter(MazeView mazeview) {
        this.mazeview = mazeview;
        try {
            imageBuffer = new BufferedImage(mazeview.getWidth(), mazeview.getHeight(),BufferedImage.TYPE_INT_RGB);
            output = new FileImageOutputStream(new File("out.gif"));
            writer = new GifSequenceWriter(output, imageBuffer.getType(), 100, false);
        } catch (Exception e){e.printStackTrace();}
    }
        
    public void snapshot() {
        try {
            mazeview.repaint();
            mazeview.paintAll(imageBuffer.getGraphics());
            writer.writeToSequence(imageBuffer);
        } catch (Exception e){e.printStackTrace();}
    }
    
    public void close() {
        try {
            writer.writeToSequence(imageBuffer);
            writer.close();
            output.close();
        } catch (Exception e){e.printStackTrace();}
    }
}