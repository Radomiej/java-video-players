package de.disupport.video;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoPanel extends JPanel {
    private BufferedImage image;

    public VideoPanel() {
    }

    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.setColor(Color.white);
        g.drawOval(0, 0, width, height);
        if (image != null) g.drawImage(image, 0, 0, null);
        System.out.println("Repaint");
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
}
