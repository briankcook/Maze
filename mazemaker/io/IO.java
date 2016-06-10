package mazemaker.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import mazemaker.MazeMaker;

public interface IO {
    
    public static File pickFile(String extension, boolean save) throws IOException {
        String dot = "." + extension;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(dot + " files", extension));
        int status = save ?
                     fileChooser.showSaveDialog(null):
                     fileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(dot))
                file = new File(file.getPath() + dot);
            return file;
        }
        throw new IOException();
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
