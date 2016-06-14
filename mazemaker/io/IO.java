package mazemaker.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import mazemaker.MazeMaker;

public interface IO {
    
    public static File pickFile(String extension, boolean save) throws IOException {
        String dot = "." + extension;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter(dot + " files", extension));
        File file = save ?
                    fileChooser.showSaveDialog(null):
                    fileChooser.showOpenDialog(null);
        if (file == null)
            throw new IOException();
        if (!file.getName().endsWith(dot))
            file = new File(file.getPath() + dot);
        return file;
    }
    
    public static List<String> readFile(String name) {
        Scanner scanner = new Scanner(MazeMaker.class.getResourceAsStream("resources/" + name), "UTF-8");
        List<String> lines = new ArrayList();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        return lines;
    }
    
    public static void saveToPNG(Canvas canvas) {
        try {
            WritableImage fximage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
            canvas.snapshot(null, fximage);
            ImageIO.write(SwingFXUtils.fromFXImage(fximage, null), "png", pickFile("png", true));
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Image export failed", e);
        }
    }
}
