package maze;

import gifsequencewriter.GifSequenceWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

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
            output = new FileImageOutputStream(pickFile());
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
    
    private File pickFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter(){
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String name = f.getName();
                if (name.length() < 5)
                    return false;
                return ".gif".equalsIgnoreCase(name.substring(name.length()-4));
            }
            @Override
            public String getDescription() {
                return ".gif files";
            }
        });
        int status = fileChooser.showSaveDialog(null);
        if (status == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();
        throw new IOException();
    }
}