package mazemaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JColorChooser;

public class ColorButton extends JButton {
    private Color color;

    private ColorButton(Action action) {
        super(action);
    }

    public static ColorButton create(Action action, String label, Color color) {
        ColorButton colorButton = new ColorButton(action);
        colorButton.color = color;
        colorButton.setText(label);
        colorButton.setHorizontalAlignment(JButton.LEFT);
        return colorButton;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        int padding = size.height / 10;
        int x = size.width - size.height + padding;
        int y = padding;
        int s = size.height - padding * 2;
        g.setColor(color);
        g.fillRect(x, y, s, s);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, s, s);
    }

    public Color pickColor() {
        Color chosen = JColorChooser.showDialog(null, getText(), color);
        if (chosen != null)
            color = chosen;
        return color;
    }

    public Color getColor() {
        return color;
    }
}