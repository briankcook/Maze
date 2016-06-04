package maze;

import gifsequencewriter.GifSequenceWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifWriter {
       
    private MazeView mazeview;
    BufferedImage imageBuffer;
    ImageOutputStream output;
    GifSequenceWriter writer;
    
    public GifWriter(MazeView mazeview) throws IOException {
        this.mazeview = mazeview;
            imageBuffer = new BufferedImage(mazeview.getWidth(), mazeview.getHeight(),BufferedImage.TYPE_INT_RGB);
            output = new FileImageOutputStream(new File("out.gif"));
            writer = new GifSequenceWriter(output, imageBuffer.getType(), 100, false);
    }
        
    public void snapshot() throws IOException {
            mazeview.repaint();
            mazeview.paintAll(imageBuffer.getGraphics());
            writer.writeToSequence(imageBuffer);
    }
    
    public void close() throws IOException {
            writer.writeToSequence(imageBuffer);
            writer.close();
            output.close();
    }
}