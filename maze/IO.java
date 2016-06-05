package maze;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public abstract class IO {
    
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
}
