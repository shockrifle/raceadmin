package hu.danielb.raceadmin;

import javax.swing.*;
import java.awt.*;

class BackgroundPanel extends JPanel {

    Image image;

    public BackgroundPanel(Image img) {
        image = img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}
