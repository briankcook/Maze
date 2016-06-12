package mazemaker.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
}
